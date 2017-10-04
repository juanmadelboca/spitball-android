package com.kalantos.spitball.engine;

/**
 * Thread used to count time, used to mantain action bar hide.
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
