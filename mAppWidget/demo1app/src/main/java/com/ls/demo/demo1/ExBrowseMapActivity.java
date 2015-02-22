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

package com.ls.demo.demo1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.config.MapGraphicsConfig;
import com.ls.widgets.map.config.OfflineMapConfig;
import com.ls.widgets.map.events.MapScrolledEvent;
import com.ls.widgets.map.events.MapTouchedEvent;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.interfaces.MapEventsListener;
import com.ls.widgets.map.interfaces.OnLocationChangedListener;
import com.ls.widgets.map.interfaces.OnMapScrollListener;
import com.ls.widgets.map.interfaces.OnMapTouchListener;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.PivotFactory;
import com.ls.widgets.map.utils.PivotFactory.PivotPosition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class ExBrowseMapActivity 
    extends Activity 
{
    /** Called when the activity is first created. */
	
	private MapWidget mapWidget;
	private Thread animationThread;
	private boolean stopThreads;
	
	
	public static final long LAYER_A = 1000;
	public static final long LAYER_B = 2000;
	public static final long LAYER_C = 3000;
	public static final long PIN_LAYER = 4000;
	
	private int amplitude = 400;
	private float currX = 0;
	private float currY = 0;
	
	private boolean followPointer;
	
	public static final long[] LAYERS = new long[]{};
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopThreads = false;
        followPointer = true;
        
        File dir = Environment.getExternalStorageDirectory();
        Log.d("ExBrowseActivity", "External Storage Directory: " + Environment.getDataDirectory().getAbsolutePath() + "/maps/grid2");
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.d("ExBrowseActivity", "Window format is: " + getWindow().getAttributes().format);
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
        layout.setBackgroundColor(0xFFFFFFFF);
        mapWidget = new MapWidget(savedInstanceState, this, new File("/sdcard/maps/grid2"),12);

        mapWidget.createLayer(0);
        mapWidget.createLayer(1);
        mapWidget.setAnimationEnabled(true);
        mapWidget.setZoomButtonsVisible(true);
        
        MapGraphicsConfig gconfig = mapWidget.getMapGraphicsConfig();
        if (gconfig != null) {
            gconfig.setAccuracyAreaBorderColor(Color.RED);
            gconfig.setAccuracyAreaColor(0x4400FF00);
        }
        
        mapWidget.setOnMapTouchListener(new OnMapTouchListener() {
			
			@Override
			public void onTouch(MapWidget v, MapTouchedEvent event) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setMessage("OnTouch, X: " + event.getScreenX() + " Y: " + event.getScreenY() 
						+ " MAPX: " + event.getMapX() + " MAPY: " + event.getMapY() +
						" Touched Count: " + event.getTouchedObjectIds().size());
				builder.create().show();
			}
		});
        
        
        mapWidget.setOnMapScrolledListener(new OnMapScrollListener()
        { 
            @Override
            public void onScrolledEvent(MapWidget v, MapScrolledEvent event)
            {           
                if (event.isByUser()) {
                    followPointer = false;
                }      
            }
        });
        
        
        mapWidget.setMinZoomLevel(1);
        mapWidget.setMaxZoomLevel(12);
       // mapWidget.setZoomButtonsVisible(true);
 //       mapWidget.setScale(2.0f);
        layout.addView(mapWidget);
        
     mapWidget.addMapEventsListener(new MapEventsListener() {
            
            
            public void onPreZoomOut() 
            {
                Log.i("BrowseMapActivity", "On Map will zoom out");
            }
            
            
            public void onPreZoomIn() 
            {
                Log.i("BrowseMapActivity", "On Map will zoom in");
                
            }
            
            
            public void onPostZoomOut() 
            {
                Log.i("BrowseMapActivity", "On Map did zoom out");
                
            }
            
            
            public void onPostZoomIn() 
            {
                Log.i("BrowseMapActivity", "On Map did zoom in");
                
            }

        });
     
     mapWidget.setOnLocationChangedListener(new OnLocationChangedListener()
     {   
        @Override
        public void onLocationChanged(MapWidget v, Location location)
        {
            if (followPointer) {
                v.scrollMapTo(location);
            }
        }
    });
        
        Runnable runnable = new Runnable() {
        	@Override
        	public void run() {
                generateRandomMarkers();
//                BitmapDrawable image = (BitmapDrawable) getResources().getDrawable(R.drawable.maps_blue_dot);
//                if (image != null) {
//                	Layer layer = mapWidget.getLayerById(PIN_LAYER);
//                	MapObject object = new MapObject(PIN_ID, image, new Point(500, 250), PivotFactory.createPivotPoint(image, PivotPosition.PIVOT_CENTER), false, true);
//    				layer.addMapObject(object);
//                 	layer.setVisible(true);
//                }
        	}
        };
        
        Thread thread = new Thread(runnable);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.run();
        
        OfflineMapConfig config = mapWidget.getConfig();
        if (config != null) {
            config.setFlingEnabled(true);
            config.setMapCenteringEnabled(false);
        }
        
        //layout.removeView(mapWidget);
       
        
        //mapWidget.setShowMyPosition(true);
        mapWidget.centerMap();
    }


	private void generateRandomMarkers() {
		try {
			final int GREEN_POINT_COUNT = 100;
			final int BLUE_POINT_COUNT = GREEN_POINT_COUNT;
			final int MAP_WIDTH = 1500;
			final int MAP_HEIGHT = 900;
			
			InputStream is = getAssets().open("other/trail_difficulty_green_circle.png");
			BitmapDrawable drawable = new BitmapDrawable(is);
			is.close();
			Random random = new Random(System.currentTimeMillis());
			
			Layer layer = mapWidget.getLayer(0);
			
        	for (int i = 0; i < GREEN_POINT_COUNT; ++i) {
				int randW = random.nextInt(MAP_WIDTH);
				int randH = random.nextInt(MAP_HEIGHT);
				BitmapDrawable dr = new BitmapDrawable(drawable.getBitmap());

				MapObject object = new MapObject(Integer.valueOf(random.nextInt(1000)), dr, randW, randH, false);
				layer.addMapObject(object);
				//layer.addTouchable(new Integer(random.nextInt(1000)), dr, randW, randH);
			}
        		
			
			is = getAssets().open("other/trail_difficulty_blue_rect.png");
			drawable = new BitmapDrawable(is);
			is.close();
			
			 layer = mapWidget.getLayer(1);
        	for (int i = 0; i < BLUE_POINT_COUNT; ++i) {
				int randW = random.nextInt(MAP_WIDTH);
				int randH = random.nextInt(MAP_HEIGHT);
				
				BitmapDrawable dr = new BitmapDrawable(drawable.getBitmap());	
				
				MapObject object = new MapObject(Integer.valueOf((random.nextInt(1000))), dr, new Point(randW, randH), PivotFactory.createPivotPoint(dr, PivotPosition.PIVOT_CENTER), true, false);
				layer.addMapObject(object);
				
				
				
//				layer.addTouchable(new Integer(random.nextInt(1000)), dr, new Point(randW, randH), PivotFactory.createPivotPoint(dr, PivotPosition.PIVOT_CENTER), false);

//				mapWidget.addDrawable(dr, randW, randH);
			}
			
		} catch (IOException e) {
			Log.e("TileManagerWorkerThread", "Exception: " + e);
			e.printStackTrace();
		}
	}
    
 
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
		int i = item.getItemId();
		if (i == R.id.zoom_in) {
			try {
				mapWidget.zoomIn();
				mapWidget.scrollMapTo(1500, 700);
			} catch (Exception e) {
				Log.e("BrowseMapActivity", "Exception while zoom in. " + e);
			}
			return true;
		}
		else if (i == R.id.zoom_out) {
			try {
				mapWidget.zoomOut();
			} catch (Exception e) {
				Log.e("BrowseMapActivity", "Exception while zoom out. " + e);
			}
			return true;
		}
		else if (i == R.id.double_size) {
			mapWidget.setScale(2.0f);
			return true;
		}
		else if (i == R.id.original_size) {
			mapWidget.setScale(1.0f);
			return true;
		}
		else if (i == R.id.half_size) {
			mapWidget.setScale(0.5f);
			return true;
		}
		else if (i == R.id.open_map) {
			Intent intent = new Intent(this, ExBrowseMapActivity.class);
			startActivity(intent);
			return true;
		}
		else if (i == R.id.my_location) {
			//mapWidget.scrollToCurrentLocation();
			//followPointer = true;
			mapWidget.centerMap();
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
    }


	private void animatePin() {
//		if (animationThread == null || (animationThread != null && !animationThread.isAlive())) {
//			animationThread = new Thread(new Runnable() {
//				@Override
//				public void run() {
//				//	MapObject pin = mapWidget.getLayerById(PIN_LAYER).getMapObject(PIN_ID);
//					
//					if (pin == null) {
//						Log.e("PIN", "Pin not foind");
//					}
//					
//					while (!stopThreads) {
//						currX += 0.01;
//						currY += 0.01;
//						
//						if (pin != null) {
//							pin.moveTo((int)(700 + Math.sin(currX) * amplitude), (int)(350 + Math.cos(currY)*250));
//						}
//						
//						try {
//							Thread.sleep(50);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//							return;
//						}
//					}
//					
//				}
//			});
//			
//			animationThread.start();
//		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Layer layer = null;
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			layer = mapWidget.getLayer(0);
			break;
		case KeyEvent.KEYCODE_2:
			layer = mapWidget.getLayer(1);
			break;
		}
		
		if (layer != null) {
			layer.setVisible(!layer.isVisible());
			return true;
		} else 
			return super.onKeyDown(keyCode, event);
	}



	@Override
	protected void onStop() {
		stopThreads = true;
		super.onStop();
	}

	
	@Override
	protected void onPause() {
		super.onPause();
	}



	@Override
	protected void onDestroy() 
	{
		//mapWidget.destroy();
		super.onDestroy();
	}


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        mapWidget.saveState(outState);     
        super.onSaveInstanceState(outState);

    }
	
	
	
};