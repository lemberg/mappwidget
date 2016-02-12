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

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.test.AndroidTestCase;
import android.util.Log;
import android.view.animation.Animation.AnimationListener;

import com.ls.widgets.map.config.OfflineMapConfig;
import com.ls.widgets.map.interfaces.Layer;
import com.ls.widgets.map.interfaces.MapEventsListener;
import com.ls.widgets.map.interfaces.TileManagerDelegate;
import com.ls.widgets.map.model.MapObject;
import com.ls.widgets.map.providers.AssetTileProvider;

public class MapWidgetTest
        extends AndroidTestCase {
    private TestMapWidget map;
    private BitmapDrawable drawable;

    @Override
    protected void setUp() throws Exception {
        map = new TestMapWidget(getContext(), "map", 11);
        map.setScale(1.0f);
        drawable = new BitmapDrawable(getContext().getResources(),
                BitmapFactory.decodeResource(getContext().getResources(),
                        com.ls.widgets.map.test.R.drawable.maps_blue_dot));
        super.setUp();
    }


    @Override
    protected void tearDown() throws Exception {
        map.removeAllLayers();
        map.startProcessing();
        super.tearDown();
    }


    public void testCreateLayer() {
        int layerIds[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Layer layers[] = new Layer[layerIds.length];

        for (int i = 0; i < layerIds.length; ++i) {
            layers[i] = map.createLayer(layerIds[i]);
        }

        for (int i = 0; i < layerIds.length; ++i) {
            Layer layer = map.getLayerById(Integer.valueOf(i));

            assertNotNull(layer);
            assertSame(layers[i], layer);
        }

        boolean ok = false;
        try {
            map.createLayer(0);
        } catch (IllegalArgumentException e) {
            ok = true;
        }

        assertTrue(ok);
    }


    public void testRemoveLayer() {
        for (int i = -100; i <= 100; ++i) {
            map.createLayer(i);
        }

        for (int i = -100; i <= 100; ++i) {

            Layer layer = map.getLayerById(i);
            assertNotNull(layer);

            map.removeLayer(i);

            layer = map.getLayerById(i);
            assertNull(layer);
        }

        try {
            map.removeLayer(0);
        } catch (Exception e) {
            fail();
        }
    }


    public void testGetLayer() {
        int layerIds[] = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        Layer layers[] = new Layer[layerIds.length];

        for (int i = 0; i < layerIds.length; ++i) {
            layers[i] = map.createLayer(layerIds[i]);
        }

        for (int i = 0; i < layerIds.length; ++i) {
            Layer layer = map.getLayer(i);
            assertSame(layers[i], layer);
        }

        boolean ok = false;
        try {
            map.getLayer(layerIds.length);
        } catch (IndexOutOfBoundsException e) {
            ok = true;
        }

        assertTrue(ok);
    }


    public void testGetLayerById() {
        int layerIds[] = {10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        Layer layers[] = new Layer[layerIds.length];

        for (int i = 0; i < layerIds.length; ++i) {
            layers[i] = map.createLayer(layerIds[i]);
        }

        for (int i = 0; i < layerIds.length; ++i) {
            Layer layer = map.getLayerById(layerIds[i]);
            assertSame(layers[i], layer);
        }

        assertNull(map.getLayerById(0));
    }


    public void testGetLayerCount() {
        for (int j = 0; j < 100; ++j) {
            for (int i = 0; i < j; ++i) {
                map.createLayer(i);
            }

            assertEquals(map.getLayerCount(), j);
            map.removeAllLayers();
        }
    }


    public void testGetMapHeight() {
        int height = map.getConfig().getImageHeight();

        for (int i = 1; i <= 11; ++i) {
            assertEquals("i:" + i, height, map.getMapHeight());
            map.zoomOut();
            height = (int) Math.ceil(height / 2.0f);
        }
    }


    public void testGetMapWidth() {
        int width = map.getConfig().getImageWidth();

        for (int i = 1; i <= 11; ++i) {
            assertEquals("i:" + i, width, map.getMapWidth());
            map.zoomOut();
            width = (int) Math.ceil(width / 2.0f);
        }
    }


    public void testGetOriginalMapWidth() {
        int width = map.getConfig().getImageWidth();

        for (int i = 1; i <= 11; ++i) {
            assertEquals(width, map.getOriginalMapWidth());
            map.zoomOut();
        }
    }


    public void testGetOriginalMapHeight() {
        int height = map.getConfig().getImageHeight();

        for (int i = 1; i <= 11; ++i) {
            assertEquals(height, map.getOriginalMapHeight());
            map.zoomOut();
        }
    }


    public void testGetConfig() {
        OfflineMapConfig config = map.getConfig();
        assertNotNull(config);

        config.setMaxZoomLevelLimit(1);
        config = map.getConfig();
        assertTrue(config.getMaxZoomLevelLimit() == 1);
    }


    public void testGetScale() {
        float scalesUp[] = {1.0f, 2.0f, 4.0f, 8.0f, 16f, 32f};
        float scalesDown[] = {1.0f, 0.5f, 0.25f, 0.125f};
        map.setAnimationEnabled(false);

        for (int i = 0; i < scalesUp.length; ++i) {
            assertEquals("i=" + i, scalesUp[i], map.getScale());
            map.zoomIn();
        }

        for (int i = 0; i < scalesUp.length; ++i) {
            map.zoomOut();
        }

        for (int i = 0; i < scalesDown.length; ++i) {
            assertEquals(scalesDown[i], map.getScale());
            map.zoomOut();
        }

        for (int i = 0; i < 20; i++) {
            double testScale = 0.1;
            final double step = 0.1;

            while (testScale < 3.0f) {
                float prevScale = map.getScale();
                int prevWidth = map.getMapWidth();

                Log.d("MapWidgetTest", "ZL: " + map.getZoomLevel() + ", scale: " + map.getScale() + ", width: " + map.getMapWidth() + ", testScale: " + testScale);

                map.setScale((float) testScale);

                assertEquals("ZL: " + map.getZoomLevel() + ", prevScale: " + prevScale + ", CurScale:" + (float) testScale + ", prevWidth: " + prevWidth,
                        (float) (prevScale * testScale), map.getScale());

                map.setScale(1.0f);

                testScale += step;
            }

            map.zoomIn();
        }
    }


    public void testGetZoomLevel() {
        map.setAnimationEnabled(false);

        // Test lower bound
        for (int i = 11; i >= -11; --i) {
            int zl = map.getZoomLevel();

            if (i >= 0) {
                assertEquals(i, zl);
            }

            map.zoomOut();
        }

        assertEquals(0, map.getZoomLevel());

        // Test regular zoom with software zoom is enabled
        for (int i = 0; i <= 22; ++i) {
            int zl = map.getZoomLevel();
            assertEquals(i, zl);

            map.zoomIn();
        }

        // Test upper bound when software zoom is disabled
        map = new TestMapWidget(getContext(), "map", 1);
        map.setScale(1.0f);
        map.setAnimationEnabled(false);

        OfflineMapConfig conf = map.getConfig();
        conf.setSoftwareZoomEnabled(false);

        for (int i = 1; i <= 22; ++i) {
            int zl = map.getZoomLevel();

            if (i <= 11) {
                assertEquals(i, zl);
            } else {
                assertEquals(11, zl);
            }

            map.zoomIn();
        }
    }


    public void testRemoveAllLayers() {
        for (int i = -50; i < 50; ++i) {
            map.createLayer(i);
        }

        assertEquals(100, map.getLayerCount());
        map.removeAllLayers();

        for (int i = -50; i < 50; ++i) {
            assertNull("i:" + i, map.getLayerById(i));
        }
    }

    public void testClearLayers() {
        int count = 50;
        MapObject[] objects = generateMapObjects(count);
        ArrayList<Layer> layers = new ArrayList<Layer>(10);


        for (int i = -5; i < 5; ++i) {
            Layer layer = map.createLayer(i);

            for (MapObject object : objects) {
                layer.addMapObject(object);
            }

            layers.add(layer);

            assertEquals(count, layer.getMapObjectCount());
        }

        map.clearLayers();

        for (Layer layer : layers) {
            assertEquals(0, layer.getMapObjectCount());
        }
    }


    public void testSetMinZoomLevel() {
        for (int j = 10; j >= 0; --j) {
            map = new TestMapWidget(getContext(), "map", 11);
            map.setAnimationEnabled(false);
            map.setMinZoomLevel(j);

            for (int i = j; i >= 0; --i) {
                map.zoomOut();

                int zl = map.getZoomLevel();

                if (zl < j) {
                    fail("ZL: " + zl + ", j: " + j);
                }
            }
        }
    }


    public void testSetMaxZoomLevel() {
        for (int j = 2; j <= 11; ++j) {
            map = new TestMapWidget(getContext(), "map", 1);
            map.setAnimationEnabled(false);
            map.setMaxZoomLevel(j);

            for (int i = 2; i <= 11; ++i) {
                map.zoomIn();

                int zl = map.getZoomLevel();

                if (zl > j) {
                    fail("ZL: " + zl + ", j: " + j);
                }
            }
        }
    }


    public void testSetScale() {
        map = new TestMapWidget(getContext(), "map", 1);
        map.setAnimationEnabled(false);

        float scalesUp[] = {1.0f, 2.0f, 4.0f, 8.0f, 16f, 32f};
        float scalesDown[] = {1.0f, 0.5f, 0.25f, 0.125f};

        for (int j = 0; j < 15; ++j) {
            for (int i = 0; i < scalesDown.length; ++i) {
                float oldScale = map.getScale();
                map.setScale(scalesDown[i]);
                float newScale = map.getScale();

                assertEquals("zl:" + map.getZoomLevel() + ", i:" + i + ", os:" + oldScale + ", ns:" + newScale,
                        scalesDown[i], newScale / oldScale);

                map.setScale(1.0f);
            }


            for (int i = 0; i < scalesUp.length; ++i) {
                float oldScale = map.getScale();
                map.setScale(scalesUp[i]);
                float newScale = map.getScale();

                assertEquals("zl:" + map.getZoomLevel(), scalesUp[i], newScale / oldScale);

                map.setScale(1.0f);
            }

            map.zoomIn();
        }
    }


    public void testUseSoftwareZoom() {
        map.setAnimationEnabled(false);
        map.setUseSoftwareZoom(false);

        for (int i = 0; i < 10; ++i) {
            map.zoomIn();
        }

        assertEquals(11, map.getZoomLevel());

        map.setUseSoftwareZoom(true);

        for (int i = 0; i < 10; ++i) {
            map.zoomIn();
        }

        assertEquals(11 + 10, map.getZoomLevel());
    }


    public void testZoomIn() {
        map = new TestMapWidget(getContext(), "map", 1);
        map.setAnimationEnabled(false);
        TestMapEventsListener listener = new TestMapEventsListener();

        for (int i = 2; i <= 13; ++i) {
            map.addMapEventsListener(listener);

            float oldScale = map.getScale();

            map.zoomIn();

            float newScale = map.getScale();

            assertTrue("I:" + i + ", zoomIn: " + listener.counters[TestMapEventsListener.PRE_ZOOM_IN] +
                            " " + listener.counters[TestMapEventsListener.POST_ZOOM_IN],
                    listener.zoomInValid());

            assertEquals("i:" + i, 2.0f, newScale / oldScale);

            map.removeMapEventsListener(listener);
            listener.clearCounters();
        }
    }


    public void testZoomOut() {
        map.setAnimationEnabled(false);
        TestMapEventsListener listener = new TestMapEventsListener();
        for (int i = 2; i <= 11; ++i) {

            map.addMapEventsListener(listener);

            float oldScale = map.getScale();
            map.zoomOut();

            float newScale = map.getScale();

            assertTrue("I:" + i + ", zoomOut: " + listener.counters[TestMapEventsListener.PRE_ZOOM_OUT] +
                    " " + listener.counters[TestMapEventsListener.POST_ZOOM_OUT], listener.zoomOutValid());

            assertEquals(2.0f, oldScale / newScale);

            map.removeMapEventsListener(listener);
            listener.clearCounters();
        }
    }

// FIXME: Temporarily disabled
//    public void testAnimatedZoomIn() {
//        map = new TestMapWidget(getContext(), "map", 1);
//        map.setAnimationEnabled(true);
//        final Object event = new Object();
//
//        TestMapEventsListener listener = new TestMapEventsListener() {
//            @Override
//            public void onPostZoomIn() {
//                super.onPostZoomIn();
//
//                synchronized (event) {
//                    event.notify();
//                }
//            }
//        };
//
//
//        for (int i = 2; i <= 11; ++i) {
//            map.addMapEventsListener(listener);
//            float oldScale = map.getScale();
//            map.zoomIn();
//
//            synchronized (event) {
//                try {
//                    event.wait(3000);
//                } catch (InterruptedException e) {
//                    fail(e.toString());
//                    e.printStackTrace();
//                }
//            }
//
//            float newScale = map.getScale();
//            map.removeMapEventsListener(listener);
//            assertTrue("I:" + i + ", zoomIn: " + listener.counters[TestMapEventsListener.PRE_ZOOM_IN] +
//                            " " + listener.counters[TestMapEventsListener.POST_ZOOM_IN],
//                    listener.zoomInValid());
//            assertEquals(2.0f, newScale / oldScale);
//            listener.clearCounters();
//        }
//    }


    public void testAnimatedZoomOut() {
        map.setAnimationEnabled(true);

        TestMapEventsListener listener = new TestMapEventsListener();


        for (int i = 2; i <= 11; ++i) {
            map.addMapEventsListener(listener);

            float oldScale = map.getScale();

            map.zoomOut();

            float newScale = map.getScale();

            map.removeMapEventsListener(listener);

            assertTrue("I:" + i + ", zoomOut: " + listener.counters[TestMapEventsListener.PRE_ZOOM_OUT] +
                            " " + listener.counters[TestMapEventsListener.POST_ZOOM_OUT],
                    listener.zoomOutValid());

            assertEquals(2.0f, oldScale / newScale);

            listener.clearCounters();
        }
    }


    private MapObject[] generateMapObjects(int count) {
        MapObject[] result = new MapObject[count];

        for (int i = 0; i < count; ++i) {
            MapObject object = new MapObject(i, drawable, new Point(0, 0));
            result[i] = object;
        }

        return result;
    }


    private class TestMapEventsListener implements MapEventsListener {
        public static final int PRE_ZOOM_IN = 0;
        public static final int PRE_ZOOM_OUT = 1;
        public static final int POST_ZOOM_IN = 2;
        public static final int POST_ZOOM_OUT = 3;

        private int counters[];

        public TestMapEventsListener() {
            counters = new int[]{0, 0, 0, 0};
        }

        @Override
        public void onPreZoomIn() {
            counters[PRE_ZOOM_IN] += 1;
        }

        @Override
        public void onPostZoomIn() {
            counters[POST_ZOOM_IN] += 1;
        }

        @Override
        public void onPreZoomOut() {
            counters[PRE_ZOOM_OUT] += 1;
        }

        @Override
        public void onPostZoomOut() {
            counters[POST_ZOOM_OUT] += 1;
        }


        public void clearCounters() {
            counters = new int[]{0, 0, 0, 0};
        }


        public boolean zoomInValid() {
            return counters[PRE_ZOOM_IN] == 1 && counters[POST_ZOOM_IN] == 1;
        }

        public boolean zoomOutValid() {
            return counters[PRE_ZOOM_OUT] == 1 && counters[POST_ZOOM_OUT] == 1;
        }
    }


    private class TestMapWidget extends MapWidget {

        public TestMapWidget(Context context, String rootMapFolder) {
            super(context, rootMapFolder);

            this.tileProvider = new TestTileManager(context, this.getConfig());
            this.setTileProvider(tileProvider);
        }

        public TestMapWidget(Context context, String rootMapFolder, int initialZl) {
            super(context, rootMapFolder, initialZl);

            this.tileProvider = new TestTileManager(context, this.getConfig());

            this.setTileProvider(tileProvider);
        }


        @Override
        protected void animateZoomIn(final AnimationListener listener, float pivotX,
                                     float pivotY) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    post(new Runnable() {
                        public void run() {
                            onAnimationEnd();
                        }
                    });

                }
            })).start();
        }

        @Override
        protected void animateZoomOut(final AnimationListener listener) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    post(new Runnable() {
                        public void run() {
                            onAnimationEnd();
                        }
                    });
                    //onAnimationEnd();
                    //listener.onAnimationEnd(null);
                }
            })).start();
        }

        public void startProcessing() {
            super.startProcessingRequests();
        }
    }


    private class TestTileManager extends AssetTileProvider {

        public TestTileManager(Context context, OfflineMapConfig config) {
            super(context, config);
        }

        @Override
        public void requestTile(int zoomLevel, int col, int row,
                                TileManagerDelegate delegate) {
            delegate.onTileReady(zoomLevel, col, row, drawable);
        }

    }
}
