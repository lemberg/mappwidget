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

package com.ls.widgets.map.providers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.ls.widgets.map.interfaces.MapLocationListener;

public final class GPSLocationProvider implements LocationListener
{
	private static final String TAG = "GPSLocationProvider";

	private LocationManager locManager;

	private int refreshRate;
	private int minDistance;
	
	private MapLocationListener listener;

	private long mLastLocationMillis;
	private Location mLastLocation;
	
	private boolean isGpsFix;
	private boolean filterNonGPSFix;
	
	private boolean started;
	private boolean permGranted;
	private boolean passiveMode;
	
	private MyGPSListener gpsStatusListener;

	
	public GPSLocationProvider(Context context)
	{
		locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		started = false;
		passiveMode = false;
		
		gpsStatusListener = new MyGPSListener();
		
		PackageManager mgr = context.getPackageManager();
		if (mgr.checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
			permGranted = true;
		} else {
			permGranted = false;
		}
	}

	
	public void setMinRefreshTime(int refreshRate)
	{
		this.refreshRate = refreshRate;
	}
	
	
	public void setMinRefreshDistance(int minDistance)
	{
		this.minDistance = minDistance;
	}
	

	public void setMapLocationListener(MapLocationListener listener)
	{
		this.listener = listener;
	}
	
	
	/**
	 * Registers location update listeners.
	 * @param passiveMode
	 */
	public void start(boolean passiveMode)
	{
		if (!permGranted) {
			Log.w(TAG,"Can't start receiving the location updates. You have no ACCESS_FINE_LOCATION permission enabled.");
			return;
		}
		
		if (started) {
			Log.w(TAG, "Can't start receiving the location updates. Already started.");
			return;
		}
		
		started = true;
		
		try {
			this.passiveMode = passiveMode;
			
			if (passiveMode) {
				locManager.requestLocationUpdates("passive", 0, 0, this);
				Log.d(TAG, "Registering for receiving updates from passive provider.");
			} else {
				
				locManager.addGpsStatusListener(gpsStatusListener);
				
				if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, refreshRate, minDistance, this);
				}
				
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, refreshRate, minDistance, this);
			}
			
			
			Location loc1 = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			Location loc2 = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			
			if (loc1 != null && loc2 != null) {
				if (loc1.getAccuracy() < loc2.getAccuracy()) {
					onLocationChanged(loc1);
				} else {
					onLocationChanged(loc2);
				}			
			} else {
				Location loc = loc1 != null? loc1: loc2;
				
				if (loc != null) {
					onLocationChanged(loc);
				}
			}
			
			if (listener != null) {
				listener.onChangePinVisibility(true);
			}
				
		} catch (SecurityException e) {
			Log.w(TAG, "Can't get location provider due to " + e);
		}
	}
	
	
	public void stop()
	{
		locManager.removeGpsStatusListener(gpsStatusListener);
		if (listener != null) {
			listener.onChangePinVisibility(false);
		}
		started = false;
		locManager.removeUpdates(this);
	}

	
	@Override
	public void onLocationChanged(Location location) 
	{
		if (location == null) return;
		
		if (!passiveMode) {
	    	mLastLocationMillis = SystemClock.elapsedRealtime();

		    if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
		    	if (!filterNonGPSFix) {
		        	   
		    		listener.onMovePinTo(location);
		    	}
		    } else if (location.getProvider().equals(LocationManager.GPS_PROVIDER)){
		    	listener.onMovePinTo(location);
		    }
		    
		    mLastLocation = location;
	    } else {
	    	listener.onMovePinTo(location);
	    }
	}

	
	@Override
	public void onProviderDisabled(String name) {
		Log.d(TAG, "Provider disabled: " + name);
		
		
	}

	
	@Override
	public void onProviderEnabled(String name) {
		Log.d(TAG, "Provider enabled: " + name);
		
		if (started && !passiveMode) {
			if (name.equals(LocationManager.GPS_PROVIDER)) {
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, refreshRate, minDistance, this);
			}
		}
	}
	

	@Override
	public void onStatusChanged(String name, int status, Bundle arg2) 
	{
		Log.d(TAG, "Status of "+ name + " changed for " + statusToString(status));
	}
	
	
	private String statusToString(int status)
	{
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			return "OUT_OF_SERVICE";
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			return "TEMPORARILY_UNAVAILABLE";
		case LocationProvider.AVAILABLE:
			return "AVAILABLE:";
		}
		
		return "UNKNOWN";
	}
	
	
	private class MyGPSListener implements GpsStatus.Listener {
	    public void onGpsStatusChanged(int event) {
	    	
	    	if (passiveMode) {
	    		return;
	    	}
	    	
	        switch (event) {
	            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
	                if (mLastLocation != null)
	                    isGpsFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;

	                if (isGpsFix) { // A fix has been acquired.
	                	filterNonGPSFix = true;
	                    // Do something.
	                } else { // The fix has been lost.
	                	filterNonGPSFix = false;
	                    // Do something.
	                }

	                break;
	            case GpsStatus.GPS_EVENT_FIRST_FIX:
	                // Do something.
	            	isGpsFix = true;
	            	filterNonGPSFix = true;

	                break;
	        }
	    }
	}
}
