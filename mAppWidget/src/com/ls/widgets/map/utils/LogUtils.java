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

package com.ls.widgets.map.utils;


public class LogUtils {
	
	public static final String APP = "MAP WIDGET";
	
	public static void logHeap(Class clazz) 
	{
//	    Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
//	    Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
//	    Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
//	    DecimalFormat df = new DecimalFormat();
//	    df.setMaximumFractionDigits(2);
//	    df.setMinimumFractionDigits(2);
//
//	    Log.d(APP, "debug. =================================");
//	    Log.d(APP, "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free) in [" + clazz.getName().replaceAll("com.myapp.android.","") + "]");
//	    Log.d(APP, "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
//	    System.gc();
//	    System.gc();

	    // don't need to add the following lines, it's just an app specific handling in my app        
//	    if (allocated>=(new Double(Runtime.getRuntime().maxMemory())/new Double((1048576))-MEMORY_BUFFER_LIMIT_FOR_RESTART)) {
//	        android.os.Process.killProcess(android.os.Process.myPid());
//	    }
	}
	
}
