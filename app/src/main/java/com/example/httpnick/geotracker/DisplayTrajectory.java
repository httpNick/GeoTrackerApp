package com.example.httpnick.geotracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;

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
import java.util.Calendar;


/**
 * Created by httpnick on 5/22/15.
 * @author Nick Duncan
 * An activity that displays locations/times that are stored for the currently
 * logged in user. Puts all points through an adapter and adds it to the view for this
 * acitivty.
 */
public class DisplayTrajectory extends Activity {

    /** preferences for the current application */
    private SharedPreferences pref;
    /** JSON array that points will be stored in from the web service.*/
    private JSONArray points;
    /** ArrayList that will hold Location packages that will be used by the view adapter*/
    private ArrayList<LocationPackage> lps;
    /** List view on the view that points will be displayed on.*/
    private ListView locationListView;
    /** Adapter that will be used to go on the ListView.*/
    private LocationAdapter la;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_table);

        lps = new ArrayList<>();

        locationListView = (ListView) findViewById(R.id.loc_list_view);

        la = new LocationAdapter(this, lps);

        Bundle b = getIntent().getExtras();
        /** Gain a reference to the shared preferences.*/
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        // Start time from trajectory page
        ArrayList<Integer> startTime = b.getIntegerArrayList("startTime");
        // Start date from trajectory page
        ArrayList<Integer> startDate = b.getIntegerArrayList("startDate");
        // End time for the trajectory page
        ArrayList<Integer> endTime = b.getIntegerArrayList("endTime");
        // end date from the trajectory page
        ArrayList<Integer> endDate = b.getIntegerArrayList("endDate");
        // convert integers from trajectory page to a unix timestamp for start time.
        int startUnix = componentTimeToTimestamp(startDate.get(0), startDate.get(1), startDate.get(2),
                startTime.get(0), startTime.get(1));
        // convert integers from trajectory page to a unix timestamp for end time.
        int endUnix = componentTimeToTimestamp(endDate.get(0), endDate.get(1), endDate.get(2),
                endTime.get(0), endTime.get(1));

        String DB_URL = "http://450.atwebpages.com/view.php?uid="+pref.getString("userid", "default")+
                "&start="+startUnix+"&end="+endUnix;

        DownloadWebPageTask task = new DownloadWebPageTask();

        task.execute(new String[]{DB_URL});

    }

    /**
     * Takes in y/m/d/h/min and converts to a unix time stamp.
     * @param year year input
     * @param month month input
     * @param day day input
     * @param hour hour input
     * @param minute minute input
     * @return unix timestamp based on passed in parameters.
     */
    private int componentTimeToTimestamp(int year, int month, int day, int hour, int minute) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return (int) (c.getTimeInMillis() / 1000L);
    }

    /**
     * Running the loading of the JSON in a separate thread.
     * Code adapted from http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
     */
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            try {
                JSONObject obj = new JSONObject(result);
                /** Check to make sure the web service successfully processed the post*/
                String pass = (String) obj.get("result");
                if (pass.equals("success")) {
                    points = obj.getJSONArray("points");
                    /**
                     * Loop through the returned JSONArray and save all points into
                     * LocationPackage objects to be used by the adapter.
                     */
                    for(int i = 0; i < points.length(); i++) {
                        JSONObject o = points.getJSONObject(i);
                        LocationPackage lp = new LocationPackage(o.getString("heading"),
                                o.getString("lon"), o.getString("lat"),
                                o.getString("speed"), o.getString("time"));
                        lps.add(lp);
                    }

                    System.out.println("SUCCESSFULLY PULLED FROM THE WEBSERVICE!!");
                } else {
                    System.out.println("NO RESULTS");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!lps.isEmpty()) {
                locationListView.setAdapter(la);
            }
        }
    }
}
