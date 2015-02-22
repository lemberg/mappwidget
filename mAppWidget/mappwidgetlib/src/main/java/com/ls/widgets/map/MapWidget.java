/*************************************************************************
* Copyright (c) 2015 Lemberg Solutions
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
**************************************************************************/

package com.ls.widgets.map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Scroller;
import android.widget.ZoomButtonsController;
import android.widget.ZoomButtonsController.OnZoomListener;

import com.ls.widgets.map.config.GPSConfig;
import com.ls.widgets.map.config.MapConfigParser;
import com.ls.widgets.map.config.MapGraphicsConfig;
import com.ls.widgets.map.config.OfflineMapConfig;
import com.ls.widgets.map.events.MapScrolledEvent;
import com.ls.widgets.map.events.MapTouchedEvent;
import com.ls.widgets.map.events.ObjectTouchEvent;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.interfaces.MapEventsListener;
import com.ls.widgets.map.interfaces.MapLocationListener;
import com.ls.widgets.map.interfaces.OnGridReadyListener;
import com.ls.widgets.map.interfaces.OnLocationChangedListener;
import com.ls.widgets.map.interfaces.OnMapDoubleTapListener;
import com.ls.widgets.map.interfaces.OnMapLongClickListener;
import com.ls.widgets.map.interfaces.OnMapScrollListener;
import com.ls.widgets.map.interfaces.OnMapTilesFinishedLoadingListener;
import com.ls.widgets.map.interfaces.OnMapTouchListener;
import com.ls.widgets.map.location.PositionMarker;
import com.ls.widgets.map.model.Grid;
import com.ls.widgets.map.model.MapLayer;
import com.ls.widgets.map.providers.AssetTileProvider;
import com.ls.widgets.map.providers.ExternalStorageTileProvider;
import com.ls.widgets.map.providers.GPSLocationProvider;
import com.ls.widgets.map.providers.TileProvider;
import com.ls.widgets.map.utils.Graphics;
import com.ls.widgets.map.utils.MathUtils;
import com.ls.widgets.map.utils.OfflineMapUtil;
import com.ls.widgets.map.utils.PivotFactory;
import com.ls.widgets.map.utils.PivotFactory.PivotPosition;
import com.ls.widgets.map.utils.Resources;
import com.ls.widgets.map.utils.TransformUtils;

public class MapWidget extends View implements MapLocationListener {
	private static final String MSG_MAP_DATA_IS_CORRUPTED_OR_MISSING = "Map data is corrupted or missing.";

	private final static String TAG = "MAP WIDGET";

	private final static long POS_PIN_ID = 1;

	private enum Mode {
		NONE, ZOOMED, ZOOM
	};

	private OfflineMapConfig config;
	private ZoomButtonsController zoomBtnsController;

	private Grid grid;
	private Grid prevGrid;

	private Paint paint;

	private float scale;
	private double pinchZoomScale;

	private boolean doNotZoom;
	private boolean isAnimationEnabled;
	private boolean byUser;

	// Represents layers in the map
	private MapLayer topmostLayer;
	private ArrayList<MapLayer> layers;
	private Map<Long, Layer> layersMap;

	// Provider that handles loading of map tiles.
	protected TileProvider tileProvider;
	protected GPSLocationProvider locationProvider;

	// Listeners
	private OnMapTouchListener mapTouchListener;
	private OnMapTilesFinishedLoadingListener mapTilesReadyListener;
	private OnMapScrollListener mapScrollListener;
	private ArrayList<MapEventsListener> mapEventsListeners;
	private OnLocationChangedListener locationChangeListener;
	private OnLongClickListener longClickListener;
	private OnMapLongClickListener mapLongClicklistener;
	private OnMapDoubleTapListener onDoubleTapListener;
	private OnTouchListener onTouchListener;
	
	private Mode mode;

	// Smooth scrolling
	private GestureDetector gestureDetector;
	private Scroller scroller;

	// debug
	private boolean debugEnabled = false;
	private RectF lastTouchedRect;

	private boolean isZooming;
	private boolean isDestroying;
	private boolean userTouching;
	private double pinchStartDistance;
	private int mapPivotX;
	private int mapPivotY;

	private static Bitmap logo;
	private Rect drawingRect;

	private Runnable restoreScrollPosRunnable;
	private Runnable performAfterZoom;
	private Runnable performAfterTranslate;

	private boolean requestCenterMap;




	/**
	 * Creates instance of map widget.
	 * 
	 * @param context
	 *            - context
	 * @param rootMapFolder
	 *            - folder that contains map resources inside your assets.
	 * @param initialZoomLevel
	 *            - initial zoom level.
	 */
	public MapWidget(Context context, String rootMapFolder, int initialZoomLevel) {
		this(null, context, rootMapFolder, initialZoomLevel);
	}

	/**
	 * Creates instance of map widget.
	 * 
	 * @param context
	 *            - context
	 * @param rootMapFolder
	 *            - instance of File that points to the map resources which are
	 *            located on the external storage.
	 * @param initialZoomLevel
	 *            - initial zoom level
	 */
	public MapWidget(Context context, File rootMapFolder, int initialZoomLevel) {
		this(null, context, rootMapFolder, initialZoomLevel);
	}

	/**
	 * Creates instance of map widget. Zoom level will be set to 10.
	 * 
	 * @param context
	 *            - Context
	 * @param rootMapFolder
	 *            - folder that contains map resources inside your assets.
	 */
	public MapWidget(Context context, String rootMapFolder) {
		this(null, context, rootMapFolder, 10);
	}

	/**
	 * Creates instance of map widget. Zoom level will be set to 10.
	 * 
	 * @param context
	 *            - Context
	 * @param rootMapFolder
	 *            - instance of File that points to the map resources which are
	 *            located on the external storage.
	 */
	public MapWidget(Context context, File rootMapFolder) {
		this(null, context, rootMapFolder, 10);
	}

	/**
	 * Creates instance of map widget.
	 * 
	 * @param bundle
	 *            - bundle that were used to save map widget's state.
	 * @param context
	 *            - Context
	 * @param rootMapFolder
	 *            - instance of File that points to the map resources which are
	 *            located on the external storage.
	 * @param initialZoomLevel
	 *            - zoom level that will be set in case if bundle doesn't
	 *            contain previously saved state.
	 */
	public MapWidget(Bundle bundle, Context context, File rootMapFolder,
			int initialZoomLevel) {
		super(context);

		initCommonStuff(context);

		String configPath = OfflineMapUtil.getConfigFilePath(rootMapFolder
				.getAbsolutePath());

		try {
			MapConfigParser configParser = new MapConfigParser(
					rootMapFolder.getAbsolutePath());

			config = configParser.parse(context, new File(configPath));

			if (config != null) {
				tileProvider = new ExternalStorageTileProvider(config);
				int maxZoomLevel = OfflineMapUtil.getMaxZoomLevel(
						config.getImageWidth(), config.getImageHeight());
				int zoomLevel = initialZoomLevel;
				float scale = 1.0f;

				if (bundle != null) {
					if (bundle.containsKey("com.ls.zoomLevel"))
						zoomLevel = bundle.getInt("com.ls.zoomLevel");

					if (bundle.containsKey("com.ls.scale"))
						scale = bundle.getFloat("com.ls.scale");
				}

				if (zoomLevel > maxZoomLevel) {
					grid = new Grid(this, config, tileProvider, maxZoomLevel);
					if (scale == 1.0f) {
						scale = (float) Math.pow(2, zoomLevel - maxZoomLevel);
					}
				} else {
					grid = new Grid(this, config, tileProvider, zoomLevel);
				}

				this.scale = scale;
				grid.setInternalScale(scale);

				initPositionPin();
				restoreMapPosition(bundle);
			}
		} catch (SAXException e) {
			Log.e(TAG, "Exception: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Exception: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Creates instance of map widget.
	 * 
	 * @param bundle
	 *            - bundle that were used to save map widget's state.
	 * @param context
	 *            - Context
	 * @param rootMapFolder
	 *            - folder that contains map resources inside your assets.
	 * @param initialZoomLevel
	 *            - zoom level that will be set in case if bundle doesn't
	 *            contain previously saved state.
	 */
	public MapWidget(Bundle bundle, Context context, String rootMapFolder,
			int initialZoomLevel) {
		super(context);

		initCommonStuff(context);

		String configPath = OfflineMapUtil.getConfigFilePath(rootMapFolder);

		try {
			MapConfigParser configParser = new MapConfigParser(rootMapFolder);

			config = configParser.parse(context, configPath);
			tileProvider = new AssetTileProvider(getContext(), config);

			int maxZoomLevel = OfflineMapUtil.getMaxZoomLevel(
					config.getImageWidth(), config.getImageHeight());
			int zoomLevel = initialZoomLevel;
			float scale = 1.0f;

			if (bundle != null) {
				if (bundle.containsKey("com.ls.zoomLevel"))
					zoomLevel = bundle.getInt("com.ls.zoomLevel");

				if (bundle.containsKey("com.ls.scale"))
					scale = bundle.getFloat("com.ls.scale");
			}

			if (zoomLevel > maxZoomLevel) {
				grid = new Grid(this, config, tileProvider, maxZoomLevel);
				if (scale == 1.0f) {
					scale = (float) Math.pow(2, zoomLevel - maxZoomLevel);
				}
			} else {
				grid = new Grid(this, config, tileProvider, zoomLevel);
			}

			this.scale = scale;
			grid.setInternalScale(scale);

			initPositionPin();
			restoreMapPosition(bundle);
		} catch (SAXException e) {
			Log.e(TAG, "Exception: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "Exception: " + e);
			e.printStackTrace();
		}
	}

	private void restoreMapPosition(Bundle bundle) {
		if (bundle != null && bundle.containsKey("com.ls.curPosOnMapX")) {
			final int mapX = (int) bundle.getFloat("com.ls.curPosOnMapX");
			final int mapY = (int) bundle.getFloat("com.ls.curPosOnMapY");

			Log.d("MapWidget", "Restored pos: [" + mapX + "," + mapY + "]");

			restoreScrollPosRunnable = new Runnable() {
				public void run() {
					jumpTo(new Point(mapX, mapY));
				};
			};

		} else {
			doCorrectPosition(false, false);
		}
	}

	private void initCommonStuff(Context context) {
		scale = 1.0f;
		mode = Mode.NONE;
		drawingRect = new Rect();
		isAnimationEnabled = true;
		requestCenterMap = false;
		userTouching = false;

		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		setBackgroundDrawable(null);

		this.setClickable(true);
		this.setEnabled(true);
		this.setFocusable(true);

		initializeZoomBtnsController();

		gestureDetector = new GestureDetector(context, new MyGestureDetector());
		decelerateInterpolator = new DecelerateInterpolator(1.5f);

		scroller = new Scroller(context, decelerateInterpolator);

		topmostLayer = new MapLayer(1, this);
		topmostLayer.setVisible(false);

		layers = new ArrayList<MapLayer>();
		layersMap = new HashMap<Long, Layer>();
		mapEventsListeners = new ArrayList<MapEventsListener>();

		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(1);

		if (Resources.LOGO != null) {
			logo = BitmapFactory.decodeByteArray(Resources.LOGO, 0,
					Resources.LOGO.length);
		}

		locationProvider = null;
		performAfterTranslate = null;
	}

	private void initPositionPin() {
		BitmapDrawable arrow = new BitmapDrawable(getResources(),
				BitmapFactory.decodeByteArray(Graphics.BLUE_ARROW, 0,
						Graphics.BLUE_ARROW.length));

		BitmapDrawable dot = new BitmapDrawable(getResources(),
				BitmapFactory.decodeByteArray(
				Graphics.BLUE_DOT, 0, Graphics.BLUE_DOT.length));
		PositionMarker pin = new PositionMarker(this, POS_PIN_ID, dot, arrow);

		topmostLayer.addMapObject(pin);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (w == 0 && h == 0)
			return;

		if (restoreScrollPosRunnable != null) {
			restoreScrollPosRunnable.run();
			restoreScrollPosRunnable = null;
		}
	}

	protected void setTileProvider(TileProvider tileManager) {
		if (grid != null) {
			grid.setTileProvider(tileManager);
		}
	}

	/**
	 * Adds listener for map events.
	 * 
	 * @param listener
	 *            - instance of MapEventListener
	 */
	public void addMapEventsListener(MapEventsListener listener) {
		if (mapEventsListeners == null) {
			mapEventsListeners = new ArrayList<MapEventsListener>();
		}

		mapEventsListeners.add(listener);
	}

	/**
	 * Creates new map layer with a given id.
	 * 
	 * @param theLayerId
	 *            - id of the new layer
	 * @return returns instance of the MapLayer or null if error occured.
	 * @throws IllegalArgumentException
	 *             when layer with the given id exists already.
	 */
	public MapLayer createLayer(long theLayerId) {
		if (this.layersMap.containsKey(theLayerId)) {
			throw new IllegalArgumentException(
					"Attempt to create layer with duplicated ID");
		}

		try {
			MapLayer layer = new MapLayer(theLayerId, this);

			layers.add(layer);
			layersMap.put(theLayerId, layer);

			return layer;
		} catch (Exception e) {
			Log.e("MapWidget", "Exception: " + e);
			return null;
		}
	}

	/**
	 * Removes layer with the given id from the map.
	 * 
	 * @param theLayerId
	 *            the id of previously created layer.
	 */
	public void removeLayer(long theLayerId) {
		Layer layer = layersMap.remove(theLayerId);
		layers.remove(layer);
	}

	/**
	 * Removes all layers from the map.
	 */
	public void removeAllLayers() {
		layers.clear();
		layersMap.clear();
	}

	/**
	 * Centers the map horizontally
	 */
	public void centerMapHorizontally() {
		if (grid.getWidth() > getWidth()) {
			int dx = (getWidth() - grid.getWidth()) / 2;
			scrollBy(-dx, 0);
		}
	}

	/**
	 * Returns map layer by index.
	 * 
	 * @param index
	 *            the index of the layer
	 * @return instance of Layer
	 * @throws ArrayIndexOutOfBoundsException
	 *             when index is out of bounds.
	 */
	public Layer getLayer(int index) {
		return layers.get(index);
	}

	/**
	 * Returns map layer by layer id.
	 * 
	 * @param id
	 *            - layer id
	 * @return instance of Layer
	 */
	public Layer getLayerById(long id) {
		return layersMap.get(id);
	}

	/**
	 * Returns total layer count
	 * 
	 * @return layer count
	 */
	public int getLayerCount() {
		return layers.size();
	}

	/**
	 * Returns height of the map taking current scale into account.
	 * 
	 * @return height of the map in pixels.
	 */
	public int getMapHeight() {
		if (grid != null) {
			return grid.getHeight();
		}

		return 0;
	}

	/**
	 * Returns width of the map taking current scale into account.
	 * 
	 * @return width of the map in pixels.
	 */
	public int getMapWidth() {
		if (grid != null) {
			return grid.getWidth();
		}

		return 0;
	}

	/**
	 * Returns the height of the map on the max zoom level.
	 * 
	 * @return original map height in pixels.
	 */
	public int getOriginalMapHeight() {
		if (grid != null) {
			return grid.getOriginalHeight();
		}

		return 0;
	}

	/**
	 * Returns the width of the map on the max zoom level.
	 * 
	 * @return original map width in pixels.
	 */
	public int getOriginalMapWidth() {
		if (grid != null) {
			return grid.getOriginalWidth();
		}

		return 0;
	}

	/**
	 * Returns the copy of the map widget's configuration
	 * 
	 * @return instance of OfflineMapConfig class.
	 */
	public OfflineMapConfig getConfig() {
		return config;
	}

	/**
	 * Returns the current scale of the map.
	 * 
	 * @return float that represents the scale of the map. 1.0 = max zoom level.
	 *         0.5 - map is scaled down to half of it's size.
	 */
	public float getScale() {
		if (grid != null) {
			return (float) grid.getScale();
		}

		return 0;
	}

	/**
	 * Returns current zoom level of the map.
	 * 
	 * @return current zoom level of the map. Should be greater than 0.
	 */
	public int getZoomLevel() {
		if (grid == null) {
			return 0;
		}

		double scale = grid.getScale();
		int zoomLevel = grid.getZoomLevel();

		if (scale <= 1.0f) {
			return zoomLevel;
		} else {
			return OfflineMapUtil.getMaxZoomLevel(grid.getWidth(),
					grid.getHeight());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (config == null) {
			return false;
		}

		int keyCode2 = event.getKeyCode();

		switch (keyCode2) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			scrollBy(-config.getTrackballScrollStepX(), 0);
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			scrollBy(config.getTrackballScrollStepX(), 0);
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			scrollBy(0, -config.getTrackballScrollStepY());
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			scrollBy(0, config.getTrackballScrollStepY());
			return true;
		case KeyEvent.KEYCODE_I:
			zoomIn();
			return true;
		case KeyEvent.KEYCODE_O:
			zoomOut();
			return true;
		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (config == null) {
			return false;
		}

		if (isDestroying) {
			Log.w("MapWidget", "Map is destroying... OnTouch skipped");
			return false;
		}
		
		if (this.onTouchListener != null) {
		//	this.onTouchListener.onTouch(this, event);
		}
		boolean result = false;
		
		super.onTouchEvent(event);

		gestureDetector.onTouchEvent(event);

		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;

		if (actionCode == MotionEvent.ACTION_DOWN) {
			byUser = true;
			userTouching = true;
			try {
				if (config.isZoomBtnsVisible()) {
					zoomBtnsController.setVisible(true);
				}
			} catch (Exception e) {
				Log.w("MapWidget", "Exception e: " + e);
			}

			tileProvider.pauseProcessingCommands();

			result = true;
		} else if (actionCode == MotionEvent.ACTION_POINTER_DOWN) {
			if (config.isPinchZoomEnabled()) {
				mode = Mode.ZOOM;
				doNotZoom = false;
	
				float x1 = event.getX(0);
				float y1 = event.getY(0);
				float x2 = event.getX(1);
				float y2 = event.getY(1);
	
				pinchStartDistance = MathUtils.distance(x1, y1, x2, y2);
	
				PointF mapPivot = MathUtils.middle(x1, y1, x2, y2);
	
				mapPivotX = (int) (mapPivot.x);
				mapPivotY = (int) (mapPivot.y);
	
				result =  true;
			}
		} else if (actionCode == MotionEvent.ACTION_MOVE) {
			if (zoomBtnsController != null && config.isZoomBtnsVisible()
					&& !zoomBtnsController.isVisible())
				zoomBtnsController.setVisible(true);


			if (mode == Mode.ZOOM) {
				float x1 = event.getX(0);
				float y1 = event.getY(0);
				float x2 = event.getX(1);
				float y2 = event.getY(1);

				double pinchDistance = MathUtils.distance(x1, y1, x2, y2);

				if (pinchStartDistance != 0) {
					pinchZoomScale = pinchDistance / pinchStartDistance;

					if (pinchZoomScale >= 1.025) {
						mode = Mode.ZOOMED;
						zoomIn(mapPivotX, mapPivotY);
					} else if (pinchZoomScale <= 0.975) {
						mode = Mode.ZOOMED;
						zoomOut();
					}
				}
			}

			result = true;
		} else if (actionCode == MotionEvent.ACTION_POINTER_UP) {
			mode = Mode.NONE;
			result = true;
		} else if (actionCode == MotionEvent.ACTION_UP) {

			byUser = false;
			userTouching = false;

			if (!isZooming) {
				doCorrectPosition();
			}

			result = true;
		}

		return result;
	}

	/**
	 * Removes map event listener from the map.
	 * 
	 * @param listener
	 *            instance of the MapEventsListener
	 */
	public void removeMapEventsListener(MapEventsListener listener) {
		if (mapEventsListeners != null) {
			mapEventsListeners.remove(listener);
		}
	}

	/**
	 * Removes all map event listeners.
	 */
	public void removeAllMapEventsListeners() {
		if (mapEventsListeners != null) {
			mapEventsListeners.clear();
		}

		mapEventsListeners = new ArrayList<MapEventsListener>();
	}

	/**
	 * Removes all graphical object from all layers
	 */
	public void clearLayers() {

		if (Looper.myLooper() == null) {
			throw new IllegalThreadStateException(
					"Should be called from UI thread");
		}

		for (int i = layers.size() - 1; i >= 0; --i) {
			layers.get(i).clearAll();
		}

		layers.clear();
	}

	
	/**
	 * Sets touch listener to the map.
	 * 
	 * @param mapTouchListener
	 *            instance of OnMapTouchListener.
	 */
	public void setOnMapTouchListener(OnMapTouchListener mapTouchListener) {
		this.mapTouchListener = mapTouchListener;
	}

	
	/**
	 * Sets the listener to handle long click on the map.
	 * @param onMapLongClickListener - instance of {@link OnMapLongClickListener}. Can be null.
	 */
	public void setOnMapLongClickListener(OnMapLongClickListener onMapLongClickListener) 
	{
		this.mapLongClicklistener = onMapLongClickListener;
	}
	
	
	/**
	 * Sets the listener that will be called when all visible tiles has been
	 * loaded and displayed.
	 * 
	 * @param listener
	 *            instance of OnMapTilesFinishedLoadingListener or null.
	 */
	public void setOnMapTilesFinishLoadingListener(
			OnMapTilesFinishedLoadingListener listener) {
		this.mapTilesReadyListener = listener;

		if (grid != null) {
			grid.setOnReadyListener(new OnGridReadyListener() {

				@Override
				public void onReady() {
					if (mapTilesReadyListener != null) {
						mapTilesReadyListener.onMapTilesFinishedLoading();
					}
				}
			});
		}
	}

	/**
	 * Sets scroll listener to the map.
	 * 
	 * @param mapScrollListener
	 *            instance of OnMapScrollListener.
	 */
	public void setOnMapScrolledListener(OnMapScrollListener mapScrollListener) {
		this.mapScrollListener = mapScrollListener;
	}

	/**
	 * Sets listener for retrieving the location
	 * 
	 * @param listener
	 *            - instance of OnLocationChangedListener. May be null.
	 */
	public void setOnLocationChangedListener(OnLocationChangedListener listener) {
		this.locationChangeListener = listener;
	}


	/**
	 * Sets the listener to handle long click event.
	 * 
	 * @param listener
	 *            instance of OnLongClickListener. Can be null.
	 * @see android.view.View.OnLongClickListener
	 */
	@Override
	public void setOnLongClickListener(OnLongClickListener listener) {
		this.longClickListener = listener;
	}


	/**
	 * Sets the listener to handle double tap event.
	 * @param listener instance of OnMapDoubleTapListener. Can be null.
	 * @see com.ls.widgets.map.interfaces.OnMapDoubleTapListener
	 */
	public void setOnDoubleTapListener(OnMapDoubleTapListener listener) 
	{
		this.onDoubleTapListener = listener;
	}
	
	
	/**
	 * Sets the listener to handle touch events
	 * @param listener instance of OnTouchListener
	 * @see android.view.View.OnTouchListener
	 */
	@Override
	public void setOnTouchListener(OnTouchListener listener) 
	{
		super.setOnTouchListener(listener);
	}

	/**
	 * Enables or disables the standard zoom controls.
	 * 
	 * @param enabled
	 *            true in order to make zoom controls visible, otherwise false.
	 */
	public void setZoomButtonsVisible(boolean enabled) {
		if (config != null) {
			config.setZoomBtnsVisible(enabled);

			if (enabled) {
				if (zoomBtnsController == null) {
					initializeZoomBtnsController();
				}
			} else {
				if (zoomBtnsController != null) {
					zoomBtnsController.setVisible(false);
					zoomBtnsController.setOnZoomListener(null);
					zoomBtnsController = null;
				}
			}
		} else {
			Log.w(TAG, "Ignored. Map is not initialized properly.");
		}
	}

	/**
	 * Sets the min zoom level the user can zoom out to.
	 * 
	 * @param minZoomLevel
	 *            int from 0 to count of zoom levels.
	 */
	public void setMinZoomLevel(int minZoomLevel) {
		if (config == null) {
			Log.w(TAG,
					"setMinZoomLevel skipped. MapWidget is not initialized properly");
			return;
		}

		int maxAvailableZoomLevel = grid.getMaxZoomLevel();
		int minAvailableZoomLevel = grid.getMinZoomLevel();

		if (minZoomLevel < minAvailableZoomLevel) {
			Log.w(TAG, "There is no " + minZoomLevel + " zoom level. Will use "
					+ minAvailableZoomLevel + " as min zoom level.");
			config.setMinZoomLevelLimit(minZoomLevel);
		} else if (minZoomLevel > maxAvailableZoomLevel) {
			Log.w(TAG,
					"Min zoom level should be less than max zoom level. Min zoom level: "
							+ minAvailableZoomLevel + " Max zoom level: "
							+ maxAvailableZoomLevel + ", "
							+ " You are setting: "
							+ config.getMaxZoomLevelLimit()
							+ " as min zoom level.");
			Log.w(TAG, "Will use max zoom level as min zoom level.");
			config.setMinZoomLevelLimit(maxAvailableZoomLevel);
		} else {
			config.setMinZoomLevelLimit(minZoomLevel);
		}

		updateZoomButtons();
	}

	/**
	 * The max zoom level the user can zoom in to.
	 * 
	 * @param maxZoomLevel
	 *            int from 0 to max zoom levels.
	 */
	public void setMaxZoomLevel(int maxZoomLevel) {
		if (config == null) {
			Log.w(TAG,
					"setMaxZoomLevel skipped. MapWidget was not initialized properly");
			return;
		}

		if (grid == null) {
			throw new IllegalStateException();
		}

		int maxAvailableZoomLevel = grid.getMaxZoomLevel();
		int minAvailableZoomLevel = grid.getMinZoomLevel();

		if (!config.isSoftwareZoomEnabled()
				&& maxZoomLevel > maxAvailableZoomLevel) {
			Log.w(TAG, "There is no " + maxZoomLevel + " zoom level. Will use "
					+ maxAvailableZoomLevel + " as max zoom level.");
			config.setMaxZoomLevelLimit(maxAvailableZoomLevel);
		} else if (maxZoomLevel < minAvailableZoomLevel) {
			Log.w(TAG,
					"Max zoom level should be greater than min zoom level. Min zoom level: "
							+ minAvailableZoomLevel + " Max zoom level: "
							+ maxAvailableZoomLevel + ", "
							+ " you are setting: " + maxZoomLevel
							+ " as max zoom level.");
			Log.w(TAG, "Will use min zoom level as max zoom level.");
			config.setMaxZoomLevelLimit(minAvailableZoomLevel);
		} else {
			config.setMaxZoomLevelLimit(maxZoomLevel);
		}

		updateZoomButtons();
	}

	/**
	 * Set's the scale to the map. Map will be scaled by resizing the existing
	 * tiles. Zoom level will be preserved.
	 * 
	 * @param scale
	 *            scale value. 2.0 means that you want to make map two times
	 *            bigger.
	 */
	public void setScale(float scale) {

		if (Looper.myLooper() == null) {
			throw new IllegalThreadStateException(
					"Should be called from UI thread");
		}

		if (grid == null) {
			return;
		}

		grid.setSoftScale(scale);
		setScaleToOtherDrawables((float) getScale());
		invalidate();
	}

	/**
	 * Enables the map to keep zooming in when no more zoom levels left.
	 * Software scale will be used.
	 * 
	 * @param useSoftwareZoom
	 *            true if you want to enable software zoom, false otherwise.
	 */
	public void setUseSoftwareZoom(boolean useSoftwareZoom) {
		if (config != null) {
			config.setSoftwareZoomEnabled(useSoftwareZoom);
		}
	}

	/**
	 * Enables/disables the zoom in/zoom out animations
	 * 
	 * @param isEnabled
	 *            true if you want to enable the animations, false otherwise.
	 */
	public void setAnimationEnabled(boolean isEnabled) {
		this.isAnimationEnabled = isEnabled;
	}

	/**
	 * Sets the size of the touch area when user touches the map.
	 * 
	 * @param pixels
	 *            radius of the touch area in pixels
	 */
	public void setTouchAreaSize(int pixels) {
		if (config != null) {
			config.setTouchAreaSize(pixels);
		}
	}

	/**
	 * Zooms map in by one zoom level.
	 */

	public void zoomIn() {
		final int pivotX = (getWidth() / 2);
		final int pivotY = (getHeight() / 2);

		zoomIn(pivotX, pivotY);
	}

	/**
	 * Shows current position of the user on the map. You can configure the view
	 * of the pointer. See MapWidget.getMapGraphicsConfig() for details. You can
	 * configure the GPS receiver by setting configuration parameters before
	 * setShowMyPosition is called. See MapWidget.getGPSConfig() for details.
	 * <p>
	 * 
	 * In order to calibrate the map you should add the calibration data to the
	 * map.xml. Calibration data consists of two points - top left and bottom
	 * right. X and Y is a coordinate of the point in your original map image in
	 * pixels. lat and lon is latitude and longitude of the same point in real
	 * world.<br>
	 * 
	 * <pre>
	 * For example, your map.xml may look like this:<br>
	 * {@code
	 * <Image TileSize="256" Overlap="1" Format="png">
	 * <Size Width="1918" Height="978"/>
	 * <CalibrationRect> 
	 * 	<Point x="0" y="0" lat="42.924251753870685" lon="-103.99658203125" topLeft="1"/>
	 * 	<Point x="1918" y="978" lat="40.81380923056961" lon="-98.3056640625"/>
	 * </CalibrationRect>
	 * </Image>
	 * }
	 * </pre>
	 * 
	 * @param show
	 *            - set true in order to show the position marker on the map,
	 *            false - in order to hide it.
	 * @throws java.lang.IllegalStateException
	 *             () in case if map is not calibrated.
	 */
	public void setShowMyPosition(boolean show) {
		GPSConfig config = getConfig().getGpsConfig();

		if (!config.isMapCalibrated()) {
			throw new IllegalStateException(
					"Map is not calibrated in order to use gps positioning");
		}

		if (show) {
			MapGraphicsConfig graphics = getConfig().getGraphicsConfig();
			PositionMarker marker = (PositionMarker) topmostLayer
					.getMapObject(POS_PIN_ID);

			if (graphics.getDotPointerDrawableId() != -1) {
				Drawable dot = getResources().getDrawable(
						graphics.getDotPointerDrawableId());
				marker.setDotPointer(dot, PivotFactory.createPivotPoint(dot,
						PivotPosition.PIVOT_CENTER));
			}

			if (graphics.getArrowPointerDrawableId() != -1) {
				Drawable arrow = getResources().getDrawable(
						graphics.getArrowPointerDrawableId());
				marker.setArrowPointer(arrow, PivotFactory.createPivotPoint(
						arrow, PivotPosition.PIVOT_CENTER));
			}

			marker.setColor(graphics.getAccuracyAreaColor(),
					graphics.getAccuracyAreaBorderColor());

			if (locationProvider == null) {
				locationProvider = new GPSLocationProvider(this.getContext());
				locationProvider.setMinRefreshTime(config.getMinTime());
				locationProvider.setMinRefreshDistance(config.getMinDistance());

				locationProvider.setMapLocationListener(this);
			}

			locationProvider.start(config.getPassiveMode());
		} else {
			if (locationProvider != null) {
				locationProvider.stop();
			}
		}
	}

	/**
	 * @return instance of MapGraphicsConfig or null, if map was not configured
	 *         properly
	 */
	public MapGraphicsConfig getMapGraphicsConfig() {
		if (config != null) {
			return config.getGraphicsConfig();
		}

		return null;
	}

	/**
	 * Returns GPSConfig object that will allow you to configure the GPS
	 * receiver.
	 * 
	 * @return instance of GPSConfig, or null if map configuration file doesn't
	 *         contain GPS calibration data or file was not found.
	 */
	public GPSConfig getGpsConfig() {
		if (config != null)
			return config.getGpsConfig();

		return null;
	}

	public void zoomIn(final int pivotX, final int pivotY) {
		if (doNotZoom) {
			Log.d(TAG, "Zoom is in progress. Skipped...");
			return;
		}

		if (config == null) {
			Log.w(TAG, "Zoom in skipped. Map was not initialized properly");
			return;
		}

		if (!config.isSoftwareZoomEnabled()
				&& getZoomLevel() == grid.getMaxZoomLevel()) {
			return;
		} else if (config.isSoftwareZoomEnabled()
				&& config.getMaxZoomLevelLimit() != 0
				&& getZoomLevel() >= config.getMaxZoomLevelLimit()) {
			return;
		}

		if (Looper.myLooper() == null) {
			throw new IllegalThreadStateException(
					"Should be called from UI thread");
		}

		notifyAboutPreZoomIn(mapEventsListeners);

		isZooming = true;
		doNotZoom = true;

		if (!isAnimationEnabled) {
			doZoom(getZoomLevel() + 1, pivotX, pivotY);
			isZooming = false;
			doCorrectPosition();
			return;
		}

		performAfterZoom = new Runnable() {
			@Override
			public void run() {
				doZoom(getZoomLevel() + 1, pivotX, pivotY);
				isZooming = false;
				doCorrectPosition(true);
			}
		};

		animateZoomIn(null, pivotX, pivotY);
	}

	/**
	 * Zooms map out by one zoom level.
	 */
	public void zoomOut() {
		if (doNotZoom) {
			Log.d(TAG, "Zoom is in progress. Skipped...");
			return;
		}

		if (config == null) {
			Log.w(TAG, "Zoom in skipped. Map was not initialized properly");
			return;
		}

		int currZoomLevel = getZoomLevel();

		if (currZoomLevel == 0
				|| currZoomLevel <= config.getMinZoomLevelLimit()) {
			return;
		}

		doNotZoom = true;

		if (grid == null) {
			Log.w(TAG, "zoomOut() grid is null");
			doNotZoom = false;
			return;
		}

		if (Looper.myLooper() == null) {
			throw new IllegalThreadStateException(
					"Should be called from UI thread");
		}

		int pivotX = getWidth() / 2;
		int pivotY = getHeight() / 2;

		notifyAboutPreZoomOut(mapEventsListeners);

		doZoom(currZoomLevel - 1, pivotX, pivotY);

		if (!isAnimationEnabled) {
			doNotZoom = false;
			isZooming = false;
			doCorrectPosition();
			return;
		}

		isZooming = true;

		performAfterZoom = new Runnable() {
			public void run() {
				isZooming = false;
				doCorrectPosition(true);
			}
		};

		animateZoomOut(null);
	}

	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();

		Animation animation = getAnimation();
		if (animation == null) {
			Log.w(TAG, "Unknown animation has been finished.");
		}

		if (animation instanceof ScaleAnimation && performAfterZoom != null) {
			performAfterZoom.run();
			performAfterZoom = null;
		}

		if (animation instanceof TranslateAnimation
				&& performAfterTranslate != null) {
			performAfterTranslate.run();
			performAfterTranslate = null;
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		this.getDrawingRect(drawingRect);

		if (config != null) {
			if (prevGrid != null) {
				prevGrid.draw(canvas, paint, drawingRect);
			}

			if (grid != null) {
				grid.draw(canvas, paint, drawingRect);
			}

			drawLayers(canvas, drawingRect);

			if (logo != null) {
				canvas.drawBitmap(logo,
						getWidth() + getScrollX() - logo.getWidth() - 10,
						getHeight() + getScrollY() - logo.getHeight() - 10,
						null);
			}
		} else {
			scrollTo(0, 0);
			drawMissingDataErrorMessage(canvas);
		}
	}

	private void drawMissingDataErrorMessage(Canvas canvas) {
		paint.setTextSize(24);
		paint.setStyle(Style.FILL);
		paint.setAntiAlias(true);
		paint.setSubpixelText(true);
		paint.setColor(Color.BLACK);
		canvas.drawPaint(paint);

		paint.setColor(Color.WHITE);
		Rect rect = new Rect();
		paint.getTextBounds(MSG_MAP_DATA_IS_CORRUPTED_OR_MISSING, 0,
				MSG_MAP_DATA_IS_CORRUPTED_OR_MISSING.length(), rect);

		Rect rect2 = canvas.getClipBounds();

		canvas.drawText(MSG_MAP_DATA_IS_CORRUPTED_OR_MISSING,
		// (getWidth() - rect.width()) / 2, getHeight() / 2, paint);
				(rect2.width() - rect.width()) / 2, rect2.height() / 2, paint);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (requestCenterMap) {
			requestCenterMap = false;

			final ViewTreeObserver observer = this.getViewTreeObserver();
			if (observer.isAlive()) {
				observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						doCorrectPosition(true, false);
						jumpTo(getOriginalMapWidth() / 2,
								getOriginalMapHeight() / 2);

						observer.removeGlobalOnLayoutListener(this);
					}
				});
			}
		} else {
			doCorrectPosition(false, false);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	static final MapScrolledEvent scrolledEvent = new MapScrolledEvent(0, 0);

	private DecelerateInterpolator decelerateInterpolator;

	@Override
	protected void onScrollChanged(int horOrigin, int verOrigin, int oldl,
			int oldt) {
		super.onScrollChanged(horOrigin, verOrigin, oldl, oldt);

		if (mapScrollListener != null) {
			scrolledEvent.setData(oldl - horOrigin, oldt - verOrigin, byUser);
			mapScrollListener.onScrolledEvent(this, scrolledEvent);
		}
	}

	protected void animateZoomIn(AnimationListener listener, float pivotX,
			float pivotY) {
		Animation zoomInAnimation = getZoomInAnimation(pivotX, pivotY);

		if (listener != null) {
			zoomInAnimation.setAnimationListener(listener);
		}

		this.startAnimation(zoomInAnimation);
	}

	protected void animateZoomOut(AnimationListener listener) {
		Animation zoomOutAnimation = null;

		if (zoomOutAnimation == null) {
			zoomOutAnimation = getZoomOutAnimation();
		}

		if (listener != null) {
			zoomOutAnimation.setAnimationListener(listener);
		}

		this.startAnimation(zoomOutAnimation);
	}

	private void doCorrectPosition() {
		doCorrectPosition(false);
	}

	private void doCorrectPosition(boolean force) {
		doCorrectPosition(force, isAnimationEnabled);
	}

	private void doCorrectPosition(boolean force, boolean animate) {
		if (Looper.myLooper() == null)
			throw new IllegalThreadStateException(
					"Should be called from UI thread");

		if (grid == null) {
			return;
		}

		if (isZooming)
			return;

		if (!config.isMapCenteringEnabled() && !force) {
			onPositionCorrected();
			return;
		}

		int viewWidth = getWidth();
		int viewHeight = getHeight();

		float fromX = 0.0f;
		float toX = 0.0f;
		float fromY = 0.0f;
		float toY = 0.0f;

		boolean positionCorrected = false;
		int gridWidth = getMapWidth();
		int gridHeight = getMapHeight();

		if (gridWidth > viewWidth) {
			int dx2 = gridWidth - getScrollX();

			if (getScrollX() < 0) {
				fromX = getScrollX() * -1;
				scrollTo(0, getScrollY());
				positionCorrected = true;
			} else if (dx2 < viewWidth) {
				int gap = viewWidth - dx2;
				fromX = -gap;
				scrollTo((int) getScrollX() - gap, getScrollY());
				positionCorrected = true;
			}
		} else {
			toX = (viewWidth - gridWidth) / 2;
			fromX = ((-getScrollX()) - toX);
			scrollTo(-(int) toX, getScrollY());
			positionCorrected = true;
			toX = 0;
		}

		if (gridHeight > viewHeight) {

			int dy2 = gridHeight - getScrollY();

			if (getScrollY() < 0) {
				fromY = -getScrollY();
				scrollTo(getScrollX(), 0);
				positionCorrected = true;
			} else if (dy2 < viewHeight) {
				int gap = viewHeight - dy2;
				fromY = -gap;
				scrollTo((int) getScrollX(), (int) (getScrollY() - gap));
				positionCorrected = true;
			}

		} else {
			toY = (viewHeight - gridHeight) / 2.0f;
			fromY = ((-getScrollY()) - toY);
			scrollTo(getScrollX(), -(int) toY);
			positionCorrected = true;
			toY = 0;
		}

		if (positionCorrected || force) {

			if (animate) {
				TranslateAnimation moveAnimation = new TranslateAnimation(
						fromX, toX, fromY, toY);
				performAfterTranslate = new Runnable() {
					public void run() {
						onPositionCorrected();
					};
				};

				moveAnimation.setDuration(500);
				moveAnimation.setInterpolator(decelerateInterpolator);
				moveAnimation.setFillAfter(true);

				this.startAnimation(moveAnimation);
			} else {
				onPositionCorrected();
			}
		} else {
			onPositionCorrected();
		}
	}

	private void onPositionCorrected() {
		grid.freeResources();

		if (!userTouching) {
			tileProvider.startProcessingCommands();
		}
	}

	private void doZoom(int zoomLevel, int pivotX, int pivotY) {
		if (grid == null) {
			doNotZoom = false;
			return;
		}

		int maxZoomLevel = OfflineMapUtil.getMaxZoomLevel(
				config.getImageWidth(), config.getImageHeight());
		int currZoomLevel = getZoomLevel();

		prevGrid = grid;

		prevGrid.setLoadTiles(false);

		final int gWidth = grid.getWidth();
		final int gHeight = grid.getHeight();

		float newScale = (float) Math.pow(2, zoomLevel - getZoomLevel());

		// Resolving offsets that we need to move the map in order pivot point
		// become in the center of the screen.
		final Rect currRect = new Rect(-getScrollX(), -getScrollY(), gWidth
				- getScrollX(), gHeight - getScrollY());

		final Rect transformed = TransformUtils.scaleRect(currRect, newScale,
				pivotX, pivotY);

		boolean zoomIn = zoomLevel > currZoomLevel;

		if ((zoomIn && currZoomLevel < maxZoomLevel)
				|| (!zoomIn && currZoomLevel > 0) && scale == 1.0f) {

			grid = new Grid(this, config, tileProvider, zoomLevel);

			grid.setOnReadyListener(new OnGridReadyListener() {

				@Override
				public void onReady() {
					grid.setOnReadyListener(null);
					prevGrid = null;

					if (mapTilesReadyListener != null) {
						mapTilesReadyListener.onMapTilesFinishedLoading();
					}
				}
			});

			prevGrid.setInternalScale(newScale);
		} else {
			scale *= newScale;

			if (prevGrid != null) {
				prevGrid = null;
			}

			grid.setOnReadyListener(null);
			grid.setLoadTiles(false);
			grid.setInternalScale(scale);
			grid.setLoadTiles(true);
		}

		updateZoomButtons();
		float scale_temp = getScale();
		setScaleToOtherDrawables(scale_temp);
		scrollTo(-transformed.left, -transformed.top);

		doNotZoom = false;

		if (zoomIn) {
			notifyAboutPostZoomIn(mapEventsListeners);
		} else {
			notifyAboutPostZoomOut(mapEventsListeners);
		}
	}

	private Animation getZoomInAnimation(float pivotX, float pivotY) {
		float fromX = 1.0f;
		float fromY = 1.0f;
		float toX = 2.0f;
		float toY = 2.0f;

		Animation zoomInAnimation = new ScaleAnimation(fromX, toX, fromY, toY,
				pivotX, pivotY);
		zoomInAnimation.setDuration(500);
		zoomInAnimation.setInterpolator(decelerateInterpolator);
		zoomInAnimation.setFillAfter(true);

		return zoomInAnimation;
	}

	private Animation getZoomOutAnimation() {
		float fromX = 2.0f;
		float fromY = 2.0f;
		float toX = 1.0f;
		float toY = 1.0f;
		float pivotX = getWidth() / 2.0f;
		float pivotY = getHeight() / 2.0f;
		Animation zoomOutAnimation = new ScaleAnimation(fromX, toX, fromY, toY,
				pivotX, pivotY);
		zoomOutAnimation.setDuration(500);
		zoomOutAnimation.setInterpolator(decelerateInterpolator);
		zoomOutAnimation.setFillAfter(true);

		return zoomOutAnimation;
	}

	static final Rect touchRect = new Rect();

	private ArrayList<ObjectTouchEvent> getTouchedElementIds(final int normX,
			final int normY) {
		ArrayList<ObjectTouchEvent> result = new ArrayList<ObjectTouchEvent>();

		float d = 5.0f;
		if (config != null) {
			d = (float) config.getTouchAreaSize() / 2.0f;
		}

		touchRect.set((int) (normX - d), (int) (normY - d), (int) (normX + d),
				(int) (normY + d));

		for (int i = layers.size() - 1; i >= 0; --i) {
			MapLayer layer = layers.get(i);

			if (layer.isVisible()) {
				ArrayList<Object> tempResult = layer.getTouched(touchRect);

				for (Object id : tempResult) {
					ObjectTouchEvent touchEvent = new ObjectTouchEvent(id, layer.getId());

					result.add(touchEvent);
				}
			}
		}

		return result;
	}

	private void initializeZoomBtnsController() {
		zoomBtnsController = new ZoomButtonsController(this);
		zoomBtnsController.setOnZoomListener(new OnZoomListener()
		{

			@Override
			public void onVisibilityChanged(boolean arg0) {
				// Left unimplemented
			}

			@Override
			public void onZoom(boolean zoomIn) {
				if (zoomIn) {
					try {
						zoomIn();
					} catch (Exception e) {
						doNotZoom = false;
						Log.e("MapWidget", "Exception while zoom in. " + e);
					}
				} else {
					try {
						zoomOut();
					} catch (Exception e) {
						doNotZoom = false;
						Log.e("MapWidget", "Exception while zoom out. " + e);
					}
				}

			}
		});
	}

	private void drawLayers(Canvas canvas, Rect drawingRect) {
		int size = layers.size();

		for (int i = 0; i < size; ++i) {
			MapLayer layer = layers.get(i);
			layer.draw(canvas, drawingRect);
		}

		topmostLayer.draw(canvas, drawingRect);
	}

	private void setScaleToOtherDrawables(float scale) {
		int size = layers.size();

		topmostLayer.setScale(scale);

		for (int i = 0; i < size; ++i) {
			MapLayer layer = layers.get(i);
			layer.setScale(scale);
		}
	}

	private float translateXToMapCoordinate(float x) {
		float scale = getScale();

		if (scale != 0) {
			return (x + (float) getScrollX()) / scale;
		}

		return 0;
	}

	private float translateYToMapCoordinate(float y) {
		float scale = getScale();

		if (scale != 0) {
			return (y + (float) getScrollY()) / scale;
		} else {
			return 0;
		}
	}

	private void updateZoomButtons() {
		if (config == null || zoomBtnsController == null
				|| config.isZoomBtnsVisible() == false)
			return;

		int currZoomLevel = getZoomLevel();
		int minZoomLevel = Math.max(config.getMinZoomLevelLimit(),
				grid.getMinZoomLevel());
		int maxZoomLevel = grid.getMaxZoomLevel();
		int maxZoomLevelLimit = config.getMaxZoomLevelLimit();

		if (maxZoomLevelLimit != 0 && config.isSoftwareZoomEnabled()) {
			maxZoomLevel = maxZoomLevelLimit;
		} else if (maxZoomLevelLimit != 0 && !config.isSoftwareZoomEnabled()) {
			maxZoomLevel = Math.min(maxZoomLevelLimit, maxZoomLevel);
		}

		if (currZoomLevel == maxZoomLevel) {
			// At the max zoom level
			// zoomBtnsController.setZoomInEnabled(false);
			zoomBtnsController.setZoomOutEnabled(true);

			if (!config.isSoftwareZoomEnabled() || maxZoomLevelLimit != 0) {
				zoomBtnsController.setZoomInEnabled(false);
			}

		} else if (currZoomLevel == minZoomLevel) {
			// At the min zoom level
			zoomBtnsController.setZoomInEnabled(true);
			zoomBtnsController.setZoomOutEnabled(scale > 1);
		} else {
			// In the middle
			zoomBtnsController.setZoomInEnabled(true);
			zoomBtnsController.setZoomOutEnabled(true);
		}
	}

	protected void startProcessingRequests() {
		isDestroying = false;
		if (!userTouching) {
			tileProvider.startProcessingCommands();
		}
	}

	private static final void notifyAboutPreZoomIn(
			ArrayList<MapEventsListener> listeners) {
		for (MapEventsListener listener : listeners) {
			if (listener != null) {
				try {
					listener.onPreZoomIn();
				} catch (Exception e) {
					Log.e(TAG, "Exception " + e + " on willZoomIn");
				}
			} else {
				Log.w(TAG, "WillZoomIn: Map Events listener is null");
			}
		}
	}

	private static final void notifyAboutPostZoomIn(
			ArrayList<MapEventsListener> listeners) {
		for (MapEventsListener listener : listeners) {
			if (listener != null) {
				try {
					listener.onPostZoomIn();
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, "Exception " + e + " on didlZoomIn");
				}
			} else {
				Log.w(TAG, "DidZoomIn: Map Events listener is null");
			}
		}
	}

	private static final void notifyAboutPreZoomOut(
			ArrayList<MapEventsListener> listeners) {
		for (MapEventsListener listener : listeners) {
			if (listener != null) {
				try {
					listener.onPreZoomOut();
				} catch (Exception e) {
					e.printStackTrace();
					Log.e(TAG, "Exception " + e + " on willZoomOut");
				}
			} else {
				Log.w(TAG, "WillZoomOut: Map Events listener is null");
			}
		}
	}

	private static final void notifyAboutPostZoomOut(
			ArrayList<MapEventsListener> listeners) {
		for (MapEventsListener listener : listeners) {
			if (listener != null) {
				try {
					listener.onPostZoomOut();
				} catch (Exception e) {
					Log.e(TAG, "Exception " + e + " on didZoomOut");
				}
			} else {
				Log.w(TAG, "DidZoomOut: Map Events listener is null");
			}
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (tileProvider != null) {
			tileProvider.startProcessingCommands();
		} else {
			Log.e(TAG, "Tile manager is not initialized");
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (zoomBtnsController != null) {
			zoomBtnsController.setVisible(false);
		}

		if (tileProvider != null) {
			tileProvider.stopProcessingCommands();
		}

		if (locationProvider != null) {
			locationProvider.stop();
		}
		super.onDetachedFromWindow();
	}

	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			invalidate();
		}

		super.computeScroll();
	}

	@Override
	public void onMovePinTo(Location location) {
		PositionMarker pin = (PositionMarker) topmostLayer
				.getMapObject(POS_PIN_ID);

		if (pin != null) {
			pin.setAccuracy(location.getAccuracy());
			pin.setBearing(location.getBearing());
			pin.setBearingEnabled(location.hasBearing());

			pin.moveTo(location);
		}

		notifyAboutLocationChanged(location);
	}

	private void notifyAboutLocationChanged(Location location) {
		if (locationChangeListener != null) {
			try {
				locationChangeListener.onLocationChanged(this, location);
			} catch (Exception e) {
				Log.w(TAG, "Exception while executing onLocationChanged. " + e);
			}
		}

	}

	@Override
	public void onChangePinVisibility(boolean visible) {
		topmostLayer.setVisible(visible);
	}

	/**
	 * Scrolls the map to current location marker without animation. Location
	 * marker should be visible in order for this method to work.
	 */
	public void jumpToCurrentLocation() {
		if (!topmostLayer.isVisible()) {
			Log.i(TAG,
					"Location marker is not visible. Jump to current location skipped");
			return;
		}

		PositionMarker pin = (PositionMarker) topmostLayer
				.getMapObject(POS_PIN_ID);
		Point tempPoint = (pin.getPosition());
		jumpTo(tempPoint);
	}

	/**
	 * Scrolls the map to current location marker using scroll animation.
	 * Location marker should be visible in order for this method to work.
	 */
	public void scrollToCurrentLocation() {
		if (!topmostLayer.isVisible()) {
			Log.i(TAG,
					"Location pin is not visible. Scroll to current location skipped");
			return;
		}

		PositionMarker pin = (PositionMarker) topmostLayer
				.getMapObject(POS_PIN_ID);
		Point tempPoint = (pin.getPosition());
		scrollMapTo(tempPoint);
	}

	/**
	 * Scrolls the map to specific location without animation.
	 * 
	 * @param location
	 *            - instance of {@link android.location.Location} object.
	 * @throws IllegalStateException
	 *             if map was not calibrated. For more details see
	 *             MapWidget.setShowMyPosition().
	 */
	public void jumpTo(Location location) {
		if (config == null) {
			Log.w(TAG, "Jump to skipped. Map is not initialized properly.");
			return;
		}

		if (!config.getGpsConfig().isMapCalibrated()) {
			throw new IllegalStateException("Map is not calibrated.");
		}

		Point point = new Point();
		getGpsConfig().getCalibration().translate(location, point);
		point.set((int) (point.x * getScale()), (int) (point.y * getScale()));

		jumpTo(point);
	}

	/**
	 * Scrolls the map to specific location.
	 * 
	 * @param location
	 *            - instance of Point. The coordinates of the point should be
	 *            set in pixels in map coordinate system.
	 */
	public void jumpTo(Point location) {
		jumpTo(location.x, location.y);

		doCorrectPosition(false, false);
	}

	/**
	 * Scrolls map to specific location
	 * 
	 * @param x
	 *            - x coordinate in map coordinate system.
	 * @param y
	 *            - y coordinate in map coordinate system.
	 */
	public void jumpTo(int x, int y) {
		int width = getWidth();
		int height = getHeight();

		scrollTo((int) (x * getScale() - width / 2),
				(int) (y * getScale() - height / 2));

		doCorrectPosition(false, false);
	}

	/**
	 * Scrolls the map to specific location using scroll animation.
	 * 
	 * @param location
	 *            - instance of {@link android.location.Location}.
	 * @throws IllegalStateException
	 *             if map is not calibrated. For more details see
	 *             MapWidget.setShowMyPosition().
	 */
	public void scrollMapTo(Location location) {
		if (config == null) {
			Log.w(TAG, "Jump to skipped. Map is not initialized properly.");
			return;
		}

		if (!config.getGpsConfig().isMapCalibrated()) {
			throw new IllegalStateException("Map is not calibrated.");
		}

		Point point = new Point();
		getGpsConfig().getCalibration().translate(location, point);
		scrollMapTo(point.x, point.y);
	}

	/**
	 * Scrolls the map to specific location using scroll animation.
	 * 
	 * @param location
	 *            - instance of {@link android.graphics.Point}. Coordinates of
	 *            the point should be set in pixels in map coordinates.
	 */
	public void scrollMapTo(Point location) {
		scrollMapTo(location.x, location.y);
	}

	/**
	 * Scrolls the map to specific location using scroll animation.
	 * 
	 * @param x
	 *            - x coordinate of the point in map coordinates.
	 * @param y
	 *            - y coordinate of the point in map coordinates.
	 */
	public void scrollMapTo(int x, int y) {
		if (!isAnimationEnabled) {
			jumpTo(x, y);
			return;
		}

		int viewWidth = getWidth();
		int viewHeight = getHeight();

		if (isLayoutRequested())
			return;

		int mapHeight = getMapHeight();
		int mapWidth = getMapWidth();
		float mapScale = getScale();
		int scrollX = getScrollX();
		int scrollY = getScrollY();

		int newX = (int) (x * mapScale) - viewWidth / 2;
		int newY = (int) (y * mapScale) - viewHeight / 2;

		if (viewHeight < mapHeight && newY + viewHeight > mapHeight) {
			newY -= (newY + viewHeight - mapHeight);
		}

		if (mapWidth > viewWidth && newX + viewWidth > mapWidth) {
			newX -= (newX + viewWidth - mapWidth);
		}

		if (newX < 0) {
			newX = 0;
		}

		if (newY < 0) {
			newY = 0;
		}

		if (viewHeight > mapHeight)
			newY = scrollY;

		if (viewWidth > mapWidth) {
			newX = scrollX;
		}

		scroller.abortAnimation();
		scroller.startScroll(scrollX, scrollY, newX - scrollX, newY - scrollY,
				500);
		invalidate();
	}

	static MapTouchedEvent mapTouchedEvent = new MapTouchedEvent();
	private class MyGestureDetector extends SimpleOnGestureListener {
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			boolean result = false;
			
			if (onDoubleTapListener != null) {
				updateMapTouchedEvent(e);
				result = onDoubleTapListener.onDoubleTap(MapWidget.this, mapTouchedEvent);
			}
			
			if (result == false) {
				float pivotX = e.getX();
				float pivotY = e.getY();
				zoomIn((int) pivotX, (int) pivotY);
				result = true;
			}
			
			return result;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (config == null) {
				Log.w(TAG, "Jump to skipped. Map is not initialized properly.");
				return false;
			}

			if (!config.isFlingEnabled() || isZooming) {
				return false;
			}

			int speed = 800;
			if (Math.abs(velocityX) > speed) {
				if (velocityX > 0) {
					velocityX = speed;
				} else {
					velocityX = -speed;
				}
			}

			if (Math.abs(velocityY) > speed) {
				if (velocityY > 0) {
					velocityY = speed;
				} else {
					velocityY = -speed;
				}
			}

			int minX = 0;
			int minY = 0;
			int maxX = 0;
			int maxY = 0;

			if (config.isMapCenteringEnabled()) {
				minX = (getWidth() - getMapWidth()) / 2;
				minY = (getHeight() - getMapHeight()) / 2;
				maxX = (int) getMapWidth() - getWidth();
				maxY = (int) getMapHeight() - getHeight();

				if (minX < 0) {
					minX = 0;
				}

				if (minY < 0) {
					minY = 0;
				}

				minX *= -1;
				minY *= -1;
			} else {
				minX = -getMapWidth();
				minY = -getMapHeight();
				maxX = getMapWidth();
				maxY = getMapHeight();

				if (minX > -getWidth()) {
					minX = -getWidth();
				}

				if (minY > -getHeight()) {
					minY = -getHeight();
				}

				if (maxY < getHeight()) {
					maxY = getHeight();
				}

				if (maxX < getWidth()) {
					maxX = getWidth();
				}

			}

			scroller.fling(getScrollX(), getScrollY(), -(int) (velocityX),
					-(int) (velocityY), minX, // MinX
					maxX, // MaxX
					minY, // MinY
					maxY); // MaxY

			invalidate();

			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			if (!scroller.isFinished()) { // is flinging
				scroller.forceFinished(true); // to stop flinging on touch
			}
			return true; // else won't work
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (mode != Mode.NONE || isZooming)
				return false;

			scrollBy((int) distanceX, (int) distanceY);

			// invalidate();
			return true;
		}

		public boolean onSingleTapConfirmed(MotionEvent event) {
			if (mapTouchListener != null) {
				{
					updateMapTouchedEvent(event);

					if (debugEnabled) {
						lastTouchedRect = new RectF(
								translateXToMapCoordinate(event.getX()),
								translateYToMapCoordinate(event.getY()),
								translateXToMapCoordinate(event.getX()) + 10,
								translateYToMapCoordinate(event.getY() + 10));
					}

					mapTouchListener.onTouch(MapWidget.this, mapTouchedEvent);
				}
			}
			return false;
		}
		
		
		@Override
		public void onLongPress(MotionEvent e) 
		{
			if (longClickListener != null) {
				longClickListener.onLongClick(MapWidget.this);
			}
			
			if (mapLongClicklistener != null) {
				updateMapTouchedEvent(e);
				mapLongClicklistener.onLongClick(MapWidget.this, mapTouchedEvent);
			}
		};
	}

	/**
	 * Saves mapWidget internal state, that can be restored from onCreate();
	 * 
	 * @param bundle
	 */
	public void saveState(Bundle bundle) {
		float currPosOnMapX = translateXToMapCoordinate(getWidth() / 2.0f);
		float currPosOnMapY = translateYToMapCoordinate(getHeight() / 2.0f);

		bundle.putFloat("com.ls.curPosOnMapX", currPosOnMapX);
		bundle.putFloat("com.ls.curPosOnMapY", currPosOnMapY);
		if (grid != null) {
			bundle.putInt("com.ls.zoomLevel", grid.getZoomLevel());
		}
		bundle.putFloat("com.ls.scale", scale);

		Log.d("MapWidget", "Saved point pos: [" + currPosOnMapX + ", "
				+ currPosOnMapY + " ]");
	}

	/**
	 * Centers the map.
	 */
	public void centerMap() {
		int width = getWidth();
		int height = getHeight();

		if (width == 0 || height == 0) {
			requestCenterMap = true;
		} else {
			doCorrectPosition(true, isAnimationEnabled);
			jumpTo(getOriginalMapWidth() / 2, getOriginalMapHeight() / 2);
		}
	}
	

	private void updateMapTouchedEvent(MotionEvent event) 
	{
		ArrayList<ObjectTouchEvent> touchedElementIds = getTouchedElementIds(
				(int) event.getX() + getScrollX(),
				(int) event.getY() + getScrollY());
		
		mapTouchedEvent.setScreenX((int) event.getX());
		mapTouchedEvent.setScreenY((int) event.getY());
		mapTouchedEvent
				.setMapX((int) translateXToMapCoordinate(event
						.getX())); // X coordinate in map's
		// coordinates
		mapTouchedEvent
				.setMapY((int) translateYToMapCoordinate(event
						.getY())); // Y coordinate in map's
		// coordinates
		mapTouchedEvent.setTouchedObjectEvents(touchedElementIds);
	}
}
