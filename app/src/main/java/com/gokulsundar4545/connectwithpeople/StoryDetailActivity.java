package com.gokulsundar4545.connectwithpeople;

import static com.gokulsundar4545.ClsGlobal.isDateGreaterThen24Hours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gokulsundar4545.Client;
import com.gokulsundar4545.Data;
import com.gokulsundar4545.Sender;
import com.gokulsundar4545.connectwithpeople.Fragment.BottomSheetFragmentforview;
import com.gokulsundar4545.connectwithpeople.Model.Story;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.Model.UserStories;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;
import omari.hamza.storyview.model.MyStory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private StoriesProgressView storiesProgressView;
    private ArrayList<MyStory> myStories;
    private int counter = 0;
    private TextView description;

    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference PostRef;

    de.hdodenhof.circleimageview.CircleImageView itemImage1;

    TextView title,subtitle;

    EditText reply;
    User user;
    Story story;
    ImageView send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        reply=findViewById(R.id.reply);




        send=findViewById(R.id.send);

        Auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        title=findViewById(R.id.title);
        subtitle=findViewById(R.id.subtitle1);


        itemImage1=findViewById(R.id.itemImage1);
        // Initialize views
        imageView = findViewById(R.id.imageView);
        storiesProgressView = findViewById(R.id.stories);
        LinearLayout previewLayout = findViewById(R.id.preview);
        LinearLayout nextLayout = findViewById(R.id.next);
        description = findViewById(R.id.description); // Initialize description TextView

        // Retrieve Story object from Intent
        story = getIntent().getParcelableExtra("story");

        database.getReference().child("Users").child(story.getStoryBy()).addListenerForSingleValueEvent(new ValueEventListener() {
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

        if (story.getStoryBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            reply.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
        }


        // Check if story and its stories list are valid
        if (story != null && story.getStories() != null && !story.getStories().isEmpty()) {
            // Initialize list for MyStory objects
            myStories = new ArrayList<>();

            // Build toast message with story details (optional)
            //  StringBuilder toastMessage = new StringBuilder();
//            toastMessage.append("Story Details:\n");
//            toastMessage.append("Story By: ").append(story.getStoryBy()).append("\n");
//            toastMessage.append("Story At: ").append(story.getStoryAt()).append("\n");

            // Populate myStories list with MyStory objects
            for (UserStories userStory : story.getStories()) {
                myStories.add(new MyStory(userStory.getImage()));

                // Append each story's image URL to toast message (optional)
                // toastMessage.append("Story Image URL: ").append(userStory.getImage()).append("\n");
            }

            // Display toast message with all story details (optional)
            //    Toast.makeText(this, toastMessage.toString(), Toast.LENGTH_LONG).show();

            // Set description text for the first story initially
            if (!myStories.isEmpty()) {
                description.setText(story.getStories().get(0).getStatusCaption());
            }

            // Configure StoriesProgressView
            storiesProgressView.setStoriesCount(myStories.size()); // Number of stories
            storiesProgressView.setStoryDuration(5000); // Duration for each story (ms)
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage(reply.getText().toString(),myStories.get(counter).getUrl(), story.getStoryBy(), user.getToken());

                }
            });
            addNewUserStoryToFirebase(counter,myStories.get(counter).getUrl());

            reply.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Not needed for your case

                    storiesProgressView.pause();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Not needed for your case
                    storiesProgressView.pause();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    storiesProgressView.pause();
                }
            });

            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storiesProgressView.pause();
                }
            });


            reply.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        storiesProgressView.pause();
                    }
                }
            });
            // Set listener for StoriesProgressView to handle story navigation
            storiesProgressView.setStoriesListener(new StoriesProgressView.StoriesListener() {
                @Override
                public void onNext() {
                    // Move to the next story/image
                    if (counter < myStories.size() - 1) {
                        counter++;
                        Picasso.get().load(myStories.get(counter).getUrl()).into(imageView);
                        // Update description text for the next story
                        description.setText(story.getStories().get(counter).getStatusCaption());

                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendMessage(reply.getText().toString(),myStories.get(counter).getUrl(), story.getStoryBy(), user.getToken());

                            }
                        });

                        Toast.makeText(StoryDetailActivity.this, String.valueOf(counter), Toast.LENGTH_SHORT).show();

                        addNewUserStoryToFirebase(counter,myStories.get(counter).getUrl());



                    }
                }

                @Override
                public void onPrev() {
                    // Move to the previous story/image
                    if (counter > 0) {
                        counter--;
                        Picasso.get().load(myStories.get(counter).getUrl()).into(imageView);
                        // Update description text for the previous story
                        description.setText(story.getStories().get(counter).getStatusCaption());
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendMessage(reply.getText().toString(),myStories.get(counter).getUrl(), story.getStoryBy(), user.getToken());

                            }
                        });

                        addNewUserStoryToFirebase(counter,myStories.get(counter).getUrl());
                    } else if (counter == 0) {
                        // Optionally handle when counter is already at the first story/image
                        // Example: load the first image again or do nothing
                        Picasso.get().load(myStories.get(counter).getUrl()).into(imageView);
                        // Update description text for the first story
                        description.setText(story.getStories().get(counter).getStatusCaption());
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                sendMessage(reply.getText().toString(),myStories.get(counter).getUrl(), story.getStoryBy(), user.getToken());

                            }
                        });

                        addNewUserStoryToFirebase(counter,myStories.get(counter).getUrl());
                    }
                }

                @Override
                public void onComplete() {
                    // Handle completion of all stories, if needed
                    finish();

                }
            });

            // Set onClickListener for preview LinearLayout
            previewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Trigger onPrev() when preview is clicked
                    storiesProgressView.reverse();
                }
            });

            nextLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    storiesProgressView.pause();
                    return false;
                }
            });
            // Set onClickListener for next LinearLayout
            nextLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Trigger onNext() when next is clicked
                    storiesProgressView.skip();
                }
            });



            // Start progress
            storiesProgressView.startStories();

            // Load initial image into ImageView
            if (!myStories.isEmpty()) {
                Picasso.get().load(myStories.get(0).getUrl()).into(imageView);
            }

        } else {
            // Display toast message if no story found or no stories available
            Toast.makeText(this, "No story found or no stories available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        // Stop StoriesProgressView to prevent memory leaks
        if (storiesProgressView != null) {
            storiesProgressView.destroy();
        }
        super.onDestroy();
    }

    private void sendMessage(String message, String imageUrl, String hisUid, String hisToken) {
        APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = user.getUid();

        // Combined message HashMap
        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("sender", myUid);
        messageMap.put("receiver", hisUid);
        messageMap.put("timestamp", timestamp);
        messageMap.put("isseen", false);
        messageMap.put("type", "both"); // Set type to "both"

        // Check if message is text
        if (message != null && !message.trim().isEmpty()) {
            messageMap.put("message", message);
        }

        // Check if message is image URL
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            messageMap.put("messageimage", imageUrl);
        }

        // Save message to Firebase Database
        databaseReference.child("Chat").push().setValue(messageMap)
                .addOnSuccessListener(aVoid -> {
                    updateChatList(myUid, hisUid);
                    Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("TAG", "Failed to send message: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
                });

        // Send notification via FCM
        sendNotification(apiService, myUid, hisUid, hisToken, "New Message or Image");

        // Log and handle responses if needed for FCM notifications
    }

    // Utility method to send FCM notification
    private void sendNotification(APIService apiService, String myUid, String hisUid, String hisToken, String messageContent) {
        Data data = new Data(myUid, R.drawable.logo,
                messageContent,
                "New Message or Image",
                hisUid);
        Sender sender = new Sender(data, hisToken);

        apiService.sendNotification(sender)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        Log.e("tag", "response.body().success: " + response.body());
                        Log.e("tag", "response.code(): " + response.code());
                        if (response.code() == 200) {
                            Log.e("tag", "response.body().success: " + response.body().success);
                            if (response.body().success != 1) {
                                // Handle failure scenario if needed
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Log.e("tag", "onFailure: " + t.getMessage());
                    }
                });
    }

    private void updateChatList(String myUid, String hisUid) {
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(myUid).child(hisUid);
        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef1.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "Failed to update chat list: " + error.getMessage());
            }
        });

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(hisUid).child(myUid);
        chatRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef2.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "Failed to update chat list: " + error.getMessage());
            }
        });
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void addNewUserStoryToFirebase(int currentPosition, String displaystory) {



        DatabaseReference userStoriesRef = FirebaseDatabase.getInstance().getReference()
                .child("stories")
                .child(story.getStoryBy()) // Assuming story.getStoryBy() retrieves user ID
                .child("userStories");

        userStoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                String imageUrl = null;
                String storyid = null; // Initialize storyid

                for (DataSnapshot userStorySnapshot : snapshot.getChildren()) {
                    if (count == currentPosition) {
                        // Retrieve image URL and status caption
                        String statusCaption = userStorySnapshot.child("statusCaption").getValue(String.class);
                        imageUrl = userStorySnapshot.child("image").getValue(String.class);
                        storyid = userStorySnapshot.getKey(); // Get the key (storyid) of the current snapshot

                        if (imageUrl != null && displaystory.equals(imageUrl)) {
                            // Retrieve current list of user IDs from currentUserUid
                            ArrayList<String> userIds = new ArrayList<>();
                            if (userStorySnapshot.child("currentUserUid").exists()) {
                                for (DataSnapshot idSnapshot : userStorySnapshot.child("currentUserUid").getChildren()) {
                                    userIds.add(idSnapshot.getValue(String.class));
                                }
                            }

                            // Add current user's ID to the list if not already present
                            String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            if (!userIds.contains(currentUserUid)) {
                                userIds.add(currentUserUid);
                            }

                            // Update the list back to Firebase
                            userStorySnapshot.getRef().child("currentUserUid").setValue(userIds);
                            Toast.makeText(StoryDetailActivity.this, storyid, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StoryDetailActivity.this, "Image URL not found or does not match", Toast.LENGTH_SHORT).show();
                        }

                        break; // Exit loop once the desired user story is found
                    }

                    count++;
                }

                // Outside the loop, use storyid to fetch currentUserUid
                if (storyid != null) {
                    DatabaseReference userStoriesRef = FirebaseDatabase.getInstance().getReference()
                            .child("stories")
                            .child(story.getStoryBy())
                            .child("userStories")
                            .child(storyid)
                            .child("currentUserUid");

                    userStoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<String> currentUserUids = new ArrayList<>();
                            if (snapshot.exists()) {
                                // Get the count of children under currentUserUid
                                long count = snapshot.getChildrenCount();
                                Log.d("ChildrenCount", "Count of children: " + count);
                                TextView countviewtxt=findViewById(R.id.count);

                                countviewtxt.setText(String.valueOf(count));
                                // Alternatively, you can iterate through children if needed
                                for (DataSnapshot uidSnapshot : snapshot.getChildren()) {
                                    String currentUserUid = uidSnapshot.getValue(String.class);
                                    if (currentUserUid != null) {
                                        currentUserUids.add(currentUserUid);
                                    }
                                    // Use currentUserUid as needed
                                    Log.d("CurrentUserUid", currentUserUid);
                                }
                                ImageView viewimg=findViewById(R.id.viewimg);


                                if (story.getStoryBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    Toast.makeText(StoryDetailActivity.this, "Same", Toast.LENGTH_SHORT).show();
                                    countviewtxt.setVisibility(View.VISIBLE);
                                    viewimg.setVisibility(View.VISIBLE);
                                }
                                countviewtxt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        BottomSheetFragmentforview bottomSheetFragment = BottomSheetFragmentforview.newInstance(currentUserUids);
                                        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                                        storiesProgressView.pause();

                                    }
                                });


                                viewimg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        BottomSheetFragmentforview bottomSheetFragment = BottomSheetFragmentforview.newInstance(currentUserUids);
                                        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                                        storiesProgressView.pause();
                                    }
                                });


                            } else {
                                Log.d("CurrentUserUid", "No currentUserUids found for this story ID");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Failed to retrieve currentUserUids: " + error.getMessage());
                        }
                    });
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to get user stories details: " + error.getMessage());
                Toast.makeText(StoryDetailActivity.this, "Failed to get user stories details", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
