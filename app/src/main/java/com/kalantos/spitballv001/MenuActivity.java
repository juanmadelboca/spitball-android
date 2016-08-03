package com.kalantos.spitballv001;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
    public void intentGame(View view){
        Intent intent=new Intent(MenuActivity.this,GameActivity.class);
        startActivity(intent);
        //better finish activity? or let it background so you can go back to menu?
        finish();
    }
}
