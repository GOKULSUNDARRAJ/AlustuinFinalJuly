package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gokulsundar4545.AddHighlitescover;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AddHighlitesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference storiesRef;
    private List<UserStory> userStories;
    private UserStoriesAdapter adapter;
    private TextView btnSendSelected;

    String covername,coverImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_highlites);
        setStatusBarColor(getResources().getColor(android.R.color.white));
        recyclerView = findViewById(R.id.recycler_view_user_stories);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));



        Intent intent = getIntent();
        covername = intent.getStringExtra("covername");
        coverImage= intent.getStringExtra("coverImage");
        btnSendSelected = findViewById(R.id.login); // Ensure you have this button in your layout

        // Initialize Firebase Database reference
        storiesRef = FirebaseDatabase.getInstance().getReference()
                .child("stories2")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userStories2");

        userStories = new ArrayList<>();
        adapter = new UserStoriesAdapter(userStories);
        recyclerView.setAdapter(adapter);

        // Retrieve data from Firebase
        retrieveStoriesFromFirebase();

        btnSendSelected.setOnClickListener(v -> {
            List<UserStory> selectedStories = adapter.getSelectedStories();
            DatabaseReference userHighlightsRef = FirebaseDatabase.getInstance().getReference("storieshighlites")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("userhighlites");

            // Using AtomicInteger for mutable integer reference
            AtomicInteger nextChildIndex = new AtomicInteger(0);

            userHighlightsRef.child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long currentCount = dataSnapshot.exists() ? (long) dataSnapshot.getValue() : 0;

                    nextChildIndex.set((int) currentCount + 1);

                    // Upload coverImage to Firebase Storage
                    Uri fileUri = Uri.parse(coverImage); // Assuming coverImage is a valid URL
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                            .child("coverImages")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("coverImage_" + System.currentTimeMillis()); // Generate unique filename
                    UploadTask uploadTask = storageRef.putFile(fileUri);

                    // Handle upload success/failure
                    uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return storageRef.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            // Store coverImage URL and other data in Firebase Database
                            Map<String, Object> coverImageValue = new HashMap<>();
                            coverImageValue.put("coverImage", downloadUri.toString());

                            userHighlightsRef.child(String.valueOf(nextChildIndex.get())).child("coverText").setValue(covername);
                            userHighlightsRef.child(String.valueOf(nextChildIndex.get())).child("coverImage").setValue(downloadUri.toString());

                            // Store stories
                            for (UserStory story : selectedStories) {
                                String storyKey = userHighlightsRef.child(String.valueOf(nextChildIndex.get())).push().getKey();
                                Map<String, Object> storyValues = new HashMap<>();
                                storyValues.put("image", story.getImage());
                                storyValues.put("caption", story.getStatusCaption());
                                storyValues.put("storyAt", story.getStoryAt());
                                userHighlightsRef.child(String.valueOf(nextChildIndex.get())).child(storyKey).setValue(storyValues);
                            }

                            // Update the count in Firebase after storing all stories
                            userHighlightsRef.child("count").setValue(nextChildIndex.get());

                            // Navigate to MainActivity
                            startActivity(new Intent(AddHighlitesActivity.this, MainActivity.class));
                        } else {
                            // Handle failures
                            Toast.makeText(getApplicationContext(), "Failed to upload cover image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors here
                    Toast.makeText(getApplicationContext(), "Failed to send stories: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });



    }

    private void retrieveStoriesFromFirebase() {
        storiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userStories.clear(); // Clear existing data

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserStory story = snapshot.getValue(UserStory.class);
                    if (story != null) {
                        userStories.add(story);
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddHighlitesActivity.this,
                        "Failed to retrieve stories: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(color);
        }
    }
}
