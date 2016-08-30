package com.kalantos.spitball;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MenuActivity extends AppCompatActivity {
    boolean clicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);


    }
    public void intentGame(View view){
        Intent intent=new Intent(MenuActivity.this,GameActivity.class);
        intent.putExtra("AI",false);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        finish();
    }

    public void intentChooseDifficulty(View view){
        Intent intent=new Intent(MenuActivity.this,ChooseDifficultyActivity.class);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        //finish();
    }
    public void intentSettings(View view){
        Intent intent=new Intent(MenuActivity.this,settings.class);
        startActivity(intent);
    }
    public void intentHowToPlay(View view){
        Intent intent= new Intent(MenuActivity.this,howToPlayActivity.class);
        startActivity(intent);
    }

}
