package com.kalantos.spitball;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class finishGameActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_game);
        chooseWinner();


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
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });
    }

    private void chooseWinner(){


        imageView=(ImageView)findViewById(R.id.imageView);
        TextView editText=(TextView) findViewById(R.id.textView);
        Bundle extras = getIntent().getExtras();
        int green=extras.getInt("green");
        int pink=extras.getInt("pink");
        if(green>pink){
            if(editText!=null){
            editText.setText(R.string.winnerGreen);
            }
            Drawable pic=getResources().getDrawable(R.drawable.ballgreen);

                imageView.setImageDrawable(pic);
        }
        else{
            if(editText!=null) {
                editText.setText(R.string.winnerPink);
            }
            Drawable pic=getResources().getDrawable(R.drawable.ballpink);

             imageView.setImageDrawable(pic);
        }
        new Handler().postDelayed(new Runnable(){
            public void run(){
                Intent intent = new Intent(finishGameActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, 2000);
    }


}
