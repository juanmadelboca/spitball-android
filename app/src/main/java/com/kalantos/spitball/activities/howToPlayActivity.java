package com.kalantos.spitball.activities;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.kalantos.spitball.R;
import com.kalantos.spitball.engine.Timer;
import java.util.Calendar;

public class howToPlayActivity extends AppCompatActivity {
    double startClickTime;
    PointF startPoint,endPoint;
    final int MAX_CLICK_DURATION = 200;
    int level = 0 ;
    ImageView pointer, greenBall;
    int widthScreen, heightScreen;
    boolean animating = false;
    boolean touchBall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);
        pointer = (ImageView) findViewById(R.id.pointer);
        greenBall = (ImageView) findViewById(R.id.howToPlayGreenBall);
        startPoint = new PointF();
        endPoint = new PointF();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        widthScreen = size.x;
        heightScreen = size.y;

        Thread howToPlayThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(level < 3){
                    if (animating){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d("","CONTINUE");
                        continue;
                    }
                    switch (level){
                        case 0:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    moveTutorial(0,0,0,(widthScreen/6),700);
                                }
                            });
                            break;
                        case 1:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    moveTutorial(0,0,-(heightScreen/3),(widthScreen/6),700);
                                }
                            });
                            break;
                        case 2:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pointer.setX((widthScreen * 4350) / 10000);
                                    fadePointerEffect(false, 0);
                                    fadePointerEffect(true, 700);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            pointer.setX(widthScreen - (widthScreen/6));
                                            fadePointerEffect(false, 0);
                                            fadePointerEffect(true, 700);
                                        }
                                    }, 500);
                                }
                            });
                            break;

                    }
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        howToPlayThread.start();
        final View decorView = getWindow().getDecorView();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

        getWindow().getDecorView().setSystemUiVisibility(flags);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {
            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                {
                    Thread thread=new Thread(new Timer());
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        Log.e("AUTO-HIDE BAR", e.getMessage());
                    }

                    decorView.setSystemUiVisibility(flags);
                }
            }
        });

    }

    public void moveTutorial(int initialY, int initialX, int finalY, int finalX, int duration) {
        TranslateAnimation animation = new TranslateAnimation(initialX,  finalX, initialY, finalY); // Change x from 0 to 200
        animation.setDuration(duration);
        //animation.setFillAfter(true); // View will stay in the position where animation finished. Not return back
        pointer.startAnimation(animation);
    }

    public void fadePointerEffect(boolean fadeOut,int duration) {
        if(fadeOut){
            pointer.setVisibility(View.VISIBLE);
            pointer.animate().alpha(0f).setDuration(duration).setListener(null).start();
        }else {
            pointer.setVisibility(View.VISIBLE);
            pointer.animate().alpha(1f).setDuration(duration).setListener(null).start();
        }
    }

    private void swipeAnimation(int initialY, int initialX, int finalY, int finalX){
        TranslateAnimation animation = new TranslateAnimation(initialX,  finalX, initialY, finalY); // Change x from 0 to 200
        animation.setDuration(0);
        animation.setFillAfter(true); // View will stay in the position where animation finished. Not return back
        greenBall.startAnimation(animation);
        animating = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshBall();
            }
        }, 2000);
        level++;
    }

    private void refreshBall(){
        TranslateAnimation animation = new TranslateAnimation(0,  0, 0, 0); // Change x from 0 to 200
        animation.setDuration(0);
        animation.setFillAfter(true); // View will stay in the position where animation finished. Not return back
        greenBall.startAnimation(animation);
        animating = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int eventId = event.getAction();
        switch (eventId) {
            case MotionEvent.ACTION_DOWN:
                //start counting when press.
                startClickTime = Calendar.getInstance().getTimeInMillis();
                startPoint.y = event.getRawY();
                startPoint.x = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                //each time it moves actualize.
                endPoint.y = event.getRawY();
                endPoint.x = event.getRawX();
                break;
            case MotionEvent.ACTION_UP:
                //when the finger is raised final coordinates are send to detectMove for processing.
                if (((Calendar.getInstance().getTimeInMillis() - startClickTime) >= MAX_CLICK_DURATION)) {
                    //if drag duration is longer than max click is processed as a swipe, and send to the correct gestion.
                    //game.clicks = 0;
                    if(((startPoint.x - endPoint.x) < widthScreen/5) &&((Math.abs(startPoint.y - endPoint.y)) < heightScreen/4) && (level == 0)){
                        //SWIPE RIGHT
                        swipeAnimation(0,0,0,((widthScreen*10)/44));
                    }else if (((endPoint.x - startPoint.x) > widthScreen/10) && ((startPoint.y - endPoint.y) > heightScreen/5) && (level == 1)){
                        //SWIPE DIAGONAL
                        swipeAnimation(0,0,-(heightScreen/3),((widthScreen*10)/44));
                    }
                }else{
                    if(touchBall){
                        if((startPoint.x > ((widthScreen - (widthScreen/6)) - (widthScreen / 7))) &&
                                (startPoint.x < ((widthScreen - (widthScreen/6)) + (widthScreen / 7))) &&
                                (startPoint.y < ((heightScreen / 2) + (heightScreen / 5))) &&
                                (startPoint.y > ((heightScreen / 2) - (heightScreen / 5)))) {
                            swipeAnimation(0,0, 0,((widthScreen * 10) / 22));
                        }
                    }else{
                        if((startPoint.x > ((widthScreen / 2) - (widthScreen / 8))) &&
                                (startPoint.x < ((widthScreen / 2) + (widthScreen / 10))) &&
                                (startPoint.y < ((heightScreen / 2) + (heightScreen / 5))) &&
                                (startPoint.y > ((heightScreen / 2) - (heightScreen / 5)))) {
                            touchBall = true;
                        }else {
                            touchBall = false;
                        }
                    }
                    Log.d("",startPoint.x+" "+ startPoint.y);

                }
                break;
            default:
                break;

        }
        return true;
    }
}