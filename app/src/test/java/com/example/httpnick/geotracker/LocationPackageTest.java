package com.example.httpnick.geotracker;

import junit.framework.TestCase;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class is a test case for the LocationPackage class.
 *
 * @author mmills
 */
public class LocationPackageTest extends TestCase {

    LocationPackage locations;
    String id = "seattle";
    float heading = (float) 20.178545;
    double longitude = 122.20;
    double lat = 47.37;
    float speed = (float) 65.4587;
    long time = System.currentTimeMillis();

    /**
     * Setup method initiazlies the Location package object.
     * @throws Exception
     */
    public void setUp() throws Exception {
        super.setUp();

        locations = new LocationPackage(id,heading,longitude, lat,speed, time);
    }

    /**
     *Tests that the object was properly instantiated by comparing my string
     * to the toString the object produces.
     *
     * @throws Exception
     */
    @Test
    public void testToString() throws Exception {

        long unixSeconds = time;
        Date date = new Date(unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        String formattedDate = sdf.format(date);

        String testString = "Heading: " + heading +
                " longitude: " + longitude +
                " latitude: " + lat +
                " speed: " + speed +
                " Date: " + formattedDate;

        assertEquals(testString, locations.toString());
    }
}