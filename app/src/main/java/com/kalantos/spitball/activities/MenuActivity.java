package com.kalantos.spitball.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.kalantos.spitball.R;
import com.kalantos.spitball.engine.Timer;
import com.kalantos.spitball.fragments.ChooseDifficultyFragment;
import com.kalantos.spitball.fragments.ChooseTypeOfGameFragment;
import com.kalantos.spitball.fragments.MenuFragment;
import com.kalantos.spitball.utils.HTTPSocket;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;


public class MenuActivity extends AppCompatActivity {

    boolean clicker;
    int GameId, NumPlayers, turn;
    FragmentTransaction transaction;
    ImageView imageSettings;
    boolean creatingRoom = false;
    Thread createOnlineGameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
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
                        Log.e("AUTO-HIDE BAR", e.getMessage());
                    }

                    decorView.setSystemUiVisibility(flags);
                }
            }
        });

        imageSettings = (ImageView)findViewById(R.id.imageSettings);
        FragmentManager fragmentManager= getSupportFragmentManager();
        transaction= fragmentManager.beginTransaction();
        MenuFragment startFragment= new MenuFragment();
        transaction.add(R.id.fragmentHolderMenu,startFragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(creatingRoom) {
            try {
                createOnlineGameThread.interrupt();
                leaveRoom();
            }catch (Exception e){
                e.printStackTrace();
                Log.e("CREATE ONLINE GAME","failed to stop");
            }
        }
        imageSettings.setVisibility(View.VISIBLE);
        super.onBackPressed();
    }

    public void intentChooseTypeOfGame(View view){
    /*
    * Launches a new menu to choose between online or local.
    * */
        ChooseTypeOfGameFragment newFragment = new ChooseTypeOfGameFragment();
        transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolderMenu, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        imageSettings.setVisibility(View.GONE);
    }

    public void chooseDifficultyFragment(View view){
    /*
    * Launches new menu to choose IA difficult.
    * */
        ChooseDifficultyFragment newFragment = new ChooseDifficultyFragment();
        transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolderMenu, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        imageSettings.setVisibility(View.GONE);
    }

    public void intentSettings(View view){
    /*
    * Launches settings menu.
    * */
        Intent intent=new Intent(MenuActivity.this,settingsActivity.class);
        startActivity(intent);
    }

    public void intentHighScores(View view){
    /*
    * Launches menu activity.
    * */
        Toast.makeText(this,"En desarrollo",Toast.LENGTH_SHORT).show();
        /*Intent intent= new Intent(MenuActivity.this,HighScoresActivity.class);
        startActivity(intent);
        finish();*/
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
    /*
    * Start a Game vs IA with using parameter received as difficulty.
    * */
        Intent intent=new Intent(MenuActivity.this,GameActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("clicker",clicker);
        startActivity(intent);
        finishAffinity();
    }

    private void intentGameOnline(){
    /*
    * Start Online Game with GameId global variable.
    * */
        Intent intent=new Intent(MenuActivity.this,GameActivity.class);
        intent.putExtra("AI",false);
        intent.putExtra("GAMEID",GameId);
        intent.putExtra("TURN",turn);
        startActivity(intent);
        finishAffinity();
    }

    public void intentGame(View view){
    /*
    * Start a local multiplayer game.
    * */
        Intent intent=new Intent(MenuActivity.this,GameActivity.class);
        intent.putExtra("AI",false);
        startActivity(intent);
        finishAffinity();
    }

    public void createOnlineGame(View view){
    /*
    * Create a game in database or connect to a game created which need another player, if timeout
    * create a vs IA game in the hardest difficulty.
    * */
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                creatingRoom = true;
                try {
                    if (connect("CREATE")) {
                        int timeCounter = 0;
                        while (NumPlayers == 1 && timeCounter < 40) {

                                connect("GET");
                                Thread.sleep(500);

                            timeCounter++;
                            Log.d("CREATE ONLINE GAME", "timecounter:" + timeCounter + " numplays:" + NumPlayers);
                        }
                        if (NumPlayers == 2) {
                            intentGameOnline();

                        } else {
                            intentGameVsAI(2);
                            leaveRoom();
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e("ONLINE GAME", "CreateOnlineGame was interrupted");
                    return;
                }
            }
        };
        createOnlineGameThread = new Thread(runnable);
        createOnlineGameThread.start();
        Log.d("CREATE ONLINE GAME", "thread created");
    }

    private void leaveRoom(){
        Log.d("CREATE ONLINE GAME","leaving room");
        try {
            //TODO: Think a way that leave the game even if the player leave the search room with back button
            String jsonData = createJson("GAMEID",Integer.toString(GameId));
            new HTTPSocket().execute("http://spitball.000webhostapp.com/leaveGame.php","POST",jsonData).get();
        } catch (Exception e) {
            Log.e("ONLINE GAME", "Error leaving Room");
        }
    }

    private boolean connect(String method){
    /*
    * connect to database and refresh room stats, as number of players.
    * */
        try {
            String jsonData = createJson("METHOD",method);
            String json= new HTTPSocket().execute("http://spitball.000webhostapp.com/createGame.php","POST",jsonData).get();
            JSONObject JSONobject= new JSONObject(json);
            if(method.equals("CREATE")){
                turn=JSONobject.getInt("TURN");
            }
            GameId=JSONobject.getInt("GAMEID");
            NumPlayers=JSONobject.getInt("NUMPLAYERS");
        } catch (Exception e) {
            Log.e("ONLINE GAME", "Connection error");
            Toast.makeText(this,"No fue posible conectarse al servidor",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String createJson(String... strings){
    /*
    * Create a Json with all data received and returns the json in string format.
    * */
        JSONObject json= new JSONObject();
        for(int i=0; i<strings.length; i=i+2){
            try{
                json.put(strings[i],strings[i+1]);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return json.toString();
    }
}