package com.example.httpnick.geotracker;

import android.app.Activity;
import android.os.Bundle;


/**
 * Created by httpnick on 5/22/15.
 */
public class DisplayTrajectory extends Activity {
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Bundle b = getIntent().getExtras();
        System.out.println(b.getIntegerArrayList("startTime"));
    }
}
