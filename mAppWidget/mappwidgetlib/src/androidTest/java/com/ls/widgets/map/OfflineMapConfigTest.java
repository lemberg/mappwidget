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

import com.ls.widgets.map.config.OfflineMapConfig;

public class OfflineMapConfigTest extends AndroidTestCase {

	private static final int TEST_IMAGE_WIDTH = 1024;
	private static final int TEST_IMAGE_HEIGHT = 768;
	private static final int TEST_TILE_SIZE = 256;
	private static final int TEST_OVERLAP = 1;
	private static final String TEST_IMAGE_FORMAT = "png";
	private static final String MAP_ROOT_PATH = "map";
	
	private OfflineMapConfig config;
	
	protected void setUp() throws Exception 
	{
		config = new OfflineMapConfig(MAP_ROOT_PATH, TEST_IMAGE_WIDTH, TEST_IMAGE_HEIGHT, TEST_TILE_SIZE, TEST_OVERLAP, TEST_IMAGE_FORMAT);
		super.setUp();

	}

	protected void tearDown() throws Exception 
	{
		super.tearDown();
	}
	
	
	public void testDefaults()
	{
		assertEquals(0, config.getMaxZoomLevelLimit());
		assertEquals(0, config.getMinZoomLevelLimit());
		assertEquals(5, config.getTouchAreaSize());
		assertEquals(64, config.getTrackballScrollStepX());
		assertEquals(64, config.getTrackballScrollStepY());
		assertEquals(false, config.isPinchZoomEnabled());
		assertEquals(false, config.isFlingEnabled());
		assertEquals(true, config.isSoftwareZoomEnabled());
		assertEquals(true, config.isZoomBtnsVisible());
	}
	
	
	public void testConstructor()
	{
		assertEquals(TEST_IMAGE_WIDTH, config.getImageWidth());
		assertEquals(TEST_IMAGE_HEIGHT, config.getImageHeight());
		assertEquals(TEST_TILE_SIZE, config.getTileSize());
		assertEquals(TEST_OVERLAP, config.getOverlap());
		assertEquals(TEST_IMAGE_FORMAT, config.getImageFormat());
	}
	
	
	public void testCopyConstructor()
	{
		// If this exception is thrown, please, update this test in order to test new members or
		// throw the tests for removed fields out.
		assertEquals(19, config.getClass().getDeclaredFields().length);
		
		config.setFlingEnabled(true);
		config.setMaxZoomLevelLimit(15);
		config.setMinZoomLevelLimit(10);
		config.setPinchZoomEnabled(true);
		config.setMapCenteringEnabled(true);
		config.setSoftwareZoomEnabled(false);
		config.setTouchAreaSize(10);
		config.setTrackballScrollStepX(25);
		config.setTrackballScrollStepY(30);
		config.setZoomBtnsVisible(false);
		
		OfflineMapConfig testConfig = new OfflineMapConfig(config);
		
		assertEquals(MAP_ROOT_PATH, testConfig.getMapRootPath());
		assertEquals(true, testConfig.isFlingEnabled());
		assertEquals(15, testConfig.getMaxZoomLevelLimit());
		assertEquals(10, testConfig.getMinZoomLevelLimit());
		assertEquals(true, testConfig.isPinchZoomEnabled());
		assertEquals(true, testConfig.isMapCenteringEnabled());
		assertEquals(false, testConfig.isSoftwareZoomEnabled());
		assertEquals(10, testConfig.getTouchAreaSize());
		assertEquals(25, testConfig.getTrackballScrollStepX());
		assertEquals(30, testConfig.getTrackballScrollStepY());
		assertEquals(false, testConfig.isZoomBtnsVisible());
		
		assertNotSame(config, testConfig);
	}
	
	
	public void testFlingEnabledOption()
	{
		config.setFlingEnabled(true);
		assertEquals(true, config.isFlingEnabled());
		
		config.setFlingEnabled(false);
		assertEquals(false, config.isFlingEnabled());
	}
	
	
	public void testMaxZoomLevelLimitOption()
	{
		for (int i=-1000; i<0; ++i) {
			boolean result = false;
			
			try {
				config.setMaxZoomLevelLimit(i);
			} catch (IllegalArgumentException e) {
				result = true;
			}
			
			assertTrue("Exception was not thrown for i=" + i, result);
		}
		
		for (int i=0; i<1000; ++i) {
			config.setMaxZoomLevelLimit(i);
			assertEquals(i, config.getMaxZoomLevelLimit());
		}
	}
	
	
	public void testMinZoomLevelLimitOption()
	{
		for (int i=-1000; i<0; ++i) {
			boolean result = false;
			
			try {
				config.setMinZoomLevelLimit(i);
			} catch (IllegalArgumentException e) {
				result = true;
			}
			
			assertTrue("Exception was not thrown for i=" + i, result);
		}
		
		for (int i=0; i<1000; ++i) {
			config.setMinZoomLevelLimit(i);
			assertEquals(i, config.getMinZoomLevelLimit());
		}
	}
	
	
	public void testPinchZoomEnabledOption()
	{
		config.setPinchZoomEnabled(true);
		assertEquals(true, config.isPinchZoomEnabled());
		
		config.setPinchZoomEnabled(false);
		assertEquals(false, config.isPinchZoomEnabled());
	}
	
	
	public void testSoftwareZoomEnabledOption()
	{
		config.setSoftwareZoomEnabled(true);
		assertEquals(true, config.isSoftwareZoomEnabled());
		
		config.setSoftwareZoomEnabled(false);
		assertEquals(false, config.isSoftwareZoomEnabled());
	}
	
	
	public void testTouchAreaSizeOption()
	{
		for (int i=-1000; i<=0; ++i) {
			boolean result = false;
			
			try {
				config.setTouchAreaSize(i);
			} catch (IllegalArgumentException e) {
				result = true;
			}
			
			assertTrue("Exception was not thrown for i=" + i, result);
		}
		
		for (int i=1; i<1000; ++i) {
			config.setTouchAreaSize(i);
			assertEquals(i, config.getTouchAreaSize());
		}
	}
	
	
	public void testTrackballScrollStepXOption()
	{
		for (int i=-1000; i< 0; ++i) {
			boolean result = false;
			
			try {
				config.setTrackballScrollStepX(i);
			} catch (IllegalArgumentException e) {
				result = true;
			}
			
			assertTrue("Exception was not thrown for i=" + i, result);
		}
		
		for (int i=0; i<1000; ++i) {
			config.setTrackballScrollStepX(i);
			assertEquals(i, config.getTrackballScrollStepX());
		}
	}
	
	
	public void testTrackballScrollStepYOption()
	{
		for (int i=-1000; i< 0; ++i) {
			boolean result = false;
			
			try {
				config.setTrackballScrollStepY(i);
			} catch (IllegalArgumentException e) {
				result = true;
			}
			
			assertTrue("Exception was not thrown for i=" + i, result);
		}
		
		for (int i=0; i<1000; ++i) {
			config.setTrackballScrollStepX(i);
			assertEquals(i, config.getTrackballScrollStepX());
		}
	}
	
	
	public void testZoomBtnsVisibleOption()
	{
		config.setZoomBtnsVisible(true);
		assertEquals(true, config.isZoomBtnsVisible());
		
		config.setZoomBtnsVisible(false);
		assertEquals(false, config.isZoomBtnsVisible());
	}
}
