package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StorthighlitsSetailActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private List<Highlight> highlightsList;
    private StoriesProgressView storiesProgressView;
    private ImageView imageView;
    private TextView titleTextView;
    private TextView subtitleTextView;
    private TextView descriptionTextView;
    private int currentStoryIndex = 0;
    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference PostRef;
    User user;

    de.hdodenhof.circleimageview.CircleImageView itemImage1;

    TextView title,subtitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storthighlits_setail);
        String key = getIntent().getStringExtra("KEY_EXTRA");
        String uid = getIntent().getStringExtra("uid");

        highlightsList = new ArrayList<>();
        storiesProgressView = findViewById(R.id.stories);
        imageView = findViewById(R.id.imageView);
        titleTextView = findViewById(R.id.description);
        subtitleTextView = findViewById(R.id.subtitle1);
        descriptionTextView = findViewById(R.id.description);

        Auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        title=findViewById(R.id.title);
        subtitle=findViewById(R.id.subtitle1);
        itemImage1=findViewById(R.id.itemImage1);

        database.getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    user=snapshot.getValue(User.class);

                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(itemImage1);

                    title.setText(user.getName());
                    subtitle.setText(user.getProfission());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        loadHighlights(key,uid);
    }

    private void loadHighlights(String key,String uid) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("storieshighlites")
                .child(uid)
                .child("userhighlites")
                .child(key);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0; // Counter to limit the number of items
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (count < dataSnapshot.getChildrenCount() - 2) { // Limiting to all except last 2
                        String highlightId = snapshot.getKey(); // Get highlight ID

                        // Get caption, image URL, and story timestamp
                        String caption = snapshot.child("caption").getValue(String.class);
                        String imageUrl = snapshot.child("image").getValue(String.class);
                        Long storyAtLong = snapshot.child("storyAt").getValue(Long.class);
                        long storyAt = (storyAtLong != null) ? storyAtLong : 0;

                        // Create a Highlight object
                        Highlight highlight = new Highlight(highlightId, caption, imageUrl, storyAt);

                        // Add highlight to list
                        highlightsList.add(highlight);

                        count++; // Increment count
                    }
                }

                // After retrieving all highlights, initialize StoriesProgressView and display first story
                initializeStoriesProgressView();
                displayCurrentStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
                Log.e("Firebase", "Error fetching data", databaseError.toException());
            }
        });
    }


    private void initializeStoriesProgressView() {
        storiesProgressView.setStoriesCount(highlightsList.size());
        storiesProgressView.setStoryDuration(5000); // Set a duration for each story
        storiesProgressView.setStoriesListener(this);
        storiesProgressView.startStories();
    }

    private void displayCurrentStory() {
        if (currentStoryIndex < highlightsList.size()) {
            Highlight currentHighlight = highlightsList.get(currentStoryIndex);
            Picasso.get().load(currentHighlight.getImageUrl()).into(imageView);
            titleTextView.setText(currentHighlight.getCaption());


            // Example: Setting description text
            // descriptionTextView.setText(currentHighlight.getDescription());
        }
    }

    @Override
    public void onNext() {
        if (currentStoryIndex < highlightsList.size()) {
            currentStoryIndex++;
            displayCurrentStory();
        }
    }

    @Override
    public void onPrev() {
        if (currentStoryIndex > 0) {
            currentStoryIndex--;
            displayCurrentStory();
        }
    }

    @Override
    public void onComplete() {
        // Reset or handle completion as needed
        Toast.makeText(this, "All stories viewed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }
}
