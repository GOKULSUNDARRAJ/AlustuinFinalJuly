package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
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

import com.gokulsundar4545.connectwithpeople.databinding.FriendRvSampleBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.viewHolder> {

    ArrayList<Follow> list;
    Context context;

    public FollowersAdapter(ArrayList<Follow> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.friend_rv_sample,parent,false);



        return new viewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull  FollowersAdapter.viewHolder holder, int position) {



        Follow model=list.get(position);


        holder.binding.camera.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Camera", Toast.LENGTH_SHORT).show( );
            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        FriendRvSampleBinding binding;
        public viewHolder(@NonNull  View itemView) {
            super(itemView);

            binding =FriendRvSampleBinding.bind(itemView);
        }
    }
}
