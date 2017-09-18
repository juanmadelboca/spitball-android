package com.kalantos.spitball.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.kalantos.spitball.R;
import com.kalantos.spitball.engine.BallGreen;
import com.kalantos.spitball.engine.GameManager;
import com.kalantos.spitball.engine.Timer;
import com.kalantos.spitball.utils.TileView;
import java.util.Calendar;

public class GameActivity extends AppCompatActivity {

    TileView[][] tiles;
    final int width = 10;
    final int height = 6;
    private int greenBallsLeft=0, pinkBallsLeft=0;
    private int widthScreen, heightScreen;
    private int bouncingState=1;
    private GameManager game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final View decorView = getWindow().getDecorView();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

        getWindow().getDecorView().setSystemUiVisibility(flags);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        widthScreen = size.x;
        heightScreen = size.y;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean click = settings.getBoolean("clickMode", false);
        boolean bouncing = settings.getBoolean("bouncing", true);
        if(!bouncing){
            bouncingState=-1;
        }
        Intent intent = getIntent();
        int difficulty = intent.getIntExtra("difficulty", 0);
        int onlineTurn = intent.getIntExtra("TURN", 0);
        int GameId = intent.getIntExtra("GAMEID", 0);
        boolean ArtificialInteligence = intent.getBooleanExtra("AI", true);
        //create logic board.
        game = new GameManager(GameId, difficulty, onlineTurn,ArtificialInteligence);
        if (click) {
            clickBoard();
        } else {
            swipeBoard();
        }
        //Tell the user with which color plays.
        if(onlineTurn==0) {
            Toast.makeText(this, " JUGADOR VERDE!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, " JUGADOR ROSA!", Toast.LENGTH_LONG).show();
        }
        startAnimationThread();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Desea volver al menu?").setTitle("Terminar Juego");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent=new Intent(GameActivity.this,MenuActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startAnimationThread(){
    /*
    * Refresh the UI board periodically.
    * */
        Runnable runnable= new Runnable() {
            @Override
            public void run() {
                while(game.gameStatus()){
                    refresh();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Log.e("BOUNCING ANIMATION", e.getMessage());
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        paint();
                    }
                });
            }
        };
        final Thread refreshThread1 = new Thread(runnable);refreshThread1.start();
        paint();
    }

    private void resetFrame(){
    /*
    * Reset status of press tiles and ball counters
    * */
        if(game.detectMoves()){
            unpressTiles();
        }
        greenBallsLeft = 0;
        pinkBallsLeft = 0;
    }

    private void checkGameStatus(){
        if (greenBallsLeft == 0 || pinkBallsLeft == 0) {
            finishGame();
            game.setGameStatus(true);
        }
    }
    public void paint() {
    /*
    * Re paint all board images, rescaling and animating with the information provide by game class, also counts
    * each team balls.
    * CONSTRAINTS: images cant exceed 5k, or app must use thread logic
    * */
        int ballSize;
        String ballImage;
        Bitmap bitmapImage,scaled;
        //debugging time stuff
        long time_start, time_end;
        time_start = System.currentTimeMillis();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                if(game.getTiles()[i][j].getBall().getSize() > 0){
                    //image
                    if (game.getTiles()[i][j].getBall() instanceof BallGreen) {
                        ballImage = "ballgreen_small";
                        greenBallsLeft++;
                    } else {
                        ballImage = "ballpink_small";
                        pinkBallsLeft++;
                    }
                    int idR;
                    //animation
                    if(tiles[i][j].isPressed()){
                        ballImage = ballImage+bouncingState;
                        bouncingState++;
                        if(bouncingState==9){
                            bouncingState=1;
                        }
                    }

                    idR= getResources().getIdentifier(ballImage,"drawable",getPackageName());
                    //painting
                    ballSize =(int)((game.getTiles()[i][j].getBall().getSize()) + heightScreen/14 -
                            ((game.getTiles()[i][j].getBall().getSize())*(1/7)*((double)widthScreen/heightScreen)));
                    bitmapImage = BitmapFactory.decodeResource(getResources(), idR);
                    scaled = Bitmap.createScaledBitmap(bitmapImage, ballSize, ballSize, false);
                    tiles[i][j].getBallImage().setImageBitmap(scaled);
                    tiles[i][j].getBallImage().setScaleType(ImageView.ScaleType.CENTER);

                } else {
                    tiles[i][j].getBallImage().setImageDrawable(null);
                }
            }
        }
        time_end = System.currentTimeMillis();
        Log.d("PAINT","the task has taken " + (time_end - time_start) + " milliseconds");
    }

    private void unpressTiles(){
    /*
    * Unpress all the UI Tiles
    * */
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j].release();
            }
        }
    }
    private void finishGame() {
    /*
    * Ends the game, and send info to the finish activity to show winner and stats.
    * */
        //se ejecuta cuando termina el juego, finaliza la activad y procede a la acitvidad que muestra al ganador
        Intent intent = new Intent(GameActivity.this, finishGameActivity.class);
        intent.putExtra("green", greenBallsLeft);
        intent.putExtra("pink", pinkBallsLeft);
        startActivity(intent);
        finishAffinity();

    }
    private void clickBoard() {
    /*
    * Make a custom UI board with the size of the screen.
    * TODO:USE LEGIBLE VARIABLES.
    * TODO: DUPLICATE! ONLY NEED ONE BOARD A LITTLE MORE COMPLETE
    * */
        tiles = new TileView[height][width];
        LinearLayout layout = (LinearLayout) findViewById(R.id.layaout); //Can also be done in xml by android:orientation="vertical"

        if (layout != null) {
            for (int i = 0; i < height; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                for (int j = 0; j < width; j++) {
                    tiles[i][j] = new TileView(this, (heightScreen / 6) * (i + 1), (widthScreen / 10) * (j + 1));
                    tiles[i][j].getBallImage().setLayoutParams(new LinearLayout.LayoutParams(widthScreen / 10, heightScreen / 6));
                    tiles[i][j].getBallImage().setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //retun the pressed one
                            for (int i = 0; i < height; i++) {

                                for (int j = 0; j < width; j++) {
                                    if (v.getId() == tiles[i][j].getBallImage().getId()) {
                                        if(game.ClickGestion(i, j)){
                                            tiles[i][j].press();
                                        }else{
                                            tiles[i][j].release();
                                        }
                                    }
                                }
                            }
                        }
                    });
                    tiles[i][j].getBallImage().setId(j + (i * 10));
                    row.addView(tiles[i][j].getBallImage());
                }

                layout.addView(row);
            }
        }
    }

    private void swipeBoard() {
    /*
    * Make a custom UI board with the size of the screen.
    * */
        tiles = new TileView[height][width];
        LinearLayout layout = (LinearLayout) findViewById(R.id.layaout); //Can also be done in xml by android:orientation="vertical"

        if (layout != null) {
            for (int i = 0; i < height; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                for (int j = 0; j < width; j++) {
                    tiles[i][j] = new TileView(this, (heightScreen / 6) * (i + 1), (widthScreen / 10) * (j + 1));
                    tiles[i][j].getBallImage().setLayoutParams(new LinearLayout.LayoutParams(widthScreen / 10, heightScreen / 6));
                    //config swipe action
                    tiles[i][j].getBallImage().setOnTouchListener(new View.OnTouchListener() {
                        final int MAX_CLICK_DURATION = 200;
                        long startClickTime;
                        PointF startPoint = new PointF();
                        PointF endPoint = new PointF();

                        public boolean onTouch(View v, MotionEvent event) {

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
                                    if ((Calendar.getInstance().getTimeInMillis() - startClickTime) >= MAX_CLICK_DURATION) {
                                        //if drag duration is longer than max click is processed as a swipe, and send to the correct gestion.
                                        game.clicks = 0;
                                        int[] temporalStart = detectMove(startPoint.y, startPoint.x);
                                        int[] temporalEnd = detectMove(endPoint.y, endPoint.x);

                                        if (temporalStart != null && temporalEnd != null) {
                                            game.swipeGestion(temporalStart[0], temporalStart[1]);
                                            game.swipeGestion(temporalEnd[0], temporalEnd[1]);
                                        }
                                    } else {
                                        //if drag time is not overcome, is processed as a click and keep waiting for another click.
                                        int[] temporal = detectMove(startPoint.y, startPoint.x);
                                        if (temporal != null) {
                                            if(game.ClickGestion(temporal[0], temporal[1])&&bouncingState>0){
                                                tiles[temporal[0]][temporal[1]].press();
                                            }
                                        }
                                    }


                                    break;
                                default:
                                    break;

                            }
                            return true;
                        }
                    });
                    tiles[i][j].getBallImage().setId(j + (i * 10));
                    row.addView(tiles[i][j].getBallImage());
                }

                layout.addView(row);
            }
        }
    }

    private int[] detectMove(float y, float x) {
    /*
    * Receive 2 float coordinates identifying a click or drag in the screen, and returns
    * a Tile position from the board.
    * */
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((tiles[i][j].getBoundsY() - (heightScreen / 6)) < (int) y && (int) y < tiles[i][j].getBoundsY()) {

                    if ((tiles[i][j].getBoundsX() - (widthScreen / 10)) < (int) x && (int) x < tiles[i][j].getBoundsX()) {
                        return new int[]{i, j};

                    }
                }
            }
        }
        //TODO: when called the function must catch the exception
        return null;
    }


    public void refresh(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resetFrame();
                paint();
                checkGameStatus();
            }
        });

    }


}



