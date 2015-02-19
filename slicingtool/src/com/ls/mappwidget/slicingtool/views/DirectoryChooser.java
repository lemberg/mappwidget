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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

public class DirectoryChooser extends Composite
{

	public static final String DIR_PATH = "directorychooser.file_path";

	private static final String BTN_TXT = "...";

	private Text text;

	private Button button;

	private String title = null;

	public DirectoryChooser(Composite parent)
	{
		super(parent, SWT.NONE);
		createContent();
	}

	public void createContent()
	{
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 4;
		setLayout(layout);

		text = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gd);

		button = new Button(this, SWT.NONE);
		button.setText(BTN_TXT);
		button.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dirDialog = new DirectoryDialog(button.getShell());
				dirDialog.setFilterPath(text.getText());
				dirDialog.setText("Select your home directory");
				String selectedDir = dirDialog.open();

				if (selectedDir == null)
				{
					return;
				}

				text.setText(selectedDir);
				FileChooser.savePath(DirectoryChooser.this, DIR_PATH, selectedDir);
			}
		});
	}

	public void setText(String text)
	{
		this.text.setText(text);
	}

	public String getText()
	{
		return text.getText();

	}

	public Text getTextControl()
	{
		return text;
	}

	public File getFile()
	{
		String text = this.text.getText();

		if (text.length() == 0)
		{
			return null;
		}

		return new File(text);
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
}
