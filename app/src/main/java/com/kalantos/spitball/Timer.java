package com.kalantos.spitball;

/**
 * Created by kalantos on 30/08/16.
 */
public class Timer implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
