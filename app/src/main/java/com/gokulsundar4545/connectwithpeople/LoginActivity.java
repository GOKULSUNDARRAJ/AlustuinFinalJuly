package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.gokulsundar4545.connectwithpeople.R.color.voilet1;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth Auth;
    FirebaseDatabase database;

    FirebaseUser CurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Auth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        CurrentUser=Auth.getCurrentUser();




        binding.email.addTextChangedListener(textWatcher);
        binding.password.addTextChangedListener(textWatcher);




        binding.letuserloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email=binding.email.getText().toString();
                String Password=binding.password.getText().toString();

                if (Email.isEmpty() && Password.isEmpty()){
                    Snackbar.make(view, "Field Can not be Empty", Snackbar.LENGTH_LONG)
                            .show();
                }else {

                    Auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull  Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                String uid=task.getResult().getUser().getUid();
                                FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                                firebaseDatabase.getReference().child("Users").child(uid).child("department").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String department=snapshot.getValue(String.class);

                                        MyPref.saveToPrefs(getApplicationContext(),MyPref.EMAIL,Email);


                                        Intent intentIt=new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intentIt);

                                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                            finish();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }

                        }
                    });
                }



            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (CurrentUser!=null){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    private final TextWatcher textWatcher = new TextWatcher( ) {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String Email = binding.email.getText( ).toString( ).trim( );
            String Password = binding.password.getText( ).toString( ).trim( );

            if (!Email.isEmpty( ) && !Password.isEmpty( ) && Password.length()>7) {

                binding.letuserloginbtn.setVisibility(View.VISIBLE);
                binding.letuserloginbtn.setEnabled(true);

            }else {
                binding.letuserloginbtn.setVisibility(View.GONE);
                binding.letuserloginbtn.setEnabled(false);
            }


        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}