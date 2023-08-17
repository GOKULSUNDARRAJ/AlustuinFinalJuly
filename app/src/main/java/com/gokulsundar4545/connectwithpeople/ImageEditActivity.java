package com.gokulsundar4545.connectwithpeople;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ImageEditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FrameLayout draggableContainer;
    private Button addImageButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);

        draggableContainer = findViewById(R.id.draggableContainer);
        addImageButton = findViewById(R.id.addImageButton);

        addImageButton.setOnClickListener(v -> {
            // Open gallery to select an image
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            addImageToContainer(imageUri);
        }
    }

    private void addImageToContainer(Uri imageUri) {
        // Create a new ImageView to display the selected image
        ImageView newImageView = new ImageView(this);

        // Load the image from the Uri
        Glide.with(this)
                .load(imageUri)
                .into(newImageView);

        // Set parameters for the new ImageView
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        newImageView.setLayoutParams(params);

        newImageView.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;
            private ScaleGestureDetector scaleGestureDetector;
            private float scaleFactor = 1.0f;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (scaleGestureDetector == null) {
                    scaleGestureDetector = new ScaleGestureDetector(ImageEditActivity.this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        @Override
                        public boolean onScale(ScaleGestureDetector detector) {
                            scaleFactor *= detector.getScaleFactor();
                            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f)); // Prevent scaling too small or too large
                            view.setScaleX(scaleFactor);
                            view.setScaleY(scaleFactor);
                            return true;
                        }
                    });
                }
                scaleGestureDetector.onTouchEvent(event);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        return true;

                    default:
                        return false;
                }
            }
        });

        // Add the ImageView to the FrameLayout
        draggableContainer.addView(newImageView);
    }
}
