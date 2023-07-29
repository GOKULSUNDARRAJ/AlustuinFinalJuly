package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.gokulsundar4545.Client;
import com.gokulsundar4545.Data;
import com.gokulsundar4545.Sender;
import com.gokulsundar4545.connectwithpeople.Adapter.CommentAdapter;
import com.gokulsundar4545.connectwithpeople.Model.Comment;
import com.gokulsundar4545.connectwithpeople.Model.Notification;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.User;

import com.gokulsundar4545.connectwithpeople.databinding.ActivityCommentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;



public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding binding;
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
        binding=ActivityCommentBinding.inflate(getLayoutInflater());
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
                .child("posts")
                .child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                Post post=snapshot.getValue(Post.class);
                Picasso.get()
                        .load(post.getPostImg())
                        .into(binding.postImg);
                binding.description.setText(post.getPostDescription());
                binding.like.setText(post.getPostLike()+"");
                binding.comment.setText(post.getCommentCount()+"");
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

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


                APIService apiService;

                Comment comment=new Comment();
                comment.setCommentbody(binding.commentEd.getText().toString());
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentedBy(FirebaseAuth.getInstance().getUid());



                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("comments")
                        .push()
                        .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference()
                                .child("posts")
                                .child(postId)
                                .child("commentCount").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                                int commentCount=0;
                                if (snapshot.exists()){
                                    commentCount=snapshot.getValue(Integer.class);

                                }

                                database.getReference()
                                        .child("posts")
                                        .child(postId)
                                        .child("commentCount")
                                        .setValue(commentCount+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        binding.commentEd.setText("");
                                        Toast.makeText(CommentActivity.this, "Commented Successfully", Toast.LENGTH_SHORT).show();

                                        Notification notification=new Notification();
                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                        notification.setNotificationAt(new Date().getTime());
                                        notification.setPostId(postId);
                                        notification.setPostBy(postBy);
                                        notification.setType("comment");

                                        FirebaseDatabase.getInstance().getReference()
                                                .child("notification")
                                                .child(postBy)
                                                .push()
                                                .setValue(notification);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull  DatabaseError error) {

                            }
                        });

                    }
                });
            }
        });

        CommentAdapter adapter=new CommentAdapter(this,list);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.commentRv.setLayoutManager(layoutManager);
        binding.commentRv.setAdapter(adapter);


        database.getReference()
                .child("posts")
                .child(postId)
                .child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Comment comment=dataSnapshot.getValue(Comment.class);
                    list.add(comment);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }

}