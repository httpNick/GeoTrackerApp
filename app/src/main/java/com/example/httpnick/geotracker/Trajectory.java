package com.example.httpnick.geotracker;



import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
    private ListView userListView;
    private ProgressDialog progressDialog;
    Intent serviceIntent;
    private static final String USER_URL
            = "http://cssgate.insttech.washington.edu/~_450team7/databaseService.php?cmd=users";
    ArrayList<String> usersList;
    ArrayAdapter<String> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trajectory);
        serviceIntent = new Intent(this, GPSService.class);
    }


    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            startService(serviceIntent);
            /*GPSService.setServiceAlarm(view.getContext(), true);

            ComponentName receiver = new ComponentName(view.getContext(), GPSBroadcastReceiver.class);
            PackageManager pm = view.getContext().getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP); */
        } else {
            stopService(serviceIntent);
            /*GPSService.setServiceAlarm(view.getContext(), false);

            ComponentName receiver = new ComponentName(view.getContext(), GPSBroadcastReceiver.class);
            PackageManager pm = view.getContext().getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP); */
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    /**
     * Running the loading of the JSON in a separate thread.
     * Code adapted from http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
     */
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Trajectory.this, "Wait", "Downloading...");
        }


        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
                try {
                    JSONArray arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        usersList.add(obj.getString("username"));
                        usersList.add(obj.getString("password"));
                        usersList.add(obj.getString("answer"));
                    }
                } catch (JSONException e) {
                    System.out.println("JSON Exception");
                }

            if (!usersList.isEmpty()) {
                userListView.setAdapter(adapter);
            }
        }
    }
}
