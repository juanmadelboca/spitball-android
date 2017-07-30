package com.kalantos.spitball.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kalantos.spitball.R;
import com.kalantos.spitball.engine.Timer;
import com.kalantos.spitball.fragments.ChooseDifficultyFragment;
import com.kalantos.spitball.fragments.ChooseTypeOfGameFragment;
import com.kalantos.spitball.fragments.MenuFragment;
import com.kalantos.spitball.utils.ConnectionTask;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class MenuActivity extends AppCompatActivity {

    boolean clicker;
    int GameId=1000000083,NumPlayers,turn;
    FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
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
        FragmentManager fragmentManager= getSupportFragmentManager();
        transaction= fragmentManager.beginTransaction();
        MenuFragment startFragment= new MenuFragment();
        transaction.add(R.id.fragmentHolderMenu,startFragment);
        transaction.commit();
    }

    public void intentChooseTypeOfGame(View view){

        ChooseTypeOfGameFragment newFragment = new ChooseTypeOfGameFragment();
        transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolderMenu, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void chooseDifficultyFragment(View view){

        ChooseDifficultyFragment newFragment = new ChooseDifficultyFragment();
        transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolderMenu, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void intentSettings(View view){
        //abre las configuraciones del juego
        Intent intent=new Intent(MenuActivity.this,settingsActivity.class);
        startActivity(intent);
    }

    public void intentHighScores(View view){
        //abre los puntajes
        Intent intent= new Intent(MenuActivity.this,HighScoresActivity.class);
        startActivity(intent);
        //finish();
    }

    public void hardDifficult(View view){
        intentGameVsAI(2);
    }

    public void forDummiesDifficult(View view){
        intentGameVsAI(0);
    }

    public void easyDifficult(View view){
        intentGameVsAI(1);
    }

    private void intentGameVsAI( int difficulty){
        Intent intent=new Intent(MenuActivity.this,GameActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("clicker",clicker);
        startActivity(intent);
        finishAffinity();
        //better finish activity? or let it background so you can go back to menu?
    }

    private void intentGameOnline(){
        //inicia la actividad de juego con el GameID de la partida
        Intent intent=new Intent(MenuActivity.this,GameActivity.class);
        intent.putExtra("AI",false);
        intent.putExtra("GAMEID",GameId);
        intent.putExtra("TURN",turn);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        finishAffinity();
    }

    public void intentGame(View view){
        //inicia una instancia de juego de 2 jugadores en el mismo celular
        Intent intent=new Intent(MenuActivity.this,GameActivity.class);
        intent.putExtra("AI",false);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        finishAffinity();
    }

    public void createOnlineGame(View view) throws ExecutionException, InterruptedException {
        //crea un juego o se conecta a uno si es que hay una partida creada
        //al crearla espera un tiempo y luego arranca una partida online si encontro oponente o una contra IA avanzada
        if(connect("CREATE")) {
            int counter = 0;
            while (NumPlayers == 1 && counter < 20) {
                connect("GET");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter++;
            }
            Log.d("TEST", "" + counter);
            if (NumPlayers == 2) {
                intentGameOnline();

            } else {
                intentGameVsAI(2);
                try {
                    new ConnectionTask().execute("http://spitball.servegame.com/leaveGame.php", "").get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean connect(String method){
        //conecta a una url fija y refresca los datos de GameId y numero de jugadores
        try {
            String json= new ConnectionTask().execute("http://spitball.servegame.com/createGame.php",method).get();
            JSONObject JSONobject= new JSONObject(json);
            if(method.equals("CREATE")){
                turn=JSONobject.getInt("TURN");
            }
            GameId=JSONobject.getInt("GAMEID");
            NumPlayers=JSONobject.getInt("NUMPLAYERS");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"No fue posible conectarse al servidor",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
