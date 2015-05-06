package com.example.httpnick.geotracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by httpnick on 4/20/15.
 */
public class PasswordRecovery extends ActionBarActivity {
    PasswordRecovery ma;
    String recoverUrl;
    SharedPreferences pref;
    private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_recover);
        ma = this;
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        recoverUrl = "http://450.atwebpages.com/reset.php";
        findViewById(R.id.getPassword).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText ans = (EditText) findViewById(R.id.securityAnswerRecoverPage);
                String email = ans.getText().toString();
                recoverUrl = "http://450.atwebpages.com/reset.php?email="+email;
                DownloadWebPageTask task = new DownloadWebPageTask();
                task.execute(new String[]{recoverUrl});
            }
        });
    }
    /**
     * Running the loading of the JSON in a separate thread.
     * Code adapted from http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
     */
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(PasswordRecovery.this, "Wait", "Downloading...");
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
                JSONObject obj = new JSONObject(result);
                String pass = (String) obj.get("result");
                EditText ans = (EditText) findViewById(R.id.securityAnswerRecoverPage);
                String email = ans.getText().toString();
                if (pass.equals("success")) {
                    new AlertDialog.Builder(ma)
                            .setTitle("Success!")
                            .setMessage("Email sent with instructions to reset your password.")
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
                } else {
                    new AlertDialog.Builder(ma)
                            .setTitle("Email does not exist in the system")
                            .setMessage("Please re-enter your email.")
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
