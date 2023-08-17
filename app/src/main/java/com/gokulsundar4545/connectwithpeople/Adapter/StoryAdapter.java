package com.gokulsundar4545.connectwithpeople.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Model.Story;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.Model.UserStories;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.StoryDetailActivity;
import com.gokulsundar4545.connectwithpeople.databinding.StoryRvDesignBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.viewHolder> {

    ArrayList<Story> list;
    Context context;
    Activity activity;

    public StoryAdapter(ArrayList<Story> list, Context context, Activity activity) {
        this.list = list;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.story_rv_design,parent,false);
        return new viewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.viewHolder holder, int position) {
        Story story = list.get(position);

        if (story.getStories().size() > 0) {
            UserStories lastStory = story.getStories().get(story.getStories().size() - 1);

            // Load profile image using Picasso
            Picasso.get()
                    .load(lastStory.getImage())
                    .into(holder.binding.profileImage);

            // Set portions count for circular status view
            holder.binding.circularstatusview.setPortionsCount(story.getStories().size());

            // Retrieve user information from Firebase
            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(story.getStoryBy())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                holder.binding.name.setText(user.getName());

                                // Handle profile image click to show stories
                                holder.binding.profileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Prepare MyStory list
                                        ArrayList<MyStory> myStories = new ArrayList<>();
                                        for (UserStories stories : story.getStories()) {
                                            myStories.add(new MyStory(stories.getImage()));
                                        }
                                        Intent intent = new Intent(view.getContext(), StoryDetailActivity.class);
                                        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
                                        // Put the Story object and MyStory list in the intent
                                        intent.putExtra("story", story);


                                        // Start the new activity
                                        view.getContext().startActivity(intent,bundle);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled event if needed
                            Log.e("StoryAdapter", "Firebase onCancelled: " + error.getMessage());
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }




    public class viewHolder extends RecyclerView.ViewHolder{

       StoryRvDesignBinding binding;

        public viewHolder(@NonNull  View itemView) {
            super(itemView);

            binding= StoryRvDesignBinding.bind(itemView);



        }
    }
}
