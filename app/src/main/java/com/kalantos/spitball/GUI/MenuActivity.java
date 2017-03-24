package com.kalantos.spitball.GUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kalantos.spitball.R;
import com.kalantos.spitball.logic.GameActivity;
import com.kalantos.spitball.logic.Timer;


public class MenuActivity extends AppCompatActivity {
    boolean clicker;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView textView=(TextView)findViewById(R.id.textView3);
        textView.setText(result);
        Log.d("TEST","ALL SET");

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
                                e.printStackTrace();
                            }

                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });


    }
    public void intentChooseTypeOfGame(View view){
        //va al menu de tipo de juego de 2 jugadores
        Intent intent=new Intent(MenuActivity.this,ChooseTypeOfGame.class);
        startActivity(intent);
        finish();
    }

    public void intentChooseDifficulty(View view){
        //va al menu para elegir dificultad de IA
        Intent intent=new Intent(MenuActivity.this,ChooseDifficultyActivity.class);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        finish();
    }
    public void intentSettings(View view){
        //abre las configuraciones del juego
        Intent intent=new Intent(MenuActivity.this,settings.class);
        startActivity(intent);
    }
    public void intentHighScores(View view){
        //abre los puntajes
        Intent intent= new Intent(MenuActivity.this,HighScores.class);
        startActivity(intent);
        finish();
    }

}
