package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentAboutBinding;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentAddBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class About extends Fragment {

    FragmentAboutBinding binding;

    FirebaseAuth Auth;
    FirebaseDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        binding= FragmentAboutBinding.inflate(inflater, container, false);

        database.getReference().child("Users").child(Auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    User user=snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .into(binding.profileImage);
                    binding.username.setText(user.getName());
                    binding.date.setText(TimeAgo.using(user.getTime()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }
}