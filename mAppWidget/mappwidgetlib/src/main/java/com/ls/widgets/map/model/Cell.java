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

import java.lang.ref.SoftReference;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.View;

import com.ls.widgets.map.interfaces.TileManagerDelegate;
import com.ls.widgets.map.providers.TileProvider;

public class Cell implements Callback, TileManagerDelegate
{
	// Model
	private int zoomLevel;
	private float width;
	private float height;
	private int col;
	private int row;
	
	// View
	private Grid parent;
	private View rootView;
	private double scale;
	private float density;
	protected Drawable image;
	private SoftReference<Drawable> imageCache;
	private TileProvider tileProvider;
	private Rect imageBounds;
	private float x1;
	private float y1;
	
	// Controller
	protected boolean invalidateRect;
	private boolean isImageLoading;
	private boolean loadImage;
	private boolean isReady;
	private String TAG = Cell.class.getSimpleName();
	private boolean doNotProcessThisCell;
	

	public Cell(Grid parent, TileProvider tileProvider, int zoomLevel, int col, int row, int tileSize, double scale)
	{
		doNotProcessThisCell = false;
		this.width = tileSize;
		this.height = tileSize;
		this.col = col;
		this.row = row;
		this.scale = scale;
		this.zoomLevel = zoomLevel;
		invalidateRect = true;
		
		this.parent = parent;
		this.rootView = parent.getParentView();
		this.tileProvider = tileProvider;
		loadImage = true;
		
		x1 = col * width;
		y1 = row * height;
		
		imageBounds = new Rect();
		
		density = rootView.getResources().getDisplayMetrics().density;

		isImageLoading = false;
		isReady = false;
	}

	
	public void cacheImage(float dx, float dy)
	{
		if (image == null) {
			loadImage(dx, dy, false);
		}
	}
	
	
	public void draw (Canvas canvas, Paint paint, float dx, float dy) 
	{
		try {
			if (image == null)
				loadImage(dx, dy, true);
			
			if (image != null) {
				
				if (invalidateRect) {
					recalculateDrawableRect(dx, dy);
				}
				
				image.draw(canvas);
			}
		} catch (Exception e) {
		    e.printStackTrace();
			Log.e("Cell", "Exception during cell painting. E: " + e);
		}
	}
	
	
	public void setLoadImage(boolean loadImage)
	{
		this.loadImage = loadImage;
	}
	
	
	public void setScale(double scale)
	{
		this.scale = scale;
		invalidateRect = true;
		
		if (image != null) {
			recalculateDrawableRect(0, 0);
		}
	}
	
	
	public void freeResources()
	{
	    imageCache = new SoftReference<Drawable>(image);
		image = null;
		invalidateRect = true;
	}
	
	
	private void loadImage(final float dx, final float dy, final boolean invalidateOnDownload)
	{
		if (doNotProcessThisCell)
			return;
		
		if (isImageLoading || !loadImage)
			return;
		
		if (imageCache != null) {
            Drawable cached = imageCache.get();
            if (cached != null) {
                processNewTile(0, 0, cached, true);
                return;
            }
		}
		
		isImageLoading = true;
		
		tileProvider.requestTile(zoomLevel, col, row, this);
	}
	
	 private void processNewTile(final float dx, final float dy, Drawable drawable, boolean fromCache)
     {
         if (drawable != null) {
             image = drawable;
             
             if (!fromCache) {
            	 Rect bounds = image.getBounds();
            	 width = (float)Math.round(bounds.width());
            	 height = (float)Math.round(bounds.height());
             } else {
            	 if (image instanceof TransitionDrawable) {
            		 TransitionDrawable transDrawable = (TransitionDrawable) drawable;
            		 BitmapDrawable tile = (BitmapDrawable) transDrawable.getDrawable(1);
            		 width = tile.getBitmap().getWidth();
            		 height = tile.getBitmap().getHeight();
            	 }	 
             }

             recalculateDrawableRect(dx, dy);
             
             if (image instanceof TransitionDrawable) {
                 ((TransitionDrawable) image).setCallback(Cell.this);
                 ((TransitionDrawable) image).startTransition(150);
                 
                 rootView.postDelayed(new Runnable(){public void run(){onIsReady();}}, 150);
             }
         }
     }

	 
	private void onIsReady() 
	{
		isReady = true;	
		parent.onCellReady(this);
	}
	
	
	protected void recalculateDrawableRect(final float dx, final float dy) 
	{	
		imageBounds.left   = (int)((x1) * scale);
		imageBounds.top    = (int)((y1) * scale);
		imageBounds.right  = (int)((x1 + width) * scale);
		imageBounds.bottom = (int)((y1 + height) * scale);
		
		image.setBounds(imageBounds);	
		invalidateRect = false;
	}


	@Override
	public void invalidateDrawable(Drawable who) 
	{
		Rect bounds = who.getBounds();
		rootView.postInvalidate(bounds.left, bounds.top, bounds.right, bounds.bottom);
	}


	@Override
	public void scheduleDrawable(Drawable who, Runnable what, long when) 
	{
		rootView.scheduleDrawable(who, what, when);
	}


	@Override
	public void unscheduleDrawable(Drawable who, Runnable what) 
	{
		rootView.unscheduleDrawable(who, what);
	}


	public boolean isReady() 
	{
		return isReady;
	}


	public void setTileProvider(TileProvider tileManager) 
	{
		this.tileProvider = tileManager;	
	}


	@Override
	public void onTileReady(int zoomLevel, int col, int row, Drawable drawable) {
		processNewTile(0, 0, drawable, false);

		//rootView.postInvalidate(imageBounds.left, imageBounds.top, imageBounds.right, imageBounds.bottom);
		isImageLoading = false;
	}


	@Override
	public void onError(Exception e) {
		isImageLoading = false;
		doNotProcessThisCell = true;
		onIsReady();
	}
}
