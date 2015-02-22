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

import com.ls.widgets.map.utils.MapCalibrationData;

/**
 * GPSConfig class allows to configure the location aware aspects of the MapWidget.
 * Do not create this class directly. Instead use MapWidget.getGPSConfig() method.
 */

public class GPSConfig
{
    private final static int DEFAULT_MIN_TIME = 1000;
    private final static int DEFAULT_MIN_DISTANCE = 10;
    
    private boolean passiveMode;
    private int minTime;
    private int minDistance;
    private MapCalibrationData calibrationData;
    
    
    public GPSConfig()
    {
        passiveMode = false;
        minTime = DEFAULT_MIN_TIME;
        minDistance = DEFAULT_MIN_DISTANCE;
    }
 
    
    public boolean getPassiveMode()
    {
        return passiveMode;
    }


    /**
     * Tells the map to not use GPS by itself, it will use "passive" location provider in order to display user's position.
     * You will need to call this method before call to setShowMyPosition.
     * @param passiveMode - true if you want the map to work in passive mode, false otherwise.
     */
    public void setPassiveMode(boolean passiveMode)
    {
        this.passiveMode = passiveMode;
    }


    /**
     * Sets the calibration data for the map. This calibration data contains the top left and bottom right coordinate of the corners of the map.
     * @param geoArea - instance of RectGeo class.
     */
    public void setGeoArea(MapCalibrationData geoArea) 
    {
        this.calibrationData = geoArea; 
    }
    
    
    /**
     * Sets the GPS sensor update time interval and distance.
     * @param minTime the minimum time interval for notifications, in milliseconds. This field is only used as a hint to conserve power, and actual time between location updates may be greater or lesser than this value.
     * @param minDistance the minimum distance interval for notifications, in meters
     */
    public void setGPSUpdateInterval(int minTime, int minDistance)
    {
       this.minTime = minTime;
       this.minDistance = minDistance;
    }
    
    
    /**
     * Returns minimal refresh time in milliseconds.
     */
    public int getMinTime()
    {
        return minTime;
    }
    
    
    /**
     * @return Returns minimal refresh distance in meters. Min distance is a distance that user should pass
     * in order to receive location update.
     */
    public int getMinDistance()
    {
        return minDistance;
    }
    
    
    /**
     * Returns calibration data for the map.
     * @return instance of MapCalibratinData.
     */
    public MapCalibrationData getCalibration()
    {
        return calibrationData;
    }
    
    /**
     * @return Returns true if config contains calibration data, false otherwise.
     */
    public boolean isMapCalibrated()
    {
        return calibrationData != null;
    }
}

