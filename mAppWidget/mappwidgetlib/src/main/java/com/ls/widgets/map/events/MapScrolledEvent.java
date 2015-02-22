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

package com.ls.widgets.map.events;

/**
 * Represents scroll event. Event will be fired when map is scrolled.
 */
public class MapScrolledEvent 
{
	private int dx;
	private int dy;
	boolean byUser;
	
	public MapScrolledEvent(int originX, int originY)
	{
		this.dx = originX;
		this.dy = originY;
	}
	
	public void setData(int dx, int dy, boolean byUser)
	{
	    this.dx = dx;
	    this.dy = dy;
	    this.byUser = byUser;
	}
	
	/**
	 * @return
	 * Returns the number of pixels the map was scrolled from it's last position by X axis.
	 */
	public int getDX() 
	{
		return dx;
	}
	
	/**
	 * @return
	 * Returns the number of pixels the map was scrolled from it's last position by Y axis.
	 */	
	public int getDY() 
	{
		return dy;
	}
	
	/**
	 * @returns Returns true if map scroll was caused by the user, false otherwise.
	 */
	public boolean isByUser()
	{
	    return byUser;
	}
}
