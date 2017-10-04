package com.kalantos.spitball.utils;

import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class createRoom implements Runnable {


    @Override
    public void run() {
        boolean run = true;
        while (run) {
            try {
                Thread.sleep((long) 15000);

            } catch (InterruptedException e) {
                run = false;
            }
        }

    }
}