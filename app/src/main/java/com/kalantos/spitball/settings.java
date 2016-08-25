package com.kalantos.spitball;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by kalantos on 25/08/16.
 */
public class settings extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }


    }
