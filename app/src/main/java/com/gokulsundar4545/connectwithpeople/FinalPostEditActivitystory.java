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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.Fragment.BottomSheetFragmentmuisc;
import com.gokulsundar4545.connectwithpeople.Fragment.HomeFragment;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.Story;
import com.gokulsundar4545.connectwithpeople.Model.UserStories;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
// other imports...

public class FinalPostEditActivitystory extends AppCompatActivity {

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
    String modifiedImageUriString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_post_editstory);
        sharedPreferences = getSharedPreferences("ModifiedImagePrefsfinalstory", Context.MODE_PRIVATE);

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


            modifiedImageUriString = sharedPreferences.getString("modified_image_uri_finalstory", null);
            if (modifiedImageUriString != null) {
                modifiedImageUri = Uri.parse(modifiedImageUriString);
                Glide.with(this).load(modifiedImageUri).into(imageView);
            } else {
                Toast.makeText(this, "Modified image URI not found", Toast.LENGTH_SHORT).show();
            }


        postdescription=findViewById(R.id.postdescription);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UploadPost(postdescription.getText().toString());

            }
        });




    }

    private void UploadPost(String caption) {



        // Check if the URI string is valid
        if (modifiedImageUriString != null) {
            Uri modifiedImageUri = Uri.parse(modifiedImageUriString);

            // Firebase Storage reference
            final StorageReference reference = Storage.getReference()
                    .child("stories")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(String.valueOf(new Date().getTime()));

            // Upload file to Firebase Storage
            reference.putFile(modifiedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get download URL for the uploaded file
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            // Create and set values for Story object
                            long timeEnd = System.currentTimeMillis() + 86400000;

                            Story story = new Story();
                            story.setStoryAt(new Date().getTime());
                            story.setStatusCaption(caption);
                            story.setTimeend(timeEnd);

                            // Example: Update `stories` node in Firebase Realtime Database
                            database.getReference()
                                    .child("stories")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .child("postedBy")
                                    .setValue(story.getStoryAt(), story.getStatusCaption())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Create UserStories object
                                            UserStories stories = new UserStories(downloadUri.toString(), story.getStoryAt(), story.getStatusCaption());

                                            // Push UserStories object to `userStories` node under `stories`
                                            database.getReference()
                                                    .child("stories")
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .child("userStories")
                                                    .push()
                                                    .setValue(stories)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getApplicationContext(), "Status updated Successfully", Toast.LENGTH_SHORT).show();

                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                        }
                                                    });
                                        }
                                    });

                            // Example: Update `stories2` node in Firebase Realtime Database (if needed)
                            database.getReference()
                                    .child("stories2")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .child("postedBy2")
                                    .setValue(story.getStoryAt(), story.getStatusCaption())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            UserStories stories = new UserStories(downloadUri.toString(), story.getStoryAt(), story.getStatusCaption());

                                            database.getReference()
                                                    .child("stories2")
                                                    .child(FirebaseAuth.getInstance().getUid())
                                                    .child("userStories2")
                                                    .push()
                                                    .setValue(stories)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getApplicationContext(), "Status updated Successfully", Toast.LENGTH_SHORT).show();

                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle failed upload
                    Toast.makeText(getApplicationContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        } else {
            // Handle case where modifiedImageUriString is null or invalid
            Toast.makeText(getApplicationContext(), "Modified Image URI is null or invalid", Toast.LENGTH_SHORT).show();

        }
    }
}
