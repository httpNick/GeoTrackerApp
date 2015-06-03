package com.example.httpnick.geotracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @class LocationPackage class used as an object representation of a user location data
 * @author Nick Duncan
 * @author Jon Sobocinski
 */
public class LocationPackage {
    String id;
    Object heading;
    Object longitude;
    Object latitude;
    Object speed;
    Object time;


    /**
     * Sets the user data being passed in as the users current location
     * @param id
     * @param heading
     * @param longitude
     * @param latitude
     * @param speed
     * @param time
     */
    public LocationPackage(String id, float heading, double longitude,
                           double latitude, float speed, long time) {
        this.id = id;
        this.heading = heading;
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.time = time;
    }

    public LocationPackage(String heading, String longitude, String latitude,
                           String speed, String time) {

        this.heading = heading;
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this.latitude.equals(((LocationPackage) o).latitude)
                && this.longitude.equals(((LocationPackage) o).longitude)){
            return true;
        }else {
            return false;
        }

    }

    /**
     * toString method the prints a nice view of the users location,.
     * @return A string of a locaiton point of the user
     */
    @Override
    public String toString() {
        long unixSeconds = (long) time;
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
