package com.kalantos.spitball.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kalantos.spitball.R;
import com.kalantos.spitball.utils.ConnectionTask;
import com.kalantos.spitball.views.adapters.Score;
import com.kalantos.spitball.engine.Timer;
import com.kalantos.spitball.views.adapters.ScoreAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HighScoresActivity extends AppCompatActivity {
    TextView textView;
    ArrayList<Score>scores= new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);
        textView=(TextView)findViewById(R.id.textView4);
        listView=(ListView)findViewById(R.id.listView);

        //obtiene en un JSON la lista de scores de la tabla albergada en el servidor
        try{
            String st=new ConnectionTask().execute("http://192.168.1.32/highScores.php","NO METHOD").get();

           parseJSON(new JSONArray(st));
            ScoreAdapter adapter= new ScoreAdapter(scores,this);
            listView.setAdapter(adapter);


        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"No fue posible conectarse al servidor",Toast.LENGTH_SHORT).show();
        }


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
    public void intentMenu(View view){
        //vuelve al menu inicial
        Intent intent= new Intent(HighScoresActivity.this,MenuActivity.class);
        startActivity(intent);
        finish();
    }
    public void parseJSON(JSONArray jsonArray){
    //parsea un JSON para obtener un objeto de tipo score
        try{
            JSONObject JSONobject;
          for(int i=0;i<jsonArray.length();i++){
              Score tempScore=new Score();
                JSONobject = jsonArray.getJSONObject(i);
                tempScore.player=JSONobject.getString("PLAYER");
                tempScore.score=JSONobject.getInt("SCORE");
                scores.add(tempScore);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
