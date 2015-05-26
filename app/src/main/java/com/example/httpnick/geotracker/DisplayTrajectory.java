package com.example.httpnick.geotracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

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
 */
public class DisplayTrajectory extends Activity {

    /** preferences for the current application */
    private SharedPreferences pref;

    private JSONArray points;

    private TableLayout table_layout;

    private ArrayList<LocationPackage> lps;

    private ListView locationListView;

    private LocationAdapter la;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_table);
        lps = new ArrayList<LocationPackage>();
        locationListView = (ListView) findViewById(R.id.loc_list_view);
        la = new LocationAdapter(this, lps);
        Bundle b = getIntent().getExtras();
        //table_layout = (TableLayout) findViewById(R.id.tableLayout1);
        //table_layout.setVisibility(View.INVISIBLE);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ArrayList<Integer> startTime = b.getIntegerArrayList("startTime");
        ArrayList<Integer> startDate = b.getIntegerArrayList("startDate");
        ArrayList<Integer> endTime = b.getIntegerArrayList("endTime");
        ArrayList<Integer> endDate = b.getIntegerArrayList("endDate");
        int startUnix = componentTimeToTimestamp(startDate.get(0), startDate.get(1), startDate.get(2),
                startTime.get(0), startTime.get(1));
        int endUnix = componentTimeToTimestamp(endDate.get(0), endDate.get(1), endDate.get(2),
                endTime.get(0), endTime.get(1));
        System.out.println("START : " + startUnix + " END : " + endUnix);
        String DB_URL = "http://450.atwebpages.com/view.php?uid="+pref.getString("userid", "default")+
                "&start="+startUnix+"&end="+endUnix;
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[]{DB_URL});

    }

    int componentTimeToTimestamp(int year, int month, int day, int hour, int minute) {

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

    private void fillTable() {
        try {
            TableRow headerRow = new TableRow(this);
            headerRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            headerRow.setPadding(0, 0, 0, 2);

            TableRow.LayoutParams headerLlp = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            headerLlp.setMargins(0, 0, 2, 0);//2px right-margin

            headerRow.setLayoutParams(headerLlp);

            TextView latHeader = new TextView(this);
            latHeader.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            latHeader.setPadding(0, 0, 4, 3);
            latHeader.setText("Latitude");
            headerRow.addView(latHeader);

            TextView lonHeader = new TextView(this);
            lonHeader.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            lonHeader.setPadding(0, 0, 4, 3);
            lonHeader.setText("Longitude");
            headerRow.addView(lonHeader);

            TextView speedHeader = new TextView(this);
            speedHeader.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            speedHeader.setPadding(0, 0, 4, 3);
            speedHeader.setText("Speed");
            headerRow.addView(speedHeader);

            TextView headingHeader = new TextView(this);
            headingHeader.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            headingHeader.setPadding(0, 0, 4, 3);
            headingHeader.setText("Heading");
            headerRow.addView(headingHeader);

            TextView timeHeader = new TextView(this);
            timeHeader.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            timeHeader.setPadding(0, 0, 4, 3);
            timeHeader.setText("Time");
            headerRow.addView(timeHeader);

            table_layout.addView(headerRow);



            JSONObject o;


            for(int i = 0; i < points.length(); i++) {
                o = points.getJSONObject(i);
                TableRow row = new TableRow(this);
                row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
                row.setPadding(0, 0, 0, 2);

                TableRow.LayoutParams llp = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                llp.setMargins(0, 0, 2, 0);//2px right-margin

                row.setLayoutParams(llp);

                TextView lat = new TextView(this);
                lat.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                lat.setPadding(0, 0, 4, 3);
                lat.setText((String) o.get("lat"));
                row.addView(lat);

                TextView lon = new TextView(this);
                lon.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                lon.setPadding(0, 0, 4, 3);
                lon.setText((String) o.get("lon"));
                row.addView(lon);

                TextView speed = new TextView(this);
                speed.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                speed.setPadding(0, 0, 4, 3);
                speed.setText((String) o.get("speed"));
                row.addView(speed);


                TextView heading = new TextView(this);
                heading.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                heading.setPadding(0, 0, 4, 3);
                heading.setText((String) o.get("heading"));
                row.addView(heading);


                TextView time = new TextView(this);
                time.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
                time.setPadding(0, 0, 4, 3);
                time.setText(String.valueOf(o.get("time")));
                row.addView(time);

                table_layout.addView(row);
            }
            table_layout.setVisibility(View.VISIBLE);
        } catch(JSONException j) {
            j.printStackTrace();
        }
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
                    for(int i = 0; i < points.length(); i++) {
                        JSONObject o = points.getJSONObject(i);
                        LocationPackage lp = new LocationPackage(o.getString("heading"),
                                o.getString("lon"), o.getString("lat"),
                                o.getString("speed"), o.getString("time"));
                        lps.add(lp);
                    }

                    //fillTable();
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
