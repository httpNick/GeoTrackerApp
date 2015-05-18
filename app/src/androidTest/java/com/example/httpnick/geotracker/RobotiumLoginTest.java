package com.example.httpnick.geotracker;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * A class to test the Login feature from the MainActivity.java class. Enters a pre-verified
 * user name and password, tests the login button, and clicks logout once a user has successfully
 * logged in.
 * @author Matthew Moore
 * 5/18/15.
 */
public class RobotiumLoginTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    /**
     * Constructor passes in the MainActivity.java class to ActivityInstrumentationTestCase2
     */
    public RobotiumLoginTest() {
        super(MainActivity.class);
    }

    /**
     * Call super.setUp() and initial Solo solo.
     * @throws Exception
     */
    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Enter a valid e-mail address.
     * Enter a valid password.
     * Test for log in success.
     */
    public void testLogin() {
        solo.enterText(0, "moorem27@uw.edu");
        solo.enterText(1, "password");
        solo.clickOnButton("Login");
        boolean in = solo.waitForText("Logged in as: moorem27@uw.edu");
        assertTrue("Successfully logged in", in);
    }

    /**
     * Exit once the testing is complete.
     * @throws Exception
     */
    public void tearDown() throws Exception {
        solo.clickOnButton("Logout");
        solo.finishOpenedActivities();
    }
}
