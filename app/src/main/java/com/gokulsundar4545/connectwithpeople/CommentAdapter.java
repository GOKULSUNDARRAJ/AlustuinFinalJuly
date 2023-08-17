package com.gokulsundar4545.connectwithpeople;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.google.common.collect.BiMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentrealsitem, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);


        String text= TimeAgo.using(comment.getTimestamp());


        holder.textViewTimestamp.setText(text);


        holder.comments.setText(comment.getText());



        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();


        database.getReference().child("Users").child(comment.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    User user = snapshot.getValue(User.class);

                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(holder.profile);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ThereProfileActivity.class);
                intent.putExtra("uid", comment.getUserId());
                v.getContext().startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewComment;
        TextView textViewTimestamp;
        TextView comments;

        ImageView profile;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            profile= itemView.findViewById(R.id.profile_image);
            textViewTimestamp = itemView.findViewById(R.id.time);
            comments = itemView.findViewById(R.id.comment);
        }
    }
}
