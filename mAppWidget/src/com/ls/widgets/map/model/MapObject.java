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

package com.ls.widgets.map.model;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.ls.widgets.map.config.GPSConfig;
import com.ls.widgets.map.utils.MapCalibrationData;

public class MapObject
{
	protected MapLayer parent;

    protected float scale; // Current map scale    
   
    // Pivot point
    protected Point pivotPoint;
    
	private Object id;

	// Position in map coordinates
	protected Point pos;
	// Position in map coordinates taking the scale into account
	protected Point posScaled;
	
    private Drawable drawable;

	private boolean isScalable; // Map object is scalable or not (on map zoom)
	private boolean isTouchable; // Shows whether this object should respond to touch events.
	
	protected Rect touchRect; // Object's touch area.	

	public MapObject(Object id)
	{
		this(id, null, 0, 0, false);
	}
	
	
	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image.
	 * @param x  - x coordinate of the object in map coordinates.
	 * @param y - y coordinate of the object in map coordinates.
	 */
	public MapObject(Object id, Drawable drawable, int x, int y)
	{
		this(id, drawable, x, y, false);
	}
	
	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image
	 * @param position - coordinate of the object in map coordinates.
	 */
	public MapObject(Object id, Drawable drawable, Point position)
	{
		this(id, drawable, position.x, position.y, false);
	}
	
	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image.
	 * @param x - x coordinate of the object in map coordinates.
	 * @param y - y coordinate of the object in map coordinates.
	 * @param pivotX - x coordinate of pivot point in image coordinates.
	 * @param pivotY - y coordinate of pivot point in image coordinates.
	 */
	public MapObject(Object id, Drawable drawable, int x, int y, int pivotX, int pivotY)
	{
		this(id, drawable, x, y, pivotX, pivotY, false, false);
	}
	
	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image.
	 * @param position - coordinate of the object in map coordinates.
	 * @param pivotPoint - coordinate of the pivot point in image coordinates.
	 */
	public MapObject(Object id, Drawable drawable, Point position, Point pivotPoint)
	{
		this(id, drawable, position.x, position.y, pivotPoint.x, pivotPoint.y);
	}
	
	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image.
	 * @param x - x coordinate of the object in map coordinates.
	 * @param y - y coordinate of the object in map coordinates.
	 * @param isTouchable - true if the object should respond to touch events, false otherwise.
	 */
	public MapObject(Object id, Drawable drawable, int x, int y, boolean isTouchable)
	{
		this(id, drawable, x, y, isTouchable, true);
	}
	
	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image.
	 * @param position - coordinate of the object in map coordinates.
	 * @param isTouchable - true if the object should respond to touch events, false otherwise.
	 */
	public MapObject (Object id, Drawable drawable, Point position, boolean isTouchable)
	{
		this(id, drawable, position.x, position.y, true, true);
	}
	
	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image.
	 * @param position - coordinate of the object in map coordinates.
	 * @param pivotPoint - coordinate of the pivot point in image coordinates.
	 * @param isTouchable - true if the object should respond to touch events, false otherwise.
	 */
	public MapObject (Object id, Drawable drawable, Point position, Point pivotPoint, boolean isTouchable)
	{
		this(id, drawable, position.x, position.y, pivotPoint.x, pivotPoint.y, isTouchable, true);
	}
	
	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image.
	 * @param x - x coordinate of the object in map coordinates.
	 * @param y - y coordinate of the object in map coordinates.
	 * @param isTouchable - true if the object should respond to touch events, false otherwise.
	 * @param isScalable - true, if map object should be scaled on map zoom, false otherwise.
	 */
	public MapObject(Object id, Drawable drawable, int x, int y,  boolean isTouchable, boolean isScalable)
	{
		this(id, drawable, x, y, 0, 0, isTouchable, isScalable);
	}
	
	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image.
	 * @param position - coordinate of the object in map coordinates.
	 * @param isTouchable - true if the object should respond to touch events, false otherwise.
	 * @param isScalable - true, if map object should be scaled on map zoom, false otherwise.
	 */
	public MapObject(Object id, Drawable drawable, Point position,  boolean isTouchable, boolean isScalable)
	{
		this(id, drawable, position.x, position.y, 0, 0, isTouchable, isScalable);
	}
	
	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image.
	 * @param position - coordinate of the object in map coordinates.
	 * @param pivotPoint - coordinate of the pivot point in image coordinates.
	 * @param isTouchable - true if the object should respond to touch events, false otherwise.
	 * @param isScalable - true, if map object should be scaled on map zoom, false otherwise.
	 */
	public MapObject (Object id, Drawable drawable, Point position, Point pivotPoint, boolean isTouchable, boolean isScalable)
	{
		this(id, drawable, position.x, position.y, pivotPoint.x, pivotPoint.y, isTouchable, isScalable);
	}
	

	/**
	 * Creates new MapObject.
	 * @param id - id of the object.
	 * @param drawable - image.
	 * @param x - x coordinate of the object in map coordinates.
	 * @param y - y coordinate of the object in map coordinates.
	 * @param pivotX - x coordinate of pivot point in image coordinates.
	 * @param pivotY - y coordinate of pivot point in image coordinates.
	 * @param isTouchable - true if the object should respond to touch events, false otherwise.
	 * @param isScalable - true, if map object should be scaled on map zoom, false otherwise.
	 */
	public MapObject(Object id, Drawable drawable, int x, int y, int pivotX, int pivotY, boolean isTouchable, boolean isScalable)
	{
		this.id = id;
		this.drawable = drawable;
		
		pos = new Point(x, y);
		posScaled = new Point();
		this.pivotPoint = new Point(pivotX, pivotY);

		this.isTouchable = isTouchable;
		this.isScalable = isScalable;
		
		this.scale = 1.0f;
		
		this.touchRect = new Rect();	
	}
	
	
	/**
	 * Returns image that was passed to the constructor.
	 * @return instance of Drawable.
	 */
	public Drawable getDrawable()
	{
		return drawable;
	}
	
	/**
	 * Sets object's image.
	 * @param drawable instance of Drawable.
	 * @throws IllegalArgumentException when null is set.
	 */
	public void setDrawable(Drawable drawable)
	{
		if (drawable == null)
			throw new IllegalArgumentException();
		
		if (Looper.myLooper() == null) 
			throw new IllegalThreadStateException("setDrawable should be called from UI thread");
		
		this.drawable = drawable;
		
		recalculateBounds();
	}
	
	
	/**
	 * Draws the map object on the canvas
	 * @param canvas - Canvas
	 */
	public void draw(Canvas canvas)
	{
		if (drawable != null) {
	        drawable.draw(canvas);
		}
	}

	/**
	 * Returns id of this object
	 * @return Object that was passed as an id to the constructor.
	 */
	public Object getId()
	{
		return id;
	}
	
	
	/**
	 * Determines whether touch rectangle intersects the bounds of this object.
	 * Coordinates that are passed in touchRect are the screen coordinates + scroll offset.
	 * @param touchRect - area inside of the map.
	 * @return - true if touchRect intersects the map object's touch area, false otherwise.
	 */
	public boolean isTouched(Rect touchRect)
	{
		return Rect.intersects(this.touchRect, touchRect);
	}

	/**
	 * Returns X coordinate of the map object in map coordinates.
	 * @return X coordinate taking the map scale into account.
	 */
	public int getXScaled() 
	{
		return posScaled.x;
	}

	/**
	 * Returns Y coordinate of the map object in map coordinates.
	 * @return Y coordinate taking the map scale into account.
	 */
	public int getYScaled() 
	{
		return posScaled.y;
	}
	
	/**
	 * Returns X coordinate of the map object in map coordinates.
	 * @return X coordinate of the object.
	 */
	public int getX()
	{
		return pos.x;
	}

	/**
	 * Returns Y coordinate of the map object in map coordinates.
	 * @return Y coordinate of the object.
	 */
	public int getY()
	{
		return pos.y;
	}
	
	/**
	 * Returns position of the map object in map coordinates in pixels of the original image.
	 * @return instance of {@link android.graphics.Point}. 
	 */
	public Point getPosition()
	{
	    return pos;
	}
	
	/**
	 * Shows whether this object is touchable.
	 * @return true if this object should respond to touch events, false otherwise.
	 */
	public boolean isTouchable()
	{
		return isTouchable;
	}
	
	/**
	 * Set's pivot point within the drawable
	 * @param x - x coordinate in pixels
	 * @param y - y coordinate in pixels
	 */
	public void setPivotPoint(int x, int y)
	{
		pivotPoint.x = x;
		pivotPoint.y = y;
	}
	
	
	/**
	 * Set's pivot point within the drawable
	 * @param pivotPoint position of pivot point within the drawable.
	 * @throws java.lang.IllegalArgumentException if null is passed
	 */
	public void setPivotPoint(Point pivotPoint)
	{
	    if (pivotPoint == null) {
	        throw new IllegalArgumentException();
	    }
	    
	    this.pivotPoint = pivotPoint;
	}
	
	
	/**
	 * Returns bounds of the image that represents the map object.
	 * Note: for efficiency, the returned object may be the same object stored in the drawable (though this is not guaranteed), so if a persistent copy of the bounds is needed, call copyBounds(rect) instead. You should also not change the object returned by this method as it may be the same object stored in the drawable.
	 * @return instance of Rect with size of the image in pixels taking the scale of the map into account.
	 */
	public Rect getBounds()
	{	
	    if (drawable != null) {
	        return drawable.getBounds();
	    } else return null;
	}
	
	
	/**
	 * This method is responsible for calculation of position and size of the map object.
	 * It will be called when map changes its scale or when other event that may affect the size of the map object occurs.
	 * It should perform quick and do not contain memory allocations as it may be called pretty often.
	 */
	protected void recalculateBounds()
	{	
	    posScaled.x = (int)(pos.x*scale);
		posScaled.y = (int)(pos.y*scale);
	
		int width = 0;
		int height = 0;
		
		if (drawable == null)
			return;

		width = drawable.getIntrinsicWidth();
		height = drawable.getIntrinsicHeight();

		if (!isScalable) {
			//ignore scale	
			drawable.setBounds(posScaled.x - pivotPoint.x, posScaled.y-pivotPoint.y, posScaled.x + width - pivotPoint.x, 
			        posScaled.y + height - pivotPoint.y);
		} else {
			drawable.setBounds(posScaled.x - (int)(pivotPoint.x*scale), posScaled.y-(int)(pivotPoint.y*scale), posScaled.x + (int)(width*scale) - (int)(pivotPoint.x*scale), 
			        posScaled.y + (int)(height*scale) - (int)(pivotPoint.y*scale));
		}
	
		if (isTouchable) {
		    touchRect.set(drawable.getBounds());
		}
	}
	
	
	
	/**
	 * Moves object to another position on the map that is defined in pixels.
	 * @param x - horizontal coordinate of the object within the map in pixels.
	 * @param y - vertical coordinate of the object within the map in pixels.
	 */
	public void moveTo(int x, int y) 
	{
		invalidateSelf();
		pos.x = x;
		pos.y = y;
		recalculateBounds();

		invalidateSelf();
	}
	
	
	/**
	 * Moves object to another position on the map that is defined in pixels. In order for this method to work you should 
	 * ensure that geo area is configured in /assets/map/map.xml file.
	 * @param location - location of the object.
	 * @throws IllegalStateException if geo area is not configured in map.xml file.
	 * @see android.location.Location
	 */
	public void moveTo(Location location)
	{
		GPSConfig config = parent.getConfig().getGpsConfig();
		
		if (!config.isMapCalibrated()) {
			Log.w("MapObject", "Can't move object to location because map has not been calibrated.");
			throw new IllegalStateException("Map is not calibrated. Please, add calibration info into map's configuration file.");
		}
		
		invalidateSelf();
	
		if (config.isMapCalibrated()) {
    		MapCalibrationData calibration = config.getCalibration();

    		calibration.translate(location, pos);
    		
    		recalculateBounds();
    		invalidateSelf();
		} 
	}
	
	
	@Override
	public boolean equals(Object o) 
	{
		if (o == null)
			return false;
		
		if (!(o instanceof MapObject)) {
			return false;
		} 
		
		return ((MapObject)o).id.equals(this.id); 	
	}

	
	@Override
	public int hashCode() 
	{
		return id.hashCode();
	}

	
	protected void invalidateSelf()
	{
		parent.invalidate(this);
	}
	
	
	/*
	 * Sets the scale for drawable and recalculates bounds.
	 * Package level visibility. Is called from MapLayer.
	 */
	void setScale(float scale)
	{
		this.scale = scale;
		recalculateBounds();
	}

	
	/*
	 * Used by the layer to set itself as a parent for this map object.
	 * Not intended to use by the user.
	 */
	void setParent(MapLayer layer)
	{
		this.parent = layer;
	}
}
