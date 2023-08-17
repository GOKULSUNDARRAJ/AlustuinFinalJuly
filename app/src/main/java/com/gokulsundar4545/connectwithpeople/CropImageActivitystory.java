// CropImageActivity.java
package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CropImageActivitystory extends AppCompatActivity {

    private CropImageView cropImageView;

    TextView down;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        // Initialize CropImageView
        cropImageView = findViewById(R.id.image_view);
        down=findViewById(R.id.down);

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCroppedImage();
            }
        });

        // Set aspect ratio for square crop
        cropImageView.setAspectRatio(1, 1); // 1:1 aspect ratio (square crop)

        // Retrieve image URL from intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String imageUrl = intent.getStringExtra("image_urlstory");

            if (imageUrl != null) {
                // Show the URL in a Toast
                Toast.makeText(this, "Image URL: " + imageUrl, Toast.LENGTH_LONG).show();

                // Load image using Glide into CropImageView
                // Load image using Glide into CropImageView
                // Load image using Glide into CropImageView with downsampling
                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Prevent caching of image to avoid loading the same image in CropImageView
                        .skipMemoryCache(true) // Skip memory cache to prevent OutOfMemoryError
                        .override(1024, 1024); // Example downsampling size

                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .apply(options)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                // Set bitmap to CropImageView
                                cropImageView.setImageBitmap(resource);

                                // Manually adjust crop window size to 400x400 pixels
                                cropImageView.setFixedAspectRatio(true);
                                cropImageView.setCropRect(new Rect(0, 0, 900, 900));
                            }

                            @Override
                            public void onLoadCleared(Drawable placeholder) {
                                // Remove any placeholder if needed
                            }
                        });



            } else {
                Toast.makeText(this, "No image URL found", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle case where intent extras are not available
            Toast.makeText(this, "Intent extras not found", Toast.LENGTH_SHORT).show();
            finish(); // Optionally finish the activity if intent extras are required
        }
    }

    // Example method to retrieve cropped image and save it
    private void saveCroppedImage() {
        Bitmap cropped = cropImageView.getCroppedImage();
        if (cropped != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + ".jpg";

            File saveFile = new File(getExternalFilesDir(null), imageFileName);
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(saveFile);
                cropped.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                outputStream.flush();
                outputStream.close();
                Toast.makeText(this, "Cropped image saved: " + saveFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                // Send cropped image URI to new activity
                Uri saveUri = Uri.fromFile(saveFile);
                Intent intent = new Intent(this, DisplayImageActivitystory.class);
                intent.putExtra("image_uristory", saveUri.toString());
                startActivity(intent);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save cropped image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
        }
    }



    // Handle back press to potentially save cropped image
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
