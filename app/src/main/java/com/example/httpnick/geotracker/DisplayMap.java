package com.example.httpnick.geotracker;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
 * A FragmentActivity that will be used to display GoogleMap markers.
 * @author Matthew Moore
 * 5/25/15.
 */
public class DisplayMap extends FragmentActivity implements OnMapReadyCallback {

    private SharedPreferences pref;
    private MapFragment mapFragment;
    private double lat;
    private double lon;
    private JSONArray points;
    private ArrayList<LocationPackage> lps;
    private LocationAdapter la;
    private ListView locationListView;
    private GoogleMap map;

    /**
     *
     * @param savedInstance
     */
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Bundle b = getIntent().getExtras();
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ArrayList<Integer> startTime = b.getIntegerArrayList("startTime");
        ArrayList<Integer> startDate = b.getIntegerArrayList("startDate");
        ArrayList<Integer> endTime = b.getIntegerArrayList("endTime");
        ArrayList<Integer> endDate = b.getIntegerArrayList("endDate");
        int startUnix = componentTimeToTimestamp(startDate.get(0), startDate.get(1), startDate.get(2),
                startTime.get(0), startTime.get(1));
        int endUnix = componentTimeToTimestamp(endDate.get(0), endDate.get(1), endDate.get(2),
                endTime.get(0), endTime.get(1));
        String DB_URL = "http://450.atwebpages.com/view.php?uid="+pref.getString("userid", "default")+
                "&start="+startUnix+"&end="+endUnix;
        setContentView(R.layout.map);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[]{DB_URL});

    }

    /**
     *
     * @param map
     */
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMyLocationEnabled(true);
//        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//                lat = location.getLatitude();
//                lon = location.getLongitude();
//
//            }
//        });
    }

    private void plotPoints() {
        for(int i = 0; i < points.length(); i++) {
            try {

                JSONObject o = points.getJSONObject(i);
                lat = o.getDouble("lat");
                lon = o.getDouble("lon");
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lon))
                        .title("Points"));

            } catch(JSONException e) {
                //Do nothing
            }
        }

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

                    //fillTable();
                    System.out.println("SUCCESSFULLY PULLED FROM THE WEBSERVICE!!");
                } else {
                    System.out.println("NO RESULTS");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (points.length() > 0) {
                plotPoints();
            }
        }
    }
}
