package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class EmailActivity extends AppCompatActivity {

    TextInputLayout Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        Email = findViewById(R.id.login_phone_Number);

    }


    public void callNextSignupScreen(View view) {

        String _fullName=getIntent().getStringExtra("fullName");

        ImageView backBtn = findViewById(R.id.login_back_button);
        Button next = findViewById(R.id.letuserloginbtn);
        TextView titleText = findViewById(R.id.title);
        TextView textView = findViewById(R.id.text);


        String Email1 = Email.getEditText( ).getText( ).toString( ).trim( );


        Intent intent = new Intent(getApplicationContext( ), Password.class);

        intent.putExtra("Email", Email1);
        intent.putExtra("fullName",_fullName);

        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(backBtn, "transition_back_arrow_btn");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(titleText, "transition_title_text");
        pairs[3] = new Pair<View, String>(textView, "text_tt");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(EmailActivity.this, pairs);
        startActivity(intent, options.toBundle( ));


    }
}