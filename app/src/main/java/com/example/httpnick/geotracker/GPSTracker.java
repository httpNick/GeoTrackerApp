package com.example.httpnick.geotracker;

import android.bluetooth.BluetoothClass.Service;
import android.location.LocationListener;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.location.LocationManager;
import android.content.Context;
import android.app.Activity;



/**
 * Created by matthewmoore on 5/6/15.
 */
public class GPSTracker extends Activity implements LocationListener {
    //flag for GPS status
    boolean isGPSEnabled = false;

    //flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    protected LocationManager locationManager;

    private Location location;

    //test functionality
    double lat;
    double lon;

    Context context;

    /**
     *
     * @param context
     */
    public GPSTracker(Context context) {
        this.context = context;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();

        } else {
            lat = 8675309;
            lon = 8675309;
        }
        System.out.println("Latitude: " + lat + ". Longitude: " + lon);
    }


    public void onProviderEnabled(String string) {

    }

    public void onStatusChanged(String string, int i, Bundle bundle) {

    }

    public void onProviderDisabled(String string) {

    }

    public void onLocationChanged(Location l) {
        System.out.println("LOCATION CHANGED");
    }
}
