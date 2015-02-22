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

package com.ls.demo.demo1;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.ls.widgets.map.model.MapObject;

public class CaptionMapObject extends MapObject 
{

	private String caption;
	private Paint paint;
	
	public CaptionMapObject(Object id, Drawable drawable, Point position,
			Point pivotPoint, boolean isTouchable, boolean isScalable) {
		super(id, drawable, position, pivotPoint, isTouchable, isScalable);
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(14);
		paint.setTypeface(Typeface.SANS_SERIF);
		paint.setFakeBoldText(true);
		paint.setShadowLayer(1, 0, 0, Color.BLACK);
	}

	
	public void setCaption(String caption)
	{
		this.caption = caption;
	}
	
	
	@Override
	public void draw(Canvas canvas) 
	{		
		super.draw(canvas);
		
		if (caption != null) {
			canvas.drawText(caption, posScaled.x, posScaled.y - 10, paint);
		}
	}
	
}
