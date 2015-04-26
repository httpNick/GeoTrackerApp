package com.example.httpnick.geotracker;



import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

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
    private static final String COURSE_URL
            = "http://cssgate.insttech.washington.edu/~mmuppa/Android/test.php?cmd=courses";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trajectory);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[]{COURSE_URL});

        userListView = (ListView) findViewById(R.id.user_list_view);
/*
        mCourseList = new ArrayList<>();
        mAdapter = new CourseAdapter(this, mCourseList); */


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
                    /*for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        Course course = new Course(obj.getString(Course.ID), obj.getString(Course.SHORT_DESC)
                                , obj.getString(Course.LONG_DESC), obj.getString(Course.PRE_REQS));
                        mCourseList.add(course);
                    }*/
                } catch (JSONException e) {
                    System.out.println("JSON Exception");
                }
            /*
            if (!mCourseList.isEmpty()) {
                mCoursesListView.setAdapter(mAdapter);
            } */
        }
    }
}
