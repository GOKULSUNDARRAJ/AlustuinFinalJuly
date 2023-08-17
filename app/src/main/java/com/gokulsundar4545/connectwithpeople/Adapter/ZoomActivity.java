package com.gokulsundar4545.connectwithpeople.Adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.databinding.ActivityZoomBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ZoomActivity extends AppCompatActivity {

    private PostAdapter2 searchPostAdapter;
    private ArrayList<Post> postList;
    private DatabaseReference databaseReference;
    ActivityZoomBinding binding;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityZoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setStatusBarColor(getResources().getColor(android.R.color.white));

        Intent intent = getIntent();
        position = intent.getIntExtra("Position", -1);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");

        // Initialize RecyclerView and its components
        postList = new ArrayList<>();
        searchPostAdapter = new PostAdapter2(postList, ZoomActivity.this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ZoomActivity.this);
        binding.userRv.setLayoutManager(layoutManager);
        binding.userRv.setNestedScrollingEnabled(false);
        binding.userRv.setAdapter(searchPostAdapter);

        // Set up ValueEventListener for fetching data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setPostId(dataSnapshot.getKey());
                        postList.add(post);
                    }
                }
                Collections.reverse(postList); // Reverse the list
                searchPostAdapter.notifyDataSetChanged();

                // Scroll to the specified position in the reversed list
                if (postList.size() > 0 && position != -1) {
                    layoutManager.scrollToPosition(postList.size() - 1 - position);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
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
