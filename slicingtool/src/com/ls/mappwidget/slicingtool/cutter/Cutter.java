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

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.ls.mappwidget.slicingtool.views.MainView;
import com.ls.mappwidget.slicingtool.vo.PointVO;

public class Cutter
{
	private OnProgressUpdateListener onUpdate;

	private OnCompliteListener onComplite;

	public Cutter(OnProgressUpdateListener listener, OnCompliteListener onComplite)
	{
		this.onUpdate = listener;
		this.onComplite = onComplite;
	}

	public void startCuttingAndroid(final String inFile, final String outDir, final String mapName, final int tileSize, final PointVO pointTopLeft, final PointVO pointBottomRight)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				File fileXml = new File(outDir, mapName);
				createDir(fileXml);

				File file = new File(outDir, mapName + "\\" + mapName + "_files");
				createDir(file);

				String temp = file.getAbsolutePath() + "\\tmp.png";
				saveImage(inFile, "png", temp);

				int count = (int) Math.ceil(Math.log(getMaxSide(temp)) / Math.log(2));
				MainView.PROGRESS_MAX = count;

				for (int i = count; i >= 0; i--)
				{
					File fdir = new File(file.getAbsoluteFile(), "\\" + i);
					createDir(fdir);

					if (i == count)
					{
						imageCut(temp, fdir.getAbsolutePath(), tileSize, fileXml.getAbsoluteFile() + "\\" + mapName + ".xml", true, "", pointTopLeft, pointBottomRight);
					}
					else
					{
						imageCut(temp, fdir.getAbsolutePath(), tileSize, null, false, "", pointTopLeft, pointBottomRight);
					}

					imageResize(temp, temp, 50);
					onUpdate.onProgressUpdate(count - i + 1);
				}

				File tmp = new File(temp);
				tmp.delete();
				onComplite.onComplite();
			}
		}).start();
	}

	private void createDir(File file)
	{
		if (!file.exists())
		{
			file.mkdirs();
		}
	}

	private void imageCut(String inFile, String outDir, int tileSize, String mapName, boolean xml, String concut, PointVO pointTopLeft, PointVO pointBottomRight)
	{
		String s = "";

		if (!outDir.endsWith("\\"))
		{
			s = "\\";
		}

		BufferedImage image = getImage(inFile);

		int w = image.getWidth();
		int h = image.getHeight();

		if (xml)
		{
			ImageXML.createXML(mapName, tileSize, w, h, pointTopLeft, pointBottomRight);
		}

		if (w < tileSize && h < tileSize)
		{
			saveImage(image, "png", outDir + s + concut + "0_0.png");
			return;
		}

		for (int i = 0, k = 0; i < w - 1; i += tileSize, k++)
		{
			for (int j = 0, l = 0; j < h - 1; j += tileSize, l++)
			{
				int tileWidth = tileSize;
				int tileHeight = tileSize;

				if (tileWidth > (w - i - 1))
				{
					tileWidth = w - i - 1;
				}

				if (tileHeight > (h - j - 1))
				{
					tileHeight = h - j - 1;
				}

				BufferedImage part = image.getSubimage(i, j, tileWidth, tileHeight);
				saveImage(part, "png", outDir + s + concut + k + "_" + l + ".png");

			}
		}
	}

	private boolean imageResize(String outFile, String inFile, int percents)
	{
		BufferedImage originalImage;

		originalImage = getImage(inFile);
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

		BufferedImage resizedImage = doResize(originalImage, type, percents);
		saveImage(resizedImage, "png", outFile);

		return true;
	}

	private BufferedImage doResize(BufferedImage originalImage, int type, int percents)
	{

		int h = (int) Math.round((originalImage.getHeight() * percents / 100.0));
		int w = (int) Math.round((originalImage.getWidth() * percents / 100.0));

		if (h <= 0)
		{
			h = 1;
		}

		if (w <= 0)
		{
			w = 1;
		}

		BufferedImage resizedImage = new BufferedImage(w, h, type);
		Graphics2D g = resizedImage.createGraphics();

		
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.drawImage(originalImage, 0, 0, w, h, null);
		
		g.dispose();

		return resizedImage;
	}

	public int getWidth(String fileName)
	{
		BufferedImage image = getImage(fileName);

		if (image == null)
		{
			return -1;
		}

		int width = image.getWidth();
		return width;
	}

	private int getMaxSide(String fileName)
	{
		BufferedImage image = getImage(fileName);
		int width = image.getWidth();
		int height = image.getHeight();

		if (width > height)
		{
			return width;
		}
		else
		{
			return height;
		}
	}

	private BufferedImage getImage(String fileName)
	{
		try
		{
			BufferedImage image = ImageIO.read(new File(fileName));
			return image;

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private boolean saveImage(String inFile, String formatName, String fileName)
	{
		try
		{
			BufferedImage image = getImage(inFile);
			ImageIO.write(image, formatName, new File(fileName));
			return true;

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	private boolean saveImage(BufferedImage image, String formatName, String fileName)
	{
		try
		{
			ImageIO.write(image, formatName, new File(fileName));
			return true;

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return false;
	}
}
