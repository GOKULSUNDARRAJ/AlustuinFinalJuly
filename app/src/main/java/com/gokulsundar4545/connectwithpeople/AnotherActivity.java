package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class AnotherActivity extends AppCompatActivity {

    private List<UserStory> selectedStories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);


        selectedStories = getIntent().getParcelableArrayListExtra("selectedStories");

        // Create a message to display in the Toast
        StringBuilder message = new StringBuilder("Selected Stories:\n");
        for (UserStory story : selectedStories) {
            message.append("Image: ").append(story.getImage()).append("\n");
            message.append("Caption: ").append(story.getStatusCaption()).append("\n");
            message.append("Story At: ").append(story.getStoryAt()).append("\n\n");
        }

        // Show the Toast message
        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show();

    }
}