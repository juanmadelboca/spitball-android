package com.kalantos.spitballv001;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChooseDifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_difficulty);
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

    public void intentGameVsAI( int difficulty){
        Intent intent=new Intent(ChooseDifficultyActivity.this,GameActivity.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        finish();
    }
}
