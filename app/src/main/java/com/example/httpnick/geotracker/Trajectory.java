package com.example.httpnick.geotracker;

import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that displays the GPS coordinate information.
 * @author Nick Duncan
 */
public class Trajectory extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    Intent serviceIntent;
    private LocationDatabaseHelper db;
    TextView userTextView;
    SharedPreferences sp;

    private HashMap<TextView, int[]> times;

    private TextView startDateDisplay;
    private TextView startTimeDisplay;
    private Button startPickDate;
    private Button startPickTime;

    private TextView endDateDisplay;
    private TextView endTimeDisplay;
    private Button endPickDate;
    private Button endPickTime;


    private TextView activeDateDisplay;
    private TextView activeTimeDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trajectory);
        startDateDisplay = (TextView) findViewById(R.id.startDateText);
        startTimeDisplay = (TextView) findViewById(R.id.startTimeText);
        startPickDate = (Button) findViewById(R.id.trajectoryDatePickButton);
        startPickTime = (Button) findViewById(R.id.trajectoryTimeButton);

        endDateDisplay = (TextView) findViewById(R.id.endDateText);
        endTimeDisplay = (TextView) findViewById(R.id.endTimeText);
        endPickDate = (Button) findViewById(R.id.trajectoryEndDatePickButton);
        endPickTime = (Button) findViewById(R.id.trajectoryEndTimeButton);

        times = new HashMap<TextView, int[]>();

        Bundle b = new Bundle();
        b.

        startPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog(startDateDisplay);
            }
        });

        startPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showTimePickerDialog(startTimeDisplay);
            }
        });

        endPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDatePickerDialog(endDateDisplay);
            }
        });

        endPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showTimePickerDialog(endTimeDisplay);
            }
        });


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
            //sb.append(array[i]+"\n" + "\n");

            i++;
            cursor.moveToNext();
        }
        //userTextView.setText(sb.toString());

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
    public void showTimePickerDialog(TextView v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(this.getFragmentManager(), "timePicker");
        activeTimeDisplay = v;
    }
    public void showDatePickerDialog(TextView v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getFragmentManager(), "datePicker");
        activeDateDisplay = v;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        updateDateDisplay(activeDateDisplay, year, month, day);
    }

    public void onTimeSet(TimePicker view, int hour, int minute) {
        updateTimeDisplay(activeTimeDisplay, hour, minute);
    }

    public void updateDateDisplay(TextView view, int year, int month, int day) {
        view.setText("Y: " + year + " M: " + month + " D: " + day);
        int[] temp = new int[5];
        temp[0] = year;
        temp[1] = month;
        temp[2] = day;
        if(times.containsKey(view)) {
            temp[3] = times.get(view)[3];
            temp[4] = times.get(view)[4];
            times.put(view, temp);
        } else {
            temp[3] = 0;
            temp[4] = 0;
            times.put(view, temp);
        }
    }

    public void updateTimeDisplay(TextView view, int hour, int minute) {
        view.setText("M: " + hour + " S: " + minute);
        int[] temp = new int[5];
        temp[3] = hour;
        temp[4] = minute;
        if(times.containsKey(view)) {;
            temp[0] = times.get(view)[0];
            temp[1] = times.get(view)[1];
            temp[2] = times.get(view)[2];
            times.put(view, temp);
        } else {
            temp[0] = 0;
            temp[1] = 0;
            temp[2] = 0;
            times.put(view, temp);
        }
    }
}

