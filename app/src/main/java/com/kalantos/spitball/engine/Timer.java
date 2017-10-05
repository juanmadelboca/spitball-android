package com.kalantos.spitball.engine;

/**
 * Created by kalantos on 30/08/16.
 */
public class Timer implements Runnable {
    //thread que cuenta el tiempo para mantener las barras ocultas cuando se presionan
    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
