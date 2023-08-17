package com.gokulsundar4545.connectwithpeople.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.CommentActivity;
import com.gokulsundar4545.connectwithpeople.Fragment.BottomSheetFragmentforlikes;
import com.gokulsundar4545.connectwithpeople.Fragment.CustomBottomSheetDialog;
import com.gokulsundar4545.connectwithpeople.Model.Notification;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.ThereProfileActivity;
import com.gokulsundar4545.connectwithpeople.TransitionHandler;
import com.gokulsundar4545.connectwithpeople.databinding.DashboardRvBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CombinedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_AUDIO = 2;

    private List<Post> posts;
    private Context context;
    private MediaPlayer mediaPlayer;
    private int playingPosition = -1; // Track which item is currently playing

    public CombinedAdapter(List<Post> posts, Context context, MediaPlayer mediaPlayer) {
        this.posts = posts;
        this.context = context;
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public int getItemViewType(int position) {
        Post post = posts.get(position);
        switch (post.getPostType()) {
            case "image":
                return TYPE_IMAGE;
            case "video":
                return TYPE_VIDEO;
            case "audio":
                return TYPE_AUDIO;
            default:
                throw new IllegalArgumentException("Invalid post type");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_IMAGE:
                View imageView = inflater.inflate(R.layout.dashboard_rv, parent, false);
                return new ImagePostViewHolder(imageView);
            case TYPE_VIDEO:
                View videoView = inflater.inflate(R.layout.item_video_post, parent, false);
                return new VideoPostViewHolder(videoView);
            case TYPE_AUDIO:
                View audioView = inflater.inflate(R.layout.item_audio_post, parent, false);
                return new AudioPostViewHolder(audioView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);
        switch (getItemViewType(position)) {
            case TYPE_IMAGE:
                ((ImagePostViewHolder) holder).bind(post, position);
                break;
            case TYPE_VIDEO:
                ((VideoPostViewHolder) holder).bind(post, position);
                break;
            case TYPE_AUDIO:
                ((AudioPostViewHolder) holder).bind(post, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void stopAllPlayback() {
        // Stop all media playback
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public class ImagePostViewHolder extends RecyclerView.ViewHolder {
        private ImageView postImageView;
        private TextView postDescriptionTextView;
        private DashboardRvBinding binding;
        private FirebaseAuth auth;
        private FirebaseStorage storage;
        private FirebaseDatabase database;

        public ImagePostViewHolder(View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postimage);
            postDescriptionTextView = itemView.findViewById(R.id.description);
            binding = DashboardRvBinding.bind(itemView);

            auth = FirebaseAuth.getInstance();
            storage = FirebaseStorage.getInstance();
            database = FirebaseDatabase.getInstance();
        }

        public void bind(Post post, int position) {


            postDescriptionTextView.setText(post.getPostDescription());
            Glide.with(itemView.getContext()).load(post.getPostImg()).into(postImageView);

            database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfile_photo()).into(binding.pro);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            Picasso.get().load(post.getPostImg()).into(binding.postimage);
            binding.textView6.setText("View All " + post.getCommentCount() + " Comments");
            binding.description.setText("# " + post.getPostDescription() + " ");
            binding.likedby.setText("likes " + post.getPostLike());

            boolean isPlaying = position == playingPosition && mediaPlayer != null && mediaPlayer.isPlaying();
            binding.pause.setVisibility(isPlaying ? View.VISIBLE : View.GONE);
            binding.play.setVisibility(isPlaying ? View.GONE : View.VISIBLE);

            binding.play.setOnClickListener(v -> {
                stopAllPlayback(); // Stop any ongoing playback
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(post.getSongurl());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    playingPosition = position;
                    notifyDataSetChanged(); // Update UI
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            binding.pause.setOnClickListener(v -> {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    playingPosition = -1;
                    notifyDataSetChanged(); // Update UI
                }
            });
        }
    }

    public class VideoPostViewHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private ImageView thumbnailImageView;
        private TextView postDescriptionTextView;
        private ImageView play, pause;
        private FirebaseAuth auth;
        private FirebaseStorage storage;
        private FirebaseDatabase database;
        private CircleImageView pro, profile;

        public VideoPostViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.video_view); // Ensure this ID matches your layout
            thumbnailImageView = itemView.findViewById(R.id.video_thumbnail); // Add this ID to your layout XML
            postDescriptionTextView = itemView.findViewById(R.id.description);
            play = itemView.findViewById(R.id.play);
            pause = itemView.findViewById(R.id.pause);

            // Initialize play and pause buttons
            play.setOnClickListener(v -> {
                if (videoView != null) {
                    playVideo();
                    stopAllPlayback(); // Stop all media playback before starting a new one
                    playingPosition = getAdapterPosition();
                    notifyDataSetChanged(); // Notify adapter to refresh

                    thumbnailImageView.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.start();
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                }
            });
            pause.setOnClickListener(v -> {
                if (videoView != null && videoView.isPlaying()) {
                    pauseVideo();


                }
            });

            auth = FirebaseAuth.getInstance();
            storage = FirebaseStorage.getInstance();
            database = FirebaseDatabase.getInstance();

            pro = itemView.findViewById(R.id.pro);
            profile = itemView.findViewById(R.id.profile_image);
        }

        public void bind(Post post, int position) {
            postDescriptionTextView.setText(post.getPostDescription());

            // Load video thumbnail
            Glide.with(itemView.getContext())
                    .load(post.getPostImg()) // You should provide a method or URL to get the thumbnail
                    .into(thumbnailImageView);

            // Hide video view initially and show thumbnail
            videoView.setVisibility(View.INVISIBLE);
            thumbnailImageView.setVisibility(View.VISIBLE);

            thumbnailImageView.setOnClickListener(v -> {
                // Hide thumbnail and show video view
                thumbnailImageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(post.getPostImg()));
                videoView.start();
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            });

            videoView.setOnPreparedListener(mp -> {
                // Ensure the video starts automatically when it is prepared
                if (position == playingPosition) {
                    videoView.start();
                    play.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                } else {
                    videoView.pause();
                    play.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.GONE);
                }
            });

            // Loop video playback
            videoView.setOnCompletionListener(mp -> videoView.start());

            videoView.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(context, "Error playing video", Toast.LENGTH_SHORT).show();
                return true;
            });

            // Set visibility of play/pause buttons
            boolean isPlaying = position == playingPosition && videoView.isPlaying();
            play.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
            pause.setVisibility(isPlaying ? View.VISIBLE : View.GONE);

            // Load user profile images
            database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getProfile_photo()).into(pro);
                        Picasso.get().load(user.getProfile_photo()).into(profile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        private void playVideo() {
            if (videoView != null) {
                videoView.start();
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }
        }

        private void pauseVideo() {
            if (videoView != null && videoView.isPlaying()) {
                videoView.pause();
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }
        }

        public void stopPlayback() {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
        }
    }



    public class AudioPostViewHolder extends RecyclerView.ViewHolder {

        public AudioPostViewHolder(View itemView) {
            super(itemView);

        }

        public void bind(Post post, int position) {

        }

        private void playAudio() {
            // Handle audio play functionality
        }

        private void pauseAudio() {
            // Handle audio pause functionality
        }
    }
}

