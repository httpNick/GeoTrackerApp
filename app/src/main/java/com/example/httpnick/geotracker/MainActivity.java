package com.example.httpnick.geotracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

// This is a test commit to make sure git is working.
public class MainActivity extends ActionBarActivity {
    private Button register;
    private Button login;
    private SharedPreferences pref;
    EditText email, pw;
    private ProgressDialog progressDialog;
    MainActivity ma;
    private String USER_URL;
    private Button gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        register = (Button) findViewById(R.id.Register);
        login = (Button) findViewById(R.id.Login);
        email = (EditText) findViewById(R.id.email);
        pw = (EditText) findViewById(R.id.password);
        gps = (Button) findViewById(R.id.GPS);
        ma = this;
        //GPSTracker tracker = new GPSTracker(this);


        if (pref.getBoolean("loggedIn", true)) {
            Intent i = new Intent(getApplicationContext(), UserAccount.class);
            startActivity(i);
        }
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), LocationTracking.class);
                startActivity(i);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), TermsOfUse.class);
                startActivity(i);
            }
        });

        findViewById(R.id.forgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), PasswordRecovery.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                USER_URL = ("http://450.atwebpages.com/login.php?email="+email.getText().toString()+"&password="+pw.getText().toString());
                DownloadWebPageTask task = new DownloadWebPageTask();
                task.execute(new String[]{USER_URL});
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        if (pref.getBoolean("loggedIn", false)) {
            Intent i = new Intent(getApplicationContext(), UserAccount.class);
            startActivity(i);
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
            progressDialog = ProgressDialog.show(MainActivity.this, "Wait", "Downloading...");
        }


        @Override
        protected String doInBackground(String... urls) {
            System.out.println(USER_URL);
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
                JSONObject obj = new JSONObject(result);
                String pass = (String) obj.get("result");
                String id = (String) obj.get("userid");
                if (pass.equals("success")) {
                    pref.edit().putBoolean("loggedIn", true).apply();
                    pref.edit().putString("userid", id).apply();
                    pref.edit().putString("email", email.getText().toString()).apply();
                    Intent i = new Intent(ma.getBaseContext(), UserAccount.class);
                    startActivity(i);
                }

            } catch (JSONException e) {
                new AlertDialog.Builder(ma)
                        .setTitle("Incorrect login credentials")
                        .setMessage("Please re-enter your email and password.")
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
    }
}
