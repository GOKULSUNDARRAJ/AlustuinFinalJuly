package com.gokulsundar4545.connectwithpeople;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public class SignUp3nd extends AppCompatActivity {

    ScrollView scrollView;
    TextInputLayout phoneNumber;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up3nd);

        scrollView=findViewById(R.id.signup_3th_Screen_ScrollView);
        phoneNumber=findViewById(R.id.sigiup_phone_number);
        countryCodePicker=findViewById(R.id.country_code_picker);
    }

    public  void CallVerifyOTPScreen(View view){


        if (!validatephonenumber()){
            return;
        }

        String _fullName=getIntent().getStringExtra("fullName");
        String _email=getIntent().getStringExtra("email");
        String _username=getIntent().getStringExtra("username");
        String _password=getIntent().getStringExtra("password");
        String _date=getIntent().getStringExtra("date");
        String _gender=getIntent().getStringExtra("gender");

        String _getUserEnterPhoneNumber= phoneNumber.getEditText().getText().toString().trim();
        String _phoneNo = "+"+countryCodePicker.getFullNumber()+_getUserEnterPhoneNumber;

        Intent intent=new Intent(getApplicationContext(),VerifyOTP.class);

        intent.putExtra("fullName",_fullName);
        intent.putExtra("email",_email);
        intent.putExtra("username",_username);
        intent.putExtra("password",_password);
        intent.putExtra("date",_date);
        intent.putExtra("gender",_gender);
        intent.putExtra("phoneNo",_phoneNo);

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(scrollView, "transition_OTP_Screen");


        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp3nd.this, pairs);
        startActivity(intent, options.toBundle());




    }

    private boolean validatephonenumber() {
        String val = phoneNumber.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            phoneNumber.setError("Field can not be empty");
            return false;
        } else {
            phoneNumber.setError(null);
            return true;

        }

    }
}