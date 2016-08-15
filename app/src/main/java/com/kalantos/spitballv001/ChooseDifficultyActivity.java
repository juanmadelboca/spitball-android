package com.kalantos.spitballv001;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ChooseDifficultyActivity extends AppCompatActivity {

    boolean click;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_difficulty);
        click=getIntent().getBooleanExtra("clicker",false);
        System.out.println("BOOOL ="+click);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
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
        intent.putExtra("clicker",click);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        finish();
    }
}
