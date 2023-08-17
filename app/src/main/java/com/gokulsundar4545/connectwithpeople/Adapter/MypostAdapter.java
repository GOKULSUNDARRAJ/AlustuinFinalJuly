package com.gokulsundar4545.connectwithpeople.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.CommentActivity;
import com.gokulsundar4545.connectwithpeople.Model.Notification;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.MyPostActivity;
import com.gokulsundar4545.connectwithpeople.R;


import com.gokulsundar4545.connectwithpeople.databinding.PostdashboardBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MypostAdapter extends RecyclerView.Adapter<MypostAdapter.viewHolder> {

    ArrayList<Post> list;
    Context context;

    public MypostAdapter(ArrayList<Post> list, Context context) {
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

                Intent intent=new Intent(context, MyPostActivity.class);
                intent.putExtra("postId",model.getPostId());
                intent.putExtra("postedBy",model.getPostedBy());
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
