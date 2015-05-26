package com.example.httpnick.geotracker;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author Matthew Moore
 * 5/25/15.
 */
public class DisplayMap extends FragmentActivity implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private double lat;
    private double lon;
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.map);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
              lat = location.getLatitude();
              lon = location.getLongitude();

            }
        });
        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title("Marker"));
    }
}
