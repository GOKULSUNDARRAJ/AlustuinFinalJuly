package com.gokulsundar4545.connectwithpeople;
import static androidx.databinding.DataBindingUtil.setContentView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;

public class EditablrActivity extends AppCompatActivity {

    private PhotoEditorView photoEditorView;
    private PhotoEditor photoEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editable_activity);

        // Initialize PhotoEditorView
        photoEditorView = findViewById(R.id.photoEditorView);

        // Initialize PhotoEditor
        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true) // Set whether text can be scaled or not
                .build();

        // Load an image into the PhotoEditorView
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.posteredit);
        photoEditorView.getSource().setImageBitmap(bitmap);

        // Create a TextStyleBuilder object
        TextStyleBuilder textStyleBuilder = new TextStyleBuilder();
        textStyleBuilder.withTextColor(Color.RED); // Text color
        textStyleBuilder.withTextSize(20); // Text size
        textStyleBuilder.withTextFont(Typeface.DEFAULT_BOLD); // Font style

        // Example: Add a text to the image
        try {
            photoEditor.addText("Sample Text", textStyleBuilder); // Add text with style
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to add text", Toast.LENGTH_SHORT).show();
        }
    }
}
