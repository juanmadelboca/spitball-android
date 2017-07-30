package com.kalantos.spitball.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;


public class TileView extends Activity {
    private ImageView imageView;
    private final int boundsX, boundsY;
    private boolean isPressed;

    public TileView(Context context, int boundsY, int boundsX) {
        imageView = new ImageView(context);
        this.boundsX = boundsX;
        this.boundsY = boundsY;
        this.isPressed = false;
    }


    public int getBoundsX() {
        return boundsX;
    }

    public int getBoundsY() {
        return boundsY;
    }
    //BOUNCING
    public boolean isPressed() {
        return isPressed;
    }

    public void press(){
        isPressed=true;
    }

    public void release(){
        isPressed=false;
    }

    public ImageView getImageView(){
        return imageView;
    }

}

