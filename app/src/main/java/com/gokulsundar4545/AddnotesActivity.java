package com.gokulsundar4545;

import static com.gokulsundar4545.connectwithpeople.R.id.music;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.BottomSheetFragmentfornotetxt;
import com.gokulsundar4545.connectwithpeople.BottomSheetFragmentmuiscnotes;
import com.gokulsundar4545.connectwithpeople.Fragment.BottomSheetFragmentmuisc;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddnotesActivity extends AppCompatActivity {

    FirebaseAuth Auth;

    FirebaseDatabase database;

    CircleImageView profile;
    ImageView music;

    String storedCoverUrl, title2, subtitle2,songurl;
    TextView titletxt;


    TextView sendButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnotes);

        EditText notesstart2 =findViewById(R.id.notetxt);

        profile=findViewById(R.id.pro);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsnotes", Context.MODE_PRIVATE);
        storedCoverUrl = sharedPreferences.getString("coverUrl", "");
        title2 = sharedPreferences.getString("title", "");
        subtitle2 = sharedPreferences.getString("subtitle", "");
        songurl = sharedPreferences.getString("songurl", "");


        sendButton=findViewById(R.id.login);
        sendButton.setOnClickListener(view -> {
            sendToFirebase(notesstart2.getText().toString(), storedCoverUrl, title2, subtitle2, songurl);
        });







        titletxt=findViewById(R.id.title);


        titletxt.setText(title2);


        Auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        database.getReference().child("Users").child(Auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    User user = snapshot.getValue(User.class);

                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(profile);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        music=findViewById(R.id.music);

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragmentmuiscnotes bottomSheetFragment = new BottomSheetFragmentmuiscnotes(AddnotesActivity.this);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });




    }

    private void sendToFirebase(String notes, String coverUrl, String title, String subtitle, String songUrl) {
        // Get a reference to your Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notesData");

// Create a new object with the data
        Map<String, Object> data = new HashMap<>();
        data.put("coverUrl", coverUrl);
        data.put("notes", notes);
        data.put("songurl", songUrl);
        data.put("subtitle", subtitle);
        data.put("title", title);
        data.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());

// Push the data with a unique key
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data was successfully written
                        Log.d("Firebase", "Data saved successfully.");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        Log.e("Firebase", "Failed to save data", e);
                    }
                });

    }


}