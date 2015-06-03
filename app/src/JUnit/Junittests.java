package com.example.httpnick.geotracker;

import com.example.httpnick.geotracker.LocationPackage;

/**
 * Created by Jon on 6/3/2015.
 */
public class Junittests {

    LocationPackage locationPackage = new LocationPackage("123",123,0,0,15,0)

    @Test
    public void testLocationPackage() {

        assertEquals("Testing Long",locationPackage.longitude,
                new LocationPackage("tetetew",111,0,55,13,9).longitude);

        assertEquals("Testing Lat",locationPackage.latitude,
                new LocationPackage("HAHAHA",111,7272,0,43,19).latitude);

        assertTrue("Testing equals method",locationPackage.equals(locationPackage));

    }

}
