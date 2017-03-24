package com.kalantos.spitball.GUI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kalantos.spitball.R;
import com.kalantos.spitball.logic.GameActivity;
import com.kalantos.spitball.logic.Timer;

public class ChooseDifficultyActivity extends AppCompatActivity {

    boolean click;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_difficulty);
        click=getIntent().getBooleanExtra("clicker",false);
        System.out.println("BOOOL ="+click);

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
        //finish();
    }
}
