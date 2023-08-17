package com.gokulsundar4545.connectwithpeople;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BottomSheetFragmentforrealcom extends BottomSheetDialogFragment {

    private static final String TAG = "BottomSheetFragment";
    private EditText commentEd;
    private ImageButton commentpost;
    private DatabaseReference commentsDatabaseRef;
    private String vedioId;

    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    public BottomSheetFragmentforrealcom(String vedioId) {
        this.vedioId = vedioId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layoutforrealcom, container, false);

        recyclerViewComments = view.findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        recyclerViewComments.setAdapter(commentAdapter);

        // Fetch data from Firebase and update the RecyclerView
        fetchCommentsFromFirebase();

        // Initialize the EditText and ImageButton
        commentEd = view.findViewById(R.id.commentEd);
        commentpost = view.findViewById(R.id.commentpost);

        // Initialize Firebase Database reference
        commentsDatabaseRef = FirebaseDatabase.getInstance().getReference("vedio").child(vedioId).child("comments");

        // Set OnClickListener for the commentpost button
        commentpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentText = commentEd.getText().toString().trim();
                if (!TextUtils.isEmpty(commentText)) {
                    // Code to send the comment
                    sendComment(commentText);
                } else {
                    Toast.makeText(getActivity(), "Please type a comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void sendComment(String commentText) {
        // Create a unique key for the new comment
        String commentId = commentsDatabaseRef.push().getKey();

        // Create a comment map
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("text", commentText);
        commentMap.put("timestamp", System.currentTimeMillis());
        commentMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Add the comment to the database
        commentsDatabaseRef.child(commentId).setValue(commentMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Comment sent", Toast.LENGTH_SHORT).show();
                        // Reset the EditText
                        commentEd.setText("");
                    } else {
                        Toast.makeText(getActivity(), "Failed to send comment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCommentsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("vedio").child(vedioId).child("comments");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                        Log.d(TAG, "Comment fetched: " + comment.getText());
                    } else {
                        Log.w(TAG, "Received null comment from snapshot: " + commentSnapshot);
                    }
                }
                // Reverse the list to show the most recent comments first
                Collections.reverse(commentList);
                commentAdapter.notifyDataSetChanged();
                Log.d(TAG, "Total comments loaded: " + commentList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load comments", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load comments: " + error.getMessage());
            }
        });
    }

}
