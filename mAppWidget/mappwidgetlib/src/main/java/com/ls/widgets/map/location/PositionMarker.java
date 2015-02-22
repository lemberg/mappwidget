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


package com.ls.widgets.map.location;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.config.GPSConfig;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.utils.MapCalibrationData;
import com.ls.widgets.map.utils.PivotFactory;
import com.ls.widgets.map.utils.PivotFactory.PivotPosition;

public class PositionMarker extends MapObject
{
    private float accuracy;
    private float bearing;
    private boolean hasBearing;
    
    private float accuracyRadius;
    private float pixelsInMeter;
    private double centerX;
    private double centerY;

    private ShapeDrawable accuracyDrawable;
    
    private Paint accuracyBorderPaint;
    private Drawable arrowPointerDrawable;
    private Drawable roundPointerDrawable;
    
    private MapWidget context;
	private Point roundPointerPivotPoint;
	private Point arrowPointerPivotPoint;

    public PositionMarker(MapWidget context, Object id, Drawable roundPointerDrawable, Drawable arrowPointerDrawable)
    {
        super(id, null, new Point(0, 0), false, false);
        this.context = context;
        
        accuracy = 500;
        pixelsInMeter = 0;
        
        hasBearing = false;
        arrowPointerPivotPoint = null;
        roundPointerPivotPoint= null;
        
        this.context = context;
        
        Shape accuracyShape = new OvalShape();
        accuracyShape.resize(getAccuracyDiameter(), getAccuracyDiameter());
        
        accuracyDrawable = new ShapeDrawable(accuracyShape);     
        this.roundPointerDrawable = roundPointerDrawable;
        this.arrowPointerDrawable = arrowPointerDrawable;
        
        Paint accuracyAreaPaint = accuracyDrawable.getPaint();
        accuracyAreaPaint.setStyle(Style.FILL);

        accuracyBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        accuracyBorderPaint.setStyle(Style.STROKE);
        
        setDrawable(roundPointerDrawable);
    }


    private float calculatePixelsInMeter(MapWidget context)
    {
        if (context == null)
            return 0;
        
        GPSConfig config = context.getGpsConfig();
      
        if (config != null && config.isMapCalibrated()) {
        	MapCalibrationData geoArea = config.getCalibration();
        	return context.getConfig().getImageWidth() / geoArea.getWidthInMeters();
        }
        
        return 0;
    }

    
    /**
     * Sets the color of accuracy area and border.
     * @param color - Color
     */
    public void setColor(int area, int border)
    {
        Paint accuracyAreaPaint = accuracyDrawable.getPaint();
        accuracyAreaPaint.setColor(area);
        accuracyBorderPaint.setColor(border);
    }


    /**
     * Sets the size of accuracy area.
     * @param accuracy - accuracy in meters. You can get this value from android.location.Location
     * @see android.location.Location
     */
    public void setAccuracy(float accuracy)
    {
        invalidateSelf();

        this.accuracy = accuracy;

        recalculateBounds();

        invalidateSelf();
    }


    /**
     * Rotates the position pointer by some degree. In order for this method to take effect you should call
     * GpsPositionMarker.setBearingEnabled(true)
     * 
     * @param bearing - GPS bearing angle value in degrees. You can get it from {@link android.location.Location} object.
     */
    public void setBearing(float bearing)
    {
    	invalidateSelf();
        this.bearing = bearing;
        invalidateSelf();
    }

    
    public void setDotPointer(Drawable dotPointer, Point pivotPoint)
    {
    	this.roundPointerDrawable = dotPointer;
    	this.roundPointerPivotPoint = pivotPoint;
    }
    
    
    public void setArrowPointer(Drawable arrowPointer, Point pivotPoint)
    {
    	this.arrowPointerDrawable = arrowPointer;
    	this.arrowPointerPivotPoint = pivotPoint;
    }
    
    
    /**
     * Enables show direction mode.
     * @param hasBearing - true to show movement direction of false otherwise.
     */
    public void setBearingEnabled(boolean hasBearing)
    {
        this.hasBearing = hasBearing;
       
        if (hasBearing) {
            setDrawable(arrowPointerDrawable);
            if (arrowPointerPivotPoint == null) {
            	setPivotPoint(PivotFactory.createPivotPoint(arrowPointerDrawable, PivotPosition.PIVOT_CENTER));
            } else {
            	setPivotPoint(arrowPointerPivotPoint);
            }
        } else {
            setDrawable(roundPointerDrawable);
            
            if (roundPointerPivotPoint == null) {
            	setPivotPoint(PivotFactory.createPivotPoint(roundPointerDrawable, PivotPosition.PIVOT_CENTER));
            } else {
            	setPivotPoint(roundPointerPivotPoint);
            }
        }

        recalculateBounds();
    }


    @Override
    protected void recalculateBounds()
    {
        super.recalculateBounds();

        float r = getAccuracyDiameter() / 2.0f;

        if (accuracyDrawable != null) {

            Rect bounds = super.getBounds();

            float diameter = getAccuracyDiameter();
            accuracyRadius = diameter / 2.0f;
            accuracyDrawable.getShape().resize(diameter, diameter);

            centerX = posScaled.x + bounds.width() / 2.0 - pivotPoint.x;
            centerY = posScaled.y + bounds.height() / 2.0 - pivotPoint.y;

            accuracyDrawable.setBounds((int) (centerX - r),
                    (int) (centerY - r), (int) (centerX + r),
                    (int) (centerY + r));
        }
    }


    @Override
    public Rect getBounds()
    {
        Rect bounds = super.getBounds();

        if ((accuracyRadius * 2.0) >= bounds.width()) {
            return accuracyDrawable.getBounds();
        }
        
        
        if (hasBearing) {
            Rect newBounds = new Rect(bounds);
            int width = newBounds.width();
            int height = bounds.height();
            if ( width > height) {
                newBounds.bottom = newBounds.top + width;
                return newBounds;
            } else if (width > height) {
                newBounds.right = newBounds.left + height;
                return newBounds;
            }
        }

        return bounds;
    }


    @Override
    public void draw(Canvas canvas)
    {
        if (accuracy > 50.0f) {
            accuracyDrawable.draw(canvas);
            canvas.drawCircle((float) centerX, (float) centerY, accuracyRadius - 1,
                    accuracyBorderPaint);
        }

        if (!hasBearing) {
            super.draw(canvas);
        } else {
            // Rotate the drawable
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(bearing, (float) centerX, (float) centerY);
            super.draw(canvas);
            canvas.restore();
        }
    }


    private float getAccuracyDiameter()
    {
        if (pixelsInMeter == 0) {
            pixelsInMeter = calculatePixelsInMeter(context);
        }
        
        return (accuracy) * pixelsInMeter * scale * 2;
    }
}
