package com.gokulsundar4545.connectwithpeople;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gokulsundar4545.connectwithpeople.Model.User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetFragmentplaynotes2 extends BottomSheetDialogFragment {

    private Context context;
    private String uid;

    private TextView title1, notes1;
    private CircleImageView profile;

    private FirebaseDatabase database;
    private FirebaseAuth auth;

    private MediaPlayer mediaPlayer;

    public BottomSheetFragmentplaynotes2(Context context) {
        this.context = context;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notsplay2, container, false);

        title1 = view.findViewById(R.id.title);
        notes1 = view.findViewById(R.id.notetxt);
        profile = view.findViewById(R.id.pro);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        // Load user profile photo
        loadUserProfilePhoto();

        // Load notes and play audio
        readData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release the MediaPlayer resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void loadUserProfilePhoto() {
        database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        Picasso.get().load(user.getProfile_photo())
                                .placeholder(R.drawable.placeholder)
                                .into(profile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to load profile photo", error.toException());
            }
        });
    }

    private void readData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("notesData");

        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String notes = dataSnapshot.child("notes").getValue(String.class);
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String songUrl = dataSnapshot.child("songurl").getValue(String.class);

                    title1.setText(title);
                    notes1.setText(notes);

                    title1.setSelected(true);

                    // Play audio asynchronously
                    playAudio(songUrl);
                } else {
                    Log.d("Firebase", "No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read data", databaseError.toException());
            }
        });
    }

    private void playAudio(String songUrl) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(mp -> mp.start());
        mediaPlayer.setOnCompletionListener(mp -> {
            // Handle completion
        });

        try {
            mediaPlayer.setDataSource(songUrl);
            mediaPlayer.prepareAsync(); // Prepare asynchronously
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error playing audio", Toast.LENGTH_SHORT).show();
        }
    }
}
