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
import com.kalantos.spitball.engine.BallPink;
import com.kalantos.spitball.engine.GameManager;
import com.kalantos.spitball.engine.Timer;
import com.kalantos.spitball.utils.TileView;

import java.sql.Timestamp;
import java.util.Calendar;

public class GameActivity extends AppCompatActivity {

    TileView[][] tiles;
    final int width = 10;
    final int height = 6;
    boolean gameOver = false;
    private int green=0, pink=0;
    private int widthScreen, heightScreen;
    private int bouncingState=1;
    private GameManager game;
    boolean debug=false;
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
                        e.printStackTrace();
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
        game = new GameManager(GameId, difficulty, onlineTurn,ArtificialInteligence);
        //creo el  tablero de imagenes correspondiente
        if (click) {
            clickBoard();
        } else {
            swipeBoard();
        }
        //aviso el color que le toca al usuario
        if(onlineTurn==0) {
            Toast.makeText(this, " JUGADOR VERDE!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, " JUGADOR ROSA!", Toast.LENGTH_LONG).show();
        }
        startAnimationThread();


        //manejo de las actividades
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
        Runnable runnable= new Runnable() {
            @Override
            public void run() {
                while(game.gameStatus()){
                    /*Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    if(debug)
                        System.out.println("Thread1: "+timestamp);*/
                    refresh();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
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
       /* final Thread refreshThread2 = new Thread(runnable);
        final Thread refreshThread3 = new Thread(runnable);
        final Thread refreshThread4 = new Thread(runnable);
        try {
            refreshThread1.start();
            Thread.sleep(200);
            refreshThread2.start();
            Thread.sleep(200);
            refreshThread3.start();
            Thread.sleep(200);
            refreshThread4.start();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        paint();
    }
    public void paint() {
        //las imagenes no pueden exeder los 5kB porque sino el tiempo de dibujo exede los 200ms y no se ve fluido
        //recorre la matriz de tiles y va colocando las imagenes correspondientes al tamaño de la bola

        if(game.detectMoves()){
            unpressTiles();
        }
        long time_start, time_end;
        time_start = System.currentTimeMillis();
        green = 0;
        pink = 0;
        for (int i = 0; i < height; i++) {

            for (int j = 0; j < width; j++) {
                //poner calculo dentro del if para no hacer calculo al pedo
                double temp = (game.getTiles()[i][j].getBall().getSize()) + heightScreen/14 - ((game.getTiles()[i][j].getBall().getSize())*(1/7)*((double)widthScreen/heightScreen));
                int ballSize = (int) temp;
                //probar velocidad declarando un bitmap afuera
                if (game.getTiles()[i][j].getBall() instanceof BallGreen) {
                    int idR;
                    if(tiles[i][j].isPressed()){
                        //BOUNCING
                        String resourstring="framel"+bouncingState;
                        idR= getResources().getIdentifier(resourstring,"drawable",getPackageName());
                        tiles[i][j].getImageView().setImageResource(idR);
                        bouncingState++;
                        if(bouncingState==9){
                            bouncingState=1;
                        }
                    }else {
                        idR= getResources().getIdentifier("ballgreen_small","drawable",getPackageName());
                    }
                    Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), idR);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, ballSize, ballSize, false);
                    tiles[i][j].getImageView().setImageBitmap(scaled);
                    tiles[i][j].getImageView().setScaleType(ImageView.ScaleType.CENTER);
                    green++;

                } else if (game.getTiles()[i][j].getBall() instanceof BallPink) {
                    int idR;
                    if(tiles[i][j].isPressed()){
                        //BOUNCING
                        String resourstring="frame"+bouncingState;
                        idR= getResources().getIdentifier(resourstring,"drawable",getPackageName());
                        tiles[i][j].getImageView().setImageResource(idR);
                        bouncingState++;
                        if(bouncingState==9){
                            bouncingState=1;
                        }
                    }else {
                        idR= getResources().getIdentifier("ballpink_small","drawable",getPackageName());
                    }
                    Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(),idR);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, ballSize, ballSize, false);
                    tiles[i][j].getImageView().setImageBitmap(scaled);
                    tiles[i][j].getImageView().setScaleType(ImageView.ScaleType.CENTER);
                    pink++;
                } else {
                    tiles[i][j].getImageView().setImageDrawable(null);
                }


            }
        }
        if (green == 0) {
            finishGame();
            game.setGameStatus(true);
        }
        if (pink == 0) {
            finishGame();
            game.setGameStatus(true);
        }
        time_end = System.currentTimeMillis();
        if(debug)
            System.out.println("the task has taken " + (time_end - time_start) + " milliseconds");
        //debug();

    }
    private void unpressTiles(){
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tiles[i][j].release();
            }
        }
    }
    private void finishGame() {
        //se ejecuta cuando termina el juego, finaliza la activad y procede a la acitvidad que muestra al ganador
        Intent intent = new Intent(GameActivity.this, finishGameActivity.class);
        intent.putExtra("green", green);
        intent.putExtra("pink", pink);
        Log.d("DEBUG","ENTRE AL FINISH,gameover: "+gameOver+" - gren: "+green+" -pink: "+pink);
        startActivity(intent);
        finishAffinity();

    }
    private void clickBoard() {
        //arma el tablero  usando las medidas de la pantalla
        tiles = new TileView[height][width];
        LinearLayout layout = (LinearLayout) findViewById(R.id.layaout); //Can also be done in xml by android:orientation="vertical"

        if (layout != null) {
            for (int i = 0; i < height; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                for (int j = 0; j < width; j++) {
                    tiles[i][j] = new TileView(this, (heightScreen / 6) * (i + 1), (widthScreen / 10) * (j + 1));
                    tiles[i][j].getImageView().setLayoutParams(new LinearLayout.LayoutParams(widthScreen / 10, heightScreen / 6));
                    tiles[i][j].getImageView().setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //devuelve el tile que fue presionado
                            for (int i = 0; i < height; i++) {

                                for (int j = 0; j < width; j++) {
                                    if (v.getId() == tiles[i][j].getImageView().getId()) {
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
                    tiles[i][j].getImageView().setId(j + (i * 10));
                    row.addView(tiles[i][j].getImageView());
                }

                layout.addView(row);
            }
        }
    }
    private void swipeBoard() {
        //arma el tablero  usando las medidas de la pantalla
        tiles = new TileView[height][width];
        LinearLayout layout = (LinearLayout) findViewById(R.id.layaout); //Can also be done in xml by android:orientation="vertical"

        if (layout != null) {
            for (int i = 0; i < height; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                for (int j = 0; j < width; j++) {
                    //la suma al ancho y alto son un factor de correccion por las barras de navegacion y la de la hora
                    tiles[i][j] = new TileView(this, (heightScreen / 6) * (i + 1), (widthScreen / 10) * (j + 1));
                    tiles[i][j].getImageView().setLayoutParams(new LinearLayout.LayoutParams(widthScreen / 10, heightScreen / 6));
                    //seteo la accion al ser tocado (deslizado en este caso)
                    tiles[i][j].getImageView().setOnTouchListener(new View.OnTouchListener() {
                        final int MAX_CLICK_DURATION = 200;
                        long startClickTime;
                        PointF startPoint = new PointF();
                        PointF endPoint = new PointF();

                        public boolean onTouch(View v, MotionEvent event) {

                            int eventId = event.getAction();
                            switch (eventId) {
                                case MotionEvent.ACTION_DOWN:
                                    //cuando apreto comienza a contar la trayectoria
                                    startClickTime = Calendar.getInstance().getTimeInMillis();
                                    startPoint.y = event.getRawY();
                                    startPoint.x = event.getRawX();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    //al mover se actualiza
                                    endPoint.y = event.getRawY();
                                    endPoint.x = event.getRawX();
                                    break;
                                case MotionEvent.ACTION_UP:
                                    //al levantar el dedo ya son coordenadas finales que son enviadas a detect move para procesarlas
                                    if ((Calendar.getInstance().getTimeInMillis() - startClickTime) >= MAX_CLICK_DURATION) {
                                        //si la duracion del arrastre es mas larga que max click time se cuenta como deslizamiento
                                        // y se comprueban las coordenadas para que luego clickgestion o swipe gestion realize los movimientos
                                        game.clicks = 0;
                                        int[] temporalStart = detectMove(startPoint.y, startPoint.x);
                                        int[] temporalEnd = detectMove(endPoint.y, endPoint.x);

                                        if (temporalStart != null && temporalEnd != null) {
                                            game.swipeGestion(temporalStart[0], temporalStart[1]);
                                            game.swipeGestion(temporalEnd[0], temporalEnd[1]);
                                        }
                                    } else {
                                        //al no superar el tiempo toma como 1 punto y luego espera al siguiente
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
                    tiles[i][j].getImageView().setId(j + (i * 10));
                    row.addView(tiles[i][j].getImageView());
                }

                layout.addView(row);
            }
        }
    }
    private int[] detectMove(float y, float x) {
        //certifica que sean coordenadas validas dentro del tablero
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((tiles[i][j].getBoundsY() - (heightScreen / 6)) < (int) y && (int) y < tiles[i][j].getBoundsY()) {

                    if ((tiles[i][j].getBoundsX() - (widthScreen / 10)) < (int) x && (int) x < tiles[i][j].getBoundsX()) {
                        return new int[]{i, j};

                    }
                }
            }
        }
        return null;
    }


    public void refresh(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                paint();
            }
        });
    }


}



