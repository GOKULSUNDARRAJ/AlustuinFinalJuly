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

public class NameActivity extends AppCompatActivity {

    TextInputLayout username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        username=findViewById(R.id.login_phone_Number);
    }



    public void callNextSignupScreen(View view) {


        if (!validateUserName()){
            return;

        }

        ImageView backBtn=findViewById(R.id.login_back_button);
        Button next=findViewById(R.id.namenext);
        TextView titleText=findViewById(R.id.textView7);



        String _FullName= username.getEditText().getText().toString().trim();


        Intent intent = new Intent(getApplicationContext(),EmailActivity.class);

        intent.putExtra("fullName",_FullName);

        Pair[] pairs = new Pair[3];
        pairs[0] = new Pair<View, String>(backBtn, "transition_back_arrow_btn");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(titleText, "transition_title_text");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(NameActivity.this, pairs);
        startActivity(intent, options.toBundle());




    }



    private boolean validateUserName() {
        String val = username.getEditText().getText().toString().trim();
        String checkspace="\\A\\w{1,20}\\Z";

        if (val.isEmpty()) {
            username.setError("Field can not be empty");
            return false;
        } else if (val.length()>20) {
            username.setError("Username is too large");
            return false;
        } else if (!val.matches(checkspace)) {
            username.setError("No white space is allowed");
            return false;
        }else{
            username.setError(null);
            username.setErrorEnabled(false);
            return true;

        }

    }
}