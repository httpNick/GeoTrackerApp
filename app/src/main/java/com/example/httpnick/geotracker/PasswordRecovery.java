package com.example.httpnick.geotracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;

/**
 * Created by httpnick on 4/20/15.
 */
public class PasswordRecovery extends ActionBarActivity {
    PasswordRecovery ma;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_recover);
        ma = this;
        findViewById(R.id.getPassword).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText ans = (EditText) findViewById(R.id.securityAnswerRecoverPage);
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (ans.getText().toString().equals(pref.getString("securityAnswer", "answer"))) {
                    new AlertDialog.Builder(ma)
                            .setTitle("Your password")
                            .setMessage("Password: " + pref.getString("password", "pw"))
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
                            .setTitle("Your password")
                            .setMessage("Incorrect answer!!")
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
}
