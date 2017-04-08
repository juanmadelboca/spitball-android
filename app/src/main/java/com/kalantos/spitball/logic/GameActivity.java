package com.kalantos.spitball.logic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kalantos.spitball.GUI.MenuActivity;
import com.kalantos.spitball.R;
import com.kalantos.spitball.GUI.finishGameActivity;
import com.kalantos.spitball.connectivity.SendMoveTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class GameActivity extends AppCompatActivity {

    Tile[][] tiles;
    final int width = 10;
    final int height = 6;
    boolean gameOver = false;
    private int clicks = 0;
    private int playerTurn = 0;
    private int GameId, onlineTurn;
    private int ax, ay, green, pink, difficulty;
    private boolean ArtificialInteligence, onlineMove, isMyTurn,movelock;
    private int widthScreen, heightScreen;


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
        display.getSize(size);
        widthScreen = size.x + size.x / 14;
        heightScreen = size.y;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean click = settings.getBoolean("clickMode", false);

        Intent intent = getIntent();
        difficulty = intent.getIntExtra("difficulty", 0);
        onlineTurn = intent.getIntExtra("TURN", 0);
        GameId = intent.getIntExtra("GAMEID", 0);
        ArtificialInteligence = intent.getBooleanExtra("AI", true);
        System.out.println("la dificultad es" + difficulty);
        if (click) {
            clickBoard();
        } else {
            swipeBoard();
        }
        inicialize();
        if(onlineTurn==0) {
            Toast.makeText(this, " JUGADOR VERDE!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, " JUGADOR ROSA!", Toast.LENGTH_LONG).show();
        }
        if (GameId != 0) {
            if (onlineTurn == 1) {
                playerTurn++;
                isMyTurn = false;
            } else {
                isMyTurn = true;
                try {
                    new SendMoveTask().execute("http://kalantos.dhs.org/gameMove.php", "MOVE", "0", "0", "0", "0", "0", Integer.toString(GameId), Integer.toString(1)).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            Thread refreshOnlineThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!gameOver) {
                        getOnlineMove();
                        Log.d("THREAD", "IS MY TURN: " + isMyTurn);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            refreshOnlineThread.start();
        }
        paint();
        //manejo de las actividades
    }

    public void inicialize() {
        //inicializa los valores de las bolas iniciales
        tiles[1][3].setBall(20, BallType.BALLGREEN);
        tiles[2][2].setBall(20, BallType.BALLGREEN);
        tiles[3][3].setBall(20, BallType.BALLGREEN);
        tiles[1][5].setBall(20, BallType.BALLPINK);
        tiles[2][6].setBall(20, BallType.BALLPINK);
        tiles[3][5].setBall(20, BallType.BALLPINK);
    }

    public void paint() {
        //las imagenes no pueden exeder los 5kB porque sino el tiempo de dibujo exede los 200ms y no se ve fluido
        //recorre la matriz de tiles y va colocando las imagenes correspondientes al tamaño de la bola

        long time_start, time_end;
        time_start = System.currentTimeMillis();
        green = 0;
        pink = 0;
        for (int i = 0; i < height; i++) {

            for (int j = 0; j < width; j++) {
                double temp = (tiles[i][j].getBall().getSize()) + 110 - ((tiles[i][j].getBall().getSize()) * 0.25);
                int ballSize = (int) temp;
                //probar velocidad declarando un bitmap afuera
                if (tiles[i][j].getBall() instanceof BallGreen) {
                    Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.ballgreen_small);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, ballSize, ballSize, false);
                    tiles[i][j].getImageView().setImageBitmap(scaled);
                    tiles[i][j].getImageView().setScaleType(ImageView.ScaleType.CENTER);
                    green++;
                } else if (tiles[i][j].getBall() instanceof BallPink) {
                    Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.ballpink_small);
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
            gameOver = true;
        }
        if (pink == 0) {
            finishGame();
            gameOver = true;
        }
        time_end = System.currentTimeMillis();
        System.out.println("the task has taken " + (time_end - time_start) + " milliseconds");
        //debug();
    }

    private void finishGame() {
        //se ejecuta cuando termina el juego, finaliza la activad y procede a la acitvidad que muestra al ganador
        Intent intent = new Intent(GameActivity.this, finishGameActivity.class);
        intent.putExtra("green", green);
        intent.putExtra("pink", pink);
        startActivity(intent);
        finishAffinity();
    }

    private void clickBoard() {
        //arma el tablero  usando las medidas de la pantalla
        tiles = new Tile[height][width];
        LinearLayout layout = (LinearLayout) findViewById(R.id.layaout); //Can also be done in xml by android:orientation="vertical"

        if (layout != null) {
            for (int i = 0; i < height; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                for (int j = 0; j < width; j++) {
                    tiles[i][j] = new Tile(this, (heightScreen / 6) * (i + 1), (widthScreen / 10) * (j + 1));
                    tiles[i][j].getImageView().setLayoutParams(new LinearLayout.LayoutParams(widthScreen / 10, heightScreen / 6));
                    tiles[i][j].getImageView().setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //devuelve el tile que fue presionado
                            for (int i = 0; i < height; i++) {

                                for (int j = 0; j < width; j++) {
                                    if (v.getId() == tiles[i][j].getImageView().getId()) {
                                        ClickGestion(i, j);
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
        tiles = new Tile[height][width];
        LinearLayout layout = (LinearLayout) findViewById(R.id.layaout); //Can also be done in xml by android:orientation="vertical"

        if (layout != null) {
            for (int i = 0; i < height; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                for (int j = 0; j < width; j++) {
                    //la suma al ancho y alto son un factor de correccion por las barras de navegacion y la de la hora
                    tiles[i][j] = new Tile(this, (heightScreen / 6) * (i + 1), (widthScreen / 10) * (j + 1));
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
                                        clicks = 0;
                                        int[] temporalStart = detectMove(startPoint.y, startPoint.x);
                                        int[] temporalEnd = detectMove(endPoint.y, endPoint.x);

                                        if (temporalStart != null && temporalEnd != null) {
                                            swipeGestion(temporalStart[0], temporalStart[1]);
                                            swipeGestion(temporalEnd[0], temporalEnd[1]);
                                        }
                                    } else {
                                        //al no superar el tiempo toma como 1 punto y luego espera al siguiente
                                        int[] temporal = detectMove(startPoint.y, startPoint.x);
                                        if (temporal != null) {
                                            ClickGestion(temporal[0], temporal[1]);
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

    public int[] detectMove(float y, float x) {
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

    public void ClickGestion(int i, int j) {
        //recibe dos enteros que le indican el Tile presionado y por medio de eso gestiona las acciones a realizar
        //por medio de la variable global clicks y turn maneja los clicks para saber cuando corresponde mover o esperar una coordenada
        if ((tiles[i][j].getBall() instanceof BallGreen) && clicks == 0 && (playerTurn % 2 == 0)) {
            ax = i;
            ay = j;
            clicks++;
            return;

        } else if ((tiles[i][j].getBall() instanceof BallPink) && clicks == 0 && (playerTurn % 2 == 1)) {
            ax = i;
            ay = j;
            clicks++;
            return;

        } else if (clicks == 1) {
            //CANCEL SELECTION
            if (ax == i && ay == j) {
                clicks = 0;
                return;
            } else if (Math.abs(ax - i) > 2 || Math.abs(ay - j) > 2) {
                //OUTBOUND MOVEMENT
                clicks = 0;
                return;
            } else if ((Math.abs(ax - i) == 1 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 0 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 1 && Math.abs(ay - j) == 0)) {
                //MOVE
                move(ax, ay, i, j);
            } else if (ax - i == -2 && ay == j) {
                //SPLIT DOWN
                split(ax, ay, 2, 0);
            } else if (ax - i == 2 && ay == j) {
                //SPLIT UP
                split(ax, ay, -2, 0);
            } else if (ay - j == -2 && i == ax) {
                //SPLIT RIGHT
                split(ax, ay, 0, 2);
            } else {
                //SPLIT LEFT
                split(ax, ay, 0, -2);
            }

            ArtificialMove();
        }
    }

    public void swipeGestion(int i, int j) {
        //recibe dos enteros que le indican el Tile presionado y por medio de eso gestiona las acciones a realizar
        if ((tiles[i][j].getBall() instanceof BallGreen) && clicks == 0 && (playerTurn % 2 == 0)) {
            ax = i;
            ay = j;
            clicks++;
            return;

        } else if ((tiles[i][j].getBall() instanceof BallPink) && clicks == 0 && (playerTurn % 2 == 1)) {
            ax = i;
            ay = j;
            clicks++;
            return;

        } else if (clicks == 1) {
            //CANCEL SELECTION
            if (ax == i && ay == j) {
                clicks = 0;
                return;
            } else if ((Math.abs(ax - i) == 1 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 0 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 1 && Math.abs(ay - j) == 0)) {
                //MOVE
                move(ax, ay, i, j);
            } else if (ax - i < 0 && ay == j) {
                //SWIPE DOWN
                move(ax, ay, ax + 1, ay);
            } else if (ax - i > 0 && ay == j) {
                //SWIPE UP
                move(ax, ay, ax - 1, ay);
            } else if (ay - j < 0 && i == ax) {
                //SWIPE RIGHT
                move(ax, ay, ax, ay + 1);
            } else if (ay - j > 0 && i == ax) {
                //SWIPE LEFT
                move(ax, ay, ax, ay - 1);
            } else if (ay - j > 0 && ax - i > 0 && ay - j == (ax - i)) {
                //SWIPE CORNERS
                //esq superior izq
                move(ax, ay, ax - 1, ay - 1);
            } else if (ay - j < 0 && ax - i > 0 && ay - j == -(ax - i)) {
                //esq superior derecha
                move(ax, ay, ax - 1, ay + 1);
            } else if (ay - j > 0 && ax - i < 0 && ay - j == -(ax - i)) {
                //esq inferior izq
                move(ax, ay, ax + 1, ay - 1);
            } else if(ay - j > 0 && ax - i > 0 && ay - j == -(ax - i)){
                //esq inferior derecha
                move(ax, ay, ax + 1, ay + 1);
            }else{
                clicks=0;
                return;
            }
            ArtificialMove();

        }
    }

    public void move(int i, int j, int y, int x) {
        // primeros 2 los originales 2 dos a donde van
        //mueve la bola
        if ((!onlineMove && isMyTurn) || (onlineMove&&!movelock) || GameId == 0) {

            tiles[y][x].battle(tiles[i][j].getBall());
            tiles[i][j].removeBall();
            clicks = 0;
            debug();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    paint();
                }
            });
            movelock=true;
        }
        //debug();
        if (!onlineMove && GameId != 0) {
            try {
                new SendMoveTask().execute("http://kalantos.dhs.org/gameMove.php", "MOVE", Integer.toString(j), Integer.toString(i), Integer.toString(x), Integer.toString(y), Integer.toString(0), Integer.toString(GameId), Integer.toString(onlineTurn)).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (GameId == 0) {
            playerTurn++;
        }
    }

    public void getOnlineMove() {
        //obtiene las coordenadas del ultimo movimiento y lo ejecuta de forma local

        int[] onlineMoves = recieveJSON();
        onlineMove = true;
        if (onlineMoves[5] != onlineTurn) {
            if (onlineMoves[4] == 0) {
                move(onlineMoves[1], onlineMoves[0], onlineMoves[3], onlineMoves[2]);
            } else {
                split(onlineMoves[1], onlineMoves[0], onlineMoves[3], onlineMoves[2]);
            }
            movelock=true;
            isMyTurn = true;
        } else {
            isMyTurn = false;
            movelock=false;

        }
        onlineMove = false;


    }

    private int[] recieveJSON() {
        try {
            String st = new SendMoveTask().execute("http://kalantos.dhs.org/gameMove.php", "GETMOVE", "5", "1", "5", "1", "1", String.valueOf(GameId), "1").get();
            //Log.d("RECIBO PARSE",st);
            //parsea un JSON para obtener un array con los movimientos
            int[] moves = new int[6];
            JSONObject json = null;
            try {
                json = new JSONObject(st);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                moves[0] = json.getInt("XINIT");
                moves[1] = json.getInt("YINIT");
                moves[2] = json.getInt("XLAST");
                moves[3] = json.getInt("YLAST");
                moves[4] = json.getInt("SPLIT");
                moves[5] = json.getInt("TURN");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            return moves;

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void ArtificialMove() {
        //va en bloque try catch porque cuando termina el juego la AI intenta mover y genera excepcion de esta forma cuando esta
        //excepcion ocurre no tenogo problemas
        if (ArtificialInteligence) {
            try {
                int[] AIMoves;
                switch (difficulty) {
                    case 0:
                        AIMoves = ArtificialInteligenceAlgorithm.easyMove(tiles);
                        break;
                    case 1:
                        AIMoves = ArtificialInteligenceAlgorithm.hardMove(tiles, false);
                        break;
                    case 2:
                        AIMoves = ArtificialInteligenceAlgorithm.hardMove(tiles, true);
                        break;
                    default:
                        AIMoves = ArtificialInteligenceAlgorithm.easyMove(tiles);
                        break;
                }
                if (AIMoves[4] != -1) {
                    move(AIMoves[0], AIMoves[1], AIMoves[2], AIMoves[3]);
                } else {
                    split(AIMoves[0], AIMoves[1], AIMoves[2], AIMoves[3]);
                }


            } catch (Exception e) {
                finishGame();
            }
        }
    }

    public void split(int i, int j, int y, int x) {
        //escupe una bola 33% del tamaño de ella misma

        if (!onlineMove&& GameId != 0) {
            try {
                new SendMoveTask().execute("http://kalantos.dhs.org/gameMove.php", "MOVE", Integer.toString(j), Integer.toString(i), Integer.toString(x), Integer.toString(y), Integer.toString(999), Integer.toString(GameId), Integer.toString(onlineTurn)).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if ((!onlineMove && isMyTurn) || (onlineMove && !movelock)|| GameId == 0) {

            int splittedBallSize = tiles[i][j].getBall().getSize() / 3;

            if (tiles[i][j].getBall() instanceof BallGreen) {
                BallGreen splittedBall = new BallGreen((int) (splittedBallSize * 1.2));
                if (tiles[i][j].getBall().getSize() >= 10) {
                    try {
                        tiles[i][j].getBall().setSize(tiles[i][j].getBall().getSize() - splittedBallSize);
                        tiles[i + y][j + x].battle(splittedBall);
                    } catch (Exception e) {
                        System.out.println("un poco de tu masa se cayo del tablero");
                    }
                }
            }
            if (tiles[i][j].getBall() instanceof BallPink) {
                BallPink splitttedBall = new BallPink((int) (splittedBallSize * 1.2));
                if (tiles[i][j].getBall().getSize() >= 10) {
                    try {
                        tiles[i][j].getBall().setSize(tiles[i][j].getBall().getSize() - splittedBallSize);
                        tiles[i + y][j + x].battle(splitttedBall);
                    } catch (Exception e) {
                        System.out.println("un poco de tu masa se cayo del tablero");
                    }
                }
            }

            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    paint();
                }
            });
            clicks = 0;
            movelock=true;
        }

        if (GameId == 0) {
            playerTurn++;
        }

    }

    private void debug() {
        //metodo para crear una matriz con los valores size de las bolas
        //util para debugear fallas graficas
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(tiles[i][j].getBall().getSize() + "    ");
            }
            System.out.println("\n");
        }

    }


}



