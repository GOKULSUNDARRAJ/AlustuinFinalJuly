package com.gokulsundar4545.connectwithpeople.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.Story;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.Model.UserStories;
import com.gokulsundar4545.connectwithpeople.Model.VedioMode;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentAddBinding;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentAddStatusBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;



import static android.app.Activity.RESULT_OK;

public class AddStatus extends Fragment {

    FragmentAddStatusBinding binding;
    Uri uri;


    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage Storage;




    public static final  int PICK_VEDIO=1;

    VideoView videoView;
    EditText editText;
    Button button;

    ImageView choosvedio;

    private Uri VedioUri;
    MediaController mediaController;

    DatabaseReference databaseReference;
    VedioMode member;
    UploadTask uploadTask;

    private SharedPreferences sharedPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        Storage=FirebaseStorage.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        binding=FragmentAddStatusBinding.inflate(inflater, container, false);


        database.getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    User user=snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(binding.profileImage);
                    binding.name.setText(user.getName());
                    binding.profession.setText(user.getProfission());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.postbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadPost();
            }
        });


        sharedPreferences = requireContext().getSharedPreferences("ModifiedImagePrefsfinal", Context.MODE_PRIVATE);


        return binding.getRoot();
    }

    private void UploadPost() {
        binding.progress.setVisibility(View.VISIBLE);

        // Retrieve modified image URI from shared preferences
        String modifiedImageUriString = sharedPreferences.getString("modified_image_uri_final", null);

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
                            story.setStatusCaption(binding.postdescription.getText().toString());
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
                                                            Toast.makeText(getContext(), "Status updated Successfully", Toast.LENGTH_SHORT).show();
                                                            binding.progress.setVisibility(View.GONE);

                                                            // Replace fragment after upload
                                                            HomeFragment pf = new HomeFragment();
                                                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                            ft.replace(R.id.fram_layout, pf).commit();
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
                                                            Toast.makeText(getContext(), "Status updated Successfully", Toast.LENGTH_SHORT).show();
                                                            binding.progress.setVisibility(View.GONE);

                                                            // Replace fragment after upload
                                                            HomeFragment pf = new HomeFragment();
                                                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                            ft.replace(R.id.fram_layout, pf).commit();
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
                    Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progress.setVisibility(View.GONE);
                }
            });
        } else {
            // Handle case where modifiedImageUriString is null or invalid
            Toast.makeText(getContext(), "Modified Image URI is null or invalid", Toast.LENGTH_SHORT).show();
            binding.progress.setVisibility(View.GONE);
        }
    }



}