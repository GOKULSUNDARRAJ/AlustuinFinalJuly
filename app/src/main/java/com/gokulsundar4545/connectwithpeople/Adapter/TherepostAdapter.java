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
import com.gokulsundar4545.connectwithpeople.MyPostActivity;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.databinding.PostdashboardBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TherepostAdapter extends RecyclerView.Adapter<TherepostAdapter.viewHolder> {

    ArrayList<Post> list;
    Context context;

    public TherepostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.postdashboard,parent,false);
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull  viewHolder holder, int position) {

        Post model=list.get(position);

        Picasso.get()
                .load(model.getPostImg())
                .into(holder.binding.postimage);




        holder.binding.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, ClickTherePostActivity.class);
                intent.putExtra("uid",model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });









    }


    @Override
    public int getItemCount() {

        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {


        PostdashboardBinding binding;
        public viewHolder(@NonNull  View itemView) {
            super(itemView);

            binding= PostdashboardBinding.bind(itemView);


        }
    }
}
