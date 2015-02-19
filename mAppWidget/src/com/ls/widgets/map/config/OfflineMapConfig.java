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

package com.ls.widgets.map.config;

import android.graphics.Rect;

public class OfflineMapConfig 
{
    private String rootMapFolder;
    
	private int imageWidth;
	private int imageHeight;
	
	private Rect imageBounds;
	private int tileSize;
	private int overlap;
	private String imageFormat;
	
	private boolean flingEnabled;
	private boolean mapCenteringEnabled;
	private boolean pinchZoomEnabled;
	private boolean zoomBtnsVisible;
	
	private int trackballScrollStepX;
	private int trackballScrollStepY;
	
	private int minZoomLevelLimit;
	private int maxZoomLevelLimit;
	
	private int touchAreaSize;
	
	private boolean softwareZoomEnabled;
	
	private GPSConfig gpsConfig;
	private MapGraphicsConfig mapGraphics;
	
	
	public OfflineMapConfig(String rootMapFolder, int imageWidth, int imageHeight, int tileSize, int overlap, String imageFormat)
	{
		// Default values
		this.trackballScrollStepX = this.trackballScrollStepY = 64;
		this.minZoomLevelLimit = 0;
		this.maxZoomLevelLimit = 0;
		this.touchAreaSize = 5;
		this.softwareZoomEnabled = true;
		this.flingEnabled = false;
		this.zoomBtnsVisible = true;
		this.mapCenteringEnabled = true;
		
		// Other values
		this.rootMapFolder = rootMapFolder;
		this.imageFormat = imageFormat;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.imageBounds = new Rect(0,0, imageWidth, imageHeight);
		this.tileSize = tileSize;
		this.overlap = overlap;
		
		gpsConfig = new GPSConfig();
		mapGraphics = new MapGraphicsConfig();	
	}
	
	
	public OfflineMapConfig(OfflineMapConfig config)
	{
		this.rootMapFolder = config.rootMapFolder;
		this.imageWidth = config.imageWidth;
		this.imageHeight = config.imageHeight;
		this.tileSize = config.tileSize;
		this.overlap = config.overlap;
		this.imageFormat = config.imageFormat;
		this.flingEnabled = config.flingEnabled;
		this.mapCenteringEnabled = config.mapCenteringEnabled;
		this.pinchZoomEnabled = config.pinchZoomEnabled;
		this.zoomBtnsVisible = config.zoomBtnsVisible;
		this.trackballScrollStepX = config.trackballScrollStepX;
		this.trackballScrollStepY = config.trackballScrollStepY;
		this.minZoomLevelLimit = config.minZoomLevelLimit;
		this.maxZoomLevelLimit = config.maxZoomLevelLimit;
		this.softwareZoomEnabled = config.softwareZoomEnabled;
		this.touchAreaSize = config.touchAreaSize;
	}
		
	/**
	 * Returns original map image width in pixels 
	 * @return
	 */
	public int getImageWidth() 
	{
		return imageWidth;
	}


	/**
	 * Returns original map image height in pixels
	 * @return
	 */
	public int getImageHeight() 
	{
		return imageHeight;
	}
	
	
	public Rect getImageRect() 
	{
		return imageBounds;
	}


	/**
	 * Returns size of a tile in pixels.
	 * @return
	 */
	public int getTileSize() 
	{
		return tileSize;
	}


	public int getOverlap() 
	{
		return overlap;
	}


	public String getImageFormat() 
	{
		return imageFormat;
	}


	public boolean isFlingEnabled() 
	{
		return flingEnabled;
	}

	
	/**
	 * Controls inertial scrolling.
	 * @param flingEnabled true to enable fling, false otherwise.
	 */
	public void setFlingEnabled(boolean flingEnabled) 
	{
		this.flingEnabled = flingEnabled;
	}


	public boolean isMapCenteringEnabled()
	{
		return this.mapCenteringEnabled;
	}
	
	
	/**
	 * Controls the ability to center the map.
	 * @param enabled - if set to true map will center itself if it is smaller than screen.
	 */
	public void setMapCenteringEnabled(boolean enabled)
	{
		this.mapCenteringEnabled = enabled;
	}
	
	
	public boolean isPinchZoomEnabled() 
	{
		return pinchZoomEnabled;
	}


	/**
	 * Controls pinch zoom gesture.
	 * @param pinchZoomEnabled - true to enable pinch zoom gesture, false otherwise.
	 */
	public void setPinchZoomEnabled(boolean pinchZoomEnabled) 
	{
		this.pinchZoomEnabled = pinchZoomEnabled;
	}


	public boolean isZoomBtnsVisible() 
	{
		return zoomBtnsVisible;
	}


	/**
	 * Controls standard zoom buttons visibility.
	 * @param zoomBtnsVisible - true to make standard zoom buttons visible, false otherwise.
	 */
	public void setZoomBtnsVisible(boolean zoomBtnsVisible) 
	{
		this.zoomBtnsVisible = zoomBtnsVisible;
	}


	public int getTrackballScrollStepX() 
	{
		return trackballScrollStepX;
	}


	/**
	 * Set's track ball scroll step by X axis.
	 * @param trackballScrollStepX scroll step in pixels.
	 * @throws IllegalArgumentException if trackballScrollStepX < 0
	 */
	public void setTrackballScrollStepX(int trackballScrollStepX) 
	{
		if (trackballScrollStepX < 0) {
			throw new IllegalArgumentException();
		}
		
		this.trackballScrollStepX = trackballScrollStepX;
	}


	public int getTrackballScrollStepY() 
	{
		return trackballScrollStepY;
	}

	
	/**
	 * Set's track ball scroll step by Y axis.
	 * @param trackballScrollStepY - scroll step in pixels.
	 * @throws IllegalArgumentException if trackballScrollStepY < 0
	 */
	public void setTrackballScrollStepY(int trackballScrollStepY) 
	{
		if (trackballScrollStepY < 0) {
			throw new IllegalArgumentException();
		}
		
		this.trackballScrollStepY = trackballScrollStepY;
	}


	public int getMinZoomLevelLimit() 
	{
		return minZoomLevelLimit;
	}


	/**
	 * Sets minimal zoom level the user can zoom out to.
	 * @param minZoomLevelLimit - represents zoom level number.
	 * @throws IllegalArgumentException if minZoomLevelLimit < 0
	 */
	public void setMinZoomLevelLimit(int minZoomLevelLimit) 
	{
		if (minZoomLevelLimit < 0) {
			throw new IllegalArgumentException();
		}
		
		this.minZoomLevelLimit = minZoomLevelLimit;
	}


	public int getMaxZoomLevelLimit() 
	{
		return maxZoomLevelLimit;
	}


	/**
	 * Sets max zoom level the user can zoom in to.
	 * @param maxZoomLevelLimit - zoom level number.
	 * @throws IllegalArgumentException if maxZoomLevelLimit < 0
	 */
	public void setMaxZoomLevelLimit(int maxZoomLevelLimit) 
	{
		if (maxZoomLevelLimit < 0) {
			throw new IllegalArgumentException();
		}
		
		this.maxZoomLevelLimit = maxZoomLevelLimit;
	}

	
	public String getMapRootPath()
	{
		return rootMapFolder;
	}

	
	public boolean isSoftwareZoomEnabled() 
	{
		return softwareZoomEnabled;
	}

	
	/**
	 * Controls the ability to use software zoom if there is no zoom levels left during zooming in.  
	 * @param softwareZoomEnabled - Set true if you want to use software zoom, false otherwise.
	 */
	public void setSoftwareZoomEnabled(boolean softwareZoomEnabled) 
	{
		this.softwareZoomEnabled = softwareZoomEnabled;
	}


	public int getTouchAreaSize()
	{
		return touchAreaSize;
	}


	/**
	 * Sets touch area size
	 * @param touchAreaSize - area size in pixels. Used when detecting objects that were hit by the user with a finger.
	 * @throws IllegalArgumentException when touchAreaSize <= 0
	 */
	public void setTouchAreaSize(int touchAreaSize) 
	{
		if (touchAreaSize <= 0)
			throw new IllegalArgumentException();
		
		this.touchAreaSize = touchAreaSize;
	}


	/**
	 * You can use {@link GPSConfig} in order to control GPS sensor settings.
	 * Please note, that you need to configure the GPS sensor before calling {@code MapWidget.setShowMyPosition(true);} 
	 * @return instance of {@link GPSConfig} class.
	 */
    public GPSConfig getGpsConfig()
    {
        return gpsConfig;
    }
    
    
    /**
     * You can use MapGraphicsConfig in order to configure the look of the position marker.
     * @return instance of {@link MapGraphicsConfig}
     */
    public MapGraphicsConfig getGraphicsConfig()
    {
    	return mapGraphics;
    }
}
