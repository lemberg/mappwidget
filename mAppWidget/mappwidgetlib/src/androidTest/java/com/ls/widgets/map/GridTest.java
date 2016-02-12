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

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.test.AndroidTestCase;
import android.view.View;
import android.widget.LinearLayout;

import com.ls.widgets.map.config.OfflineMapConfig;
import com.ls.widgets.map.interfaces.TileManagerDelegate;
import com.ls.widgets.map.model.Grid;
import com.ls.widgets.map.providers.AssetTileProvider;

public class GridTest extends AndroidTestCase {

	OfflineMapConfig config;
	AssetTileProvider tileManager;

	private static final int MAP_ORIGINAL_WIDTH = 1520;
	private static final int MAP_ORIGINAL_HEIGHT = 920;
	private static final int MAX_ZOOM_LEVEL = 11;
	
	 private int colCount[] = {1,1,1,1,1,1,1,1,1,2,3,6}; // col count on each zoom level
	 private int rowCount[] = {1,1,1,1,1,1,1,1,1,1,2,4}; // row count on each zoom level
	 private int widthOnZoomLevel[] = {1,2,3,6,12,24,48,95,190,380,760,1520}; // width of the grid on each zoom level in pixels
	 private int heightOnZoomLevel[] = {1,1,2,4,8,15,29,58,115,230,460,920}; // height of the grid on each zoom level in pixels
	 
	 // scale on each zoom level
	 private double scaleOnZoomLevel[] = {0.00048828125f, 0.0009765625f, 0.001953125f, 0.00390625f, 0.0078125f, 0.015625f, 0.03125f, 0.0625f, 0.125f, 0.25f, 0.5f, 1.0f};
	private BitmapDrawable drawable;
	
	protected void setUp() throws Exception 
	{
		super.setUp();
		
		config = new OfflineMapConfig("map", MAP_ORIGINAL_WIDTH, MAP_ORIGINAL_HEIGHT, 256, 1, "png");
		drawable = new BitmapDrawable(getContext().getResources(),
                BitmapFactory.decodeResource(getContext().getResources(),
			com.ls.widgets.map.test.R.drawable.maps_blue_dot));

	    tileManager = new TestTileManager(getContext(), config);
	}

	protected void tearDown() throws Exception 
	{
		super.tearDown();
	}

	public void testGetHeight() 
	{
		for (int i=0; i< heightOnZoomLevel.length; ++i) {
			GridToTest grid = new GridToTest(new LinearLayout(getContext()), config, tileManager, i);
			assertEquals(heightOnZoomLevel[i], grid.getHeight());
		}
	}

	public void testGetOriginalHeight() 
	{
		GridToTest grid = new GridToTest(new LinearLayout(getContext()), config, tileManager, MAX_ZOOM_LEVEL);
		assertEquals(MAP_ORIGINAL_HEIGHT, grid.getOriginalHeight());
	}

	public void testGetOriginalWidth() 
	{
		for (int i=0; i<=MAX_ZOOM_LEVEL; ++i) {
			GridToTest grid = new GridToTest(new LinearLayout(getContext()), config, tileManager, MAX_ZOOM_LEVEL);
			assertEquals(MAP_ORIGINAL_WIDTH, grid.getOriginalWidth());
		}
	}

	public void testGetWidth() 
	{
		for (int i=0; i<=MAX_ZOOM_LEVEL; ++i) {
			GridToTest grid = new GridToTest(new LinearLayout(getContext()), config, tileManager, i);
			assertEquals(widthOnZoomLevel[i], grid.getWidth());
		}
	}

	public void testGetMaxZoomLevel() 
	{
		for (int i=0; i<= MAX_ZOOM_LEVEL; ++i) {
			GridToTest grid = new GridToTest(new LinearLayout(getContext()), config, tileManager, i);
			assertEquals(MAX_ZOOM_LEVEL, grid.getMaxZoomLevel());
		}
	}

	public void testGetMinZoomLevel() 
	{
		for (int i=0; i<= MAX_ZOOM_LEVEL; ++i) {
			GridToTest grid = new GridToTest(new LinearLayout(getContext()), config, tileManager, i);
			assertEquals(0, grid.getMinZoomLevel());
		}
	}
	
	public void testGetColCount()
	{
		for (int i=0; i<colCount.length; ++i) {
			GridToTest grid = new GridToTest(new LinearLayout(getContext()), config, tileManager, i);
			
			assertEquals(colCount[i], grid.getColCountTest());
		}
	}
	
	
	public void testGetRowCount()
	{
		for (int i=0; i<rowCount.length; ++i) {
			GridToTest grid = new GridToTest(new LinearLayout(getContext()), config, tileManager, i);
			
			assertEquals(rowCount[i], grid.getRowCountTest());
		}
	}
	
	
	public void testGetScale()
	{
		for (int i=0; i<scaleOnZoomLevel.length; ++i) {
			GridToTest grid = new GridToTest(new LinearLayout(getContext()), config, tileManager, i);
			
			assertEquals(scaleOnZoomLevel[i], grid.getScale());
		}
	}
	

	public void testSetSoftScale()
	{
		final float scales[] = {0.125f, 0.25f, 0.5f, 2.0f, 4.0f, 8.0f};
		
		for (int zoom_level=7; zoom_level<=MAX_ZOOM_LEVEL; ++zoom_level) {
			GridToTest grid = new GridToTest(new LinearLayout(getContext()), config, tileManager, zoom_level);
			
			for (int i=0; i<scales.length; ++i) {
				grid.setSoftScale(1.0f);
				int width1 = grid.getWidth();
				grid.setSoftScale(scales[i]);
				int width2 = grid.getWidth();
				
				double scale = grid.getSoftScale();
				assertEquals(scales[i], (float)scale);
				
				assertEquals("ZL: " + zoom_level + ", Original W: " + width1 + ", Scaled W: " + width2 + ", scale: " + scales[i],
				(int)Math.ceil((float)width1 * scales[i]), width2);
			}
		}
	}
	

	private static class GridToTest extends Grid
	{
		public GridToTest(View parent, OfflineMapConfig config, AssetTileProvider tileManager,int initZoomLevel)
		{
			super(parent, config, tileManager, initZoomLevel);
		}
		
		
		public int getColCountTest()
		{
			return getColCount();
		}
		
		
		public int getRowCountTest()
		{
			return getRowCount();
		}
	}
	
	
	private class TestTileManager extends AssetTileProvider
	{

		public TestTileManager(Context context, OfflineMapConfig config) {
			super(context, config);
			
		}

		@Override
		public void requestTile(int zoomLevel, int col, int row,
				TileManagerDelegate delegate) {
			delegate.onTileReady(zoomLevel, col, row, drawable);
		}
	}
}
