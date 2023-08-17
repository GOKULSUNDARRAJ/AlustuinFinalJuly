package com.gokulsundar4545.connectwithpeople;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder> {
    private List<Following> followingList;
    private Context context;

    public FollowingAdapter(Context context, List<Following> followingList) {
        this.context = context;
        this.followingList = followingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_following, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Following following = followingList.get(position);



        FirebaseDatabase database;

        database=FirebaseDatabase.getInstance();

        database.getReference().child("Users").child(following.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    User user=snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(holder.profile_image);
                    holder.textViewName.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return followingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        CircleImageView profile_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name);
            profile_image=itemView.findViewById(R.id.profile_image);
        }
    }
}
