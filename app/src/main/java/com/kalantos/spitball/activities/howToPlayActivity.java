package com.kalantos.spitball.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kalantos.spitball.R;
import com.kalantos.spitball.engine.Timer;

public class howToPlayActivity extends AppCompatActivity {

    int state;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

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
/*
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        HowToPlay1 startFragment= new HowToPlay1();
        transaction.add(R.id.fragmentHolder,startFragment);
        transaction.commit();*/
    }
    public void nextFragment(View view){
        state++;
        onSelectFragment();
    }
    public void previousFragment(View view){
        state--;
        onSelectFragment();
    }
    private void onSelectFragment(){/*
        Fragment newFragment;
        switch (state){
            case 0: newFragment=new HowToPlay1();
                break;
            case 1: newFragment=new HowToPlay2();
                break;
            case 2: newFragment=new HowToPlay3();
                break;
            case 3: newFragment=new HowToPlay4();
                break;
            default: newFragment=new HowToPlay1();
        }
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();*/
    }
}
