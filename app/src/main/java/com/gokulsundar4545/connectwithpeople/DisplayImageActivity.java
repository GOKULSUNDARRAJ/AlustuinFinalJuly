package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DisplayImageActivity extends AppCompatActivity {

    private ImageView imageView, imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, imageView7, imageView8, imageView9, imageView10, imageView11;
    private boolean isGrayScale = false;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        sharedPreferences = getSharedPreferences("ModifiedImagePrefs", MODE_PRIVATE);

        // Initialize ImageView
        imageView = findViewById(R.id.image_view_display);
        imageView1 = findViewById(R.id.button1);
        imageView2 = findViewById(R.id.button2);
        imageView3 = findViewById(R.id.button3);
        imageView4 = findViewById(R.id.button4);
        imageView5 = findViewById(R.id.button5);
        imageView6 = findViewById(R.id.button6);
        imageView7 = findViewById(R.id.button7);
        imageView8 = findViewById(R.id.button8);
        imageView9 = findViewById(R.id.button9);
        imageView10 = findViewById(R.id.button10);
        imageView11 = findViewById(R.id.button11);


        // Retrieve image URI from intent extras
        Intent intent = getIntent();
        if (intent != null) {
            String imageUriString = intent.getStringExtra("image_uri");
            if (imageUriString != null) {
                // Convert URI string back to URI
                Uri imageUri = Uri.parse(imageUriString);

                // Load image into ImageView using Glide
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView1);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView2);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView3);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView4);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView5);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView6);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView7);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView8);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView9);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView10);
                Glide.with(this)
                        .load(imageUri)
                        .into(imageView11);


            } else {
                Toast.makeText(this, "Image URI not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Intent extras not found", Toast.LENGTH_SHORT).show();
            finish(); // Optionally finish the activity if intent extras are required
        }


        applyColorEffect1(getColorMatrixSepia());
        applyColorEffect2(getColorMatrixGrayScale());
        applyColorEffect3(getColorMatrixInvert()); // Example: Gray scale
        applyColorEffect4(getColorMatrixBrightness()); // Example: Gray scale effect
        applyColorEffect5(getColorMatrixRedTint()); // Example: Gray scale effect
        applyColorEffect6(getColorMatrixBlueTint()); // Example: Gray scale effect
        applyColorEffect7(getColorMatrixGreenTint()); // Example: Gray scale effect
        applyColorEffect8(getColorMatrixHighContrast()); // Example: Gray scale effect
        applyColorEffect9(getColorMatrixLowContrast()); // Example: Gray scale effect
        applyColorEffect10(getColorMatrixYellowTint()); // Example: Gray scale effect
        applyColorEffect11(getColorMatrixPurpleTint()); // Example: Gray scale effect


    }

    // Method to apply a color effect to the ImageView
    private void applyColorEffect(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    // Method to save the modified image to a file
    // Method to save the modified image to a file
    private Uri saveModifiedImage(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "modified_image_" + timeStamp + ".png"; // Use PNG format

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, imageFileName);

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // Save as PNG
            outputStream.flush();
            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save modified image", Toast.LENGTH_SHORT).show();
            return null;
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



    public void onClickProceed(View view) {
        // Create a transparent Bitmap from the ImageView
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false);

        // Convert it to a transparent Bitmap
        Bitmap transparentBitmap = getTransparentBitmap(bitmap);

        // Save the modified image to a file
        Uri modifiedImageUri = saveModifiedImage(transparentBitmap);

        if (modifiedImageUri != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("modified_image_uri", modifiedImageUri.toString());
            editor.apply();
            // Proceed to a new activity and pass the URI of the modified image
            Intent intent = new Intent(this, FinalPostEditActivity.class);
            intent.putExtra("modified_image_uri", modifiedImageUri.toString());
            startActivity(intent);
        }
    }
    public Bitmap getTransparentBitmap(Bitmap sourceBitmap) {
        Bitmap transparentBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(transparentBitmap);
        canvas.drawColor(Color.TRANSPARENT); // Set background to transparent
        canvas.drawBitmap(sourceBitmap, 0, 0, null);
        return transparentBitmap;
    }


    // Methods for different color effects (similar to your existing methods)
    private float[] getColorMatrixGrayScale() {
        return new float[]{
                0.33f, 0.33f, 0.33f, 0, 0,
                0.33f, 0.33f, 0.33f, 0, 0,
                0.33f, 0.33f, 0.33f, 0, 0,
                0, 0, 0, 1, 0
        };
    }

    private float[] getColorMatrixSepia() {
        return new float[]{
                0.393f, 0.769f, 0.189f, 0, 0, // Red component
                0.349f, 0.686f, 0.168f, 0, 0, // Green component
                0.272f, 0.534f, 0.131f, 0, 0, // Blue component
                0, 0, 0, 1, 0 // Alpha component
        };
    }

    // Example: Invert colors effect
    private float[] getColorMatrixInvert() {
        return new float[]{
                -1, 0, 0, 0, 255, // Red component
                0, -1, 0, 0, 255, // Green component
                0, 0, -1, 0, 255, // Blue component
                0, 0, 0, 1, 0 // Alpha component
        };
    }

    // Example: Increase brightness effect
    private float[] getColorMatrixBrightness() {
        return new float[]{
                1.5f, 0, 0, 0, 0, // Red component
                0, 1.5f, 0, 0, 0, // Green component
                0, 0, 1.5f, 0, 0, // Blue component
                0, 0, 0, 1, 0 // Alpha component
        };
    }

    // Example: Red tint effect
    private float[] getColorMatrixRedTint() {
        return new float[]{
                1.5f, 0, 0, 0, 0, // Red component
                0, 1, 0, 0, 0, // Green component
                0, 0, 1, 0, 0, // Blue component
                0, 0, 0, 1, 0 // Alpha component
        };
    }

    // Example: Blue tint effect
    private float[] getColorMatrixBlueTint() {
        return new float[]{
                1, 0, 0, 0, 0, // Red component
                0, 1, 0, 0, 0, // Green component
                0, 0, 1.5f, 0, 0, // Blue component
                0, 0, 0, 1, 0 // Alpha component
        };
    }

    // Example: Green tint effect
    private float[] getColorMatrixGreenTint() {
        return new float[]{
                1, 0, 0, 0, 0, // Red component
                0, 1.5f, 0, 0, 0, // Green component
                0, 0, 1, 0, 0, // Blue component
                0, 0, 0, 1, 0 // Alpha component
        };
    }

    // Example: High contrast effect
    private float[] getColorMatrixHighContrast() {
        return new float[]{
                2, 0, 0, 0, -255, // Red component
                0, 2, 0, 0, -255, // Green component
                0, 0, 2, 0, -255, // Blue component
                0, 0, 0, 1, 0 // Alpha component
        };
    }

    // Example: Low contrast effect
    private float[] getColorMatrixLowContrast() {
        return new float[]{
                0.5f, 0, 0, 0, 0, // Red component
                0, 0.5f, 0, 0, 0, // Green component
                0, 0, 0.5f, 0, 0, // Blue component
                0, 0, 0, 1, 0 // Alpha component
        };
    }

    // Example: Yellow tint effect
    private float[] getColorMatrixYellowTint() {
        return new float[]{
                1, 0, 0, 0, 0, // Red component
                0, 1, 0, 0, 0, // Green component
                0, 0, 1, 0, 0, // Blue component
                0, 0, 0, 1, 0 // Alpha component
        };
    }

    // Example: Purple tint effect
    private float[] getColorMatrixPurpleTint() {
        return new float[]{
                1, 0, 1, 0, 0, // Red component
                0, 1, 0, 0, 0, // Green component
                1, 0, 1, 0, 0, // Blue component
                0, 0, 0, 1, 0 // Alpha component
        };
    }




    public void onClickButton1(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixSepia()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }

    public void onClickButton2(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixGrayScale()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }

    public void onClickButton3(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixInvert()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }

    public void onClickButton4(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixBrightness()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }

    public void onClickButton5(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixRedTint()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }

    public void onClickButton6(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixBlueTint()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }

    public void onClickButton7(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixGreenTint()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }

    public void onClickButton8(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixHighContrast()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }

    public void onClickButton9(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixLowContrast()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }

    public void onClickButton10(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixYellowTint()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }

    public void onClickButton11(View view) {
        if (!isGrayScale) {
            applyColorEffect(getColorMatrixPurpleTint()); // Example: Gray scale effect
            // You can call other color matrix methods for different effects here
            isGrayScale = true;
        } else {
            // Clear the color filter to revert to original image
            imageView.clearColorFilter();
            isGrayScale = false;
        }
    }



    private void applyColorEffect1(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView1.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void applyColorEffect2(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView2.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void applyColorEffect3(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView3.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void applyColorEffect4(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView4.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void applyColorEffect5(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView5.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void applyColorEffect6(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView6.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void applyColorEffect7(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView7.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void applyColorEffect8(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView8.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void applyColorEffect9(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView9.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void applyColorEffect10(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView10.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private void applyColorEffect11(float[] colorMatrix) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(colorMatrix);
        imageView11.setColorFilter(new ColorMatrixColorFilter(matrix));
    }



    // Other color matrix methods for different effects as per your requirement
}
