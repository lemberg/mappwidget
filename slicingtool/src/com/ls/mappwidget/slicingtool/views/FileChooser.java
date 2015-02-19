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
import java.util.prefs.Preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

public class FileChooser extends Composite
{
	public static final String FILE_PATH = "filechooser.file.path";

	private static final String OPEN = "Open";

	private static final String OK = "Ok";

	private static final String BTN_TXT = "...";

	private Text text;

	private Button button;

	private String title = null;

	private int mode;

	public FileChooser(Composite parent, int mode)
	{
		super(parent, SWT.NONE);
		this.mode = mode;

		createContent();
	}

	public void createContent()
	{
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);

		text = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData();
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
				String path = null;

				switch (mode)
				{
				case Mode.OPEN:
					path = openFile();
					break;

				case Mode.SAVE:
					path = saveFile();
					break;
				}

				if (path == null)
				{
					return;
				}

				text.setText(path);
				savePath(FileChooser.this, FILE_PATH, path);
			}
		});
	}

	public static void savePath(Composite composite, String key, String value)
	{
		Preferences prefs = Preferences.userNodeForPackage(composite.getClass());
		prefs.put(key, value);
	}

	public String openFile()
	{
		FileDialog dlg = new FileDialog(button.getShell(), SWT.OPEN);
		dlg.setFileName(text.getText());
		dlg.setFilterExtensions(new String[] { "*.gif; *.jpg; *.png; *.ico; *.bmp" });

		dlg.setText(OPEN);
		String path = dlg.open();

		return path;
	}

	public String saveFile()
	{
		FileDialog dlg = new FileDialog(button.getShell(), SWT.SAVE);
		dlg.setFileName(text.getText());

		dlg.setText(OK);
		String path = dlg.open();

		return path;
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

	public static class Mode
	{
		public static final int OPEN = 1;

		public static final int SAVE = 2;
	}
}