package com.example.httpnick.geotracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

// This is a test commit to make sure git is working.
public class MainActivity extends ActionBarActivity {
    private Button register;
    private Button login;
    private SharedPreferences pref;
    EditText email, pw;
    MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        register = (Button) findViewById(R.id.Register);
        login = (Button) findViewById(R.id.Login);
         email = (EditText) findViewById(R.id.email);
         pw = (EditText) findViewById(R.id.password);
        email.setText(pref.getString("email", "email"));
        pw.setText(pref.getString("password", "password"));
        ma = this;

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
                if (email.getText().toString().equals(pref.getString("email", "email")) &&
                    pw.getText().toString().equals(pref.getString("password", "password"))) {
                    Intent i = new Intent(v.getContext(), UserAccount.class);
                    startActivity(i);
                }else{
                    new AlertDialog.Builder(ma)
                            .setTitle("Incorrect Password")
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
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        email.setText(pref.getString("email", "email"));
        pw.setText(pref.getString("password", "password"));
    }
}
