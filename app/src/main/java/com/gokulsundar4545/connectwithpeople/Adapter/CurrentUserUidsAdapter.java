package com.gokulsundar4545.connectwithpeople.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentUserUidsAdapter extends RecyclerView.Adapter<CurrentUserUidsAdapter.ViewHolder> {

    private List<String> currentUserUids;

    public CurrentUserUidsAdapter(List<String> currentUserUids) {
        this.currentUserUids = currentUserUids;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_uid, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String uid = currentUserUids.get(position);
        holder.bind(uid);
    }

    @Override
    public int getItemCount() {
        return currentUserUids.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FirebaseAuth auth;
        FirebaseDatabase database;
        private TextView textViewUid;

        CircleImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUid = itemView.findViewById(R.id.name);
            auth = FirebaseAuth.getInstance();
            database = FirebaseDatabase.getInstance();
            profile_image=itemView.findViewById(R.id.profile_image);
        }

        public void bind(String uid) {


            database.getReference().child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        User user = snapshot.getValue(User.class);


                        textViewUid.setText(user.getName());

                        Picasso.get()
                                .load(user.getProfile_photo())
                                .placeholder(R.drawable.profile)
                                .into(profile_image);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
