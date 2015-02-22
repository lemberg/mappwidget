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
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.events.MapTouchedEvent;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.interfaces.MapEventsListener;
import com.ls.widgets.map.interfaces.OnLocationChangedListener;
import com.ls.widgets.map.interfaces.OnMapDoubleTapListener;
import com.ls.widgets.map.interfaces.OnMapLongClickListener;
import com.ls.widgets.map.interfaces.OnMapTilesFinishedLoadingListener;
import com.ls.widgets.map.model.MapLayer;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.GeoUtils;
import com.ls.widgets.map.utils.PivotFactory;
import com.ls.widgets.map.utils.PivotFactory.PivotPosition;

public class Sample2Activity 
    extends Activity
{
    public static final int MAP_ID = 1; 
    private int currId = 35;
    
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        final MapWidget mapWidget = new MapWidget(savedInstanceState, this, "map", 10);
        mapWidget.setSaveEnabled(true);
        mapWidget.getConfig().setMinZoomLevelLimit(10);
        mapWidget.getConfig().setZoomBtnsVisible(false);
        mapWidget.setId(MAP_ID);
        
        // create map layer with specified ID
        final long LAYER_ID = 5;
        MapLayer layer = mapWidget.createLayer(LAYER_ID);        
        
        // getting icon from assets
        Drawable icon = getResources().getDrawable(R.drawable.map_icon_attractions);
        
        // define coordinates of icon on map
        int x = 200;
        int y = 300;
        
        // set ID for the object
        final long OBJ_ID = 25;
        
        // adding object to layer
        MapObject obj = new MapObject(OBJ_ID, icon, new Point(x, y), PivotFactory.createPivotPoint(icon, PivotPosition.PIVOT_CENTER), true, false);
       // obj.setCaption("5434 KNNB");
        
        //MapObject(OBJ_ID, icon, new Point(x, y), PivotFactory.createPivotPoint(icon, PivotPosition.PIVOT_CENTER), true, false)
        
        layer.addMapObject(obj);
          
        LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
        layout.setBackgroundColor(0xFFFFFFFF);
        layout.addView(mapWidget);
        
        mapWidget.getMapGraphicsConfig().setArrowPointerDrawableId(R.drawable.maps_blue_arrow);
        mapWidget.getMapGraphicsConfig().setDotPointerDrawableId(R.drawable.maps_blue_dot);
        
        mapWidget.setOnMapTilesFinishLoadingListener(new OnMapTilesFinishedLoadingListener() {
			
			@Override
			public void onMapTilesFinishedLoading() {
				mapWidget.zoomIn();
				mapWidget.scrollMapTo(1000, 800);
				mapWidget.setOnMapTilesFinishLoadingListener(null);
			}
		});
       // mapWidget.setShowMyPosition(true);
        
        
        mapWidget.addMapEventsListener(new MapEventsListener()
        {
            
            @Override
            public void onPreZoomOut()
            {
                // TODO Auto-generated method stub
                
            }
            
        
            @Override
            public void onPreZoomIn()
            {
                // TODO Auto-generated method stub
                
            }
            
        
            @Override
            public void onPostZoomOut()
            {
                // TODO Auto-generated method stub
                
            }
            
        
            @Override
            public void onPostZoomIn()
            {
                // TODO Auto-generated method stub
                
            }
        });
        
        
        mapWidget.setOnLocationChangedListener(new OnLocationChangedListener() {
            @Override
            public void onLocationChanged(MapWidget v, Location location)
            {
                v.scrollMapTo(location);               
            }
        });
        
        
        mapWidget.setOnDoubleTapListener(new OnMapDoubleTapListener() {
			@Override
			public boolean onDoubleTap(MapWidget v, MapTouchedEvent event) {
				Log.d("Sample1Activity", "On double tap");
				
				Location location = addObjetWhereTouched(mapWidget, event, R.drawable.map_icon_attractions);
				Toast.makeText(Sample2Activity.this, "New object coords: Lat: " + location.getLatitude() + " Lon:" + location.getLongitude(), Toast.LENGTH_SHORT).show();
				return true;
			}


        });
        
        
        mapWidget.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				
				return false;
			}
		});
        
        
        mapWidget.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public boolean onLongClick(MapWidget v, MapTouchedEvent e) {

				if (e.getTouchedObjectIds().size() == 0) {
					addObjetWhereTouched(v, e, R.drawable.map_icon_leasure);
				} else {
					Toast.makeText(Sample2Activity.this, "Layer Id: " + e.getTouchedObjectIds().get(0).getLayerId(), Toast.LENGTH_SHORT).show();
				}
				
				
				return false;
			}
        	
        });        
    }

    
	private Location addObjetWhereTouched(final MapWidget mapWidget, MapTouchedEvent event, int iconId) {
		// getting icon from assets
        Drawable icon = getResources().getDrawable(iconId);
        MapObject obj = new MapObject(currId++, icon, new Point(0, 0), 
        		PivotFactory.createPivotPoint(icon, PivotPosition.PIVOT_CENTER), true, false);

        Layer layer = mapWidget.getLayerById(5);   
		layer.addMapObject(obj);

		Location location = new Location("custom");
		GeoUtils.translate(mapWidget, event.getMapX(), event.getMapY(), location);
        
		obj.moveTo(location);
		return location;
	}
    
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		MapWidget map = (MapWidget) findViewById(MAP_ID);
		map.saveState(outState);
		super.onSaveInstanceState(outState);
	}
    
}
