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

package com.ls.mappwidget.slicingtool.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ls.mappwidget.slicingtool.vo.PointVO;

public class GPSChooser extends Composite
{
	private String title;

	private Text x;

	private Text y;

	private Text longitude;

	private Text latitude;

	public GPSChooser(Composite parent, String title)
	{
		super(parent, SWT.NONE);
		this.title = title;
		createContent();
	}

	public void createContent()
	{
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;

		GridLayout contentLayout = new GridLayout();
		contentLayout.numColumns = 1;
		contentLayout.marginHeight = 0;
		contentLayout.marginWidth = 0;
		setLayout(contentLayout);
		setLayoutData(gd);

		Group group = new Group(this, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginWidth = 15;
		layout.numColumns = 4;

		group.setLayoutData(gd);
		group.setLayout(layout);
		group.setText(title);

		GridData textData = new GridData();
		textData.grabExcessHorizontalSpace = true;
		textData.horizontalAlignment = GridData.FILL;

		Label label = new Label(group, SWT.WRAP);
		label.setText("X");

		x = new Text(group, SWT.SINGLE | SWT.BORDER);
		x.setLayoutData(textData);

		label = new Label(group, SWT.WRAP);
		label.setText("Longitude");

		longitude = new Text(group, SWT.SINGLE | SWT.BORDER);
		longitude.setLayoutData(textData);

		label = new Label(group, SWT.WRAP);
		label.setText("Y");

		y = new Text(group, SWT.SINGLE | SWT.BORDER);
		y.setLayoutData(textData);

		label = new Label(group, SWT.WRAP);
		label.setText("Latitude");

		latitude = new Text(group, SWT.SINGLE | SWT.BORDER);
		latitude.setLayoutData(textData);
	}

	public void setActive(boolean enabled)
	{
		x.setEnabled(enabled);
		y.setEnabled(enabled);
		latitude.setEnabled(enabled);
		longitude.setEnabled(enabled);
	}

	public PointVO getPoint()
	{
		PointVO result = new PointVO();

		try
		{
			result.setX(Double.valueOf(x.getText()));
			result.setY(Double.valueOf(y.getText()));
			result.setLat(Double.valueOf(latitude.getText()));
			result.setLon(Double.valueOf(longitude.getText()));

		} catch (NumberFormatException e)
		{
			return null;
		}

		return result;
	}
}
