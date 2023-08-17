package com.gokulsundar4545.connectwithpeople;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryHighlightsAdapter extends RecyclerView.Adapter<StoryHighlightsAdapter.ViewHolder> {
    private List<String> keysList;
    private Activity activity;
    String uid;

    public StoryHighlightsAdapter(List<String> keysList, Activity activity, String uid) {
        this.keysList = keysList;
        this.activity = activity;
        this.uid = uid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = keysList.get(position);
        holder.bind(key);

        // Assuming you have the DatabaseReference set up as described earlier
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("storieshighlites")
                .child(uid) // Replace with your user ID
                .child("userhighlites")
                .child(key); // Replace with the specific child node key

// Assuming you have a ValueEventListener to retrieve data
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Retrieve coverImage URL
                String coverImageUrl = dataSnapshot.child("coverImage").getValue(String.class);
                String covername = dataSnapshot.child("coverText").getValue(String.class);

                holder.title.setText(covername);
                // Display coverImage URL in a Toast
                if (coverImageUrl != null) {

                    Picasso.get().load(coverImageUrl).into(holder.imageview);
                } else {
                    Toast.makeText(holder.imageview.getContext(), "Cover image URL not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(holder.imageview.getContext(), "Failed to retrieve cover image URL", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return keysList.size() - 1; // Return size minus one to hide the last item
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageview;
        TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your views here if necessary
            imageview=itemView.findViewById(R.id.text_view);
            title=itemView.findViewById(R.id.title);

        }

        public void bind(String key) {
            // Handle click events for your item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create an Intent to start the new activity
                    Intent intent = new Intent(view.getContext(), StorthighlitsSetailActivity.class);

                    // Pass the key to the new activity as an extra
                    intent.putExtra("KEY_EXTRA", key);
                    intent.putExtra("uid", uid);

                    // Optionally, add scene transition animation
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();

                    // Start the new activity
                    view.getContext().startActivity(intent, bundle);
                }
            });
        }
    }
}
