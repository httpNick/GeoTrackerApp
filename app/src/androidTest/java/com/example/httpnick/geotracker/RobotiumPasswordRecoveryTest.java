package com.example.httpnick.geotracker;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * A test class to check the Password Recovery function.
 * @author Matthew Moore
 * 5/18/15.
 */
public class RobotiumPasswordRecoveryTest extends ActivityInstrumentationTestCase2<PasswordRecovery> {

    private Solo solo;

    /**
     * Constructor passes in the PasswordRecovery.java class to ActivityInstrumentationTestCase2
     */
    public RobotiumPasswordRecoveryTest() {
        super(PasswordRecovery.class);
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
     * Enter an invalid e-mail.
     * Test for alert dialog.
     * Enter a valid e-mail.
     * Test for success.
     */
    public void testRecovery() {
        solo.enterText(0, "asdfa@gmail.com");
        solo.clickOnButton("Send email");
        solo.clickOnButton("OK");
        solo.clearEditText(0);
        solo.enterText(0, "moorem27@uw.edu");
        solo.clickOnButton("Send email");
        boolean recovered = solo.searchText("Success!");
        assertTrue("Successfully logged in", recovered);
    }

    /**
     * Exit once the testing is complete.
     * @throws Exception
     */
    public void tearDown() throws Exception {
        solo.clickOnButton("OK");
        solo.finishOpenedActivities();
    }
}
