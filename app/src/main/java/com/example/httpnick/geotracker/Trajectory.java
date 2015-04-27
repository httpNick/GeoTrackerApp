package com.example.httpnick.geotracker;



import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

/**
 * Created by httpnick on 4/20/15.
 */
public class Trajectory extends ActionBarActivity {
    private ListView userListView;
    private ProgressDialog progressDialog;
    private static final String USER_URL
            = "http://cssgate.insttech.washington.edu/~_450team7/databaseService.php?cmd=users";
    ArrayList<String> usersList;
    ArrayAdapter<String> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trajectory);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[]{USER_URL});

        userListView = (ListView) findViewById(R.id.user_list_view);

        usersList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, usersList);
        userListView.setAdapter(adapter);


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
