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

package com.ls.demo.demo2.popup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class MapPopupBase
{
	protected ViewGroup parentView;
	
	protected LinearLayout container;
	protected float dipScaleFactor;
	protected int lastX;
	protected int lastY;
	protected int screenHeight;
	protected int screenWidth;
	

	public MapPopupBase(Context context, ViewGroup parentView) 
	{
	    screenHeight = context.getResources().getDisplayMetrics().heightPixels;
	    screenWidth = context.getResources().getDisplayMetrics().widthPixels;
	    
		this.parentView = parentView;
    	dipScaleFactor = context.getResources().getDisplayMetrics().density;
    	
		container = new LinearLayout(context);
		container.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	
		lastX = lastY = -1;
	}		
	
	
	public void show(ViewGroup view, int theX, int theY)
	{
		hide();

		container.measure(view.getWidth(), view.getHeight());
		
		int x = theX - (getWidth() / 2);
		int y = theY - getHeight();
		
		container.setPadding(x, y, 0, 0);

		lastX = x;
		lastY = y;
		
		parentView.addView(container);
		container.setVisibility(View.VISIBLE);
	}

	
	public int getHeight()
	{
		return container.getMeasuredHeight();
	}
	
	
	public int getWidth()
	{
		return container.getMeasuredWidth();
	}
	
	
	public boolean isVisible()
	{
		return container.getVisibility() == View.VISIBLE;
	}
	
	
	public void hide()
	{
		container.setPadding(0, 0, 0, 0);
		container.setVisibility(View.INVISIBLE);
		parentView.removeView(container);
	}
	
	
	public void setOnClickListener(View.OnTouchListener listener)
	{
	    if(container != null){
	        container.setOnTouchListener(listener);
	    }
	}
}
