package com.gokulsundar4545.connectwithpeople;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class login extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    TextInputLayout phoneNumber, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_retailer_login);

        countryCodePicker = findViewById(R.id.login_country_code_picker);
        password = findViewById(R.id.login_password);
        phoneNumber = findViewById(R.id.login_phone_Number);
    }

    public void letTheUserLoggedIn(View view) {

        if (!validatephonenumber() | !validatePassword()) {
            return;
        }

        String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();
        if (_phoneNumber.charAt(0) == '0') {
            _phoneNumber = _phoneNumber.substring(1);
        }

        String _completePhoneNumber = "+" + countryCodePicker.getFullNumber() + _phoneNumber;

        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("profission").equalTo(_completePhoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phoneNumber.setError(null);
                    phoneNumber.setErrorEnabled(false);

                    String sysytempassword = snapshot.child(_completePhoneNumber).child("password").getValue(String.class);
                    if (sysytempassword.equals(_password)) {

                        password.setError(null);
                        password.setErrorEnabled(false);
                        String _fullname=snapshot.child(_completePhoneNumber).child("fullName").getValue(String.class);
                        String _email=snapshot.child(_completePhoneNumber).child("email").getValue(String.class);
                        String _phoneNo=snapshot.child(_completePhoneNumber).child("phoneNo").getValue(String.class);
                        String _dateofBirth=snapshot.child(_completePhoneNumber).child("age").getValue(String.class);

                        Toast.makeText(login.this, _fullname+"\n"+_email+"\n"+_phoneNo+"\n"+_dateofBirth, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(login.this,MainActivity.class));

                    } else {
                        password.setError("Password does not match");
                    }
                } else {
                    phoneNumber.setError("No such user exits");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }


    private boolean validatePassword() {
        String val = password.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        } else if (val.length() < 7) {
            password.setError("Password should contain 6 characters1");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;

        }

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

    public  void  callForgetPassword(View view){
        startActivity(new Intent(getApplicationContext(),ForgetPassword.class));
    }
}