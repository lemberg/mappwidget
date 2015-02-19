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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ls.mappwidget.slicingtool.utils.EclipseUtils;

public class RadioGroup extends Composite
{
	public static final int LOCATION = 1;

	public static final int PROJECT = 2;

	private static final String BTN_LOCATION_TXT = "Export to location";

	private static final String BTN_PROJECT_TXT = "Export to Project";

	private Button btnLocation;

	private Button btnProject;

	public static final String DIR_PATH = "directorychooser.file_path";

	private static final String BTN_TXT = "...";

	private Text text;

	private Button button;

	private List<String> result;

	private File workspace;

	private Combo comboPath;

	public RadioGroup(Composite parent)
	{
		super(parent, SWT.NULL);
		createContent();
	}

	public void createContent()
	{
		GridLayout layout = new GridLayout(3, false);
		setLayout(layout);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalAlignment = SWT.FILL;
		gd.minimumWidth = 400;
		gd.minimumHeight = 50;

		btnLocation = new Button(this, SWT.RADIO);
		btnLocation.setText(BTN_LOCATION_TXT);

		text = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData gd2 = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gd2);

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
				FileChooser.savePath(RadioGroup.this, DIR_PATH, selectedDir);
			}
		});

		btnProject = new Button(this, SWT.RADIO);
		btnProject.setText(BTN_PROJECT_TXT);
		btnProject.setSelection(true);
		text.setEnabled(false);
		button.setEnabled(false);

		GridData dataCombo = new GridData();
		dataCombo.grabExcessHorizontalSpace = true;
		dataCombo.horizontalAlignment = GridData.FILL;

		workspace = EclipseUtils.getCurrentWorkspace();
		String[] list = workspace.list();
		result = new ArrayList<String>();

		for (int i = 0; i < list.length; i++)
		{
			File f = new File(workspace, list[i] + "\\" + "AndroidManifest.xml");
			if (f.exists())
			{
				result.add(list[i]);
			}
		}

		comboPath = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboPath.setItems(result.toArray(new String[result.size()]));
		comboPath.select(0);
		comboPath.setLayoutData(dataCombo);

		Label label = new Label(this, SWT.WRAP);
		label.setText("");

		btnProject.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				comboPath.setEnabled(true);
				text.setEnabled(false);
				button.setEnabled(false);
			}
		});

		btnLocation.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				comboPath.setEnabled(false);
				text.setEnabled(true);
				button.setEnabled(true);
			}
		});
	}

	public void setDirText(String text)
	{
		this.text.setText(text);
	}

	public String getDirText()
	{
		return text.getText();
	}

	public String getSelectedPath()
	{
		int pos = comboPath.getSelectionIndex();

		if (pos == -1)
		{
			return null;
		}

		return workspace.getAbsolutePath() + "\\" + result.get(pos) + "\\assets";
	}

	public int getSelected()
	{
		if (btnLocation.getSelection())
		{
			return LOCATION;
		}

		return PROJECT;
	}

}