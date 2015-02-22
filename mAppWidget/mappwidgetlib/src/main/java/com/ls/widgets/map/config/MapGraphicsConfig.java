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

/**
 * Allows to configure the appearance of the map aspects.
 */

public class MapGraphicsConfig 
{
    public static final int DEFAULT_ACCURACY_AREA_COLOR = 0x331767e9;
    public static final int DEFAULT_ACCURACY_AREA_BORDER_COLOR = 0xFF1767e9;
    
	private int dotPointerDrawableId;
	private int arrowPointerDrawableId;
	
	private int accuracyAreaColor;
	private int accuracyAreaBorderColor;
	
	
	public MapGraphicsConfig()
	{
		dotPointerDrawableId = -1;
		arrowPointerDrawableId = -1;
		accuracyAreaColor = DEFAULT_ACCURACY_AREA_COLOR;
		accuracyAreaBorderColor = DEFAULT_ACCURACY_AREA_BORDER_COLOR;
	}

	
	public int getDotPointerDrawableId() 
	{
		return dotPointerDrawableId;
	}

	/**
	 * Configures the location pointer look when no bearing is available.
	 * @param dotPointerDrawableId - id of the drawable resource.
	 */
	public void setDotPointerDrawableId(int dotPointerDrawableId) 
	{
		this.dotPointerDrawableId = dotPointerDrawableId;
	}

	
	public int getArrowPointerDrawableId() 
	{
		return arrowPointerDrawableId;
	}

	
	/**
	 * Configures the location pointer look when bearing is available.
	 * @param dotPointerDrawableId - id of the drawable resource.
	 */
	public void setArrowPointerDrawableId(int arrowPointerDrawableId) 
	{
		this.arrowPointerDrawableId = arrowPointerDrawableId;
	}

	
	public int getAccuracyAreaColor() 
	{
		return accuracyAreaColor;
	}

	
	/**
	 * Configures the accuracy area color of location pointer. Use this template to set the color: 0xAARRGGBB.
	 * AA - alpha channel, RR - red component, GG - green component, BB - blue component.
	 * @param accuracyAreaColor - color.
	 */
	public void setAccuracyAreaColor(int accuracyAreaColor) 
	{
		this.accuracyAreaColor = accuracyAreaColor;
	}

	
	public int getAccuracyAreaBorderColor() 
	{
		return accuracyAreaBorderColor;
	}


	/**
	 * Configures the accuracy area border color.
	 * @param accuracyAreaBorderColor - color.
	 */
	public void setAccuracyAreaBorderColor(int accuracyAreaBorderColor) 
	{
		this.accuracyAreaBorderColor = accuracyAreaBorderColor;
	}
}
