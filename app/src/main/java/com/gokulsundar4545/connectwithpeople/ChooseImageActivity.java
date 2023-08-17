package com.gokulsundar4545.connectwithpeople;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.Adapter.GalleryUtils2;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gokulsundar4545.AddHighlitescover;
import com.gokulsundar4545.connectwithpeople.Adapter.GalleryAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.GalleryUtils;
import com.gokulsundar4545.connectwithpeople.Adapter.ImagecoverAdapter;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChooseImageActivity extends AppCompatActivity implements GalleryAdapter.OnImageClickListener, ImagecoverAdapter.OnImageClickListener {

    private CropImageView cropImageView;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private String selectedImageUrl; // To store selected image URL
    private TextView btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooseactivity);

        cropImageView = findViewById(R.id.imageView10);
        recyclerView = findViewById(R.id.friendrv);
        btnDone = findViewById(R.id.down);

        btnDone.setOnClickListener(v -> handleDoneButtonClick());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            loadGallery();
        }
    }

    private void loadGallery() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        List<String> imagePaths = GalleryUtils.getAllImages(this);
        Collections.reverse(imagePaths);

        ImagecoverAdapter galleryAdapter = new ImagecoverAdapter(this, imagePaths, this);
        recyclerView.setAdapter(galleryAdapter);

        if (!imagePaths.isEmpty()) {
            String imageUrl = imagePaths.get(0);
            if (cropImageView != null && imageUrl != null) {
                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true);

                Glide.with(this)
                        .asBitmap()
                        .load(imageUrl)
                        .apply(options)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                cropImageView.setImageBitmap(resource);
                                cropImageView.setFixedAspectRatio(true);
                                cropImageView.setAspectRatio(1, 1);
                                cropImageView.setCropRect(new Rect(0, 0, 400, 400));
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            } else {
                Toast.makeText(this, "Error: CropImageView is not initialized", Toast.LENGTH_SHORT).show();
            }
        }
    }
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


                SharedPreferences sharedPreferences = getSharedPreferences("coverimagehighlites", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("cropped_image", saveUri.toString());
                editor.apply();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to save cropped image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to crop image", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDoneButtonClick() {
        if (selectedImageUrl != null) {
          saveCroppedImage();

          startActivity(new Intent(ChooseImageActivity.this,AddHighlitescover.class));
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadGallery();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onImageClick(String imageUrl) {
        selectedImageUrl = imageUrl;

        if (cropImageView != null && imageUrl != null) {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true);

            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .apply(options)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            cropImageView.setImageBitmap(resource);
                            cropImageView.setFixedAspectRatio(true);
                            cropImageView.setAspectRatio(1, 1);
                            cropImageView.setCropRect(new Rect(0, 0, 400, 400));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        } else {
            Toast.makeText(this, "Error: CropImageView is not initialized", Toast.LENGTH_SHORT).show();
        }
    }
}
