package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Model.Follow;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;
public class FollowersAdaptershare extends RecyclerView.Adapter<FollowersAdaptershare.viewHolder> {

    private ArrayList<Follow> list;
    private Context context;
    private ArrayList<String> selectedUIDs;
    private ArrayList<String> selectedTokens; // Array to store selected tokens
    private OnProfileImageClickListener onProfileImageClickListener;
    private OnSelectedUIDsListener onSelectedUIDsListener;
    private HashSet<Integer> selectedPositions = new HashSet<>();
    private User user;


    public FollowersAdaptershare(ArrayList<Follow> list, Context context, OnProfileImageClickListener onProfileImageClickListener) {
        this.list = list;
        this.context = context;
        this.selectedUIDs = new ArrayList<>();
        this.selectedTokens = new ArrayList<>(); // Initialize array for tokens
        this.onProfileImageClickListener = onProfileImageClickListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.share, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Follow model = list.get(position);

        // Clear references to avoid incorrect data due to view recycling
        holder.profileImage.setImageDrawable(null);
        holder.name.setText("");

        // Load user data from Firebase
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(model.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            // Update UI with user data
                            Picasso.get()
                                    .load(user.getProfile_photo())
                                    .placeholder(R.drawable.profile)
                                    .into(holder.profileImage);
                            holder.name.setText(user.getName());

                            // Get the token associated with this position
                            String token = user.getToken();

                            // Set click listener for item view
                            holder.profileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Toggle selection
                                    if (selectedPositions.contains(position)) {
                                        selectedPositions.remove(position);
                                        holder.check.setVisibility(View.GONE);
                                        selectedUIDs.remove(model.getId());
                                        selectedTokens.remove(token);
                                    } else {
                                        selectedPositions.add(position);
                                        holder.check.setVisibility(View.VISIBLE);
                                        selectedUIDs.add(model.getId());
                                        selectedTokens.add(token);
                                    }

                                    // Notify listener with selected UIDs and tokens
                                    if (onSelectedUIDsListener != null) {
                                        onSelectedUIDsListener.onSelectedUIDs(getSelectedUIDs(), getSelectedTokens());
                                    }
                                }
                            });

                            // Highlight selected items
                            if (selectedPositions.contains(position)) {
                                holder.itemView.setBackgroundResource(R.color.black);
                            } else {
                                holder.itemView.setBackgroundResource(android.R.color.transparent);
                            }

                            // Set click listener for profile image
                            holder.itemView.setOnClickListener(v -> onProfileImageClickListener.onProfileImageClick(model.getId(), token));
                        } else {
                            Log.e("TAG", "User is null"); // Log or handle the case where user is null
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors.
                        Log.e("TAG", "Firebase Database Error: " + error.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView check;
        CircleImageView profileImage;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            profileImage = itemView.findViewById(R.id.profile_image);
            check=itemView.findViewById(R.id.check);
        }
    }

    public ArrayList<String> getSelectedUIDs() {
        return selectedUIDs;
    }

    public ArrayList<String> getSelectedTokens() {
        return selectedTokens;
    }

    public interface OnProfileImageClickListener {
        void onProfileImageClick(String followedBy, String token);
    }

    public interface OnSelectedUIDsListener {
        void onSelectedUIDs(ArrayList<String> selectedUIDs, ArrayList<String> selectedTokens);
    }

    public void setOnSelectedUIDsListener(OnSelectedUIDsListener listener) {
        this.onSelectedUIDsListener = listener;
    }

    // Method to update adapter's dataset
    public void updateList(ArrayList<Follow> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}
