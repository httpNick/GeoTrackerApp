package com.example.httpnick.geotracker;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Toast;

import com.robotium.solo.Solo;

/**
 * A class that tests the RegisterUser.java class. Passes in an e-mail address, a password,
 * and an answer to the security question. Then clicks on the register button.
 * @author Matthew Moore
 * 5/18/15
 */
public class RobotiumRegisterUserTest extends ActivityInstrumentationTestCase2<RegisterUser> {

    private Solo solo;

    /**
     * Constructor passes in the RegisterUser.java class to ActivityInstrumentationTestCase2
     */
    public RobotiumRegisterUserTest() {
        super(RegisterUser.class);
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
     * Enter an e-mail address into the first text field.
     * Enter a password into the second text field.
     * Confirm above password in the third text field.
     * Enter mother's maiden name in the fourth text field.
     * Click on the register button.
     */
    public void testRegisterButton() {
        solo.enterText(0, "username@mail.com");
        solo.enterText(1, "password");
        solo.enterText(2, "password");
        solo.enterText(3, "maiden");
        solo.clickOnButton("Register");

        //User waitForText(String message) when testing for Toasts, per Google's Android API recommendations
        boolean registered = solo.waitForText("Account created Successfully!");
        assertTrue("Successfully registered a new user", registered);
    }


    /**
     * Exit once the testing is complete.
     * @throws Exception
     */
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}