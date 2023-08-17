package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class VedioEditactivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_editactivity);

        // Retrieve image URL from intent extras
        if (getIntent() != null) {
            String imageUrl = getIntent().getStringExtra("image_url");


            // Load image into ImageView using Picasso
            ImageView imageView = findViewById(R.id.image_view); // Replace with your actual ImageView id
            if (imageView != null) {
                Glide.with(this).load(imageUrl).into(imageView);
            } else {
                Toast.makeText(this, "ImageView not found", Toast.LENGTH_SHORT).show();
            }

        } else {
            // Handle case where imageUrl is not available (optional)
            Toast.makeText(this, "Image URL not found", Toast.LENGTH_SHORT).show();
            finish(); // Optionally finish the activity if imageUrl is required
        }
    }
}