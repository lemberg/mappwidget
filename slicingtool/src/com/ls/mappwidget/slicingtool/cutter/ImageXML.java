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

package com.ls.mappwidget.slicingtool.cutter;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ls.mappwidget.slicingtool.utils.XMLUtils;
import com.ls.mappwidget.slicingtool.vo.PointVO;

public class ImageXML
{
	public static void createXML(String fileName, int tileSize, int w, int h, PointVO pointTopLeft, PointVO pointBottomRight)
	{
		Document doc = XMLUtils.createDoc();

		Element rootElement = doc.createElement(Constants.Tag.IMAGE);
		doc.appendChild(rootElement);
		addRootAttr(rootElement, doc, tileSize);

		Element sizeElement = doc.createElement(Constants.Tag.SIZE);
		rootElement.appendChild(sizeElement);
		addSizeAttr(sizeElement, doc, w, h);

		if (pointTopLeft != null)
		{
			Element colibrElement = doc.createElement(Constants.Tag.CALIBRATIONRECT);
			rootElement.appendChild(colibrElement);

			Element pointElement = doc.createElement(Constants.Tag.POINT);
			colibrElement.appendChild(pointElement);
			addPointAttr(pointElement, doc, pointTopLeft, true);

			pointElement = doc.createElement(Constants.Tag.POINT);
			colibrElement.appendChild(pointElement);
			addPointAttr(pointElement, doc, pointBottomRight, false);
		}
		
		XMLUtils.saveDoc(doc, fileName);
	}

	private static void addPointAttr(Element pointElement, Document doc, PointVO point, boolean b)
	{
		Attr attr = doc.createAttribute(Constants.Attr.X);
		attr.setValue(point.getX() + "");
		pointElement.setAttributeNode(attr);

		attr = doc.createAttribute(Constants.Attr.Y);
		attr.setValue(point.getY() + "");
		pointElement.setAttributeNode(attr);

		attr = doc.createAttribute(Constants.Attr.LAT);
		attr.setValue(point.getLat() + "");
		pointElement.setAttributeNode(attr);

		attr = doc.createAttribute(Constants.Attr.LON);
		attr.setValue(point.getLon() + "");
		pointElement.setAttributeNode(attr);

		if (b)
		{
			attr = doc.createAttribute(Constants.Attr.TOPLEFT);
			attr.setValue("1");
			pointElement.setAttributeNode(attr);
		}
	}

	private static void addSizeAttr(Element sizeElement, Document doc, int w, int h)
	{
		Attr attr = doc.createAttribute(Constants.Attr.WIDTH);
		attr.setValue("" + w);
		sizeElement.setAttributeNode(attr);

		attr = doc.createAttribute(Constants.Attr.HEIGHT);
		attr.setValue("" + h);
		sizeElement.setAttributeNode(attr);
	}

	private static void addRootAttr(Element rootElement, Document doc, int tileSize)
	{
		Attr attr = doc.createAttribute(Constants.Attr.TILE_SIZE);
		attr.setValue("" + tileSize);
		rootElement.setAttributeNode(attr);

		attr = doc.createAttribute(Constants.Attr.OVERLAP);
		attr.setValue("1");
		rootElement.setAttributeNode(attr);

		attr = doc.createAttribute(Constants.Attr.FORMAT);
		attr.setValue("png");
		rootElement.setAttributeNode(attr);
	}
}
