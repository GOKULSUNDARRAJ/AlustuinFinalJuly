package com.gokulsundar4545.connectwithpeople.Adapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gokulsundar4545.connectwithpeople.CropActivity;
import com.gokulsundar4545.connectwithpeople.FinalPostEditActivity2;
import com.gokulsundar4545.connectwithpeople.Fragment.VedioViewFragment;
import com.gokulsundar4545.connectwithpeople.MainActivity;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.VedioMode;

import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class VedioCropActivity extends AppCompatActivity {

    private VedioMode member;

    private TextView next;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage Storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_crop);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        Storage = FirebaseStorage.getInstance();


        String videoUrl = getIntent().getStringExtra("vidiourl");
        String videouri = getIntent().getStringExtra("vidiourl1");


        Toast.makeText(this, videouri, Toast.LENGTH_SHORT).show();
        member = new VedioMode();


        VideoView capturedVideoView = findViewById(R.id.captured_video_view);


        if (videoUrl != null) {
            capturedVideoView.setVideoURI(Uri.parse(videoUrl));
            MediaController mediaController = new MediaController(this);
            capturedVideoView.setMediaController(mediaController);
            mediaController.setAnchorView(capturedVideoView);
            capturedVideoView.start();
        } else {
            Toast.makeText(this, "No video URL found", Toast.LENGTH_SHORT).show();
            finish(); // Optionally finish the activity if the video URL is required
        }





        next=findViewById(R.id.down);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                UploadPost("Confidence is not about being \uD83D\uDD25better than others; it's about being your best self\uD83D\uDE02",Uri.parse(videouri));

                UploadVedio("Confidence is not \uD83D\uDD25about being better than others; it's about being your best self\uD83D\uDD25",Uri.parse(videouri));
            }
        });



    }


    private void UploadPost(String des,Uri videoUri) {
        if (videoUri == null) {

            Toast.makeText(VedioCropActivity.this, "Image URI is null", Toast.LENGTH_SHORT).show();
            return;
        }

        final StorageReference reference = Storage.getReference().child("posts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(new Date().getTime() + "");
        reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Post post = new Post();
                        post.setPostImg(uri.toString());
                        post.setPostedBy(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        post.setPostDescription(des);
                        post.setPosterAt(new Date().getTime());
                        post.setPostType("video");
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        database.getReference().child("posts")
                                .push()
                                .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Dismiss ProgressDialog when post is uploaded successfully

                                        Toast.makeText(getApplicationContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    // Dismiss ProgressDialog if there's an error

                                    Toast.makeText(getApplicationContext(), "Failed to post", Toast.LENGTH_SHORT).show();
                                });
                    }
                }).addOnFailureListener(e -> {
                    // Dismiss ProgressDialog if there's an error getting the download URL

                    Toast.makeText(getApplicationContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            // Dismiss ProgressDialog if there's an error uploading the file

            Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }
    private void UploadVedio(String caption,Uri VedioUri) {



        if (VedioUri!=null ){
            final StorageReference reference=Storage.getReference().child("Vedio")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(new Date().getTime()+"");
            reference.putFile(VedioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            VedioMode post=new VedioMode();

                            post.setVedioUrl(uri.toString());
                            post.setVedioBy(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            post.setVedioDescription(caption);
                            post.setVedioposterAt(new Date().getTime());

                            database.getReference().child("vedio")
                                    .push()
                                    .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {


                                            Toast.makeText(VedioCropActivity.this, "Vedio Posted Successfully", Toast.LENGTH_SHORT).show();


                                        }
                                    });
                        }
                    });
                }
            });
        }
    }

}
