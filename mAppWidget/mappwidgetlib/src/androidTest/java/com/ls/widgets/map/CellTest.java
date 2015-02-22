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
import com.ls.widgets.map.config.OfflineMapConfig;
import com.ls.widgets.map.model.Cell;
import com.ls.widgets.map.model.Grid;
import com.ls.widgets.map.providers.AssetTileProvider;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.test.AndroidTestCase;
import android.widget.LinearLayout;


public class CellTest extends AndroidTestCase 
{
	private static final int MAP_ORIGINAL_WIDTH = 1520;
	private static final int MAP_ORIGINAL_HEIGHT = 920;
	private static final int MAX_ZOOM_LEVEL = 11;

	private OfflineMapConfig config;
	private AssetTileProvider tileManager;
	private Drawable drawable;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		config = new OfflineMapConfig("map", MAP_ORIGINAL_WIDTH, MAP_ORIGINAL_HEIGHT, 256, 1, "png");
	    tileManager = new AssetTileProvider(getContext(), config);
	    
	    drawable = new BitmapDrawable(Bitmap.createBitmap(config.getTileSize(), config.getTileSize(), Bitmap.Config.RGB_565));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	public void testRecalculateDrawableRect()
	{
		Grid grid = new Grid(new LinearLayout(getContext()), config, tileManager, MAX_ZOOM_LEVEL);
		CellImpl cell = new CellImpl(grid, tileManager, 6, 0, 0, config.getTileSize(), 1.0f);
		cell.setImage(drawable);
		cell.recalculateDrawableRect(0, 0);
		
		Rect imageRect = cell.getDrawableRect();
		Rect shouldBe = new Rect(0,0,256,256);
		
		assertEquals(shouldBe, imageRect);
		
		cell.setScale(2.0f);
		imageRect = cell.getDrawableRect();
		shouldBe.set(0,0, 512, 512);
		
		assertEquals(shouldBe, imageRect);
		
		cell = new CellImpl(grid, tileManager, 11, 1, 1, config.getTileSize(), 1.0f);
	    drawable = new BitmapDrawable(Bitmap.createBitmap(config.getTileSize(), config.getTileSize(), Bitmap.Config.RGB_565));
		cell.setImage(drawable);
		cell.setScale(1.0f);
		cell.recalculateDrawableRect(0, 0);
		
		shouldBe.set(256, 256, 512, 512);
		imageRect = cell.getDrawableRect();
		
		assertEquals(shouldBe, imageRect);
	}
	
	
	private void assertEquals(Rect r1, Rect r2) 
	{
		assertEquals(r1.top,  r2.top);
		assertEquals(r1.left,  r2.left);
		assertEquals(r1.bottom, r2.bottom);
		assertEquals(r1.right,  r2.right);
	}
	
	private static class CellImpl extends Cell
	{
		public CellImpl(Grid parent, AssetTileProvider tileManager, int zoomLevel, int col, int row, int tileSize, float scale) {
			super(parent, tileManager, zoomLevel, col, row, tileSize, scale);
		}
		
		
		@Override
		protected void recalculateDrawableRect(float dx, float dy) {
			// TODO Auto-generated method stub
			super.recalculateDrawableRect(dx, dy);
		}
		
		
		public void setImage(Drawable drawable) 
		{
			this.image = drawable;
		}
		
		
		public Rect getDrawableRect()
		{
			return image.getBounds();
		}
	}

}
