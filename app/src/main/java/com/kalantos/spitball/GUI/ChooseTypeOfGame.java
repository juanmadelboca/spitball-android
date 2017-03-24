package com.kalantos.spitball.GUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.kalantos.spitball.R;
import com.kalantos.spitball.connectivity.ConnectionTask;
import com.kalantos.spitball.logic.GameActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class ChooseTypeOfGame extends AppCompatActivity {
int GameId,NumPlayers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type_of_game);

    }
    public void intentGameOnline(){
        //inicia la actividad de juego con el GameID de la partida
        Intent intent=new Intent(ChooseTypeOfGame.this,GameActivity.class);
        intent.putExtra("AI",false);
        intent.putExtra("GAMEID",GameId);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        finish();
    }
    public void intentGame(View view){
        //inicia una instancia de juego de 2 jugadores en el mismo celular
        Intent intent=new Intent(ChooseTypeOfGame.this,GameActivity.class);
        intent.putExtra("AI",false);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        finish();
    }

    public void connect(String method){
        //conecta a una url fija y refresca los datos de GameId y numero de jugadores
        try {
            String json= new ConnectionTask().execute("http://kalantos.dhs.org/createGame.php",method).get();
            JSONObject JSONobject= new JSONObject(json);
            GameId=JSONobject.getInt("GAMEID");
            NumPlayers=JSONobject.getInt("NUMPLAYERS");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void createOnlineGame(View view){
        //crea un juego o se conecta a uno si es que hay una partida creada
        //al crearla espera un tiempo y luego arranca una partida online si encontro oponente o una contra IA avanzada
        connect("CREATE");
        int counter=0;
        while(NumPlayers==1&& counter<20) {
            connect("GET");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter++;
        }
        Log.d("TEST",""+counter);
        if(NumPlayers==2){
            intentGameOnline();
        }else{
            intentGameVsAI();
            try {
                new ConnectionTask().execute("http://kalantos.dhs.org/leaveGame.php","").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
    public void intentGameVsAI(){
        //crea una instancia de partida contra IA
        Intent intent=new Intent(ChooseTypeOfGame.this,GameActivity.class);
        intent.putExtra("difficulty", 2);
        intent.putExtra("clicker",true);

        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        //finish();
    }
}
