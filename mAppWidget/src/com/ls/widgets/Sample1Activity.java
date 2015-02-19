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

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.config.OfflineMap;
import com.ls.widgets.map.events.MapTouchedEvent;
import com.ls.widgets.map.interfaces.MapEventsListener;
import com.ls.widgets.map.interfaces.OnMapDoubleTapListener;
import com.ls.widgets.map.model.MapLayer;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.PivotFactory;
import com.ls.widgets.map.utils.PivotFactory.PivotPosition;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.interfaces.MapEventsListener;

public class Sample1Activity 
    extends Activity
{

    
    public static final int MAP_ID = 1; 
    private static final long LAYER_ID = 5;

    
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        final int initialZoomLevel = 10;
        
        final MapWidget mapWidget = new MapWidget(savedInstanceState, this, OfflineMap.MAP_ROOT, initialZoomLevel);
        mapWidget.setId(MAP_ID);
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
        layout.addView(mapWidget);
        
        mapWidget.getConfig().setFlingEnabled(true);
        mapWidget.getConfig().setPinchZoomEnabled(true);
        
        mapWidget.setMaxZoomLevel(13);
        mapWidget.setUseSoftwareZoom(true);
        mapWidget.setZoomButtonsVisible(true);
        mapWidget.setBackgroundColor(Color.GREEN);
        
        mapWidget.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(Sample1Activity.this, "Long press works!", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
        
        
        mapWidget.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}
        	
        });
        
        mapWidget.setOnDoubleTapListener(new OnMapDoubleTapListener() {
			@Override
			public boolean onDoubleTap(MapWidget v, MapTouchedEvent event) {
				Log.d("Sample1Activity", "On double tap");
				Toast.makeText(Sample1Activity.this, "Double tap overridden", Toast.LENGTH_SHORT).show();
				return true;
			}
        });

        MapLayer layer = mapWidget.createLayer(LAYER_ID);        
        
        // getting icon from assets
        Drawable icon = getResources().getDrawable(R.drawable.map_icon_attractions);
        
        // define coordinates of icon on map
        int x = 240;
        int y = 362;
        
        // set ID for the object
        final long OBJ_ID = 25;
        
        // adding object to layer
        layer.addMapObject(new MapObject(OBJ_ID, icon, new Point(x, y), PivotFactory.createPivotPoint(icon, PivotPosition.PIVOT_CENTER), false, false));

        
        
        mapWidget.addMapEventsListener(new MapEventsListener() {
			
			@Override
			public void onPreZoomOut() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPreZoomIn() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPostZoomOut() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setTitle("Zoom level: " + mapWidget.getZoomLevel());
					}
				});
			}
			
			@Override
			public void onPostZoomIn() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setTitle("Zoom level: " + mapWidget.getZoomLevel());
					}
				});
				
			}
		});

    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        
        MapWidget map = (MapWidget) findViewById(MAP_ID);
        map.saveState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.a1menu, menu);
        
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        MapWidget map = (MapWidget) findViewById(MAP_ID);

		int i = item.getItemId();
		if (i == R.id.scrollTo)
		{
			map.scrollMapTo(new Point(240, 320));
		}
		else if (i == R.id.jumpTo)
		{
			map.jumpTo(new Point(240, 320));
		}
        return super.onOptionsItemSelected(item);
    }

}
