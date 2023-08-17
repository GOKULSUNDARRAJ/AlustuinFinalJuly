package com.gokulsundar4545.connectwithpeople;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Contact> contacts;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
    private FirebaseDatabase database;

    public ContactsAdapter(List<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
        initFirebase();
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        database.getReference().child("Users").child(contact.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getProfile_photo())
                            .placeholder(R.drawable.placeholder).into(holder.profile);
                    holder.username.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Failed to load user data");
            }
        });

        holder.followBtn.setOnClickListener(view -> toggleFavorite(holder.followBtn, contact, position));

        updateFollowButtonState(holder, contact);

        holder.Product.setOnClickListener(view -> {
            Intent intent = new Intent(context, ThereProfileActivity.class);
            intent.putExtra("uid", contact.getUid());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    private void updateFollowButtonState(ContactViewHolder holder, Contact contact) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(userId).child("youfollowing");
            userFavoritesRef.orderByChild("id").equalTo(contact.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        holder.followBtn.setText("following");
                        holder.followBtn.setTextColor(context.getResources().getColor(R.color.black));
                        holder.followBtn.setBackgroundResource(R.drawable.unfollow);
                    } else {
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
        return contacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView username, followBtn;
        ConstraintLayout Product;

        ContactViewHolder(View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.pro);
            username = itemView.findViewById(R.id.username);
            followBtn = itemView.findViewById(R.id.follow);
            Product=itemView.findViewById(R.id.product);
        }
    }

    private void toggleFavorite(TextView followBtn, Contact contact, int position) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(userId).child("youfollowing");
            userFavoritesRef.orderByChild("id").equalTo(contact.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            childSnapshot.getRef().removeValue();
                        }
                        followBtn.setText("follow");
                        followBtn.setBackgroundResource(R.drawable.follow);
                        followBtn.setTextColor(context.getResources().getColor(R.color.white));
                        showToast("Started unfollowing");
                    } else {
                        String followUid = userFavoritesRef.push().getKey();
                        userFavoritesRef.child(followUid).child("id").setValue(contact.getUid());
                        followBtn.setText("following");
                        followBtn.setBackgroundResource(R.drawable.unfollow);
                        followBtn.setTextColor(context.getResources().getColor(R.color.black));
                        showToast("Started following");
                    }

                    notifyItemChanged(position);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to update favorites");
                }
            });

            DatabaseReference userFollowersRef = databaseReference.child("Users").child(contact.getUid()).child("yourfollowers");
            userFollowersRef.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            childSnapshot.getRef().removeValue();
                        }
                    } else {
                        String followUid = userFollowersRef.push().getKey();
                        userFollowersRef.child(followUid).child("id").setValue(userId);
                    }

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
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
