package com.kalantos.spitballv001;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {

    Tile[][] tiles;
    final int width = 10;
    final int height = 6;

    private int clicks = 0;
    private int turno = 0;
    private int ax, ay, green, pink,difficulty,flag;
    private boolean ArtificialInteligence;
    private int widthScreen,heightScreen;
    /////////////////////////////////////////
    //variables que pueden ser reemplazadas
    PointF startPoint=new PointF();
    PointF endPoint=new PointF();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x+size.x/14;
        heightScreen = size.y;

        Intent intent   =getIntent();
        difficulty=intent.getIntExtra("difficulty",0);
        ArtificialInteligence=intent.getBooleanExtra("AI",true);
        System.out.println("la dificultad es"+difficulty);
        boolean click=intent.getBooleanExtra("clicker",false);
        System.out.println("el booleano es: "+click);
        if(click) {
            clickBoard();
        }else {
            swipeBoard();
        }
        inicialize();
        paint();

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
        //pinta el tablero y las bolas

        long time_start, time_end;
        time_start = System.currentTimeMillis();
        green = 0;
        pink = 0;
        for (int i = 0; i < height; i++) {

            for (int j = 0; j < width; j++) {
            double temp= (tiles[i][j].getBall().getSize() ) + 110;
                int ballSize=(int)temp;

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
        }
        if (pink == 0) {
            finishGame();

        }
        time_end = System.currentTimeMillis();
     //   System.out.println("the task has taken "+ ( time_end - time_start ) +" milliseconds");
        debug();
    }

    private void finishGame() {
        //se ejecuta cuando termina el juego, finaliza la activad y procede a la acitvidad que muestra al ganador
        Intent intent = new Intent(GameActivity.this, finishGameActivity.class);
        intent.putExtra("green", green);
        intent.putExtra("pink", pink);
        startActivity(intent);
        finish();
    }

    private void clickBoard() {
        //arma el tablero  usando las medidas de la pantalla
        tiles = new Tile[height][width];
        LinearLayout layout = (LinearLayout) findViewById(R.id.layaout); //Can also be done in xml by android:orientation="vertical"


        for (int i = 0; i < height; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < width; j++) {
                tiles[i][j] = new Tile(this,(heightScreen/6)*(i+1),(widthScreen/10)*(j+1));
                tiles[i][j].getImageView().setLayoutParams(new LinearLayout.LayoutParams(widthScreen / 10, heightScreen / 6));
                tiles[i][j].getImageView().setOnClickListener(new View.OnClickListener(){
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
    private void swipeBoard() {
        //arma el tablero  usando las medidas de la pantalla
        tiles = new Tile[height][width];
        LinearLayout layout = (LinearLayout) findViewById(R.id.layaout); //Can also be done in xml by android:orientation="vertical"

           for (int i = 0; i < height; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < width; j++) {
                //la suma al ancho y alto son un factor de correccion por las barras de navegacion y la de la hora
                tiles[i][j] = new Tile(this,(heightScreen/6)*(i+1),(widthScreen/10)*(j+1));
                tiles[i][j].getImageView().setLayoutParams(new LinearLayout.LayoutParams(widthScreen / 10, heightScreen / 6));
                tiles[i][j].getImageView().setOnTouchListener(new View.OnTouchListener(){
                    public boolean onTouch(View v,MotionEvent event) {
                        //devuelve el tile que fue presionado
                       // int modification=100;

                        int eventId=event.getAction();
                        //System.out.println("CLICK NUEVO");
                        switch (eventId){
                            case MotionEvent.ACTION_DOWN:
                                System.out.println("ACTION DOWN");
                                startPoint=new PointF();
                                endPoint= new PointF();
                                //roto x por y porqe la pantalla esta en landscape
                                startPoint.y=event.getRawY();
                                startPoint.x=event.getRawX();
                                int[] temporal= detectMove(startPoint.y,startPoint.x);
                                if(temporal!=null) {
                                    ClickGestion(temporal[0], temporal[1]);
                                }
                               break;
                            case MotionEvent.ACTION_MOVE:
                                //System.out.println("ACTION MOVE");
                                // ver si esta bien con las coordenadas cruzadas
                                endPoint.y=event.getRawY();
                                endPoint.x=event.getRawX();
                                break;
                            case MotionEvent.ACTION_UP:
                                System.out.println("ACTION UP");
                                temporal=detectMove(endPoint.y,endPoint.x);
                                if(temporal!=null) {
                                    System.out.println("!null");
                                    ClickGestion(temporal[0], temporal[1]);
                                }else{
                                    System.out.println("ELSE");
                                    temporal= detectMove(startPoint.y,startPoint.x);
                                    if(temporal!=null) {
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



    public int[] detectMove(float y,float x) {
        //
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((tiles[i][j].getBoundsY() - (heightScreen / 6)) <(int) y && (int) y < tiles[i][j].getBoundsY()) {

                    if ((tiles[i][j].getBoundsX() - (widthScreen / 10)) <(int) x &&(int) x < tiles[i][j].getBoundsX()) {
                        System.out.println(" TILE :" +i+" - "+j);
                        return new int[]{i,j};

                    }
                }
            }
        }
        return null;
    }

    public void ClickGestion(int i, int j) {
        //recibe dos enteros que le indican el Tile presionado y por medio de eso gestiona las acciones a realizar

        //System.out.println(""+i+"-"+j );
        if ((tiles[i][j].getBall() instanceof BallGreen) && clicks == 0 && (turno % 2 == 0)) {
            ax = i;
            ay = j;
            clicks++;
            turno++;
            System.out.println("SOY CLICK 1 DE BALL GREEN");
            return;

        }  if ((tiles[i][j].getBall() instanceof BallPink) && clicks == 0 && (turno % 2 == 1)) {
                ax = i;
                ay = j;
                clicks++;
                turno++;
                System.out.println("SOY CLICK 1 DE BALL PINK");
                return;

                }




        if (clicks == 1) {
            System.out.println("SOY CLICK 2 DE CUALQUIER BOLA");
                //CANCEL SELECTION
                if (ax == i && ay == j) {
                    clicks = 0;
                    turno--;
                    flag=1;
                }
                //MOVE
                if ((Math.abs(ax - i) == 1 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 0 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 1 && Math.abs(ay - j) == 0)) {
                    move(ax, ay, i, j);
                }
                //SPLIT DOWN
                if (ax - i == -2 && ay == j) {

                    split(ax, ay,2,0);
                }
                //SPLIT UP
                if (ax - i == 2 && ay == j) {
                    split(ax, ay,-2,0);
                }
                //SPLIT RIGHT
                if (ay - j == -2 && i == ax) {
                    split(ax, ay,0,2);
                }
                //SPLIT LEFT
                if (ay - j == 2 && i == ax) {
                    split(ax, ay,0,-2);
                }

                //revisar
            if(ArtificialInteligence&&flag!=1) {
               ArtificialMove();
            }
            flag=0;


        }
    }


    public void move(int i, int j, int y, int x) {// primeros 2 los originales 2 dos a donde van
        //mueve la bola
        tiles[y][x].battle(tiles[i][j].getBall());
        tiles[i][j].removeBall();
        paint();
        clicks = 0;
      //  debug();
    }
    public void ArtificialMove() {
        //va en bloque try catch porque cuando termina el juego la AI intenta mover y genera excepcion de esta forma cuando esta
        //excepcion ocurre no tenogo problemas
        try {
            int[] AIMoves;
            switch (difficulty) {
                case 0:
                    AIMoves = ArtificialInteligenceAlgorithm.RandomMove(tiles);
                    break;
                case 1:
                    AIMoves = ArtificialInteligenceAlgorithm.easyMove(tiles);
                    break;
                case 2:
                    AIMoves = ArtificialInteligenceAlgorithm.hardMove(tiles);
                    break;
                default:
                    AIMoves = ArtificialInteligenceAlgorithm.RandomMove(tiles);
                    break;
            }
            System.out.println(""+AIMoves[0]+AIMoves[1]+AIMoves[2]+AIMoves[3]);
            if (AIMoves[4]!=-1) {
                move(AIMoves[0], AIMoves[1], AIMoves[2], AIMoves[3]);
            }else  {
                split(AIMoves[0], AIMoves[1], AIMoves[2], AIMoves[3]);
            }
            turno++;

        } catch (Exception e) {
            finishGame();
        }
    }


    public void split(int i, int j, int y, int x) {
        //escupe una bola 33% del tamaño de ella misma
        int splittedBallSize=tiles[i][j].getBall().getSize()/3;

        if (tiles[i][j].getBall() instanceof BallGreen) {
            BallGreen splittedBall = new BallGreen(splittedBallSize);
            if (tiles[i][j].getBall().getSize() >= 10) {
                try {
                    tiles[i][j].getBall().setSize(tiles[i][j].getBall().getSize() - splittedBallSize);
                    tiles[i + y][j+x].battle(splittedBall);
                } catch (Exception e) {
                    System.out.println("un poco de tu masa se cayo del tablero");
                }
            }
        }
        if (tiles[i][j].getBall() instanceof BallPink) {
            BallPink splitttedBall = new BallPink(splittedBallSize);
            if (tiles[i][j].getBall().getSize() >= 10) {
                try {
                    tiles[i][j].getBall().setSize(tiles[i][j].getBall().getSize() - splittedBallSize);
                    tiles[i + y][j+x].battle(splitttedBall);
                } catch (Exception e) {
                    System.out.println("un poco de tu masa se cayo del tablero");
                }
            }
        }

        paint();
        clicks=0;
        debug();

    }

    private void debug() {
        //metodo para crear una matriz con los valores size de las bolas
        //util para debugear fallas graficas
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //System.out.print(tiles[i][j].getBall().getSize() + "    ");
                System.out.print(tiles[i][j].getBoundsY()+"/"+tiles[i][j].getBoundsX() + "    ");
            }
            System.out.println("\n");
        }

    }


}



