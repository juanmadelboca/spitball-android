package com.kalantos.spitballv001;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
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
    private int ax, ay, green, pink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        armarTablero();
        inicialize();
        paint();

    }

    public void inicialize() {
        tiles[1][3].setBall(20, BallType.BALLGREEN);
        tiles[2][2].setBall(20, BallType.BALLGREEN);
        tiles[3][3].setBall(20, BallType.BALLGREEN);
        tiles[1][5].setBall(20, BallType.BALLPINK);
        tiles[2][6].setBall(20, BallType.BALLPINK);
        tiles[3][5].setBall(20, BallType.BALLPINK);
    }

    public void paint() {
        green = 0;
        pink = 0;

        Drawable drawablegreen = getResources().getDrawable(R.drawable.ballgreen);
        Drawable drawablepink = getResources().getDrawable(R.drawable.ballpink);
        for (int i = 0; i < height; i++) {

            for (int j = 0; j < width; j++) {
            int ballSize=getRescaleSize(i,j);

                if (tiles[i][j].getBall() instanceof BallGreen) {
                    Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.ballgreen);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, ballSize, ballSize, false);
                    tiles[i][j].getImageView().setImageBitmap(scaled);
                    tiles[i][j].getImageView().setScaleType(ImageView.ScaleType.CENTER);
                    green++;
                } else if (tiles[i][j].getBall() instanceof BallPink) {
                    Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.ballpink);
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
    }


    private int getRescaleSize(int i, int j) {

        int conditional = tiles[i][j].getBall().getSize();

        if (conditional < 20) {
            int a=(30+(conditional * 4));
            return a;
        }

        if(conditional<40&&conditional>=20) {
            int a=(conditional*4)+20;
            return a;
        }

        if(conditional<80&&conditional>=40) {
            int a=(conditional*3);
            return a;
        }
        else{
            int a=(conditional*2+10);
            return a;
        }

        }
    private void finishGame() {

        Intent intent = new Intent(GameActivity.this, finishGameActivity.class);
        intent.putExtra("green", green);
        intent.putExtra("pink", pink);
        startActivity(intent);
        finish();
    }

    private void armarTablero() {

        tiles = new Tile[height][width];
        LinearLayout layout = (LinearLayout) findViewById(R.id.layaout); //Can also be done in xml by android:orientation="vertical"
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int widthScreen = size.x;
        int heightScreen = size.y - 80;

        for (int i = 0; i < height; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < width; j++) {
                tiles[i][j] = new Tile(this);
                tiles[i][j].getImageView().setLayoutParams(new LinearLayout.LayoutParams(widthScreen / 10, heightScreen / 6));
                tiles[i][j].getImageView().setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {

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


    public void ClickGestion(int i, int j) {
        //ya anda bien un click puedo aprovechar y con el isntanceof hacer los turnos

        if ((tiles[i][j].getBall() instanceof BallGreen) && clicks == 0 && (turno % 2 == 0)) {
            ax = i;
            ay = j;
            clicks++;
            System.out.println("soy instancia de alguna bola verde");
            turno++;
            return;

        } else {
            if ((tiles[i][j].getBall() instanceof BallPink) && clicks == 0 && (turno % 2 == 1)) {
                ax = i;
                ay = j;
                clicks++;
                System.out.println("soy instancia de alguna bola rosa");
                turno++;
                return;

            }

        }


        if (clicks == 1) {
            //CANCEL SELECTION
            if (ax == i && ay == j) {
                clicks = 0;
                turno--;
            }
            //MOVE
            if ((Math.abs(ax - i) == 1 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 0 && Math.abs(ay - j) == 1) || (Math.abs(ax - i) == 1 && Math.abs(ay - j) == 0)) {
                move(ax, ay, i, j);
            }
            //SPLIT DOWN
            if (ax - i == -2 && ay == j) {

                split(ax, ay,0,2);
            }
            //SPLIT UP
            if (ax - i == 2 && ay == j) {
                split(ax, ay,0,-2);
            }
            //SPLIT RIGHT
            if (ay - j == -2 && i == ax) {
                split(ax, ay,2,0);
            }
            //SPLIT LEFT
            if (ay - j == 2 && i == ax) {
                split(ax, ay,-2,0);
            }
            //revisar


        }
    }


    public void move(int x1, int y1, int x2, int y2) {

        tiles[x2][y2].batalla(tiles[x1][y1].getBall());
        tiles[x1][y1].removeBall();
        paint();
        clicks = 0;
       // debug();
    }


    public void split(int i, int j, int x, int y) {

        int splittedBallSize=tiles[i][j].getBall().getSize()/3;

        if (tiles[i][j].getBall() instanceof BallGreen) {
            BallGreen splittedBall = new BallGreen(splittedBallSize);
            if (tiles[i][j].getBall().getSize() >= 10) {
                try {
                    tiles[i][j].getBall().setSize(tiles[i][j].getBall().getSize() - splittedBallSize);
                    tiles[i + y][j+x].batalla(splittedBall);
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
                    tiles[i + y][j+x].batalla(splitttedBall);
                } catch (Exception e) {
                    System.out.println("un poco de tu masa se cayo del tablero");
                }
            }
        }

        paint();
        clicks=0;
        //debug();

    }

    private void debug() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(tiles[i][j].getBall().getSize() + "    ");
            }
            System.out.println("\n");
        }

    }


}



