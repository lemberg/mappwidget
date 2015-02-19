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

package com.ls.widgets;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class HomeActivity 
    extends Activity
{

    
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        initWidgets();
    }
    
    
    private void initWidgets()
    {
        Button button1 = (Button) findViewById(R.id.button_sample_1);
        button1.setOnClickListener(new View.OnClickListener()
        {    
            public void onClick(View arg0)
            {
                doOpenSample1();
            }
        });
        
        Button button2 = (Button) findViewById(R.id.button_sample_2);
        button2.setOnClickListener(new View.OnClickListener()
        {    
            public void onClick(View arg0)
            {
                doOpenSample2();
            }
        });
        
        Button button3 = (Button) findViewById(R.id.button_sample_3);
        button3.setOnClickListener(new View.OnClickListener()
        {    
        	public void onClick(View arg0)
            {
        		doOpenExBrowseActivity();
            }
        });
    }
    
    
    private void doOpenExBrowseActivity()
    {
    	Intent intent = new Intent(this, ExBrowseMapActivity.class);
        startActivity(intent);
    }
    
    private void doOpenSample1()
    {
        Intent intent = new Intent(this,Sample1Activity.class);
        startActivity(intent);
    }
    
    
    private void doOpenSample2()
    {
        Intent intent = new Intent(this,Sample2Activity.class);
        startActivity(intent);
    }    
    
}
