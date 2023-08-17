package com.gokulsundar4545.connectwithpeople;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class SignUp extends AppCompatActivity {

    ImageView backBtn;
    Button next, login;
    TextView titleText;

    TextInputLayout fullname, username, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_retailer_sign_up);

        backBtn = findViewById(R.id.signup_back_button);
        next = findViewById(R.id.signup_next_button);
        login = findViewById(R.id.signup_login_button);
        titleText = findViewById(R.id.signup_title_text);

        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.password);

    }

    public void callNextSignupScreen(View view) {

        if (!validateFullName() | !validateUserName() | !validateEmail() | !validatePassword()){
            return;

        }


        String _FullName= fullname.getEditText().getText().toString().trim();
        String _userName= username.getEditText().getText().toString().trim();
        String _email= email.getEditText().getText().toString().trim();
        String _password= password.getEditText().getText().toString().trim();

        Intent intent = new Intent(getApplicationContext(),SignUp2nd.class);

        intent.putExtra("fullName",_FullName);
        intent.putExtra("email",_email);
        intent.putExtra("username",_userName);
        intent.putExtra("password",_password);

        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(backBtn, "transition_back_arrow_btn");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp.this, pairs);
        startActivity(intent, options.toBundle());




    }

    private boolean validateFullName() {
        String val = fullname.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            fullname.setError("Field can not be empty");
            return false;
        } else {
            fullname.setError(null);
            fullname.setErrorEnabled(false);
            return true;

        }

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


    private boolean validateEmail() {
        String val = email.getEditText().getText().toString().trim();
        String checkEmail="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        }  else if (!val.matches(checkEmail)) {
            email.setError("Invalid Email");
            return false;
        }else{
            email.setError(null);
            email.setErrorEnabled(false);
            return true;

        }

    }


    private boolean validatePassword() {
        String val = password.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        }  else if (val.length()<7) {
            password.setError("Password should contain 6 characters1");
            return false;
        }else{
            password.setError(null);
            password.setErrorEnabled(false);
            return true;

        }

    }

    public void gotologin(View view) {
        startActivity(new Intent(SignUp.this, com.gokulsundar4545.connectwithpeople.login.class));
    }
}