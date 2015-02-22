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

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.model.MapLayer;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.Size;

import android.R;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.test.AndroidTestCase;

public class MapObjectTest extends AndroidTestCase
{
    
    
    private MapWidget map;
    private MapLayer layer;
    private Drawable drawable1;
    private Drawable drawable2;
    private FakeMapObject scalableObject;
    private FakeMapObject nonScalableObject;
    private Size originalDrawableSize;

  
    protected void setUp() throws Exception
    {
        super.setUp();
        
//        map = new MapWidget(getContext(), "map", 11);
        drawable1 = new BitmapDrawable(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.presence_online));
        drawable2 = new BitmapDrawable(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.presence_online));

        originalDrawableSize = new Size(drawable1.getIntrinsicWidth(), drawable1.getIntrinsicHeight());
//        layer = map.createLayer(1);   
        
        scalableObject = new FakeMapObject("1", drawable1, 10, 10, 0, 0, true, true);
        nonScalableObject = new FakeMapObject("2", drawable2, 100, 100, 0, 0, true, false);
    }


    protected void tearDown() throws Exception
    {
       // map.removeAllLayers();
        
        super.tearDown();
    }


    public void testMapObjectObjectDrawableIntIntIntIntBooleanBoolean()
    {
        fail("Not yet implemented");
    }


    public void testGetDrawable()
    {
        fail("Not yet implemented");
    }


    public void testSetDrawable()
    {
        fail("Not yet implemented");
    }


    public void testDraw()
    {
        fail("Not yet implemented");
    }


    public void testGetId()
    {
        fail("Not yet implemented");
    }


    public void testIsTouchedIntInt()
    {
        fail("Not yet implemented");
    }


    public void testIsTouchedRect()
    {      
        assertEquals(true, nonScalableObject.isTouchable());
        
        Rect touchRect = new Rect(100,100,101,101);
        
        assertEquals(true, nonScalableObject.isTouched(touchRect));
        
        touchRect.set(100, 99, 101, 100);
        
        assertEquals(false, nonScalableObject.isTouched(touchRect));
        
        Rect drawableRect = nonScalableObject.getDrawable().getBounds();
        Rect touchableRect = nonScalableObject.getTouchArea();
        
        assertEquals(drawableRect.width(), touchableRect.width());
        
        nonScalableObject.setNewScale(2.0f);
        
        assertEquals(drawableRect.width(), touchableRect.width() / 2);
    }


    public void testGetXScaled()
    {
        fail("Not yet implemented");
    }


    public void testGetYScaled()
    {
        fail("Not yet implemented");
    }


    public void testGetX()
    {
        fail("Not yet implemented");
    }


    public void testGetY()
    {
        fail("Not yet implemented");
    }


    public void testIsTouchable()
    {
        fail("Not yet implemented");
    }


    public void testGetBounds()
    {
        fail("Not yet implemented");
    }


    public void testMoveTo()
    {
        fail("Not yet implemented");
    }


    public void testSetScale()
    {
        fail("Not yet implemented");
    }


    public void testSetParent()
    {
        fail("Not yet implemented");
    }

    private class FakeMapObject extends MapObject {

        public FakeMapObject(Object id, Drawable drawable, int x, int y, int pivotX, int pivotY, boolean isTouchable,
                boolean isScalable)
        {
            super(id, drawable, x, y, pivotX, pivotY, isTouchable, isScalable);
        }
        
        
        public void setNewScale(float scale)
        {
            this.scale = scale;
            recalculateBounds();        
        }
        
        
        public Rect getTouchArea()
        {
            return this.touchRect;
        }
        


    }
    
}
