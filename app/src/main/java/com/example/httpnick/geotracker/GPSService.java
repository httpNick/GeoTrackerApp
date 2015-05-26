package com.example.httpnick.geotracker;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @class GPSService
 * @author Nick Duncan
 * A service class that is used to provide, monitor and interact with the GPS portion of the app.
 * Provides the main service that gets the users current location that will be pushed to both
 * the local database as well as the web service db.
 */
public class GPSService extends Service {

    /** tag used for logs*/
    private static final String TAG = "GPSService";
    /** preferences for the current application */
    private SharedPreferences pref;
    /** LocationManager to manage location functionality.*/
    private LocationManager mLocationManager;
    /** Interval to pull locations and place into local sqlite DB*/
    private static final int LOCATION_INTERVAL = 6000; //6 seconds
    /** Interval to push local sqlite DB contents to the web service. */
    private static final long PUSH_NETWORK_INTERVAL = 60000;
    /** Distance in which to look for a change.*/
    private static final int LOCATION_DISTANCE = 0;
    /** Reference to the sqlite db helper for this app*/
    private LocationDatabaseHelper db;
    /** broadcast manager for notifying users*/
    private LocalBroadcastManager broadcastManager;
    /** URL to be used to push data to web service*/
    private String DB_URL;

    /**
     * Private inner class to manage location events.
     */
    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        /**
         *
         * @param location takes the current users locations when the users location has changed from the last one
         */
        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String theId = pref.getString("userid", "default");
            long unixTime = System.currentTimeMillis() / 1000L;
            LocationPackage insertion = new LocationPackage(theId, mLastLocation.getBearing(), mLastLocation.getLongitude(),
                    mLastLocation.getLatitude(), mLastLocation.getSpeed(), unixTime);
            System.out.println(insertion);
            db.insertLocation(insertion);
            mLastLocation.set(location);
        }
        /**
         *
         * @param provider used when the gps service provider has been disabled
         */
        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
        /**
         *
         * @param provider used when the gps service provider has been enabled
         */
        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
        /**
         *
         * @param provider the name of the provider
         * @param status the current status that has been changed
         * @param extras the extras being used (wrapper)
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)};

    /**
     * Testing method that always returns null
     * @param arg0
     * @return null returns
     */
    @Override
    public IBinder onBind(Intent arg0) {
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
     * Written to catch errors when there is known provider or location available
     */
    @Override
    public void onCreate() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        // get reference to local db to be used by the service.
        db = new LocationDatabaseHelper(getApplicationContext());
        try {
            // start to request location updates to grab (provided by the network).
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            // start to request location updates to grab (provided by GPS).
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
            final Handler handler = new Handler();
            // Timer to be used to send off data to web service at given interval.
            Timer timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @SuppressWarnings("unchecked")
                        public void run() {
                            try {
                                // Cursor reference to the query made on the local db.
                                Cursor cursor = db.queryLocation();
                                int i = 0;
                                cursor.moveToFirst();
                                /** Loop logic:
                                 *  - for each row returned by the query:
                                 *          - Save data into a LocationPackage object
                                 *          - Formulate the web service URL based off the data
                                 *          - Execute web task
                                 *          - Move to the next row with the cursor
                                 *          - Clear the local db*/
                                while (!cursor.isAfterLast()) {
                                    LocationPackage lp = new LocationPackage(cursor.getString(1),
                                            cursor.getFloat(2),
                                            cursor.getDouble(3),
                                            cursor.getDouble(4),
                                            cursor.getFloat(5),
                                            cursor.getLong(6));
                                    DB_URL = "http://450.atwebpages.com/logAdd.php?lat="+Math.round((double) lp.latitude*10)/10+
                                            "&lon="+Math.round((double) lp.longitude*10)/10+"&speed="+lp.speed+
                                            "&heading="+Math.round((float) lp.heading*10)/10+"&timestamp="+lp.time+
                                            "&source=" + lp.id;
                                    DownloadWebPageTask task = new DownloadWebPageTask();
                                    task.execute(new String[]{DB_URL});
                                    //sendResult(array[i].toString());
                                    i++;
                                    cursor.moveToNext();
                                    System.out.println("PUSHED TO THE NETWORK!!!");
                                    db.clearDatabase();
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            timer.schedule(doAsynchronousTask, 0, PUSH_NETWORK_INTERVAL);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    /**
     *
     * @param message The message that will be broadcasted and stored
     * @param message
     */
    public void sendResult(String message) {
        Intent intent = new Intent("Result");
        if (message != null)
            intent.putExtra("Message", message);
        broadcastManager.sendBroadcast(intent);
    }

    /**
     * Removes the location listeners
     */
    @Override
    public void onDestroy() {
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

    /**
     * Running the loading of the JSON in a separate thread.
     * Code adapted from http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
     */
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);
                /** Check to make sure the web service successfully processed the post*/
                String pass = (String) obj.get("result");
                if (pass.equals("success")) {
                    System.out.println("SUCCESSFULLY PUSHED TO THE WEBSERVICE!!");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}