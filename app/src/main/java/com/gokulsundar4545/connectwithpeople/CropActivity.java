package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gokulsundar4545.connectwithpeople.R;

public class CropActivity extends AppCompatActivity {

    private VideoView videoView;
    private String videoPath;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        videoView = findViewById(R.id.captured_video_view);
        TextView btnCrop = findViewById(R.id.crop);

        // Retrieve video path from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("VideoPrefs", Context.MODE_PRIVATE);
        videoPath = sharedPreferences.getString("video_path", null);

        if (videoPath != null) {
            videoView.setVideoURI(Uri.parse(videoPath));
            videoView.start();
        } else {
            Toast.makeText(this, "No video path found in shared preferences", Toast.LENGTH_SHORT).show();
        }

        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement cropping functionality here
                cropVideo();
            }
        });
    }

    private void cropVideo() {
        // Placeholder for video cropping functionality
        // You can implement your video cropping logic here
        Toast.makeText(this, "Video cropping not implemented yet", Toast.LENGTH_SHORT).show();
    }


}
