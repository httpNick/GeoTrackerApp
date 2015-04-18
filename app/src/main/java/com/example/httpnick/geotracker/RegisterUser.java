package com.example.httpnick.geotracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by httpnick on 4/10/15.
 */

public class RegisterUser extends Activity {
    private Button regButton;
    private EditText email, passwordOne, passwordTwo, secAnswer;
    SharedPreferences prefs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        regButton = (Button) findViewById(R.id.registerButton);
        email = (EditText) findViewById(R.id.email_address);
        passwordOne = (EditText) findViewById(R.id.passwordOne);
        passwordTwo = (EditText) findViewById(R.id.passwordTwo);
        secAnswer = (EditText) findViewById(R.id.securityAnswer);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().length() > 0 &&
                    passwordOne.getText().length() > 0 &&
                    passwordTwo.getText().length() > 0 &&
                    secAnswer.getText().length() > 0 &&
                    passwordOne.getText().toString().equals(passwordTwo.getText().toString())) {

                        prefs.edit().putString("securityAnswer", secAnswer.getText().toString()).apply();
                        prefs.edit().putString("email", email.getText().toString()).apply();
                        prefs.edit().putString("password", passwordOne.getText().toString()).apply();
                        Intent i = new Intent(v.getContext(), UserAccount.class);
                        startActivity(i);
                }
            }
        });
    }
}
