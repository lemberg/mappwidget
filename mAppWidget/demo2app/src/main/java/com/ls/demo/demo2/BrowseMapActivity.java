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

package com.ls.demo.demo2;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import com.ls.demo.demo2.model.MapObjectContainer;
import com.ls.demo.demo2.model.MapObjectModel;
import com.ls.demo.demo2.popup.TextPopup;
import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.config.GPSConfig;
import com.ls.widgets.map.config.MapGraphicsConfig;
import com.ls.widgets.map.config.OfflineMapConfig;
import com.ls.widgets.map.events.MapScrolledEvent;
import com.ls.widgets.map.events.MapTouchedEvent;
import com.ls.widgets.map.events.ObjectTouchEvent;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.interfaces.MapEventsListener;
import com.ls.widgets.map.interfaces.OnLocationChangedListener;
import com.ls.widgets.map.interfaces.OnMapScrollListener;
import com.ls.widgets.map.interfaces.OnMapTouchListener;
import com.ls.widgets.map.location.PositionMarker;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.PivotFactory;
import com.ls.widgets.map.utils.PivotFactory.PivotPosition;

public class BrowseMapActivity extends Activity 
implements MapEventsListener,
		   OnMapTouchListener
{
	private static final String TAG = "BrowseMapActivity";
	
	private static final Integer LAYER1_ID = 0;
	private static final Integer LAYER2_ID = 1;
	private static final int MAP_ID = 23;

	private int nextObjectId;
	private int pinHeight;
	
	private MapObjectContainer model;
	private MapWidget map;
	private TextPopup mapObjectInfoPopup;
	
	private Location points[];
	private int currentPoint;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        nextObjectId = 0;
        
        model = new MapObjectContainer();
        
        initTestLocationPoints();
        initMap(savedInstanceState);
        initModel();
        initMapObjects();
        initMapListeners();
        
        // Will show the position of the user on a map.
        // Do not forget to enable ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permission int the manifest.
        
       // Uncomment this if you are at Filitheyo island :) 
       // map.setShowMyPosition(true);
        
         map.centerMap();
    }
    
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	map.saveState(outState);
	}

    private void initTestLocationPoints() 
	{
		points = new Location[5];
		for (int i=0; i<points.length; ++i) {
			points[i] = new Location("test");
		}
		
		points[0].setLatitude(3.2127012756213316);
		points[0].setLongitude(73.03406774997711);
		
		points[1].setLatitude(3.2122245926560167);
		points[1].setLongitude(73.03744733333588);
		
		points[2].setLatitude(3.2112819380469135);
		points[2].setLongitude(73.03983449935913);
		
		points[3].setLatitude(3.2130494147249915);
		points[3].setLongitude(73.03946435451508);
		
		points[4].setLatitude(3.2148276002942713);
		points[4].setLongitude(73.03796768188477);
		
		currentPoint = 0;
	}
	
	
	private Location getNextLocationPoint()
	{
		if (currentPoint < points.length-1) {
			currentPoint += 1;
		} else {
			currentPoint = 0;
		}
		
		return points[currentPoint];
	}


	private void initMap(Bundle savedInstanceState) 
	{
		// In order to display the map on the screen you will need 
		// to initialize widget and place it into layout.
        map = new MapWidget(savedInstanceState, this, 
							  "map", // root name of the map under assets folder.
							  10); // initial zoom level

        map.setId(MAP_ID);
 
        OfflineMapConfig config = map.getConfig();
        config.setPinchZoomEnabled(true); // Sets pinch gesture to zoom
        config.setFlingEnabled(true);    // Sets inertial scrolling of the map
        config.setMaxZoomLevelLimit(20);
        config.setZoomBtnsVisible(true); // Sets embedded zoom buttons visible
        
        // Configuration of GPS receiver
        GPSConfig gpsConfig = config.getGpsConfig();
        gpsConfig.setPassiveMode(false);
        gpsConfig.setGPSUpdateInterval(500, 5);
        
        // Configuration of position marker
        MapGraphicsConfig graphicsConfig = config.getGraphicsConfig();
        graphicsConfig.setAccuracyAreaColor(0x550000FF); // Blue with transparency
        graphicsConfig.setAccuracyAreaBorderColor(Color.BLUE); // Blue without transparency

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.rootLayout);
        // Adding the map to the layout
        layout.addView(map, 0);
        layout.setBackgroundColor(Color.parseColor("#0049FF"));
        
        // Adding layers in order to put there some map objects
        map.createLayer(LAYER1_ID); // you will need layer id's in order to access particular layer
        map.createLayer(LAYER2_ID);
	}
	
	
	private void initModel()
	{
		// Adding objects to the model
		// You may want to implement your own model 
		MapObjectModel objectModel = new MapObjectModel(0, 100, 100, "Shows above the image 1");
		model.addObject(objectModel);
		objectModel = new MapObjectModel(1, 600, 350, "Shows above the image 2");
		model.addObject(objectModel);
		
		int id = 2;
		for (Location point:points) {
			objectModel = new MapObjectModel(id, point, "Point " + id);
			model.addObject(objectModel);
			id += 1;
		}
		
	}


	private void initMapObjects() 
	{	
		
		mapObjectInfoPopup = new TextPopup(this, (RelativeLayout)findViewById(R.id.rootLayout));
		
		Layer layer1 = map.getLayerById(LAYER1_ID);
		Layer layer2 = map.getLayerById(LAYER2_ID);
		
		for (int i=0; i<model.size(); ++i) {
			addNotScalableMapObject(model.getObject(i), layer1);
		}
		
		// Adding two map objects to the second layer
		addScalableMapObject(800, 300, layer2);
		addNotScalableMapObject(900, 350, layer2);
	}

	
	private void addNotScalableMapObject(int x, int y,  Layer layer) 
	{
		// Getting the drawable of the map object
		Drawable drawable = getResources().getDrawable(R.drawable.map_object);
		pinHeight = drawable.getIntrinsicHeight();
		// Creating the map object
		MapObject object1 = new MapObject(Integer.valueOf(nextObjectId), // id, will be passed to the listener when user clicks on it 
										  drawable,  
										  new Point(x, y), // coordinates in original map coordinate system.
										  // Pivot point of center of the drawable in the drawable's coordinate system.
										  PivotFactory.createPivotPoint(drawable, PivotPosition.PIVOT_CENTER),
										  true, // This object will be passed to the listener
										  false); // is not scalable. It will have the same size on each zoom level

		// Adding object to layer
		layer.addMapObject(object1);
		nextObjectId += 1;
	}
	
	
	private void addNotScalableMapObject(MapObjectModel objectModel,  Layer layer) 
	{
		if (objectModel.getLocation() != null) {
			addNotScalableMapObject(objectModel.getLocation(), layer);
		} else {
			addNotScalableMapObject(objectModel.getX(), objectModel.getY(),  layer);
		}
	}

	
	private void addNotScalableMapObject(Location location, Layer layer) {
		if (location == null)
			return;
		
		// Getting the drawable of the map object
		Drawable drawable = getResources().getDrawable(R.drawable.map_object);
		// Creating the map object
		MapObject object1 = new MapObject(Integer.valueOf(nextObjectId), // id, will be passed to the listener when user clicks on it 
										  drawable,  
										  new Point(0, 0), // coordinates in original map coordinate system.
										  // Pivot point of center of the drawable in the drawable's coordinate system.
										  PivotFactory.createPivotPoint(drawable, PivotPosition.PIVOT_CENTER),
										  true, // This object will be passed to the listener
										  true); // is not scalable. It will have the same size on each zoom level
		layer.addMapObject(object1);
		
		// Will crash if you try to move before adding to the layer. 
		object1.moveTo(location);
		nextObjectId += 1;
	}


	private void addScalableMapObject(int x, int y, Layer layer) 
	{
		Drawable drawable = getResources().getDrawable(R.drawable.map_object);
		MapObject object1 = new MapObject(Integer.valueOf(nextObjectId), 
										  drawable, 
										  x, 
										  y, 
										  true, 
										  true);

		layer.addMapObject(object1);
		nextObjectId += 1;
	}
	

	private void initMapListeners() 
	{
		// In order to receive MapObject touch events we need to set listener
		map.setOnMapTouchListener(this);
		
		// In order to receive pre and post zoom events we need to set MapEventsListener
        map.addMapEventsListener(this); 
        
        // In order to receive map scroll events we set OnMapScrollListener
        map.setOnMapScrolledListener(new OnMapScrollListener()
        {
            public void onScrolledEvent(MapWidget v, MapScrolledEvent event)
            {
                handleOnMapScroll(v, event);
            }
        });
        
        
        map.setOnLocationChangedListener(new OnLocationChangedListener() {
			@Override
			public void onLocationChanged(MapWidget v, Location location) {
				// You can handle location change here.
				// For example you can scroll to new location by using v.scrollMapTo(location)
			}
		});
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) {
		case R.id.zoomIn:
			map.zoomIn();
			return true;
		case R.id.zoomOut:
			map.zoomOut();
			return true;
		case R.id.hideLayer2: {
			Layer layer = map.getLayerById(LAYER2_ID);
			if (layer != null) {
				layer.setVisible(false);
				map.invalidate(); // Need to repaint the layer. This is a bug and will be fixed in next version.
			}
			return true;
			}
		case R.id.showLayer2: {
			Layer layer = map.getLayerById(LAYER2_ID);
			if (layer != null) {
				layer.setVisible(true);
				map.invalidate(); // Need to repaint the layer. This is a bug and will be fixed in next version.
			}
			return true;
			}
		
		case R.id.scroll_next:
			map.scrollMapTo(getNextLocationPoint());
			break;
			
		}
		
		return super.onOptionsItemSelected(item);
	}


	private void handleOnMapScroll(MapWidget v, MapScrolledEvent event) 
	{	
		// When user scrolls the map we receive scroll events
		// This is useful when need to move some object together with the map
		
		int dx = event.getDX(); // Number of pixels that user has scrolled horizontally
		int dy = event.getDY(); // Number of pixels that user has scrolled vertically
		
		if (mapObjectInfoPopup.isVisible()) {
			mapObjectInfoPopup.moveBy(dx, dy);
		}
	}
	
	
	
	@Override
	public void onPostZoomIn() 
	{
		Log.i(TAG, "onPostZoomIn()");
	}

	@Override
	public void onPostZoomOut() 
	{
		Log.i(TAG, "onPostZoomOut()");		
	}

	@Override
	public void onPreZoomIn() 
	{
		Log.i(TAG, "onPreZoomIn()");
		
		if (mapObjectInfoPopup != null) {
			mapObjectInfoPopup.hide();
		}	
	}

	@Override
	public void onPreZoomOut() 
	{
		Log.i(TAG, "onPreZoomOut()");		
		
		if (mapObjectInfoPopup != null) {
			mapObjectInfoPopup.hide();
		}	
	}


	//* On map touch listener implemetnation *//
	@Override
	public void onTouch(MapWidget v, MapTouchedEvent event) 
	{
		// Get touched object events from the MapTouchEvent
		ArrayList<ObjectTouchEvent> touchedObjs = event.getTouchedObjectIds();
		
		if (touchedObjs.size() > 0) {
			
			int xInMapCoords = event.getMapX();
			int yInMapCoords = event.getMapY();
			int xInScreenCoords = event.getScreenX();
			int yInScreenCoords = event.getScreenY();
			
			ObjectTouchEvent objectTouchEvent = event.getTouchedObjectIds().get(0);
			
			// Due to a bug this is not actually the layer id, but index of the layer in layers array.
			// Will be fixed in the next release.
			long layerId = objectTouchEvent.getLayerId();
			Integer objectId = (Integer)objectTouchEvent.getObjectId();
			// User has touched one or more map object
			// We will take the first one to show in the toast message.
			String message = "You touched the object with id: " + objectId + " on layer: " + layerId + 
			" mapX: " + xInMapCoords + " mapY: " + yInMapCoords + " screenX: " + xInScreenCoords + " screenY: " + 
			yInScreenCoords;
			
			Log.d(TAG, message);
				
			MapObjectModel objectModel = model.getObjectById(objectId.intValue());
					
			if (objectModel != null) {
				// This is a case when we want to show popup info exactly above the pin image
				
		        float density = getResources().getDisplayMetrics().density;
		        int imgHeight = (int) (pinHeight / density / 2);
					
		        // Calculating position of popup on the screen
	        	int x = xToScreenCoords(objectModel.getX());
				int y = yToScreenCoords(objectModel.getY()) - imgHeight;
				
				// Show it
				showLocationsPopup(x, y, objectModel.getCaption());
			} else {
				// This is a case when we want to show popup where the user has touched.
				showLocationsPopup(xInScreenCoords, yInScreenCoords, "Shows where user touched");
			}
			
			// Hint: If user touched more than one object you can show the dialog in which ask
			// the user to select concrete object
		} else {
			if (mapObjectInfoPopup != null) {
				mapObjectInfoPopup.hide();
			}
		}
	}
	
	
    private void showLocationsPopup(int x, int y, String text)
    {
        RelativeLayout mapLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        if (mapObjectInfoPopup != null)
        {
            mapObjectInfoPopup.hide();
        }

        ((TextPopup) mapObjectInfoPopup).setIcon((BitmapDrawable) getResources().getDrawable(R.drawable.map_popup_arrow));
        ((TextPopup) mapObjectInfoPopup).setText(text);

        mapObjectInfoPopup.setOnClickListener(new OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (mapObjectInfoPopup != null)
                    {
                        mapObjectInfoPopup.hide();
                    }
                }

                return false;
            }
        });

        ((TextPopup) mapObjectInfoPopup).show(mapLayout, x, y);
    }
    
    /***
     * Transforms coordinate in map coordinate system to screen coordinate system
     * @param mapCoord - X in map coordinate in pixels. 
     * @return X coordinate in screen coordinates. You can use this value to display any object on the screen.
     */
    private int xToScreenCoords(int mapCoord)
    {
    	return (int)(mapCoord *  map.getScale() - map.getScrollX());
    }
    
    private int yToScreenCoords(int mapCoord)
    {
    	return (int)(mapCoord *  map.getScale() - map.getScrollY());
    }
    
  
}