package com.kalantos.spitballv001;

import android.content.Intent;
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
    final int width=10;
    final int height=6;
    private int contador=0;
    private int turno=0;
    private int ax,ay,green, pink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        armarTablero();
        inicialize();
        pintar();

    }
    public void inicialize(){
        tiles[1][3].setBall(20,BallType.BALLGREEN);
        tiles[2][2].setBall(20,BallType.BALLGREEN);
        tiles[3][3].setBall(20,BallType.BALLGREEN);
        tiles[1][5].setBall(20,BallType.BALLPINK);
        tiles[2][6].setBall(20,BallType.BALLPINK);
        tiles[3][5].setBall(20,BallType.BALLPINK);
    }

    public void pintar(){
        green=0; 	pink=0;

        Drawable drawablegreen=getResources().getDrawable(R.drawable.ballgreen);
        Drawable drawablegreen2=getResources().getDrawable(R.drawable.ballgreen);
        Drawable drawablepink=getResources().getDrawable(R.drawable.ballpink);
        Drawable drawablepink2=getResources().getDrawable(R.drawable.ballpink);

        for(int i=0;i<height; i++){

            for(int j=0; j<width; j++){
                if(tiles[i][j].getBall() instanceof BallGreen)
                {
                    tiles[i][j].b.setImageDrawable(drawablegreen);
                   // tiles[i][j].b.setRolloverIcon(BG2);
                    tiles[i][j].b.setScaleType(ImageView.ScaleType.FIT_XY);
                    green++;
                }else if(tiles[i][j].getBall() instanceof BallPink)
                {  tiles[i][j].b.setImageDrawable(drawablepink);
                    tiles[i][j].b.setScaleType(ImageView.ScaleType.FIT_XY);

                    pink++;
                }else{
                    tiles[i][j].b.setImageDrawable(null);
                }



            }
        }
        if (green==0){
            Toast.makeText(this,"Gano Rosa",Toast.LENGTH_LONG).show();
            finishGame();
        }
        if(pink==0){
            Toast.makeText(this,"Gano Verde",Toast.LENGTH_LONG).show();
            finishGame();

        }
    }

    public void finishGame(){

        Intent intent= new Intent(GameActivity.this,finishGameActivity.class);
        intent.putExtra("green",green);
        intent.putExtra("pink",pink);
        startActivity(intent);
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
                tiles[i][j].b.setLayoutParams(new LinearLayout.LayoutParams(widthScreen / 10, heightScreen / 6));
                tiles[i][j].b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        for(int i=0;i<height; i++){

                            for(int j=0; j<width; j++){
                                if(v.getId()==tiles[i][j].b.getId() ){


                                    ClickGestion(i,j);

                                }
                            }
                        }


                    }
                });
                tiles[i][j].b.setId(j + (i * 10));
                row.addView(tiles[i][j].b);
            }

            layout.addView(row);
        }
    }


         public void ClickGestion(int i,int j)       {
            //ya anda bien un click puedo aprovechar y con el isntanceof hacer los turnos

             System.out.println("click numero"+contador);
                            if(( tiles[i][j].getBall() instanceof BallGreen)&&contador==0&&(turno%2==0))
                            { 	ax=i;
                                ay=j;
                                contador++;
                                System.out.println("soy instancia de alguna bola verde");
                                turno++;
                                return;

                            }else{
                            if(( tiles[i][j].getBall() instanceof BallPink)&&contador==0&&(turno%2==1))
                            { 	ax=i;
                                ay=j;
                                contador++;
                                System.out.println("soy instancia de alguna bola rosa");
                                turno++;
                                return;

                            }

                        }


                       if( contador==1){


                            System.out.println("aux y auy :"+ax+" "+ay+" i y j : "+i+" "+ j);
                            if(ax==i && ay==j){
                                contador=0;
                                turno--;
                            }

                            if((Math.abs(ax-i)==1 && Math.abs(ay-j)==1)	||(	Math.abs(ax-i)==0 && Math.abs(ay-j)==1)	||(	Math.abs(ax-i)==1 && Math.abs(ay-j)==0)){

                                System.out.println("entro");
                                move(ax,ay,i,j);
                                contador=0;
                            }
                            //dividirsederecha
                            if(ax-i==-2 && ay==j){

                                //dividirseDerecha(ax,ay);
                                dividirseDerecha(ax,ay);
                                contador=0;
                            }
                            //dividirseizquierda
                            if(ax-i==2 && ay==j){
                                dividirseIzquierda(ax,ay);
                                contador=0;
                            }
                            //dividirsAbajo
                            if(ay-j==-2 && i==ax){
                                dividirseAbajo(ax,ay);
                                contador=0;

                            }
                            //revisar
                            if(ay-j==2 && i==ax){
                                dividirseArriba(ax,ay);
                                contador=0;



                            }
                            //revisar


                        }
                    }


    public void move(int x1,int y1,int x2,int y2){

        tiles[x2][y2].batalla(tiles[x1][y1].getBall());
        tiles[x1][y1].removeBall();
        pintar();
        imprimir();
    }


    public void dividirseDerecha(int x1,int y1){
        if(tiles[x1][y1].getBall() instanceof BallGreen){
            BallGreen div1= new BallGreen(3);
            BallGreen div2= new BallGreen(3);
            BallGreen div3= new BallGreen(3);
            if(tiles[x1][y1].getBall().getTamaño()>3)
            {
                try{
                    tiles[x1][y1].getBall().setTamaño(tiles[x1][y1].getBall().getTamaño()-3);
                    tiles[x1+1][y1].batalla(div1);
                    tiles[x1+2][y1+1].batalla(div2);
                    tiles[x1+2][y1-1].batalla(div3);
                }catch (Exception e){
                    System.out.println("un poco de tu masa se cayo del tablero");
                }}
        }if(tiles[x1][y1].getBall() instanceof BallPink){
            BallPink divi1= new BallPink(3);
            BallPink divi2= new BallPink(3);
            BallPink divi3= new BallPink(3);
            if(tiles[x1][y1].getBall().getTamaño()>3)
            {
                try{
                    tiles[x1][y1].getBall().setTamaño(tiles[x1][y1].getBall().getTamaño()-3);
                    tiles[x1+1][y1].batalla(divi1);
                    tiles[x1+2][y1-1].batalla(divi2);
                    tiles[x1+2][y1+1].batalla(divi3);
                }catch (Exception e){
                    System.out.println("un poco de tu masa se cayo del tablero");
                }
            }
        }

        pintar();
        //imprimir();

    }
    public void dividirseIzquierda(int x1,int y1){
        if(tiles[x1][y1].getBall() instanceof BallGreen){
            BallGreen div1= new BallGreen(3);
            BallGreen div2= new BallGreen(3);
            BallGreen div3= new BallGreen(3);
            if(tiles[x1][y1].getBall().getTamaño()>3)
            {
                try{
                    tiles[x1][y1].getBall().setTamaño(tiles[x1][y1].getBall().getTamaño()-3);
                    tiles[x1-1][y1].batalla(div1);
                    tiles[x1-2][y1+1].batalla(div2);
                    tiles[x1-2][y1-1].batalla(div3);
                }catch (Exception e){
                    System.out.println("un poco de tu masa se cayo del tablero");
                }}
        }if(tiles[x1][y1].getBall() instanceof BallPink){
            BallPink divi1= new BallPink(3);
            BallPink divi2= new BallPink(3);
            BallPink divi3= new BallPink(3);
            if(tiles[x1][y1].getBall().getTamaño()>3)
            {
                try{
                    tiles[x1][y1].getBall().setTamaño(tiles[x1][y1].getBall().getTamaño()-3);
                    tiles[x1-1][y1].batalla(divi1);
                    tiles[x1-2][y1-1].batalla(divi2);
                    tiles[x1-2][y1+1].batalla(divi3);
                }catch (Exception e){
                    System.out.println("un poco de tu masa se cayo del tablero");
                }
            }
        }

        pintar();
        //	imprimir();

    }

    public void dividirseAbajo(int x1,int y1){
        if(tiles[x1][y1].getBall() instanceof BallGreen){
            BallGreen div1= new BallGreen(3);
            BallGreen div2= new BallGreen(3);
            BallGreen div3= new BallGreen(3);
            if(tiles[x1][y1].getBall().getTamaño()>3)
            {
                try{
                    tiles[x1][y1].getBall().setTamaño(tiles[x1][y1].getBall().getTamaño()-3);
                    tiles[x1][y1+1].batalla(div1);
                    tiles[x1+1][y1+2].batalla(div2);
                    tiles[x1-1][y1+2].batalla(div3);
                }catch (Exception e){
                    System.out.println("un poco de tu masa se cayo del tablero");
                }}
        }if(tiles[x1][y1].getBall() instanceof BallPink){
            BallPink divi1= new BallPink(3);
            BallPink divi2= new BallPink(3);
            BallPink divi3= new BallPink(3);
            if(tiles[x1][y1].getBall().getTamaño()>3)
            {
                try{
                    tiles[x1][y1].getBall().setTamaño(tiles[x1][y1].getBall().getTamaño()-3);
                    tiles[x1][y1+1].batalla(divi1);
                    tiles[x1+1][y1+2].batalla(divi2);
                    tiles[x1-1][y1+2].batalla(divi3);
                }catch (Exception e){
                    System.out.println("un poco de tu masa se cayo del tablero");
                }
            }
        }

        pintar();
        //	imprimir();

    }

    public void dividirseArriba(int x1,int y1){
        if(tiles[x1][y1].getBall() instanceof BallGreen){
            BallGreen div1= new BallGreen(3);
            BallGreen div2= new BallGreen(3);
            BallGreen div3= new BallGreen(3);
            if(tiles[x1][y1].getBall().getTamaño()>3)
            {
                try{
                    tiles[x1][y1].getBall().setTamaño(tiles[x1][y1].getBall().getTamaño()-3);
                    tiles[x1][y1-1].batalla(div1);
                    tiles[x1+1][y1-2].batalla(div2);
                    tiles[x1-1][y1-2].batalla(div3);
                }catch (Exception e){
                    System.out.println("un poco de tu masa se cayo del tablero");
                }}
        }if(tiles[x1][y1].getBall() instanceof BallPink){
            BallPink divi1= new BallPink(3);
            BallPink divi2= new BallPink(3);
            BallPink divi3= new BallPink(3);
            if(tiles[x1][y1].getBall().getTamaño()>3)
            {
                try{
                    tiles[x1][y1].getBall().setTamaño(tiles[x1][y1].getBall().getTamaño()-3);
                    tiles[x1][y1-1].batalla(divi1);
                    tiles[x1+1][y1-2].batalla(divi2);
                    tiles[x1-1][y1-2].batalla(divi3);
                }catch (Exception e){
                    System.out.println("un poco de tu masa se cayo del tablero");
                }
            }
        }

        pintar();
        //	imprimir();

    }
    private void imprimir(){
        for(int i=0;i<height; i++){
            for(int j=0; j<width; j++){
                System.out.print(tiles[i][j].getBall().getTamaño()+"    ");
                //cadena.append("\n");
            }
            System.out.println("\n");
        }

    }


            }



