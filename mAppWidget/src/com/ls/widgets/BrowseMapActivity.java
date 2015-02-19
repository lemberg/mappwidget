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

package com.ls.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.config.OfflineMap;
import com.ls.widgets.map.events.MapTouchedEvent;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.interfaces.MapEventsListener;
import com.ls.widgets.map.interfaces.OnLocationChangedListener;
import com.ls.widgets.map.interfaces.OnMapTouchListener;
import com.ls.widgets.map.model.MapLayer;
import com.ls.widgets.map.model.MapObject;

public class BrowseMapActivity 
    extends Activity 
{
    /** Called when the activity is first created. */
	
	private MapWidget mapWidget;
//	private static long PIN_ID = 0xdb70bca16186d187L;
	
	public static final long LAYER_ATTRACTIONS = 1000;
    public static final long LAYER_KIDS = 2000;
    public static final long LAYER_SPORT_AND_LEASURE = 3000;
    public static final long PIN_LAYER = 4000;
	
    private Model model;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        
        this.model = new Model();
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
        mapWidget = new MapWidget(this, OfflineMap.MAP_ROOT);
//        mapWidget.setMemoryEconomyMode(true);
        mapWidget.setBackgroundColor(Color.GREEN);

        MapLayer layer = mapWidget.createLayer(LAYER_ATTRACTIONS);
        initLayer(layer, Model.CAT_MAIN_ATTRACTIONS);
        
        layer = mapWidget.createLayer(LAYER_KIDS);
        initLayer(layer, Model.CAT_KIDS);
        
        layer = mapWidget.createLayer(LAYER_SPORT_AND_LEASURE);
        initLayer(layer, Model.CAT_SPORT_AND_LEISURE);
        
        mapWidget.getConfig().setMapCenteringEnabled(false); 
        
        mapWidget.createLayer(PIN_LAYER);
        
//        layer.addTouchable(id, drawable, offsetX, offsetY)
        
//        mapWidget.addLayer(2);
        mapWidget.setAnimationEnabled(true);
        mapWidget.setOnMapTouchListener(new OnMapTouchListener() {
			
			@Override
			public void onTouch(MapWidget v, MapTouchedEvent event){
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setMessage("OnTouch, X: " + event.getScreenX() + " Y: " + event.getScreenY() 
						+ " MAPX: " + event.getMapX() + " MAPY: " + event.getMapY() +
						" Touched Count: " + event.getTouchedObjectIds().size());
				builder.create().show();
			}
		});
        
        
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
               v.scrollMapTo(location);            
           }
       });
        
        mapWidget.setMinZoomLevel(1);
 //       mapWidget.setScale(2.0f);
        
        layout.addView(mapWidget);
        
//        Runnable runnable = new Runnable() {
//        	@Override
//        	public void run() {
//                generateRandomMarkers();
//                BitmapDrawable image = (BitmapDrawable) getResources().getDrawable(R.drawable.maps_blue_dot);
//                if (image != null) {
//                	Layer layer = mapWidget.getLayerById(PIN_LAYER);
//                	MapObject object = new MapObject(PIN_ID, image, new Point(500, 250), PivotFactory.createPivotPoint(image, PivotPosition.PIVOT_CENTER), true);
//                	layer.addMapObject(object);
//	              //  Pin pin = new Pin(PIN_ID, image, new android.graphics.Point(image.getBitmap().getWidth()/2, image.getBitmap().getHeight()/2));
//	              //  pin.moveTo(500, 250);
//	              //  mapWidget.getPinLayer().addPin(pin);
//	              //  mapWidget.setPinLayerVisible(true);
//                }
//        	}
//        };
//        
//        Thread thread = new Thread(runnable);
//        thread.setPriority(Thread.MIN_PRIORITY);
//        thread.start();
    }


//	private void generateRandomMarkers() {
//		try {
//			final int GREEN_POINT_COUNT = 100;
//			final int BLUE_POINT_COUNT = GREEN_POINT_COUNT;
//			final int MAP_WIDTH = 1500;
//			final int MAP_HEIGHT = 900;
//			
//			InputStream is = getAssets().open("other/trail_difficulty_green_circle.png");
//			BitmapDrawable drawable = new BitmapDrawable(is);
//			is.close();
//			Random random = new Random(System.currentTimeMillis());
//			
//        	for (int i = 0; i < GREEN_POINT_COUNT; ++i) {
//				int randW = random.nextInt(MAP_WIDTH);
//				int randH = random.nextInt(MAP_HEIGHT);
//				BitmapDrawable dr = new BitmapDrawable(drawable.getBitmap());
//
//				Layer layer = mapWidget.getLayer(0);
//				layer.addMapObject(new MapObject(new Integer(random.nextInt(1000)), dr, randW, randH));
//			}
//        		
//			
//			is = getAssets().open("other/trail_difficulty_blue_rect.png");
//			drawable = new BitmapDrawable(is);
//			is.close();
//        	for (int i = 0; i < BLUE_POINT_COUNT; ++i) {
//				int randW = random.nextInt(MAP_WIDTH);
//				int randH = random.nextInt(MAP_HEIGHT);
//				
//				BitmapDrawable dr = new BitmapDrawable(drawable.getBitmap());	
//				
//				Layer layer = mapWidget.getLayer(1);
//				layer.addMapObject(new MapObject(new Integer(random.nextInt(1000)), dr, randW, randH, true, false));
//			}
//			
//		} catch (IOException e) {
//			Log.e("TileManagerWorkerThread", "Exception: " + e);
//			e.printStackTrace();
//		}
//	}
    
    
	
	private void initLayer(Layer theLayer, String theCategoryId)
	{
//	    List<com.ls.widgets.map.location.l> listLocations = this.model.getLocations();
//	    
//        for (Location location : listLocations)
//        {
//            try
//            {
//                String categoryId = location.getCategoryId();
//
//                if (categoryId.equals(theCategoryId))
//                {
//                    Point point = location.getPoint();
//                    MapObject object = new MapObject(location.getId(), 
//                    						getIcon(categoryId),
//                    						point,
//                    						true,  // Touchable
//                    						false); // scalable
//                   
//                    theLayer.addMapObject(object);
//                }
//
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
        
        theLayer.setVisible(true);
//        if (settings.isCategorySelected(CategoryIds.LEISURE))
//        {
//            layerLeisure.enable();
//        }
	}
	
	
	
	public Drawable getIcon(String theCatId) throws IOException
    {

        String path = "media/icons/";

        if (Model.CAT_MAIN_ATTRACTIONS.equalsIgnoreCase(theCatId))
        {
            path += "map_icon_leisure.png";
        } else if (Model.CAT_KIDS.equalsIgnoreCase(theCatId))
        {
            path += "map_icon_meals.png";
        } else if (Model.CAT_SPORT_AND_LEISURE.equalsIgnoreCase(theCatId))
        {
            path += "map_icon_others_3.png";
        } 

        AssetManager manager = getAssets();

        InputStream input = manager.open(path);

        return Drawable.createFromStream(input, null);
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
			Intent intent = new Intent(this, BrowseMapActivity.class);
			startActivity(intent);
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
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
		super.onStop();
	}

	
	@Override
	protected void onPause() {
		super.onPause();
	}



	@Override
	protected void onDestroy() 
	{
	//	mapWidget.destroy();
		super.onDestroy();
	}
};