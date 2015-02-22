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

package com.ls.widgets.map;

import android.test.AndroidTestCase;

import com.ls.widgets.map.utils.OfflineMapUtil;

public class OfflineMapUtilTest extends AndroidTestCase
{

	private float imageSizesAtZoomLevel[] = {1,2,3,6,12,24,47,94,188,375,750,1500,3000,6000};
	// Zoom Level                   0  1  2  3  4   5   6   7    8    9    10    11    12    13
	private int zoomLevelStart[] = {1, 2, 3, 5, 9,  17, 33, 65,  129, 257, 513,  1025, 2049, 4097};
	private int zoomLevelEnd[]   = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};

	public void testGetScaledImageSize()
	{
		for (int i=0; i<imageSizesAtZoomLevel.length; ++i) {
			float imageSize = OfflineMapUtil.getScaledImageSize(imageSizesAtZoomLevel.length-1, i, 6000);
			
			assertEquals(imageSizesAtZoomLevel[i], imageSize);
		}
	}
	
	
	public void testGetMaxZoomLevel()
	{
		for (int i = 0; i<zoomLevelStart.length; ++i) {
			for (int zls=zoomLevelStart[i]; zls <= zoomLevelEnd[i]; ++ zls) {
				assertEquals(i,  OfflineMapUtil.getMaxZoomLevel(zls,zls));
			}
		}
			
		for (int i = 0; i<zoomLevelStart.length; ++i) {
			for (int zls=zoomLevelStart[i]; zls <= zoomLevelEnd[i]; ++ zls) {
				assertEquals(i,  OfflineMapUtil.getMaxZoomLevel(zls,1));
			}
		}
		
		for (int i = 0; i<zoomLevelStart.length; ++i) {
			for (int zls=zoomLevelStart[i]; zls <= zoomLevelEnd[i]; ++ zls) {
				assertEquals(i,  OfflineMapUtil.getMaxZoomLevel(1,zls));
			}
		}
		
	}

}
