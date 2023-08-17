package com.gokulsundar4545;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.AddHighlitesActivity;
import com.gokulsundar4545.connectwithpeople.ChooseImageActivity;
import com.gokulsundar4545.connectwithpeople.EditUserProfile;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.TransitionHandler;
import com.squareup.picasso.Picasso;

public class AddHighlitescover extends AppCompatActivity {

    TextView openimg;

    private ImageView imageView;
    EditText hintcover;

    TextView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_highlitescover);

        openimg=findViewById(R.id.textView15);
        openimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddHighlitescover.this, ChooseImageActivity.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(AddHighlitescover.this).toBundle();
                startActivity(intent, b);

                if (AddHighlitescover.this instanceof TransitionHandler) {
                    ((TransitionHandler) AddHighlitescover.this).performTransition();
                }
                finish();
            }
        });





        imageView = findViewById(R.id.pro); // Assuming you have an ImageView in your layout

        // Retrieve cropped image from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("coverimagehighlites", MODE_PRIVATE);
        String encodedImage = sharedPreferences.getString("cropped_image", null);

        Picasso.get().load(encodedImage).into(imageView);


        hintcover=findViewById(R.id.editcovername);

        next=findViewById(R.id.login);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hintcover.getText().toString().isEmpty()){
                    Toast.makeText(AddHighlitescover.this, "enter some cover name", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(AddHighlitescover.this, AddHighlitesActivity.class);
                    intent.putExtra("covername", hintcover.getText().toString());
                    intent.putExtra("coverImage", encodedImage);
                    startActivity(intent); // Start MainActivity with the intent
                }
            }
        });

    }
}