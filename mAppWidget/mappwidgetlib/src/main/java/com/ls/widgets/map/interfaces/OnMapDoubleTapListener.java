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

package com.ls.widgets.map.interfaces;

import com.ls.widgets.map.MapWidget;
import com.ls.widgets.map.events.MapTouchedEvent;

/**
 * <h1>Interface overview</h1><p>
 * Used for receiving notifications when MapWidget view is double tapped.
 */
public interface OnMapDoubleTapListener 
{
	/**
	 * Will be called if user double taps the map widget.
	 * @param v - map widget that was double tapped.
	 * @param event - instance of {@link MapTouchedEvent}.
	 * @return true - if you want to intercept this event and provide your own implementation.<br>
	 * false - if you want to leave the default map widget behavior.
	 */
	public boolean onDoubleTap(MapWidget v, MapTouchedEvent event);
}
