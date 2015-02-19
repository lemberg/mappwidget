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

package com.ls.widgets.map.commands;

import com.ls.widgets.map.config.OfflineMapConfig;

public abstract class MapCommand
	implements Runnable
{
	private OfflineMapConfig config;
	private MapCommandDelegate delegate;
	
	public MapCommand(OfflineMapConfig config, MapCommandDelegate delegate)
	{
		this.config = config;
		this.delegate = delegate;
	}

	public OfflineMapConfig getConfig() {
		return config;
	}
	
	public void onSuccess(Object data)
	{
		if (delegate != null)
			delegate.onSuccess(data);
	}
	
	public void onError(Exception e)
	{
		e.printStackTrace();
		if (delegate != null)
			delegate.onError(e);
	}
}

