package com.gokulsundar4545.connectwithpeople.Adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.APIService;
import com.gokulsundar4545.connectwithpeople.CommentActivity;
import com.gokulsundar4545.connectwithpeople.Model.Comment;
import com.gokulsundar4545.connectwithpeople.Model.Notification;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.databinding.ActivityCommentBinding;
import com.gokulsundar4545.connectwithpeople.databinding.ActivityVedioCommentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class VedioComment extends AppCompatActivity {

    @NonNull ActivityVedioCommentBinding binding;
    Intent intent;
    String postId;
    String postBy;

    FirebaseDatabase database;
    FirebaseAuth auth;

    FirebaseUser uid;
    ArrayList<Comment> list=new ArrayList<>();



    String hisUID;
    String myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityVedioCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth =FirebaseAuth.getInstance();
        uid=auth.getCurrentUser();

        intent =getIntent();

        myUID= hisUID=intent.getStringExtra("myUID");
        hisUID=intent.getStringExtra("hisUID");
        postId=intent.getStringExtra("postId");
        postBy=intent.getStringExtra("postedBy");

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();





        database.getReference()
                .child("Users")
                .child(postBy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                User user=snapshot.getValue(User.class);
                Picasso.get()
                        .load(user.getProfile_photo())
                        .into(binding.profileImage);
                binding.name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });


        binding.commentpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(VedioComment.this, "Comment", Toast.LENGTH_SHORT).show();


            }
        });




    }

}