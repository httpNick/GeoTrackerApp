package com.example.httpnick.geotracker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * A class that handles user account information
 * @author Nick Duncan
 */
public class UserAccount extends ActionBarActivity {
    SharedPreferences prefs;

    /** Reference to the sqlite db helper for this app*/
    private LocationDatabaseHelper db;

    ActionBarActivity that;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        that = this;
        setContentView(R.layout.activity_user_account);
        Button traj = (Button) findViewById(R.id.trajectoryButton);
        Button logout = (Button) findViewById(R.id.logoutButton);
        db = new LocationDatabaseHelper(getApplicationContext());
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        TextView loggedInEmail = (TextView) findViewById(R.id.currentlyLoggedInEmail);
        loggedInEmail.setText(loggedInEmail.getText().toString()
                + " Logged in as: " + prefs.getString("email", "email"));
        System.out.println(prefs.getString("userid", "default"));


        traj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Trajectory.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMyServiceRunning(GPSService.class)) {
                    Intent serviceIntent = new Intent(that, GPSService.class);
                    stopService(serviceIntent);
                }
                prefs.edit().putBoolean("loggedIn", false).commit();
                //prefs.edit().putString("userid", "default").commit();
                prefs.edit().remove("userid").commit();
                db.clearDatabase();
                Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
         //
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

// THIS CREATES THE OPTIONS THING AT THE TOP RIGHT.
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_account, menu);
        return true;
    } */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
