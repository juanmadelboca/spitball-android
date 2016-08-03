package com.kalantos.spitballv001;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class finishGameActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_finish_game);
        chooseWinner();
    }
    private void chooseWinner(){


        imageView=(ImageView)findViewById(R.id.imageView);
        EditText editText=(EditText) findViewById(R.id.editText);
        Bundle extras = getIntent().getExtras();
        int green=extras.getInt("green");
        int pink=extras.getInt("pink");
        if(green>pink){
            editText.setText("GANA VERDE");
             Drawable pic=getResources().getDrawable(R.drawable.ballgreen);

                imageView.setImageDrawable(pic);
        }
        else{
            editText.setText("GANA ROSA");
            Drawable pic=getResources().getDrawable(R.drawable.ballpink);

             imageView.setImageDrawable(pic);
        }
        new Handler().postDelayed(new Runnable(){
            public void run(){
                // Cuando pasen los 3 segundos, pasamos a la actividad principal de la aplicación
                Intent intent = new Intent(finishGameActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            };
        }, 2000);
    }


}
