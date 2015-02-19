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

package com.ls.demo.model;

import android.location.Location;

public class MapObjectModel 
{
	private int x;
	private int y;
	private int id;
	private String caption;
	private Location location;
	
	public MapObjectModel(int id, Location location, String caption)
	{
		this.location = location;
		this.caption = caption;
		this.id = id;
	}
	
	public MapObjectModel(int id, int x, int y, String caption)
	{
		this.id = id;
		this.x = x;
		this.y = y;
		this.caption = caption;
	}

	public int getId() 
	{
		return id;
	}

	
	public int getX() 
	{
		return x;
	}


	public int getY() 
	{
		return y;
	}
	
	
	public Location getLocation()
	{
		return location;
	}
	
	
	public String getCaption()
	{
		return caption;
	}

}
