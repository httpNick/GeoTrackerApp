package com.example.httpnick.geotracker;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.Arrays;
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
    Bundle b;
    SharedPreferences sp;

    private HashMap<TextView, int[]> times;
    private Button viewLocations;

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

    FragmentActivity that;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trajectory);
        viewLocations = (Button) findViewById(R.id.viewLocationsButton);
        startDateDisplay = (TextView) findViewById(R.id.startDateText);
        startTimeDisplay = (TextView) findViewById(R.id.startTimeText);
        startPickDate = (Button) findViewById(R.id.trajectoryDatePickButton);
        startPickTime = (Button) findViewById(R.id.trajectoryTimeButton);

        endDateDisplay = (TextView) findViewById(R.id.endDateText);
        endTimeDisplay = (TextView) findViewById(R.id.endTimeText);
        endPickDate = (Button) findViewById(R.id.trajectoryEndDatePickButton);
        endPickTime = (Button) findViewById(R.id.trajectoryEndTimeButton);

        times = new HashMap<TextView, int[]>();
        that = this;
        b = new Bundle();

        viewLocations.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(startDateDisplay.getText().length() > 0 &&
                        startTimeDisplay.getText().length() > 0 &&
                        endDateDisplay.getText().length() > 0 &&
                        endTimeDisplay.getText().length() > 0) {
                    fillBundle();
                    Intent i = new Intent(that.getBaseContext(), DisplayTrajectory.class);
                    i.putExtras(b);
                    startActivity(i);
                } else {
                    new AlertDialog.Builder(that)
                            .setTitle("Incomplete form")
                            .setMessage("Please complete all dates/times first.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

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
        boolean on = ((ToggleButton) view).isChecked();

        // Toggled on?
        if (on) {
            startService(serviceIntent);
            // Toggled off?
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
        int[] temp = new int[3];
        temp[0] = year;
        temp[1] = month;
        temp[2] = day;
        if(times.containsKey(view)) {
            times.get(view)[0] = temp[0];
            times.get(view)[1] = temp[1];
            times.get(view)[2] = temp[2];
            times.put(view, temp);
        } else {
            times.put(view, temp);
        }
    }

    public void updateTimeDisplay(TextView view, int hour, int minute) {
        view.setText("M: " + hour + " S: " + minute);
        int[] temp = new int[2];
        temp[0] = hour;
        temp[1] = minute;
        if(times.containsKey(view)) {;
            times.get(view)[0] = temp[0];
            times.get(view)[1] = temp[1];
            times.put(view, temp);
        } else {
            times.put(view, temp);
        }
    }

    private void fillBundle() {
        ArrayList<Integer> startTime = new ArrayList<Integer>();
        for (int i : times.get(startTimeDisplay)) {
            startTime.add(i);
        }
        ArrayList<Integer> startDate = new ArrayList<Integer>();
        for (int i : times.get(startDateDisplay)) {
            startDate.add(i);
        }
        ArrayList<Integer> endTime = new ArrayList<Integer>();
        for (int i : times.get(endTimeDisplay)) {
            endTime.add(i);
        }
        ArrayList<Integer> endDate = new ArrayList<Integer>();
        for (int i : times.get(endDateDisplay)) {
            endDate.add(i);
        }
        b.putIntegerArrayList("startTime", startTime);
        b.putIntegerArrayList("startDate", startDate);
        b.putIntegerArrayList("endTime", endTime);
        b.putIntegerArrayList("endDate", endDate);
    }
}

