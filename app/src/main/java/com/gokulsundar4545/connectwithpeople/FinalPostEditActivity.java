package com.gokulsundar4545.connectwithpeople;

import static androidx.camera.core.CameraX.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.Fragment.BottomSheetFragmentmuisc;
import com.gokulsundar4545.connectwithpeople.Fragment.HomeFragment;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import android.app.ProgressDialog;
// other imports...

public class FinalPostEditActivity extends AppCompatActivity {

    private ImageView imageView;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage Storage;
    Uri modifiedImageUri;
    EditText postdescription;
    Button share;
    ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    ImageView music;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_post_edit);
        sharedPreferences = getSharedPreferences("ModifiedImagePrefs", Context.MODE_PRIVATE);

        share = findViewById(R.id.letuserloginbtn);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        Storage = FirebaseStorage.getInstance();
        postdescription = findViewById(R.id.postdescription);
        imageView = findViewById(R.id.image_view);
        music=findViewById(R.id.music);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.setCancelable(false);


            String modifiedImageUriString = sharedPreferences.getString("modified_image_uri", null);

            if (modifiedImageUriString != null) {
                modifiedImageUri = Uri.parse(modifiedImageUriString);
                Glide.with(this).load(modifiedImageUri).into(imageView);
            } else {
                Toast.makeText(this, "Modified image URI not found", Toast.LENGTH_SHORT).show();
            }


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show ProgressDialog when sharing starts
                progressDialog.show();
                UploadPost(postdescription.getText().toString());
            }
        });

        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragmentmuisc bottomSheetFragment = new BottomSheetFragmentmuisc();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        BottomSheetFragmentmuisc bottomSheetFragment = new BottomSheetFragmentmuisc();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void UploadPost(String des) {
        final StorageReference reference = Storage.getReference().child("posts")
                .child(FirebaseAuth.getInstance().getUid())
                .child(new Date().getTime() + "");
        reference.putFile(modifiedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Post post = new Post();
                        post.setPostImg(uri.toString());
                        post.setPostedBy(FirebaseAuth.getInstance().getUid());
                        post.setPostDescription(des);
                        post.setPosterAt(new Date().getTime());

                        database.getReference().child("posts")
                                .push()
                                .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Dismiss ProgressDialog when post is uploaded successfully
                                        progressDialog.dismiss();
                                        Toast.makeText(FinalPostEditActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    // Dismiss ProgressDialog if there's an error
                                    progressDialog.dismiss();
                                    Toast.makeText(FinalPostEditActivity.this, "Failed to post", Toast.LENGTH_SHORT).show();
                                });
                    }
                }).addOnFailureListener(e -> {
                    // Dismiss ProgressDialog if there's an error getting the download URL
                    progressDialog.dismiss();
                    Toast.makeText(FinalPostEditActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            // Dismiss ProgressDialog if there's an error uploading the file
            progressDialog.dismiss();
            Toast.makeText(FinalPostEditActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }
}
