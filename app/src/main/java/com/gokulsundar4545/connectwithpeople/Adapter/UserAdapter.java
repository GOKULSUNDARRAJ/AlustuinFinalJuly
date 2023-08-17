package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Model.ModelChat;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.ThereProfileActivity;
import com.gokulsundar4545.connectwithpeople.databinding.UserSampleBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> userList;
    private boolean isChat;

    private String lastMessage;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public UserAdapter(Context context, ArrayList<User> userList, boolean isChat) {
        this.context = context;
        this.userList = userList;
        this.isChat = isChat;
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        String hisUid = user.getUid();

        holder.binding.ChatLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, ThereProfileActivity.class);
            intent.putExtra("uid", hisUid);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        if (user.getStatus().equals("online")) {
            holder.binding.online.setVisibility(View.VISIBLE);
        } else {
            holder.binding.online.setVisibility(View.GONE);
        }

        if (isChat) {
            if (user.getStatus().equals("online")) {
                holder.binding.online.setVisibility(View.VISIBLE);
            } else {
                holder.binding.online.setVisibility(View.GONE);
            }
        } else {
            holder.binding.online.setVisibility(View.GONE);
        }

        if (isChat) {
            lastMessage(user.getUid(), holder.lastMsg);
        } else {
            holder.lastMsg.setVisibility(View.GONE);
        }

        Picasso.get()
                .load(user.getProfile_photo())
                .placeholder(R.drawable.profile)
                .into(holder.binding.profileImage);

        holder.binding.name.setText(user.getName());

        holder.followBtn.setOnClickListener(view -> toggleFavorite(holder.followBtn, user, position));

        // Add listener to set the favorite icon based on whether the user is in favorites
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(userId).child("youfollowing");
            userFavoritesRef.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // User is in favorites, set unfavorite icon
                        holder.followBtn.setText("following");
                        holder.followBtn.setTextColor(context.getResources().getColor(R.color.black));
                        holder.followBtn.setBackgroundResource(R.drawable.unfollow);
                    } else {
                        // User is not in favorites, set favorite icon
                        holder.followBtn.setText("follow");
                        holder.followBtn.setBackgroundResource(R.drawable.follow);
                        holder.followBtn.setTextColor(context.getResources().getColor(R.color.white));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to update favorite icon");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lastMsg;
        UserSampleBinding binding;
        TextView followBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UserSampleBinding.bind(itemView);
            lastMsg = itemView.findViewById(R.id.profession);
            followBtn = itemView.findViewById(R.id.followbtn);
        }
    }

    private void lastMessage(String friendId, TextView lastMsg) {
        lastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chat");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelChat chat = ds.getValue(ModelChat.class);

                    if (firebaseUser != null && chat != null) {
                        if (chat.getSender().equals(friendId) && chat.getReceiver().equals(firebaseUser.getUid()) ||
                                chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(friendId)) {
                            lastMessage = chat.getMessage();
                        }
                    }
                }

                switch (lastMessage) {
                    case "default":
                        lastMsg.setText("No Message");
                        break;
                    default:
                        lastMsg.setText(lastMessage);
                }

                lastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void toggleFavorite(TextView followBtn, User user, int position) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(userId).child("youfollowing");
            userFavoritesRef.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // User is already in favorites, remove them
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            childSnapshot.getRef().removeValue();
                        }
                        followBtn.setText("follow");
                        followBtn.setBackgroundResource(R.drawable.follow);
                        followBtn.setTextColor(context.getResources().getColor(R.color.white));
                        showToast("Started unfollowing");
                    } else {
                        // User is not in favorites, add them
                        String followUid = userFavoritesRef.push().getKey(); // Generate a unique key for the follow
                        userFavoritesRef.child(followUid).child("id").setValue(user.getUid());
                        followBtn.setText("following");
                        followBtn.setBackgroundResource(R.drawable.unfollow);
                        followBtn.setTextColor(context.getResources().getColor(R.color.black));
                        showToast("Started following");
                    }

                    // Notify adapter of item change for the specific position
                    notifyItemChanged(position);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to update favorites");
                }
            });

            // Also update the follow status for the other user's followers
            DatabaseReference userFollowersRef = databaseReference.child("Users").child(user.getUid()).child("yourfollowers");
            userFollowersRef.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Current user is already a follower, remove them
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            childSnapshot.getRef().removeValue();
                        }
                    } else {
                        // Current user is not a follower, add them
                        String followUid = userFollowersRef.push().getKey(); // Generate a unique key for the follow
                        userFollowersRef.child(followUid).child("id").setValue(userId);
                    }

                    // Notify adapter of item change for the specific position
                    notifyItemChanged(position);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to update followers");
                }
            });
        }
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
