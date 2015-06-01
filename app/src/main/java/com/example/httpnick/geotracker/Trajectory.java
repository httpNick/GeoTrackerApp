package com.example.httpnick.geotracker;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class that lets the user choose a start/end date that they want to see their locations for.
 * There is an option for displaying on a map as well as an option to display in a list.
 * This activity also has a switch to start/stop the tracking service.
 * A button is also available to manually push data to the web service.
 * @author Nick Duncan
 */
public class Trajectory extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    /**
     * Reference to the location tracking service
     */
    Intent serviceIntent;
    /**
     * Bundle to be sent to other activities with start/end points saved
     */
    Bundle b;
    /**
     * Reference to the SharedPreferences for this app
     */
    SharedPreferences sp;
    /**
     * A map to hold start/end times for each TextView.
     */
    private HashMap<TextView, int[]> times;
    /**
     * View location button
     */
    private Button viewLocations;
    /**
     * display map button
     */
    private Button displayMap;
    /**
     * Text view for the start date
     */
    private TextView startDateDisplay;
    /**
     * Text view for the start time
     */
    private TextView startTimeDisplay;
    /**
     * Button to open a start date picker
     */
    private Button startPickDate;
    /**
     * Button to open a start time picker
     */
    private Button startPickTime;
    /**
     * Text view for the end date
     */
    private TextView endDateDisplay;
    /**
     * Text view for the end time
     */
    private TextView endTimeDisplay;
    /**
     * Button to open a end date picker
     */
    private Button endPickDate;
    /**
     * Button to open a end time picker
     */
    private Button endPickTime;
    /**
     * Reference to most active date text view
     */
    private TextView activeDateDisplay;
    /**
     * Reference to most active time text view
     */
    private TextView activeTimeDisplay;
    /**
     * Reference to this for private inner class
     */
    private FragmentActivity that;
    /** Reference to locationIntervalSpinner*/
    Spinner locationIntervalSpin;

    /**Reference to networkIntervalSpinner*/
    Spinner networkIntervalSpin;

    /**Array for location intervals*/
    List<Long> locationIntervals;

    /**Array for network intervals*/
    List<Long> networkIntervals;

    /**Reference to the relative layout that holds location/network pulling intervals.*/
    RelativeLayout intervalLayout;

    /**GPSService service*/
    GPSService myService;
    boolean bound = false;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GPSService.LocalBinder binder = (GPSService.LocalBinder) service;
            Trajectory.this.myService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    public void onStart() {
        //super.onCreate(savedInstanceState);
        super.onStart();
        setContentView(R.layout.trajectory);
        viewLocations = (Button) findViewById(R.id.viewLocationsButton);
        startDateDisplay = (TextView) findViewById(R.id.startDateText);
        startTimeDisplay = (TextView) findViewById(R.id.startTimeText);
        startPickDate = (Button) findViewById(R.id.trajectoryDatePickButton);
        startPickTime = (Button) findViewById(R.id.trajectoryTimeButton);
        displayMap = (Button) findViewById(R.id.showMap);
        endDateDisplay = (TextView) findViewById(R.id.endDateText);
        endTimeDisplay = (TextView) findViewById(R.id.endTimeText);
        endPickDate = (Button) findViewById(R.id.trajectoryEndDatePickButton);
        endPickTime = (Button) findViewById(R.id.trajectoryEndTimeButton);
        locationIntervalSpin = (Spinner) findViewById(R.id.locationIntervalSpin);
        intervalLayout = (RelativeLayout) findViewById(R.id.loc_pull_layout);
        networkIntervalSpin = (Spinner) findViewById(R.id.networkIntervalSpin);
        fillSpinners();
        ArrayAdapter<Long> locSpinAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, locationIntervals);
        locSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationIntervalSpin.setAdapter(locSpinAdapter);

        ArrayAdapter<Long> netSpinAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, networkIntervals);
        netSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        networkIntervalSpin.setAdapter(netSpinAdapter);

        times = new HashMap<>();
        that = this;
        b = new Bundle();

        serviceIntent = new Intent(this, GPSService.class);

        locationIntervalSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bound) {
                    myService.setLocalInterval((long) locationIntervalSpin.getItemAtPosition(position));
                    System.out.println(myService.getLocalInterval());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        networkIntervalSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bound) {
                    myService.setNetworkInterval((long) networkIntervalSpin.getItemAtPosition(position));
                    System.out.println(myService.getNetworkInterval());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

                viewLocations.setOnClickListener(new View.OnClickListener() {
                    /**
                     * button to take the user to a list of their points in between two start/end times.
                     * @param v current view
                     */
                    public void onClick(View v) {
                        // Ensure all text views are filled.
                        // NEEDED TO BE ADDED: CHECK TO MAKE START DATE IS LESS THAN END DATE!!
                        if (startDateDisplay.getText().length() > 0 &&
                                startTimeDisplay.getText().length() > 0 &&
                                endDateDisplay.getText().length() > 0 &&
                                endTimeDisplay.getText().length() > 0) {
                            // Method call that fills the bundle to be sent to the next activity.
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

        /**---------- On click listeners to open up the start/end date/time pickers --------------*/
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

        endPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showTimePickerDialog(endTimeDisplay);
            }
        });
        /** ------------------------------------------------------------------------------------- */


        displayMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (startDateDisplay.getText().length() > 0 &&
                        startTimeDisplay.getText().length() > 0 &&
                        endDateDisplay.getText().length() > 0 &&
                        endTimeDisplay.getText().length() > 0) {
                    fillBundle();
                    Intent i = new Intent(that.getBaseContext(), DisplayMap.class);
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
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ToggleButton theSwitch = (ToggleButton) findViewById(R.id.switchGPS);

        if (isMyServiceRunning(GPSService.class)) {
            theSwitch.setChecked(true);
            intervalLayout.setVisibility(View.VISIBLE);
        } else {
            theSwitch.setChecked(false);
            intervalLayout.setVisibility(View.INVISIBLE);
        }
    }


    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        // Toggled on?
        if (on) {
            startService(serviceIntent);
            bindService(serviceIntent, mConnection, Context.BIND_NOT_FOREGROUND);
            intervalLayout.setVisibility(View.VISIBLE);
            // Toggled off?
        } else {
            stopService(serviceIntent);
            intervalLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bound) {
            unbindService(mConnection);
            bound = false;
        }
    }
    /**
     * Checks to see if a service is currently running on the device.
     * @param serviceClass class of the service you want to check.
     * @return true or false (is the service running?)
     */
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
        if(times.containsKey(view)) {
            times.get(view)[0] = temp[0];
            times.get(view)[1] = temp[1];
            times.put(view, temp);
        } else {
            times.put(view, temp);
        }
    }

    /**
     * method that fills bundle to be sent to next activity.
     */
    private void fillBundle() {
        ArrayList<Integer> startTime = new ArrayList<>();
        for (int i : times.get(startTimeDisplay)) {
            startTime.add(i);
        }
        ArrayList<Integer> startDate = new ArrayList<>();
        for (int i : times.get(startDateDisplay)) {
            startDate.add(i);
        }
        ArrayList<Integer> endTime = new ArrayList<>();
        for (int i : times.get(endTimeDisplay)) {
            endTime.add(i);
        }
        ArrayList<Integer> endDate = new ArrayList<>();
        for (int i : times.get(endDateDisplay)) {
            endDate.add(i);
        }
        b.putIntegerArrayList("startTime", startTime);
        b.putIntegerArrayList("startDate", startDate);
        b.putIntegerArrayList("endTime", endTime);
        b.putIntegerArrayList("endDate", endDate);
    }

    private void fillSpinners() {
        locationIntervals = new ArrayList<>();
        networkIntervals = new ArrayList<>();
        for (int i = 10; i <= 300; i += 5) {
            locationIntervals.add((long) i);
        }
        for (int i = 60; i <= 300; i += 30) {
            networkIntervals.add((long) i);
        }
    }


}

