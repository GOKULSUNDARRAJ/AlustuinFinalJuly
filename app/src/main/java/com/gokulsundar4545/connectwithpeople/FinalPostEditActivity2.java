package com.gokulsundar4545.connectwithpeople;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class FinalPostEditActivity2 extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    ImageView image_view, itemImage1;
    TextView titletv, subtitletv;

    private ImageView imageView;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage Storage;

    EditText postdescription;
    Button share;
    ProgressDialog progressDialog;
    Uri modifiedImageUri;  // Use Uri instead of String
    ImageView music;
    String storedCoverUrl, title, subtitle,songurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_post_edit2);

        sharedPreferences = getSharedPreferences("ModifiedImagePrefs", Context.MODE_PRIVATE);

        share = findViewById(R.id.letuserloginbtn);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        Storage = FirebaseStorage.getInstance();

        postdescription = findViewById(R.id.postdescription);
        imageView = findViewById(R.id.image_view);
        music = findViewById(R.id.music);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.setCancelable(false);

        titletv = findViewById(R.id.title);
        subtitletv = findViewById(R.id.subtitle1);
        itemImage1 = findViewById(R.id.itemImage1);
        image_view = findViewById(R.id.image_view);

        String modifiedImageUriString = sharedPreferences.getString("modified_image_uri", null);

        if (modifiedImageUriString != null) {
            modifiedImageUri = Uri.parse(modifiedImageUriString);  // Convert String to Uri
            Toast.makeText(this, modifiedImageUriString, Toast.LENGTH_SHORT).show();
            Glide.with(this).load(modifiedImageUriString).into(image_view);
        } else {
            Toast.makeText(this, "Modified image URI not found", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        storedCoverUrl = sharedPreferences.getString("coverUrl", "");
        title = sharedPreferences.getString("title", "");
        subtitle = sharedPreferences.getString("subtitle", "");
        songurl = sharedPreferences.getString("songurl", "");

        titletv.setText(title);
        subtitletv.setText(subtitle);

        if (storedCoverUrl != null) {
            Toast.makeText(this, storedCoverUrl, Toast.LENGTH_SHORT).show();
            Glide.with(this).load(storedCoverUrl).into(itemImage1);
        } else {
            Toast.makeText(this, "Cover image URI not found", Toast.LENGTH_SHORT).show();
        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show ProgressDialog when sharing starts
                progressDialog.show();
                UploadPost(postdescription.getText().toString());
            }
        });
    }

    private void UploadPost(String des) {
        if (modifiedImageUri == null) {
            progressDialog.dismiss();
            Toast.makeText(FinalPostEditActivity2.this, "Image URI is null", Toast.LENGTH_SHORT).show();
            return;
        }

        final StorageReference reference = Storage.getReference().child("posts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(new Date().getTime() + "");
        reference.putFile(modifiedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                        post.setCoverUrl(storedCoverUrl); // Set cover URL
                        post.setTitle(title); // Set title
                        post.setSubtitle(subtitle); // Set subtitle
                        post.setSongurl(songurl); // Set subtitle
                        post.setPostType("image");
                        startActivity(new Intent(FinalPostEditActivity2.this,MainActivity.class));
                        database.getReference().child("posts")
                                .push()
                                .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Dismiss ProgressDialog when post is uploaded successfully
                                        progressDialog.dismiss();
                                        Toast.makeText(FinalPostEditActivity2.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    // Dismiss ProgressDialog if there's an error
                                    progressDialog.dismiss();
                                    Toast.makeText(FinalPostEditActivity2.this, "Failed to post", Toast.LENGTH_SHORT).show();
                                });
                    }
                }).addOnFailureListener(e -> {
                    // Dismiss ProgressDialog if there's an error getting the download URL
                    progressDialog.dismiss();
                    Toast.makeText(FinalPostEditActivity2.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            // Dismiss ProgressDialog if there's an error uploading the file
            progressDialog.dismiss();
            Toast.makeText(FinalPostEditActivity2.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        });
    }


}
