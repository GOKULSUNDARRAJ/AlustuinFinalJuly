
package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.Map;

public class EditUserProfile extends AppCompatActivity {

    com.google.android.material.textfield.TextInputEditText Name,Professional,Bio,gender1;


    ImageView back;
    de.hdodenhof.circleimageview.CircleImageView Profiliamge;
    TextView Update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        setStatusBarColor(getResources().getColor(android.R.color.white));
        Name=findViewById(R.id.email1);
        Professional=findViewById(R.id.email2);
        back=findViewById(R.id.back);
        Bio=findViewById(R.id.email3);
        gender1=findViewById(R.id.email4);

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
            public void onClick(View view) {
                // Get the current user’s ID from Firebase Authentication
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // Get the values from the EditText fields
                String name1 = Name.getText().toString();
                String bio1 = Bio.getText().toString();
                String gender = gender1.getText().toString();
                String links1 = Professional.getText().toString();

                // Create a Map to hold the updated values
                Map<String, Object> userUpdates = new HashMap<>();
                userUpdates.put("name", name1);
                userUpdates.put("bio", bio1);
                userUpdates.put("gender", gender);
                userUpdates.put("links", links1);

                // Get a reference to the user's node in the Firebase Realtime Database
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                // Update the user's data in Firebase Realtime Database
                userRef.updateChildren(userUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Data updated successfully
                            Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update data
                            Toast.makeText(getApplicationContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());



        // Retrieve user data from Firebase Realtime Database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user data from snapshot
                    String name1 = dataSnapshot.child("name").getValue(String.class);
                    String bio1 = dataSnapshot.child("bio").getValue(String.class);
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    String links1 = dataSnapshot.child("links").getValue(String.class);


                    Bio.setText(bio1);

                    Professional.setText(links1);

                    gender1.setText(gender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(EditUserProfile.this, "Failed to load data", Toast.LENGTH_SHORT).show();
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

                    Name.setText(user.getName());




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(color);
        }
    }
}
