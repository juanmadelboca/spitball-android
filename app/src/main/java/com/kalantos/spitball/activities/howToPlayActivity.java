package com.kalantos.spitball.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.kalantos.spitball.R;
import com.kalantos.spitball.engine.Timer;

import java.util.Calendar;

public class howToPlayActivity extends AppCompatActivity {
    double startClickTime;
    PointF startPoint,endPoint;
    final int MAX_CLICK_DURATION = 200;
    int level = 4 ;
    ImageView pointer, greenBall, pinkBall;
    int widthScreen, heightScreen;
    boolean animationFlag, touchBallFlag, instructionFlag;
    TextView instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);
        pointer = (ImageView) findViewById(R.id.pointer);
        greenBall = (ImageView) findViewById(R.id.howToPlayGreenBall);
        pinkBall = (ImageView) findViewById(R.id.howToPlayPinkBall);
        instructions = (TextView) findViewById(R.id.instructions);
        startPoint = new PointF();
        endPoint = new PointF();

        animationFlag = false;
        touchBallFlag = false;
        instructionFlag = true;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        widthScreen = size.x;
        heightScreen = size.y;

        Thread howToPlayThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(level < 5){
                    if (animationFlag || instructionFlag){
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    switch (level){
                        case 1:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    moveTutorial(0, 0, 0, (widthScreen / 6), 700);
                                }
                            });
                            break;
                        case 2:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    moveTutorial(0, 0, - (heightScreen / 3), (widthScreen / 6), 700);
                                }
                            });
                            break;
                        case 3:
                            splitTutorial();
                            break;
                        case 4:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pinkBall.setVisibility(View.VISIBLE);
                                    moveTutorial(0, 0, 0, (widthScreen / 6), 700);
                                }
                            });
                            break;
                        default:
                            break;

                    }
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fadeOutToMenu();
                    }
                });
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
                    Thread thread = new Thread(new Timer());
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

    private void fadeOutToMenu(){
        TextView black_square = (TextView) findViewById(R.id.black_square);
        black_square.setElevation(20);
        black_square.requestLayout();
        Log.d("", "FADING");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //fadeAnimationEffect(black_square,true,1200);
        Intent intent = new Intent(howToPlayActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void moveTutorial(int initialY, int initialX, int finalY, int finalX, int duration) {
        TranslateAnimation animation = new TranslateAnimation(initialX,  finalX, initialY, finalY); // Change x from 0 to 200
        animation.setDuration(duration);
        pointer.startAnimation(animation);
    }

    private void splitTutorial(){
        Thread paintThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int bouncingStatus = 1;
                while (level == 3){
                    if(touchBallFlag){
                        int idR = getResources().getIdentifier("ballgreen_small" + bouncingStatus, "drawable",getPackageName());
                        final Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), idR);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                greenBall.setImageBitmap(bitmapImage);
                            }
                        });
                        bouncingStatus++;
                        if(bouncingStatus > 8){
                            bouncingStatus = 1;
                        }

                    }else {
                        int idR = getResources().getIdentifier("ballgreen_small","drawable",getPackageName());
                        final Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), idR);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                greenBall.setImageBitmap(bitmapImage);
                            }
                        });
                        bouncingStatus = 1;
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pointer.setX((widthScreen * 4350) / 10000);
                fadeAnimationEffect(pointer, false, 0);
                fadeAnimationEffect(pointer, true, 700);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pointer.setX(widthScreen - (widthScreen / 6));
                        fadeAnimationEffect(pointer, false, 0);
                        fadeAnimationEffect(pointer, true, 700);
                    }
                }, 500);
            }
        });
        paintThread.start();
    }

    public void fadeAnimationEffect(View view, boolean fadeOut, int duration) {
        if(fadeOut){
            view.animate().alpha(0.0f).setDuration(duration).setListener(null).start();
        }else {
            view.animate().alpha(1.0f).setDuration(duration).setListener(null).start();
        }
    }

    private void swipeAnimation(int initialY, int initialX, int finalY, int finalX){
        TranslateAnimation animation = new TranslateAnimation(initialX,  finalX, initialY, finalY); // Change x from 0 to 200
        animation.setDuration(0);
        animation.setFillAfter(true); // View will stay in the position where animation finished. Not return back
        greenBall.startAnimation(animation);
        animationFlag = true;
        if(level < 4) {
            fadeAnimationEffect(greenBall, true, 800);
            successCard();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    instructions.setAlpha(0.0f);
                    refreshBall();
                }
            }, 2000);
        }
        level++;
    }

    void successCard(){
        String[] successPhrases = this.getResources().getStringArray(R.array.successPhrases);
        instructions.setText(successPhrases[(int)(Math.random() * 3)]);
        instructions.setAlpha(1.0f);
    }

    void refreshInstructions(){

        instructionFlag = true;
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (level){
            case 1:
                instructions.setText(R.string.howToPlaySwipe);
                instructions.setAlpha(1.0f);
                break;
            case 2:
                instructions.setText(R.string.howToPlaySwipeDiagonal);
                instructions.setAlpha(1.0f);
                break;
            case 3:
                instructions.setText(R.string.howToPlaySplit);
                instructions.setAlpha(1.0f);
                break;
            case 4:
                instructions.setText(R.string.howToPlayEatEnemy);
                instructions.setAlpha(1.0f);
                break;
        }
    }

    private void refreshBall(){
        TranslateAnimation animation = new TranslateAnimation(0,  0, 0, 0); // Change x from 0 to 200
        animation.setDuration(0);
        animation.setFillAfter(true); // View will stay in the position where animation finished. Not return back
        greenBall.startAnimation(animation);
        greenBall.setAlpha(1.0f);
        animationFlag = false;
        refreshInstructions();
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
                    if(((startPoint.x - endPoint.x) < widthScreen / 5) &&((Math.abs(startPoint.y - endPoint.y)) < heightScreen / 4) && ((level == 1)|| (level == 4)) && !instructionFlag){
                        //SWIPE RIGHT
                        swipeAnimation(0, 0, 0, ((widthScreen * 10) / 44));
                        if(level == 5){
                            pinkBall.setVisibility(View.INVISIBLE);
                            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 155, getResources().getDisplayMetrics());
                            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 428, getResources().getDisplayMetrics());
                            greenBall.getLayoutParams().height = height;
                            greenBall.getLayoutParams().width = width;
                            greenBall.requestLayout();

                        }
                    }else if (((endPoint.x - startPoint.x) > widthScreen / 10) && ((startPoint.y - endPoint.y) > heightScreen / 5) && (level == 2) && !instructionFlag){
                        //SWIPE DIAGONAL
                        swipeAnimation(0,0, - (heightScreen / 3),((widthScreen * 10) / 44));
                    }
                }else{
                    if(touchBallFlag){
                        if((startPoint.x > ((widthScreen - (widthScreen / 6)) - (widthScreen / 7))) &&
                                (startPoint.x < ((widthScreen - (widthScreen / 6)) + (widthScreen / 7))) &&
                                (startPoint.y < ((heightScreen / 2) + (heightScreen / 5))) &&
                                (startPoint.y > ((heightScreen / 2) - (heightScreen / 5)))
                                && (level == 3)) {
                            int idR = getResources().getIdentifier("ballgreen", "drawable",getPackageName());
                            final Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), idR);
                            greenBall.setImageBitmap(bitmapImage);
                            swipeAnimation(0, 0, 0, ((widthScreen * 10) / 22));
                        }else {
                            touchBallFlag = false;
                        }
                    }else{
                        if((startPoint.x > ((widthScreen / 2) - (widthScreen / 8))) &&
                                (startPoint.x < ((widthScreen / 2) + (widthScreen / 10))) &&
                                (startPoint.y < ((heightScreen / 2) + (heightScreen / 5))) &&
                                (startPoint.y > ((heightScreen / 2) - (heightScreen / 5)))
                                && (level == 3) && !instructionFlag) {
                            touchBallFlag = true;
                        }else {
                            touchBallFlag = false;
                            instructions.setAlpha(0.0f);
                            instructionFlag = false;
                            if(level == 0){
                                level++;
                                refreshInstructions();
                            }
                        }
                    }

                }
                break;
            default:
                break;

        }
        return true;
    }
}