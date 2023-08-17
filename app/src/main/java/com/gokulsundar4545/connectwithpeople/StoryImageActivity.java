package com.gokulsundar4545.connectwithpeople;

import static androidx.camera.core.CameraX.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.Fragment.HomeFragment;
import com.gokulsundar4545.connectwithpeople.Model.Story;
import com.gokulsundar4545.connectwithpeople.Model.UserStories;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class StoryImageActivity extends AppCompatActivity {
    Uri uri;


    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage Storage;
    ImageView image_view;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_image);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        Storage=FirebaseStorage.getInstance();
        image_view=findViewById(R.id.image_view);

        Intent intent = getIntent();
        if (intent != null) {
            String imageUrl = intent.getStringExtra("image_url2");

            if (imageUrl != null) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(image_view);
            }
        }




    }

}