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

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.ls.widgets.map.config.OfflineMapConfig;
import com.ls.widgets.map.interfaces.OnGridReadyListener;
import com.ls.widgets.map.providers.TileProvider;
import com.ls.widgets.map.utils.OfflineMapUtil;

public class Grid
{	
	private static final String TAG = "Grid";

	//Tile manager is responsible for managing image tiles and free memory
	private TileProvider tileProvider;
	private OnGridReadyListener onReadyListener;
	
	//View where drawing will be occurred
	private View parentView;
	
	//Model of the grid.
	private ArrayList<Cell>cells;
	
	//Amount of tile columns
	private int colCount;
	//Amount of tile rows
	private int rowCount;
	//Size of a square tile in pixels
	private int tileSize;	
	//Zoom level 
	private int zoomLevel;
	
	//Scale
	private double softScale;
	
	//Original image (max zoom) height in pixels
	private int imageHeight;
	//Original image (max zoom) width in pixels
	private int imageWidth;
	//Maximum possible zoom level
	private int maxZoomLevel;

	
	// For caching purpose only
	private boolean loadTiles;
	private Rect cachedDrawRect;
	private int screenXCapacity;
	private int screenYCapacity;
	private Point firstVisibleCell;
	private Point lastVisibleCell;
	private Rect gridWindow;
	private Rect gridWindowBigger;
	
	private int cellsToDrawCount;
	private int cellsReadyCount;

	private double intScale;

	private double scale;

	public Grid(View parent, OfflineMapConfig config, TileProvider tileProvider, int initZoomLevel)
	{
		this.zoomLevel = initZoomLevel;
		
		this.imageWidth = config.getImageWidth();
		this.imageHeight = config.getImageHeight();
		this.maxZoomLevel = OfflineMapUtil.getMaxZoomLevel(imageWidth, imageHeight);
		this.softScale = 1.0;
		this.intScale = 1.0;
		this.scale = 1.0;

		this.tileSize = config.getTileSize();
		
		this.parentView = parent;
		this.tileProvider = tileProvider;
		
		loadTiles = true;

		rowCount = getRowCount();
		colCount = getColCount();
		
		calcCellsInScreen();
		
		// Creating the two points here and then reusing them in onDraw() in order to
		// avoid of extensive use of garbage collector
		firstVisibleCell = new Point(0,0);
		lastVisibleCell = new Point(0,0);
		gridWindow = new Rect(0,0,0,0);
		gridWindowBigger = new Rect(0,0,0,0);

		initGrid(initZoomLevel, tileSize);
	}


	private void calcCellsInScreen() {
		screenXCapacity = (int)(parentView.getWidth() / ((float)tileSize * scale)) / 2;
		screenYCapacity = (int)(parentView.getHeight() / ((float)tileSize * scale)) / 2;
	}

	
	private void initGrid(int zoomLevel, int tileSize) 
	{	
		if (Looper.myLooper() == null) {
			throw new IllegalThreadStateException("Should be called from UI thread");
		}
		
		int size = colCount*rowCount;
		cells = new ArrayList<Cell>(size);
		
		for (int i=0; i<size; ++i) {
			int currRow = i/colCount;
			Cell cell = new Cell(this, tileProvider, zoomLevel, i - currRow * colCount, currRow, tileSize, scale);
		
			cells.add(cell);
		}
		
	}
	
	
	public void setTileProvider(TileProvider tileManager)
	{
		for (Cell cell:cells) {
			cell.setTileProvider(tileManager);
		}
	}
	
	
	public void draw(Canvas canvas, Paint paint, Rect drawingRect)
	{
		cachedDrawRect = drawingRect;
		
		if (cells == null)
			return;
		
		gridWindow = getGridWindow(/*in*/ drawingRect);
		gridWindowBigger = getGridWindow(/*in*/ drawingRect);//getRridWindowBigger(gridWindow);
		
//		Log.d(TAG, "Paint window: " + gridWindow.toShortString() + "Cache window: " + gridWindowBigger.toShortString()+
//				" Drawing rect: " + drawingRect.toShortString() +
//				"Cols: " + colCount + " Rows: " + rowCount);
//		
		cellsToDrawCount = 0;
		cellsReadyCount = 0;
		

		for (int row=gridWindowBigger.top; row<=gridWindowBigger.bottom; ++row) {
			for (int col=gridWindowBigger.left; col<=gridWindowBigger.right; ++col) {
				
				Cell cell = cells.get((int) (col + row*colCount));
			
				if (cell != null) {
					if (col >= gridWindow.left && col <= gridWindow.right &&
							row >= gridWindow.top && row <= gridWindow.bottom) {
						cell.draw(canvas, paint, 0, 0);
						if (!cell.isReady()) {
							cellsToDrawCount += 1;
						}
					} else {
						cell.cacheImage(0, 0);
					}
				}
			}
		}
	}
	
	
	private Rect getRridWindowBigger(Rect gridWindow) 
	{
		
		gridWindowBigger.left = gridWindow.left - screenXCapacity;
		
		if (gridWindowBigger.left < 0)
			gridWindowBigger.left = 0;
		
		gridWindowBigger.right = gridWindow.right + screenXCapacity;
		
		if (gridWindowBigger.right >= colCount)
			gridWindowBigger.right = colCount - 1;
		
		gridWindowBigger.top = gridWindow.top - screenYCapacity;
		
		if (gridWindowBigger.top < 0)
			gridWindowBigger.top = 0;

		gridWindowBigger.bottom = gridWindow.bottom + screenYCapacity;
		
		if (gridWindowBigger.bottom >= rowCount)
			gridWindowBigger.bottom = rowCount - 1;
		
		return gridWindowBigger;
	}


	public int getHeight()
	{
		return (int)Math.ceil(imageHeight * getScale());
	}
	
	
	public int getOriginalHeight() 
	{
		return imageHeight;
	}
	
	
	public int getOriginalWidth() 
	{
		return imageWidth;
	}

	public double getScale()
	{
		if (maxZoomLevel == zoomLevel)
			return 1.0f * scale;
		
		return (1.0/(Math.pow(2,maxZoomLevel - zoomLevel)))*scale;
	}

	
	public int getWidth()
	{
		return (int) Math.ceil(imageWidth * getScale());
	}
	
	
	public int getZoomLevel() 
	{
		return zoomLevel;
	}
	
	
	public int getMaxZoomLevel() 
	{
		return maxZoomLevel;
	}
	
	
	public View getParentView()
	{
		return parentView;
	}
	
	
	public void setLoadTiles(boolean loadTiles)
	{
		applyLoadTileState(loadTiles);
	}
	
	
	public boolean isLoadTiles()
	{
		return this.loadTiles;
	}
	
	
	public int getMinZoomLevel() 
	{
		return 0;
	}
	
	
	public void setSoftScale(float newScale)
	{
		if (newScale == 0.0)
			throw new IllegalArgumentException();
		
		this.softScale = newScale;
		
		this.scale = softScale * intScale;
		
		applyScale(this.scale);
	}
	
	
	public void setOnReadyListener(OnGridReadyListener listener)
	{
		//tileManager.setOnReadyListener(listener);
		this.onReadyListener = listener;
	}

	
	protected int getColCount()
	{
		return (int)(Math.ceil(((float)OfflineMapUtil.getScaledImageSize(maxZoomLevel, zoomLevel, imageWidth) / (float)tileSize)));
	}


	protected int getRowCount()
	{
		return (int)((float)Math.ceil(((float)OfflineMapUtil.getScaledImageSize(maxZoomLevel, zoomLevel, imageHeight) / (float)tileSize)));
	}


	public double getSoftScale() 
	{
		return softScale;
	}
	

	private Point getBottomRightVisibleCell(Rect drawingRect, Point point) 
	{
		if (point == null || drawingRect == null)
			throw new IllegalArgumentException();
		
		Point topLeft = getTopLeftVisibleCell(drawingRect, point);

		int cols = (int) Math.ceil(((float)drawingRect.width() / ((float)tileSize * scale))) + 2;
		int rows = (int) Math.ceil(((float)drawingRect.height() / ((float)tileSize * scale))) + 1;
		
		point.x = topLeft.x + cols;
		point.y = topLeft.y + rows;
		
		if (point.x >= colCount)
			point.x = colCount-1;
		
		if (point.y >= rowCount)
			point.y = rowCount-1;
		
		return point;
	}
	
	
	private Point getTopLeftVisibleCell(Rect drawingRect, Point point) 
	{
		if (point == null || drawingRect == null)
			throw new IllegalArgumentException();
		
		point.x = (int) Math.floor(((float)drawingRect.left / ((float)tileSize * scale))) - 1;
		point.y = (int) Math.floor(((float)drawingRect.top / ((float)tileSize * scale))) - 1;
		
		if (point.x < 0) {
			point.x = 0;
		}
		
		if (point.y < 0) {
			point.y = 0;
		}

		return point;
	}
	
	
	private synchronized Rect getGridWindow(Rect drawRect)
	{
		if (drawRect == null || firstVisibleCell == null || lastVisibleCell == null) 
			throw new IllegalArgumentException();
		
		getTopLeftVisibleCell(drawRect, firstVisibleCell);
		getBottomRightVisibleCell(drawRect, lastVisibleCell);

//		window.top =  firstVisibleCell.y - screenYCapacity;
//		if (window.top< 0) window.top= 0;
//		
//		window.left = firstVisibleCell.x - screenYCapacity;
//		if (window.left < 0) window.left = 0;
//		
//		window.bottom = lastVisibleCell.y + screenYCapacity;
//		if (window.bottom > rowCount) window.bottom = rowCount;
//		
//		window.right = lastVisibleCell.x + screenYCapacity;
//		if (window.right > colCount) window.right = colCount;
		
//		return window;
		gridWindow.top =  firstVisibleCell.y;
		gridWindow.left = firstVisibleCell.x;
		gridWindow.bottom = lastVisibleCell.y;
		gridWindow.right = lastVisibleCell.x;
		
		return gridWindow;
	}
	
	
	private void applyScale(double scale)
	{
		int size = cells.size();
		calcCellsInScreen();

		for (int i=0; i<size; ++i) {
			Cell cell = cells.get(i);
			cell.setScale(scale);
		}
	}
	
	
	private void applyLoadTileState(boolean allowTileLoad)
	{
		int size = cells.size();
		
		synchronized (cells) {
			for (int i=0; i<size; ++i) {
				Cell cell = cells.get(i);
				cell.setLoadImage(allowTileLoad);
			}		
		}
	}

	
	public void freeResources()
	{
		if (cachedDrawRect == null)
			return;
				
		gridWindow = getGridWindow(cachedDrawRect);
		gridWindowBigger = getRridWindowBigger(gridWindow);
		
		for (int row=0; row<rowCount; ++row) {
			for (int col=0; col<colCount; ++col) {
				if (!(col >= gridWindowBigger.left && col <=gridWindowBigger.right && row >= gridWindowBigger.top && row <= gridWindowBigger.bottom)) {
					Cell cell = cells.get((int) (col + row*colCount));
			
					if (cell != null) {
						cell.freeResources();
					}
				}
			}
		}
	}
	
	
	void onCellReady(Cell cell)
	{
		cellsReadyCount += 1;
		
		if (cellsReadyCount == cellsToDrawCount) {
			if (onReadyListener != null) {
				Log.d(TAG, "OnReady!");
				onReadyListener.onReady();
			}
		}
	}


	public void setInternalScale(float newScale) 
	{
		if (newScale == 0.0)
			throw new IllegalArgumentException();
		
		intScale = newScale;
		
		this.scale = softScale * intScale;
		
		applyScale(this.scale);
	}


	public double getIntScale() {
		return intScale;
	}

//
//	public void setInternalScale(float newScale) {
//		softScale *= newScale;
//	}
}
