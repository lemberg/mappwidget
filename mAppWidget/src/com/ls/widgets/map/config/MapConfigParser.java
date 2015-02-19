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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.os.Debug;
import android.util.DebugUtils;
import android.util.Log;
import android.util.Pair;

import com.ls.widgets.map.utils.MapCalibrationData;

public class MapConfigParser 
{
	
	private int imageWidth;
	private int imageHeight;
	private int tileSize;
	private int overlap;
	private String imageFormat;
	private String rootMapFolder;
	
	private MapCalibrationData geoArea;
	
	public MapConfigParser(String mapRoot)
	{
		this.rootMapFolder = mapRoot;
	}

	public OfflineMapConfig parse(Context context, File configFile) throws IOException, SAXException
	{   
        InputStream is = null;
        
        try {
            if (configFile.exists()) { 
                is = new FileInputStream(configFile);
            
                return parse(is);
            } else {
                Log.e("MapConfigParser", "Map config file not found.");
                return null;
            }
        } catch (ParserConfigurationException e) {
            Log.e("MapConfigParser", "Exception: " + e);
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            Log.e("MapConfigParser", "Exception: " + e);
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        
        return null;
	}
	
	public OfflineMapConfig parse(Context context, String configPath) throws IOException, SAXException
	{
		InputStream is = null;

		try {
			is = context.getAssets().open(configPath);
            
			return parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("MapConfigParser", "Exception: " + e);
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			Log.e("MapConfigParser", "Exception: " + e);
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
		
		return null;
	}

    private OfflineMapConfig parse(InputStream is) throws ParserConfigurationException, FactoryConfigurationError,
            SAXException, IOException
    {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = docBuilder.parse(is); 
        
        NodeList images = doc.getElementsByTagName("Image");
        Node image = images.item(0);
        
        NamedNodeMap attributes = image.getAttributes();
        
        Node tileSizeNode = attributes.getNamedItem("TileSize");
        Node overlapNode = attributes.getNamedItem("Overlap");
        Node formatNode = attributes.getNamedItem("Format");
        
        Node sizeNode = null;
        Node geoAreaNode = null;
        NodeList childNodes = image.getChildNodes();
        
        for (int i=0; i<childNodes.getLength(); ++i) {
        	Node node = childNodes.item(i);
        	if (node != null && node.getNodeName().equals("Size")) {
        		sizeNode = node;
        	}
        	
        	if (node != null && node.getNodeName().equals("CalibrationRect")) {
        		geoAreaNode = node;
        	}
        }
        
        attributes = sizeNode.getAttributes();
        
        Node widthNode = attributes.getNamedItem("Width");
        Node heightNode = attributes.getNamedItem("Height");
        
        
        if (geoAreaNode != null) {
        	NodeList pointNodes = geoAreaNode.getChildNodes();
        	
        	Pair<Point, Location> topLeft = null;
        	Pair<Point, Location> bottomRight = null;
        	
        	for (int i=0; i<pointNodes.getLength(); ++i) {
        		Node node = pointNodes.item(i);
        		
        		if (node != null && node.getNodeName().equals("Point")) {
        			NamedNodeMap attrs = node.getAttributes();
        			Node topLeftAttr = attrs.getNamedItem("topLeft");
        			
        			Node latNode = attrs.getNamedItem("lat");
        			Node lonNode = attrs.getNamedItem("lon");
        			Node xNode = attrs.getNamedItem("x");
        			Node yNode = attrs.getNamedItem("y");
        			
        			if (latNode != null && lonNode != null) {
        				double lat = Double.parseDouble(latNode.getNodeValue());
        				double lon = Double.parseDouble(lonNode.getNodeValue());
        				int x = Integer.parseInt(xNode.getNodeValue());
        				int y = Integer.parseInt(yNode.getNodeValue());
        					
        				if (topLeftAttr != null && topLeftAttr.getNodeValue().equals("1")) {
        					// Top left point
        					topLeft = new Pair<Point, Location>(new Point(), new Location("config"));
        					topLeft.first.set(x, y);
        					topLeft.second.setLatitude(lat);
        					topLeft.second.setLongitude(lon);
        				} else {
        					// bottom right point
        					bottomRight = new Pair<Point, Location>(new Point(), new Location("config"));
        					bottomRight.first.set(x, y);
        					bottomRight.second.setLatitude(lat);
        					bottomRight.second.setLongitude(lon);
        				}
        			}
        		}
        	}
        	
        	if (topLeft != null && bottomRight != null) {
        			geoArea = new MapCalibrationData(topLeft, bottomRight);
        	} else {
        		Log.w("MapConfigParser", "No geo area is set");
        	}
        } else {
        	Log.w("MapConfigParser", "GeoArea is not configured.");
        }

        
        if (tileSizeNode != null) {
        	tileSize = Integer.parseInt(tileSizeNode.getNodeValue());
        }
        
        if (overlapNode != null) {
        	overlap = Integer.parseInt(overlapNode.getNodeValue());
        }
        
        if (formatNode != null) {
        	imageFormat = formatNode.getNodeValue();
        }
        
        if (widthNode != null) {
        	imageWidth = Integer.parseInt(widthNode.getNodeValue());
        }
        
        if (heightNode != null) {
        	imageHeight = Integer.parseInt(heightNode.getNodeValue());
        }
        
        OfflineMapConfig config = new OfflineMapConfig(rootMapFolder, imageWidth, imageHeight, tileSize, overlap, imageFormat);
        
        config.getGpsConfig().setGeoArea(geoArea);
        
        return config;
    }
	 

}
