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

public class OfflineMapUtil 
{
	private static String filesPathCache;
	private static String cachedRoot;
	
	public static String getConfigFilePath(String root)
	{
		if (root == null) {
			throw new IllegalArgumentException("root can't be null");
		}
		
		if (root.lastIndexOf('/') == -1) {
			return root + "/" + root + ".xml";  
		} else {
			return root + "/" + extractMapName(root) + ".xml";
		}
	}
	
	
	public static String getFilesPath(String root)
	{
		if (root == null) {
			throw new IllegalArgumentException("root can't be null");
		}
		
		if (filesPathCache != null && cachedRoot.hashCode() == root.hashCode())
			return filesPathCache;
		
		if (root.lastIndexOf('/') == -1) { 
			filesPathCache = root + "/" + root + "_files/"; 	
			cachedRoot = root;
			return filesPathCache;
		} else {
			int indexOfSlash = root.lastIndexOf('/');
			
			if (indexOfSlash != -1) {
				filesPathCache = root + "/" + extractMapName(root) + "_files/";
				cachedRoot = root;
			}
			
			return filesPathCache;
		}
	}
	
	
	public static int getMaxZoomLevel(int imageWidth, int imageHeight)
	{
		int biggerSize = imageWidth > imageHeight ? imageWidth:imageHeight;
		
		if (biggerSize == 0) {
			return 0;
		}
		
		return (int) Math.ceil(Math.log((double)biggerSize) / Math.log(2.0));
	}
	
	
	public static int getScaledImageSize(int maxZoomLevel, int currZoomLevel, int size)
	{
		double scale = 1.0 / Math.pow(2, maxZoomLevel - currZoomLevel);
			
		return (int)Math.ceil(size * scale);
	}
	
	
	private static String extractMapName(String root)
	{
		int indexOfSlash = root.lastIndexOf('/');
		
		if (indexOfSlash != -1) {
			
			return root.substring(indexOfSlash+1); 
		}
		
		return root;
	}
	
}
