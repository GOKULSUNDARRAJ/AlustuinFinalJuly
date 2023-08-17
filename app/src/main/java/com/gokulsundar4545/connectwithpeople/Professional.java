package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.Model.UserSignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Professional extends AppCompatActivity {

    FirebaseAuth Auth;
    FirebaseDatabase database;

    String item;

    SharedPreferences sharedPreferences;

    TextInputLayout Professional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional);


        Professional=findViewById(R.id.login_phone_Number);



        Auth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    public void SignUp(View view) {


        String _fullName=getIntent().getStringExtra("fullName");
        String _email=getIntent().getStringExtra("Email");
        String _password=getIntent().getStringExtra("password");
        String _Prfessional = Professional.getEditText( ).getText( ).toString( ).trim( );

        if (_email.isEmpty() && _password.isEmpty()){
            Toast.makeText(Professional.this, "Field Can not be Empty", Toast.LENGTH_SHORT).show();
        }else {

            Auth.createUserWithEmailAndPassword(_email,_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){



                                FirebaseUser user1=Auth.getCurrentUser();


                                String uid=user1.getUid();
                                UserSignUp user=new UserSignUp(_email,_password,_fullName,_Prfessional,uid,item,"offline");

                                sharedPreferences = getSharedPreferences("SavedToken",MODE_PRIVATE);
                                String tokenInMain =  sharedPreferences.getString("ntoken","mynull");
                                user.setToken(tokenInMain);


                                String id=task.getResult().getUser().getUid();
                                database.getReference().child("Users").child(id).setValue(user);
                                Intent intent=new Intent(Professional.this,MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(Professional.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(Professional.this, "Register Fails", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }
}