package com.gokulsundar4545.connectwithpeople.Adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.databinding.ActivityZoomBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ZoomActivity extends AppCompatActivity {

    Intent intent;
    String postId;
    String postBy;

    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser uid;
    ActivityZoomBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityZoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent =getIntent();



        intent =getIntent();

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();


        postId=intent.getStringExtra("postId");
        postBy=intent.getStringExtra("postedBy");



        database.getReference()
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Post post = snapshot.getValue(Post.class);
                Picasso.get()
                        .load(post.getPostImg())
                        .into(binding.postImg);
                binding.description.setText(post.getPostDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}