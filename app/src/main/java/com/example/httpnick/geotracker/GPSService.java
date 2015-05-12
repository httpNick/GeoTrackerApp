package com.example.httpnick.geotracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * @class GPSService
 * @author Nick Duncan
 * A service class that is used to provide, monitor and interact with the GPS portion of the app.
 * Provides the main service that gets the users current location that will be pushed to both
 * the local database as well as the web service db.
 */
public class GPSService extends Service {

    private static final String TAG = "GPSService";
    private SharedPreferences pref;
    private LocationManager mLocationManager;
    private static final int LOCATION_INTERVAL = 6000; //60 seconds
    private static final int LOCATION_DISTANCE = 0;
    private LocationDatabaseHelper db;
    private int mId = 1;
    private TextView locationData;
    private LocalBroadcastManager broadcastManager;
    public String message = "Hello, world";


private class LocationListener implements android.location.LocationListener {
    Location mLastLocation;
    public LocationListener(String provider)
    {
        Log.e(TAG, "LocationListener " + provider);
        mLastLocation = new Location(provider);
    }

    /**
     *
     * @param location takes the current users locations when the users location has changed from the last one
     */
    @Override
    public void onLocationChanged(Location location)
    {
        Log.e(TAG, "onLocationChanged: " + location);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String theId = pref.getString("userid", "default");
        long unixTime = System.currentTimeMillis() / 1000L;
        LocationPackage insertion = new LocationPackage(theId, mLastLocation.getBearing(), mLastLocation.getLongitude(),
                mLastLocation.getLatitude(), mLastLocation.getSpeed(), unixTime);
        db.insertLocation(insertion);
        Cursor cursor = db.queryLocation();
        LocationPackage array[] = new LocationPackage[cursor.getCount()];
        int i = 0;

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            LocationPackage lp = new LocationPackage(cursor.getString(1),
                    cursor.getFloat(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getFloat(5),
                    cursor.getLong(6));
            array[i] = lp;
//            System.out.println(array[i]); //display this on screen
            sendResult(array[i].toString());
            i++;
            cursor.moveToNext();
        }
        mLastLocation.set(location);
    }

    /**
     *
     * @param provider used when the gps service provider has been disabled
     */
    @Override
    public void onProviderDisabled(String provider)
    {
        Log.e(TAG, "onProviderDisabled: " + provider);
    }

    /**
     *
     * @param provider used when the gps service provider has been enabled
     */
    @Override
    public void onProviderEnabled(String provider)
    {
        Log.e(TAG, "onProviderEnabled: " + provider);
    }

    /**
     *
     * @param provider the name of the provider
     * @param status the current status that has been changed
     * @param extras the extras being used (wrapper)
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.e(TAG, "onStatusChanged: " + provider);
    }
}

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)};

    /**
     * Testing method that always returns null
     * @param arg0
     * @return null returns
     */
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    /**
     *
     * @param intent the inent used
     * @param flags flags being passed
     * @param startId the start id of the user
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "service starting");
        return START_STICKY;
    }

    /**
     * Method that trys to start a broadcast maneger and get the network provider as well as location.
     * Written to catch errors when there is know provider or location avialble
     */
    @Override
    public void onCreate()
    {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        db = new LocationDatabaseHelper(getApplicationContext());
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    /**
     *
     * @param message The message that will be broadcasted and stored
     */
    public void sendResult(String message) {
        Intent intent = new Intent("Result");
        if(message != null)
            intent.putExtra("Message", message);
       broadcastManager.sendBroadcast(intent);
    }

    /**
     * Removes the location listeners
     */
    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }

    /**
     * Starts the location manager
     */
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}