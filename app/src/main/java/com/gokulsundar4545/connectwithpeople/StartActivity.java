package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    TextView textView4;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        textView4=findViewById(R.id.button3);
        TextPaint textPaint=textView4.getPaint();
        float width=textPaint.measureText(getResources().getString(R.string.app_name));
        Shader shader=new LinearGradient(0,0,width,textView4.getTextSize(),
                new int[]{

                        Color.parseColor("#34495E"),

                        Color.parseColor("#34495E")
                },null,Shader.TileMode.CLAMP);
        textView4.getPaint().setShader(shader);

        button=findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent( StartActivity.this,LoginActivity.class );
                Bundle b= ActivityOptions.makeSceneTransitionAnimation(StartActivity.this).toBundle();
                startActivity(intent,b);
            }
        });


    }
}