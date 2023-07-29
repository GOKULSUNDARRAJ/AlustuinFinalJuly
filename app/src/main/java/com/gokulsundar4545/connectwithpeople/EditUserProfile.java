
package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class EditUserProfile extends AppCompatActivity {

    EditText Name,Professional;


    ImageView back;
    de.hdodenhof.circleimageview.CircleImageView Profiliamge;
    ImageView Update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        Name=findViewById(R.id.email);
        Professional=findViewById(R.id.password);
        back=findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });




        Profiliamge=findViewById(R.id.profile_image);
        Update=findViewById(R.id.login);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=Name.getText().toString();
                String Password=Professional.getText().toString();

                if (Email.isEmpty() && Password.isEmpty()){
                    Toast.makeText(EditUserProfile.this, "Field can't be Empty", Toast.LENGTH_SHORT).show();
                }else{

                    UpdateUserProfile(Email,Password);
                }
            }
        });

        FirebaseDatabase database;
        FirebaseAuth auth;
        auth= FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();



        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    User user=snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .into(Profiliamge);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void UpdateUserProfile(String email, String password) {
        HashMap user=new HashMap();
        user.put("name",email);
        user.put("profission",password);
        FirebaseAuth Auth=FirebaseAuth.getInstance();
        FirebaseUser CurrentUser=Auth.getCurrentUser();


        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(CurrentUser.getUid()).updateChildren(user).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull @NotNull Task task) {

                if(task.isSuccessful()){
                    Toast.makeText(EditUserProfile.this, "Profile Updated Successful", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditUserProfile.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
