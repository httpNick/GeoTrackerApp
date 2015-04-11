package com.example.httpnick.geotracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by httpnick on 4/10/15.
 */

public class RegisterUser extends Activity {
    private Button regButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        regButton = (Button) findViewById(R.id.registerButton);

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) findViewById(R.id.email_address);
                email.setText("");
            }
        });
    }
}
