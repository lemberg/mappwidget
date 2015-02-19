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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.ls.mappwidget.slicingtool.cutter.Cutter;
import com.ls.mappwidget.slicingtool.cutter.OnCompliteListener;
import com.ls.mappwidget.slicingtool.cutter.OnProgressUpdateListener;
import com.ls.mappwidget.slicingtool.utils.FileUtils;
import com.ls.mappwidget.slicingtool.views.FileChooser.Mode;
import com.ls.mappwidget.slicingtool.vo.PointVO;

public class MainView extends ViewPart
{
	private static final String GPS_POSITIONING = "GPS Positioning";

	private static final String BOTTOM_RIGHT_POINT = "Bottom right point";

	private static final String TOP_LEFT_POINT = "Top left point";

	private static final String ATTENTION = "Attention :";

	private static final String EXPORT_OPTIONS = "Export Options";

	private static final String MAP_SOURCE = "Map Source";

	private static final String DEFAULT_NAME = "map";

	private static final String INCORECT_FILE_NAME = "Incorect file name!";

	private static final String CHOOSE_SAVE_DIR_FIRST = "Choose save dir first!";

	private static final String ENTER_NAME_FIRST = "Enter name first!";

	private static final String CHOOSE_IMAGE_FIRST = "Choose image first!";

	private static final String NAME = "Name";

	private static final String TILE_SIZE = "Tile Size";

	private static final String CUT = "Export";

	public static final String ID = "com.ls.map.image.views.SampleView";

	public static int PROGRESS_MAX = 10;

	public MainView()
	{
	}

	@Override
	public void createPartControl(Composite parent)
	{
		initWidgets(parent);
	}

	@Override
	public void setFocus()
	{
	}

	private void initWidgets(final Composite parent)
	{
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		final Composite top = new Composite(scrolledComposite, SWT.NONE);
		fillTop(top, scrolledComposite);

		Preferences prefs = Preferences.userNodeForPackage(this.getClass());

		Composite mapSource = new Composite(top, SWT.NONE);
		final FileChooser fileChooser = fillMapSource(mapSource, prefs);

		final Group group = new Group(top, SWT.NONE);
		fillGroup(group);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		GridData fileDir = new GridData();
		fileDir.grabExcessHorizontalSpace = true;
		fileDir.horizontalAlignment = GridData.FILL;

		Composite composite = new Composite(group, SWT.NONE);
		composite.setLayoutData(fileDir);
		composite.setLayout(layout);

		Label label = new Label(composite, SWT.WRAP);
		label.setText(NAME);

		final Text text = fillName(composite);

		final RadioGroup radioGroup = new RadioGroup(group);
		radioGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		radioGroup.setDirText(prefs.get(RadioGroup.DIR_PATH, ""));

		layout = new GridLayout();
		layout.numColumns = 2;

		composite = new Composite(group, SWT.NONE);
		composite.setLayoutData(fileDir);
		composite.setLayout(layout);

		GridData dataCombo = new GridData();
		dataCombo.widthHint = 50;

		label = new Label(composite, SWT.WRAP);
		label.setText(TILE_SIZE);

		final Combo comboTile = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboTile.setItems(new String[] { "128", "256" });
		comboTile.select(1);
		comboTile.setLayoutData(dataCombo);

		composite = new Composite(group, SWT.NONE);
		composite.setLayoutData(fileDir);
		composite.setLayout(layout);

		label = new Label(composite, SWT.WRAP);
		label.setText(GPS_POSITIONING);

		final Button gps = new Button(composite, SWT.CHECK);

		fileDir = new GridData();
		fileDir.grabExcessHorizontalSpace = true;
		fileDir.horizontalAlignment = GridData.FILL;

		layout = new GridLayout();
		layout.numColumns = 2;

		composite = new Composite(group, SWT.NONE);
		composite.setLayoutData(fileDir);
		composite.setLayout(layout);

		final GPSChooser gpsChooserTopLeft = new GPSChooser(composite, TOP_LEFT_POINT);
		gpsChooserTopLeft.setActive(false);

		final GPSChooser gpsChooserBottomRight = new GPSChooser(composite, BOTTOM_RIGHT_POINT);
		gpsChooserBottomRight.setActive(false);

		final Button runButton = new Button(top, SWT.NONE);
		runButton.setText(CUT);

		final ProgressBar progressBar = new ProgressBar(top, SWT.SMOOTH);

		progressBar.setMinimum(0);
		progressBar.setVisible(false);

		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Cutter cutter = new Cutter(new OnProgressUpdateListener()
		{
			@Override
			public void onProgressUpdate(int value)
			{
				setValue(top, progressBar, value);
			}
		}, new OnCompliteListener()
		{

			@Override
			public void onComplite()
			{
				setCompliete(top, runButton, progressBar);
			}
		});

		runButton.addListener(SWT.Selection, new Listener()
		{

			public void handleEvent(Event event)
			{
				PointVO pointTopLeft = gpsChooserTopLeft.getPoint();
				PointVO pointBottomRight = gpsChooserBottomRight.getPoint();

				if (gps.getSelection() && (pointTopLeft == null || pointBottomRight == null))
				{
					MessageBox dialog = new MessageBox(parent.getShell());
					dialog.setMessage("Please enter a valid values for points!.");
					dialog.open();
					return;
				}

				String dir = getDir(radioGroup);

				if (dir == null)
				{
					MessageBox dialog = new MessageBox(parent.getShell());
					dialog.setMessage("Incorect location path!");
					dialog.open();
					return;
				}

				if (!checkDir(parent, dir, text.getText()))
				{
					return;
				}

				progressBar.setSelection(0);
				runButton.setEnabled(false);

				progressBar.setMaximum(PROGRESS_MAX);
				progressBar.setVisible(true);
				top.update();

				getTiles(parent, group, fileChooser.getText(), dir, text.getText(), comboTile, cutter, pointTopLeft, pointBottomRight);
			}

		});

		gps.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				boolean selection = gps.getSelection();

				gpsChooserTopLeft.setActive(selection);
				gpsChooserBottomRight.setActive(selection);
			}

		});

	}

	private String getDir(RadioGroup radioGroup)
	{
		switch (radioGroup.getSelected())
		{
		case RadioGroup.LOCATION:

			return radioGroup.getDirText();
		case RadioGroup.PROJECT:

			return radioGroup.getSelectedPath();

		}
		return null;
	}

	protected boolean checkDir(Composite parent, String dir, String name)
	{
		String sign = "";

		if (!dir.endsWith("\\"))
		{
			sign = "\\";
		}

		String fileName = dir + sign + name;
		File file = new File(fileName);

		if (file.exists())
		{
			boolean answer = MessageDialog.openQuestion(parent.getShell(), ATTENTION, "Directory \"" + fileName + "\""
					+ " already exist. Do you want to continue(its will remove old files!)?");

			if (answer)
			{
				FileUtils.deleteDir(file);
			}

			return answer;
		}

		return true;
	}

	private void setValue(Composite top, final ProgressBar progressBar, final int value)
	{
		top.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				progressBar.setSelection(value);
			}
		});
	}

	private void setCompliete(Composite top, final Button runButton, final ProgressBar progressBar)
	{
		top.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				runButton.setEnabled(true);
			}
		});
	}

	private FileChooser fillMapSource(Composite mapSource, Preferences prefs)
	{
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		GridData fileDir = new GridData();
		fileDir.grabExcessHorizontalSpace = true;
		fileDir.horizontalAlignment = GridData.FILL;

		mapSource.setLayoutData(fileDir);
		mapSource.setLayout(layout);

		Label label = new Label(mapSource, SWT.WRAP);
		label.setText(MAP_SOURCE);

		final FileChooser fileChooser = new FileChooser(mapSource, Mode.OPEN);
		fileChooser.setLayoutData(fileDir);
		fileChooser.setText(prefs.get(FileChooser.FILE_PATH, ""));

		return fileChooser;
	}

	private Text fillName(Composite parent)
	{
		Text text = new Text(parent, SWT.SINGLE | SWT.BORDER);

		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gd);
		text.setText(DEFAULT_NAME);

		return text;
	}

	private void fillGroup(Group group)
	{
		GridLayout layout = new GridLayout();
		layout.marginWidth = 15;
		layout.numColumns = 1;

		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;

		group.setLayoutData(gd);
		group.setLayout(layout);
		group.setText(EXPORT_OPTIONS);
	}

	private void fillTop(Composite top, ScrolledComposite scrolledComposite)
	{
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		top.setLayoutData(data);
		top.setLayout(layout);

		scrolledComposite.setMinSize(500, 250);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setContent(top);
	}

	protected void getTiles(final Composite parent, final Composite banner, final String imgPath, final String saveDirPath, final String name, final Combo combo,
			final Cutter cutter, final PointVO pointTopLeft, final PointVO pointBottomRight)
	{
		MessageBox dialog = new MessageBox(parent.getShell());

		if (name == null || name.contentEquals(""))
		{
			dialog.setMessage(ENTER_NAME_FIRST);
			dialog.open();
			return;
		}

		else if (imgPath == null || imgPath.contentEquals(""))
		{
			dialog.setMessage(CHOOSE_IMAGE_FIRST);
			dialog.open();
			return;
		}

		else if (saveDirPath == null || saveDirPath.contentEquals(""))
		{
			dialog.setMessage(CHOOSE_SAVE_DIR_FIRST);
			dialog.open();
			return;
		}

		File f = new File(imgPath);

		if (f.exists())
		{
			cutter.startCuttingAndroid(imgPath, saveDirPath, name, Integer.parseInt(combo.getItem(combo.getSelectionIndex())), pointTopLeft, pointBottomRight);
		}
		else
		{
			dialog.setMessage(INCORECT_FILE_NAME);
			dialog.open();
			return;
		}

	}

	public Image loadImage(Composite composite, String filename)
	{
		File f = new File(filename);
		Image sourceImage = null;

		if (f.exists())
		{
			sourceImage = new Image(composite.getDisplay(), filename);
		}

		return sourceImage;
	}
}