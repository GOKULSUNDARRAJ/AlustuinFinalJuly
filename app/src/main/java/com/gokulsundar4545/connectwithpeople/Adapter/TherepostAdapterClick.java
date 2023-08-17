package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.ClickTherePostActivity;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.R;

import com.gokulsundar4545.connectwithpeople.databinding.PostdashboardBinding;
import com.gokulsundar4545.connectwithpeople.databinding.TherepostBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class TherepostAdapterClick extends RecyclerView.Adapter<TherepostAdapterClick.viewHolder> {

    ArrayList<Post> list;
    Context context;

    public TherepostAdapterClick(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.therepost,parent,false);
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull  viewHolder holder, int position) {

        Post model=list.get(position);

        Picasso.get()
                .load(model.getPostImg())
                .into(holder.binding.postimage);



        String uid=model.getPostedBy();




        Query query= FirebaseDatabase.getInstance( ).getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot ds:snapshot.getChildren()){
                    String name=""+ds.child("name").getValue();
                    String professional=""+ds.child("profission").getValue();
                    String profile=""+ds.child("Profile_photo").getValue();

                    holder.binding.username.setText(name);
                    holder.binding.about.setText(professional);

                    try {
                        Picasso.get()
                                .load(profile)
                                .placeholder(R.drawable.profile)
                                .into(holder.binding.profileImage);
                    }catch (Exception e){

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });








    }


    @Override
    public int getItemCount() {

        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {


        TherepostBinding binding;
        public viewHolder(@NonNull  View itemView) {
            super(itemView);

            binding= TherepostBinding.bind(itemView);


        }
    }
}
