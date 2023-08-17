package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;


public class SplashScreen extends AppCompatActivity {


    private  static  int SPLASH_TIME_OUT=1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler;

        setStatusBarColor(getResources().getColor(android.R.color.white));
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreen.this,StartActivityFirst.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this).toBundle();
                startActivity(intent,b);
                finish();
            }
        };

        handler=new Handler();
        handler.postDelayed(runnable,SPLASH_TIME_OUT);

    }
    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(color);
        }
    }



}