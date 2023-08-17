package com.gokulsundar4545.connectwithpeople;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class PhotoEditActivity extends AppCompatActivity {

    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private static final int REQUEST_CODE = 1;

    ImageView edittex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);


        edittex=findViewById(R.id.imgClose);
        edittex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mPhotoEditorView = findViewById(R.id.photoEditorView);

        // Create a PhotoEditor instance
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // enable pinch to zoom for text
                .build();

        // Example: Add sample text to the PhotoEditorView
        mPhotoEditor.addText("Sample Text", getResources().getColor(android.R.color.holo_blue_light));

        // Check and request permissions
        checkPermissions();
        addStickerToEditor();
        // Setup save button click listener
        ImageView saveImage = findViewById(R.id.imgSave);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });
    }

    private void saveImage() {
        // Generate a filename with current timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "edited_image_" + timeStamp + ".png";

        // Construct the full path where the image will be saved
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directories if they don't exist
        }
        File file = new File(directory, fileName);
        String filePath = file.getAbsolutePath();

        // Create a bitmap from the PhotoEditorView
        Bitmap bitmap = Bitmap.createBitmap(mPhotoEditorView.getWidth(), mPhotoEditorView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mPhotoEditorView.draw(canvas);

        // Save the bitmap as a file
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Image saved successfully
            Toast.makeText(this, "Image saved: " + filePath, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic (if needed)
            } else {
                // Permission denied, handle the case
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addStickerToEditor() {
        Drawable stickerDrawable = ContextCompat.getDrawable(this, R.drawable.aa);
        if (stickerDrawable != null) {
            // Convert the drawable to Bitmap
            Bitmap stickerBitmap = ((BitmapDrawable) stickerDrawable).getBitmap();

            // Add sticker to the PhotoEditorView
            mPhotoEditor.addImage(stickerBitmap);
        } else {
            Toast.makeText(this, "Failed to add sticker", Toast.LENGTH_SHORT).show();
        }
    }

}
