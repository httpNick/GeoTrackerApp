package com.example.httpnick.geotracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by httpnick on 5/25/15.
 */
public class LocationAdapter extends ArrayAdapter<LocationPackage> {

    public LocationAdapter(Context context, ArrayList<LocationPackage> objects) {
        super(context, 0, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LocationPackage lp = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_loc_list_item
                    , parent, false);
        }

        TextView lat = (TextView) convertView.findViewById(R.id.loc_lat);
        TextView lon = (TextView) convertView.findViewById(R.id.loc_lon);
        TextView speed = (TextView) convertView.findViewById(R.id.loc_speed);
        TextView heading = (TextView) convertView.findViewById(R.id.loc_heading);
        TextView time = (TextView) convertView.findViewById(R.id.loc_time);

        lat.setText("Latitude: " + lp.latitude);
        lon.setText("Longitude: " + lp.longitude);
        speed.setText("Speed: " + lp.speed);
        heading.setText("Heading: " + lp.heading);
        time.setText("Time: " + lp.time);

        return convertView;
    }
}
