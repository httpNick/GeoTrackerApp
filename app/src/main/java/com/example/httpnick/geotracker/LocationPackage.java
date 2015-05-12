package com.example.httpnick.geotracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by httpnick on 5/10/15.
 */
public class LocationPackage {
    String id;
    float heading;
    double longitude;
    double latitude;
    float speed;
    long time;


    public LocationPackage(String id, float heading, double longitude,
                           double latitude, float speed, long time) {
        this.id = id;
        this.heading = heading;
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.time = time;

    }

    @Override
    public String toString() {
        long unixSeconds = time;
        Date date = new Date(unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        String formattedDate = sdf.format(date);
        return "Heading: " + heading +
                " longitude: " + longitude +
                " latitude: " + latitude +
                " speed: " + speed +
                " Date: " + formattedDate;
    }
}
