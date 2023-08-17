package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.io.File;

public class NewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        // Retrieve image URL or video URL from intent extras
        if (getIntent() != null) {
            String imageUrl = getIntent().getStringExtra("image_url");
            String videoUrl = getIntent().getStringExtra("video_path");

            // Load image or video into ImageView or VideoView
            ImageView imageView = findViewById(R.id.image_view);
            VideoView capturedVideoView = findViewById(R.id.captured_video_view);

            if (imageView != null && capturedVideoView != null) {
                if (imageUrl != null) {
                    // Load image using Glide
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(this).load(imageUrl).into(imageView);
                } else if (videoUrl != null) {
                    // Load video using VideoView
                    capturedVideoView.setVisibility(View.VISIBLE);
                    capturedVideoView.setVideoURI(Uri.parse(videoUrl));
                    capturedVideoView.start();
                } else {
                    Toast.makeText(this, "No image or video URL found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "ImageView or VideoView not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle case where intent extras are not available
            Toast.makeText(this, "Intent extras not found", Toast.LENGTH_SHORT).show();
            finish(); // Optionally finish the activity if intent extras are required
        }
    }
}
