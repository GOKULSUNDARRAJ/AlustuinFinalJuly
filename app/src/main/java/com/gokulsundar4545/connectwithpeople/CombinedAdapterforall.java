package com.gokulsundar4545.connectwithpeople;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.Model.Post;

import java.io.IOException;
import java.util.List;

public class CombinedAdapterforall extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_IMAGE = 0;
    private static final int TYPE_VIDEO = 1;
    private static final int TYPE_AUDIO = 2;

    private List<Post> posts;
    private Context context;
    private MediaPlayer mediaPlayer;
    private int playingPosition = -1; // Track which item is currently playing

    public CombinedAdapterforall(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
        this.mediaPlayer = new MediaPlayer(); // Initialize MediaPlayer here
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_IMAGE:
                View imageView = inflater.inflate(R.layout.dashboard_rvforall, parent, false);
                return new ImagePostViewHolder(imageView);
            case TYPE_VIDEO:
                View videoView = inflater.inflate(R.layout.item_video_postforall, parent, false);
                return new VideoPostViewHolder(videoView);
            case TYPE_AUDIO:
                View audioView = inflater.inflate(R.layout.item_audio_post, parent, false);
                return new AudioPostViewHolder(audioView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);
        switch (getItemViewType(position)) {
            case TYPE_IMAGE:
                ((ImagePostViewHolder) holder).bind(post);
                break;
            case TYPE_VIDEO:
                ((VideoPostViewHolder) holder).bind(post);
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

    public class ImagePostViewHolder extends RecyclerView.ViewHolder {
        private ImageView postImageView;

        public ImagePostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImageView = itemView.findViewById(R.id.postimage);
        }

        public void bind(Post post) {
            Glide.with(itemView.getContext()).load(post.getPostImg()).into(postImageView);
        }
    }

    public class VideoPostViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailImageView;
        private VideoView videoView;

        public VideoPostViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.video_thumbnail);
            videoView = itemView.findViewById(R.id.video_view); // Add VideoView to your layout XML
        }

        public void bind(Post post) {
            Glide.with(itemView.getContext())
                    .load(post.getPostImg())
                    .into(thumbnailImageView);

            thumbnailImageView.setOnClickListener(v -> {
                videoView.setVisibility(View.VISIBLE);
                thumbnailImageView.setVisibility(View.GONE);
                videoView.setVideoURI(Uri.parse(post.getPostImg())); // Ensure post has a method to get video URL
                videoView.start();
            });
        }
    }

    public class AudioPostViewHolder extends RecyclerView.ViewHolder {

        public AudioPostViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        public void bind(Post post, int position) {
            // Handle initial setup if needed
        }


    }
}
