package com.example.httpnick.geotracker;

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
        return "id: " + id + " heading: " + heading +
                " longitude: " + longitude + " latitude: " + latitude +
                " speed: " + speed + " time: " + time;
    }
}
