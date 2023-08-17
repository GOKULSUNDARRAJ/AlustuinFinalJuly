package com.gokulsundar4545.connectwithpeople;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SignUp2nd extends AppCompatActivity {
    ImageView backBtn;
    Button next, login;
    TextView titleText;

    RadioGroup radioGroup;
    RadioButton selectGender;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up2nd);

        backBtn = findViewById(R.id.signup_back_button);
        next = findViewById(R.id.signup_next_button);
        login = findViewById(R.id.signup_login_button);
        titleText = findViewById(R.id.signup_title_text);

        radioGroup = findViewById(R.id.radio_group);
        datePicker = findViewById(R.id.age_picker);

    }

    public void callNextSignupScreen(View view) {

        if (!validateGender() | !validateAge()){
            return;
        }

        String _fullName=getIntent().getStringExtra("fullName");
        String _email=getIntent().getStringExtra("email");
        String _username=getIntent().getStringExtra("username");
        String _password=getIntent().getStringExtra("password");

        selectGender=findViewById(radioGroup.getCheckedRadioButtonId());
        String _gender =selectGender.getText().toString();

        int day=datePicker.getDayOfMonth();
        int month=datePicker.getMonth();
        int year=datePicker.getYear();

        String _date=day+"/"+month+"/"+year;





        Intent intent = new Intent(getApplicationContext(),SignUp3nd.class);
        intent.putExtra("fullName",_fullName);
        intent.putExtra("email",_email);
        intent.putExtra("username",_username);
        intent.putExtra("password",_password);
        intent.putExtra("date",_date);
        intent.putExtra("gender",_gender);






        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View, String>(backBtn, "transition_back_arrow_btn");
        pairs[1] = new Pair<View, String>(next, "transition_next_btn");
        pairs[2] = new Pair<View, String>(login, "transition_login_btn");
        pairs[3] = new Pair<View, String>(titleText, "transition_title_text");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp2nd.this, pairs);
        startActivity(intent, options.toBundle());


    }

    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValide = currentYear - userAge;
        if (isAgeValide < 14) {
            Toast.makeText(this, "You are not eligible to create account ", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}