package com.example.httpnick.geotracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.CertificateNotYetValidException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class that handles registering users.
 * @author Nick Duncan
 */
public class RegisterUser extends Activity {
    private EditText email, passwordOne, passwordTwo, secAnswer;
    private TextView question;
    private RegisterUser ma;
    private SharedPreferences prefs;
    private ProgressDialog progressDialog;
    private String USER_URL = "http://450.atwebpages.com/adduser.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Button regButton = (Button) findViewById(R.id.registerButton);
        email = (EditText) findViewById(R.id.email_address);
        passwordOne = (EditText) findViewById(R.id.passwordOne);
        passwordTwo = (EditText) findViewById(R.id.passwordTwo);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        secAnswer = (EditText) findViewById(R.id.securityAnswer);
        question = (TextView) findViewById(R.id.securityQuestion);
         ma = this;

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().length() > 0 &&
                        (passwordOne.getText().length() >= 5 && passwordOne.getText().length() < 10) &&
                        (passwordTwo.getText().length() >= 5 && passwordTwo.getText().length() < 10) &&
                        secAnswer.getText().length() > 0 &&
                        passwordOne.getText().toString().equals(passwordTwo.getText().toString()) &&
                        isEmailValid(email.getText().toString())) {

                    /* push to shared prefences.
                    prefs.edit().putString("securityAnswer", secAnswer.getText().toString()).apply();
                    prefs.edit().putString("email", email.getText().toString()).apply();
                    prefs.edit().putString("password", passwordOne.getText().toString()).apply(); */
                    //prefs.edit().putBoolean("loggedIn", true).apply();
                    /** Push to web service. */
                    try {
                        String encodedQuestion = URLEncoder.encode(question.getText().toString(), "UTF-8");
                        USER_URL += ("?email="+email.getText().toString()+"&password="+passwordOne.getText().toString()+
                                "&question="+encodedQuestion+"&answer="+secAnswer.getText().toString());
                        System.out.println(USER_URL);
                    } catch(UnsupportedEncodingException t) {
                        System.out.println("ENCODING FAILED");
                    }
                    Intent i = new Intent(v.getContext(), MainActivity.class);
                    DownloadWebPageTask task = new DownloadWebPageTask();
                    task.execute(new String[]{USER_URL});
                    startActivity(i);
                    finish();
                } else {

                    String message;

                    if(!isEmailValid(email.getText().toString())) {
                        message = "Email is not in correct format.";
                    } else if(!passwordOne.getText().toString().equals(passwordTwo.getText().toString())){
                        message = "Passwords do not match.";
                    } else {
                        message = "Password is not in between 5 and 10 characters.";
                    }

                    new AlertDialog.Builder(ma)
                            .setTitle("Please try again:")
                            .setMessage(message)
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
    }
    /**
     * method is used for checking valid email id format.
     * Source: http://stackoverflow.com/questions/6119722/how-to-check-edittexts-text-is-email-address-or-not
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;
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
            Toast.makeText(ma.getBaseContext(), "Account created Successfully!  " +
                    "A verification email has been sent to your address.", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
