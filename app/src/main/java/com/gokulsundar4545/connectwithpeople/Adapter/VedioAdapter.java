package com.gokulsundar4545.connectwithpeople.Adapter;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gokulsundar4545.connectwithpeople.CommentActivity;
import com.gokulsundar4545.connectwithpeople.Fragment.AddPostFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.AddVedioFragment;
import com.gokulsundar4545.connectwithpeople.LoginActivity;
import com.gokulsundar4545.connectwithpeople.Model.Notification;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.Model.VedioMode;
import com.gokulsundar4545.connectwithpeople.PayMentActivity;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.SplashScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VedioAdapter extends FirebaseRecyclerAdapter<VedioMode, VedioAdapter.VedioViewHolder> {


    public VedioAdapter(@NonNull FirebaseRecyclerOptions<VedioMode> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull VedioViewHolder holder, int position, @NonNull VedioMode model) {
        holder.setVedioData(model);
        holder.vedioname.setText(model.getVedioDescription());




        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getVedioBy())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_25,0,0,0);
                        }else {
                            holder.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getVedioId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("posts")
                                                    .child(model.getVedioId())
                                                    .child("postLike")
                                                    .setValue(model.getVediopostLike()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    holder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_25,0,0,0);

                                                    Notification notification=new Notification();
                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                    notification.setNotificationAt(new Date().getTime());
                                                    notification.setPostId(model.getVedioId());
                                                    notification.setPostBy(model.getVedioBy());
                                                    notification.setType("like");

                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("notification")
                                                            .child(model.getVedioBy())
                                                            .push()
                                                            .setValue(notification);
                                                }
                                            });

                                        }
                                    });

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });


        holder.comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), VedioComment.class);
                intent.putExtra("postId", model.getVedioId());
                intent.putExtra("postedBy", model.getVedioBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle b= ActivityOptions.makeSceneTransitionAnimation((Activity) view.getContext()).toBundle();
                view.getContext().startActivity(intent,b);
            }
        });

        holder.Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Share", Toast.LENGTH_SHORT).show();
            }
        });

        holder.Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Camera", Toast.LENGTH_SHORT).show();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fram_layout, new AddPostFragment()).addToBackStack(null).commit();

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").
                child(model.getVedioBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                Picasso.get()
                        .load(user.getProfile_photo())
                        .placeholder(R.drawable.profile)
                        .into(holder.profile);
                holder.username.setText(user.getName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @NonNull
    @Override
    public VedioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vedioview, parent, false);
        return new VedioViewHolder(view);
    }

    public class VedioViewHolder extends RecyclerView.ViewHolder {

        VideoView videoView;
        TextView vedioname, username;
        ProgressBar progressBar;
        ImageView profile, Camera, pause, play;
        TextView like, comment,Share;

        public VedioViewHolder(@NonNull View itemView) {
            super(itemView);

            Share=itemView.findViewById(R.id.share);
            videoView = itemView.findViewById(R.id.videoView);
            vedioname = itemView.findViewById(R.id.vedioname);
            progressBar = itemView.findViewById(R.id.progressbar);
            profile = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            Camera = itemView.findViewById(R.id.camera);
            pause = itemView.findViewById(R.id.pause);
            play = itemView.findViewById(R.id.play);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);

        }

        public void setVedioData(VedioMode vedioMode) {
            vedioname.setText(vedioMode.getVedioname());
            videoView.setVideoPath(vedioMode.getVedioUrl());


            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    progressBar.setVisibility(View.GONE);
                    mediaPlayer.start();
                }
            });


            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    progressBar.setVisibility(View.GONE);
                    mediaPlayer.start();

                }
            });


            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (videoView.isPlaying()) {
                        pause.setVisibility(View.VISIBLE);
                        play.setVisibility(View.GONE);
                        videoView.pause();
                        int SPLASH_TIME_OUT = 5000;
                        Handler handler;


                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                pause.setVisibility(View.GONE);
                            }
                        };

                        handler = new Handler();
                        handler.postDelayed(runnable, SPLASH_TIME_OUT);


                    } else {
                        play.setVisibility(View.VISIBLE);
                        pause.setVisibility(View.GONE);
                        videoView.start();

                        int SPLASH_TIME_OUT = 5000;
                        Handler handler;


                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                play.setVisibility(View.GONE);
                            }
                        };

                        handler = new Handler();
                        handler.postDelayed(runnable, SPLASH_TIME_OUT);
                    }


                }

            });

        }
    }




}