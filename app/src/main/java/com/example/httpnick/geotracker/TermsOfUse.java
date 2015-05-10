package com.example.httpnick.geotracker;

import java.io.InputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONException;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import android.renderscript.Element;
import android.widget.TextView;

/**
 * Created by httpnick on 4/16/15.
 */


public class TermsOfUse extends ActionBarActivity {
    private Button acceptButton;
    private Button declineButton;
    private Node node;
    private Document doc;
    private String tag;
    private Element root;
    private ProgressDialog progressDialog;
    private TextView userAgreement;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_of_use_view);
        userAgreement = (TextView) findViewById(R.id.terms);
        acceptButton = (Button) findViewById(R.id.acceptTerms);
        declineButton = (Button) findViewById(R.id.declineTerms);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), RegisterUser.class);
                startActivity(i);
                finish();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[]{"http://450.atwebpages.com/agreement.php"});
    }
    /**
     * Running the loading of the JSON in a separate thread.
     * Code adapted from http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
     */
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TermsOfUse.this, "Wait", "Downloading...");
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
                String agreement = (String) obj.get("agreement");
                System.out.println(agreement);
                userAgreement.setText(agreement);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
