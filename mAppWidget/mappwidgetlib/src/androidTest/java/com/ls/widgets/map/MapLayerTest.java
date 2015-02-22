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

import java.util.ArrayList;

import android.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.test.AndroidTestCase;

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.model.MapLayer;
import com.ls.widgets.map.model.MapObject;

public class MapLayerTest extends AndroidTestCase
{
	private MapWidget map;
	private Layer layer;
	private Drawable drawable;
	private FakeCanvas canvas;
	
	@Override
	protected void setUp() throws Exception 
	{
		map = new MapWidget(getContext(), "map", 11);
		drawable = new BitmapDrawable(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.presence_online));

		layer = map.createLayer(1);
		canvas = new FakeCanvas();
		
		super.setUp();
	}
	
	
	@Override
	protected void tearDown() throws Exception 
	{
		map.removeLayer(1);
		super.tearDown();
	}


	public void testAddMapObject()
	{
		for (int i=-1000; i <= 1000; ++i) {
			MapObject object = new MapObject(i,
											drawable,
											0, 0);
			layer.addMapObject(object);
			MapObject received = layer.getMapObject(i);
			
			assertSame(object, received);
		}
		
		layer.clearAll();
	}
	
	
	public void testRemoveMapObject()
	{
		for (int i=0; i <= 100; ++i) {
			MapObject object = new MapObject(i,
											drawable,
											0, 0);
			layer.addMapObject(object);
		}
		
		for (int i=0; i <= 100; ++i) {
			layer.removeMapObject(i);
			MapObject object = layer.getMapObject(i);
			assertNull("Failed for " + i + " element", object);
		}
	}

	
	public void testGetMapObject()
	{
		MapObject object1 = new MapObject(1,drawable, 0, 0);
		MapObject object4 = new MapObject(4,drawable, 4, 4);
		MapObject object16 = new MapObject(16,drawable, 16, 16);
		
		layer.addMapObject(object1);
		layer.addMapObject(object16);
		layer.addMapObject(object4);
		
		MapObject obj = layer.getMapObject(16);
		assertSame(object16, obj);
		assertEquals(16, obj.getX());
		assertEquals(16, obj.getY());
		
		obj = layer.getMapObject(1);
		assertSame(object1, obj);
		assertEquals(0, obj.getX());
		assertEquals(0, obj.getY());
		
		obj = layer.getMapObject(4);
		assertSame(object4, obj);
		assertEquals(4, obj.getX());
		assertEquals(4, obj.getY());
	}
	
	
	public void testIsVisible()
	{
		// Test default state
		assertTrue(layer.isVisible());
		
		// Test invisible state
		layer.setVisible(false);
		assertFalse(layer.isVisible());
		
		// Test visible state
		layer.setVisible(true);
		assertTrue(layer.isVisible());

		for (int i=0; i <= 20; ++i) {
			MapObject object = new MapObject(i,
											drawable,
											0, 0);
			layer.addMapObject(object);
		}		
		
		layer.setVisible(true);

		canvas.setDrawPerformed(false);
		((MapLayer)layer).draw(canvas, new Rect(0,0, 480, 800));
		assertTrue(canvas.drawPerformed);
		
		layer.setVisible(false);
		canvas.setDrawPerformed(false);
		((MapLayer)layer).draw(canvas, new Rect(0,0, 480, 800));
		assertFalse(canvas.drawPerformed);
		
		layer.setVisible(true);
		canvas.setDrawPerformed(false);
		((MapLayer)layer).draw(canvas, new Rect(480,0, 800, 800));
		assertFalse(canvas.drawPerformed);
	}
	
	
	public void testGetMapObjectByIndexInt()
	{
		for (int i=0; i < 100; ++i) {
			MapObject object = new MapObject(i,
											drawable,
											0, 0);
			layer.addMapObject(object);
		}
		
		int count = layer.getMapObjectCount();
		
		assertEquals(100, count);
		
		MapObject object = layer.getMapObject(5);
		assertEquals(object.getId(), 5);
	}
	
	public void testGetTouched()
	{
//		getTouchedTest1(true);
//		layer.clearAll();
//		getTouchedTest1(false);
//		layer.clearAll();
		
		getTouchedTest2(true);
		layer.clearAll();
		getTouchedTest2(false);
	}
	
	
	public void testSetScale()
	{
		for (int i=0; i < 200; ++i) {
			MapObject object = new MapObject(i,
										drawable,
										20, 10);
			layer.addMapObject(object);
		}
		
		float scaleToTest[] = {0.5f, 1.0f, 2.0f};
		int resultsX[] = {10, 20, 40};
		int resultsY[] = {5, 10, 20};
		
		for (int j=0; j<scaleToTest.length; ++j) {
			((MapLayer)layer).setScale(scaleToTest[j]);
			
			for (int i=0; i < 200; ++i) {
				MapObject obj = layer.getMapObject(i);
				assertNotNull(obj);
			
				assertEquals(20, obj.getX());
				assertEquals(10, obj.getY());
				assertEquals(resultsX[j], obj.getXScaled());
				assertEquals(resultsY[j], obj.getYScaled());
			}
		}
	}


//	private void getTouchedTest1(boolean touchable) 
//	{
//		int counter = 0; 
//		int width = drawable.getIntrinsicWidth();
//		int height = drawable.getIntrinsicHeight();
//		
//		getTouchedGenerateMapObjects(touchable, width, height);
//		
//		counter = 0;
//		for (int i=0; i < 480/width; ++i) {
//			for (int j=0; j < 800/height; ++j) {
//				int touchedX = (int)((float)i*(float)width+(float)width/2.0f);
//				int touchedY = (int)((float)j*(float)height+(float)height/2.0f);
//				ArrayList<Object> touchedIds = ((MapLayer)layer).getTouched(touchedX, touchedY);
//
//				if (touchable) {
//					assertEquals(1, touchedIds.size());
//				} else {
//					assertEquals(0, touchedIds.size());
//				}
//				
//				if (touchable) {
//					Object id = touchedIds.get(0);
//					assertEquals(counter, id);
//				}
//
//				counter += 1;
//			}
//		}
//	}
	
	
	private void getTouchedTest2(boolean touchable) 
	{
		int counter = 0; 
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		
		getTouchedGenerateMapObjects(touchable, width, height);
		
		counter = 0;
		for (int i=0; i < 480/width; ++i) {
			for (int j=0; j < 800/height; ++j) {
				int touchedX = (int)((float)i*(float)width+(float)width/2.0f);
				int touchedY = (int)((float)j*(float)height+(float)height/2.0f);
				ArrayList<Object> touchedIds = ((MapLayer)layer).getTouched(new Rect(touchedX - 2, touchedY - 2, touchedX + 2, touchedY + 2));

				if (touchable) {
					assertEquals(1, touchedIds.size());
				} else {
					assertEquals(0, touchedIds.size());
				}
				
				if (touchable) {
					Object id = touchedIds.get(0);
					assertEquals(counter, id);
				}
				counter += 1;
			}
		}
		
		ArrayList<Object> touchedIds = ((MapLayer)layer).getTouched(new Rect(0, 0, width * 2, height * 2));
		
		if (touchable) {
			assertEquals(4, touchedIds.size());
		} else {
			assertEquals(0, touchedIds.size());
		}
	}


	private void getTouchedGenerateMapObjects(boolean touchable,
			int width, int height) 
	{
		int counter = 0;
		for (int i=0; i < 480/width; ++i) {
			for (int j=0; j < 800/height; ++j) {
				MapObject object = new MapObject(counter,
											drawable,
											i*width, j*height, touchable);
				layer.addMapObject(object);
				
				counter += 1;
			}
		}
	}

	
	private static class FakeCanvas extends Canvas
	{
		private boolean drawPerformed;
		
		public FakeCanvas()
		{
			drawPerformed = false;
		}
		
		@Override
		public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) 
		{
			super.drawBitmap(bitmap, left, top, paint);
			drawPerformed = true;
		}

		@Override
		public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
			super.drawBitmap(bitmap, matrix, paint);
			drawPerformed = true;
		}

		@Override
		public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
			super.drawBitmap(bitmap, src, dst, paint);
			drawPerformed = true;
		}

		@Override
		public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
			super.drawBitmap(bitmap, src, dst, paint);
			drawPerformed = true;
		}
		
		
		public void setDrawPerformed(boolean performed)
		{
			drawPerformed = performed;
		}
	}
}
