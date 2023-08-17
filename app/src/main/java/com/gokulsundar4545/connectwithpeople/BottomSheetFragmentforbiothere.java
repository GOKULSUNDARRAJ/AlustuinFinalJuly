package com.gokulsundar4545.connectwithpeople;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BottomSheetFragmentforbiothere extends BottomSheetDialogFragment {
    String uid;

    public BottomSheetFragmentforbiothere(String uid) {
        this.uid = uid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layoutforbiothere, container, false);



        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);



        // Retrieve user data from Firebase Realtime Database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user data from snapshot
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    String links = dataSnapshot.child("links").getValue(String.class);

                    // Set the retrieved values to the EditText fields
                    TextView bio1=view.findViewById(R.id.bio);
                    bio1.setText("Caption - "+bio);
                    TextView links1=view.findViewById(R.id.links);
                    links1.setText("Links - "+links);
                    TextView gender1=view.findViewById(R.id.gender);
                    gender1.setText("Gender - "+gender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }


}
