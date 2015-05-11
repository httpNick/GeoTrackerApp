package com.example.httpnick.geotracker;



import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by httpnick on 4/20/15.
 */
public class Trajectory extends ActionBarActivity {
    Intent serviceIntent;
    BroadcastReceiver receiver;
    private LocationDatabaseHelper db;
    TextView userTextView;
    SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trajectory);
        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                System.out.println("Here");
                Log.d("GPS Tag", "It works");
//                String s = intent.getStringExtra(GPSService.)
            }
        };
        serviceIntent = new Intent(this, GPSService.class);
    }


    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            startService(serviceIntent);
        } else {
            stopService(serviceIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        db = new LocationDatabaseHelper(getApplicationContext());
        userTextView = (TextView) findViewById(R.id.user_text_view);
        Cursor cursor = db.querySingleUser(sp.getString("userid", "default"));
        LocationPackage array[] = new LocationPackage[cursor.getCount()];
        int i = 0;

        cursor.moveToFirst();
        StringBuilder sb = new StringBuilder();
        while (cursor.isAfterLast() == false) {
            LocationPackage lp = new LocationPackage(cursor.getString(1),
                    cursor.getFloat(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getFloat(5),
                    cursor.getLong(6));
            array[i] = lp;
            //System.out.println(array[i]); //display this on screen
            sb.append(array[i]+"\n");

            i++;
            cursor.moveToNext();
        }
        userTextView.setText(sb.toString());

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter("message"));
        ToggleButton theSwitch = (ToggleButton) findViewById(R.id.switchGPS);

        if (isMyServiceRunning(GPSService.class)) {
            theSwitch.setChecked(true);
        } else {
            theSwitch.setChecked(false);
        }

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
}
