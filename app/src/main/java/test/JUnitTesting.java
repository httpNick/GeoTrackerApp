package test;

import android.test.InstrumentationTestCase;

import com.example.httpnick.geotracker.LocationPackage;

import junit.framework.*;

/**
 * Created by Jon Nick and Matt on 6/3/2015.
 */
public class JUnitTesting extends InstrumentationTestCase {

    LocationPackage locationPackage = new LocationPackage("123",123,0,0,15,0);


    public void testLocationPackage() throws Exception {

        Assert.assertEquals("Testing Long", locationPackage.longitude,
                new LocationPackage("tetetew", 111, 0, 55, 13, 9).longitude);

        Assert.assertEquals("Testing Lat", locationPackage.latitude,
                new LocationPackage("HAHAHA", 111, 7272, 0, 43, 19).latitude);

        Assert.assertTrue("Testing equals method", locationPackage.equals(locationPackage));


    }
}
