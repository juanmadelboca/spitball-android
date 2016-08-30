package com.kalantos.spitball;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChooseDifficultyFragment extends Fragment {

    boolean click;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.activity_choose_difficulty, container, false);


    }
/*
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
        Intent intent=new Intent(MenuActivity.this,GameActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("clicker",click);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        //finish();
    }*/
}
