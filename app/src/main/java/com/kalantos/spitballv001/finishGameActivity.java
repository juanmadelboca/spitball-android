package com.kalantos.spitballv001;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class finishGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_game);
        chooseWinner();
    }
    private void chooseWinner(){

        TextView textView=(TextView)findViewById(R.id.winner);
        Bundle extras = getIntent().getExtras();
        int green=extras.getInt("green");
        int pink=extras.getInt("pink");
        if(green>pink){
            textView.setText("GANO VERDE");
        }
        else{
            textView.setText("GANO ROSA");
        }


    }
}
