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

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;

public class Model
{

    public static final String CAT_MAIN_ATTRACTIONS = "category.main.attractions";
    public static final String CAT_KIDS = "category.kids";
    public static final String CAT_SPORT_AND_LEISURE = "category.sport.and.leisure";
    
    
    private List<Location> locations;
    
    private List<Category> categories;
    
    
    public Model()
    {
        createCategories();
        createLocations();
    }
    
    
    public List<Category> getCategories()
    {
        return categories;
    }
    
    
    public List<Location> getLocations()
    {
        return locations;
    }
    
    
    
    private void createCategories()
    {
        this.categories = new ArrayList<Category>();
        
        
        Category category = new Category();
        category.setId(CAT_MAIN_ATTRACTIONS);
        category.setName("Main Attractions");
        
        this.categories.add(category);
        
        category = new Category();
        category.setId(CAT_KIDS);
        category.setName("Kids In the Park");
        
        this.categories.add(category);
        
        
        category = new Category();
        category.setId(CAT_SPORT_AND_LEISURE);
        category.setName("Sport and Leisure");
        
        this.categories.add(category);
    }
    
    
    private void createLocations()
    {
        this.locations = new ArrayList<Location>();
        
        Point point = new Point(20, 100);
        
        Location location = new Location();
        location.setId("id.thehub");
        location.setTitle("The Hub");
        location.setCategoryId(CAT_MAIN_ATTRACTIONS);
        location.setPoint(point);
        
        this.locations.add(location);
        
        point = new Point(20, 100);
        location = new Location();
        location.setId("id.queen.mary.garden");
        location.setTitle("Queen Mary's Garden");
        location.setCategoryId(CAT_MAIN_ATTRACTIONS);
        location.setPoint(point);
        
        this.locations.add(location);
        
        point = new Point(20, 100);
        location = new Location();
        location.setId("id.wildlifegarden");
        location.setTitle("Wildlife Garden");
        location.setCategoryId(CAT_KIDS);
        location.setPoint(point);
        
        this.locations.add(location);
        
        point = new Point(20, 100);
        location = new Location();
        location.setId("id.boatinglake");
        location.setTitle("Boating Lake");
        location.setCategoryId(CAT_KIDS);
        location.setPoint(point);
        
        this.locations.add(location);
        
        point = new Point(20, 100);
        location = new Location();
        location.setId("id.openairtheatre");
        location.setTitle("Open Air Theatre");
        location.setCategoryId(CAT_SPORT_AND_LEISURE);
        location.setPoint(point);
        
        this.locations.add(location);
        
        point = new Point(20, 100);
        location = new Location();
        location.setId("id.tenniscenter");
        location.setTitle("Tennis Center");
        location.setCategoryId(CAT_SPORT_AND_LEISURE);
        location.setPoint(point);
        
        this.locations.add(location);
    }
}
