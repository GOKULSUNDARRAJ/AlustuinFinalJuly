package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.R;

import com.gokulsundar4545.connectwithpeople.databinding.PostdashboardBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchPostAdapter extends RecyclerView.Adapter<SearchPostAdapter.viewHolder> {

    private ArrayList<Post> list;
    private Context context;

    public SearchPostAdapter(ArrayList<Post> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the item layout for each item in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.searchpostdashboard, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        // Binding data to the views
        Post model = list.get(position);

        // Load post image using Picasso library
        Picasso.get()
                .load(model.getPostImg())
                .into(holder.binding.postimage);

        // Set onClickListener for the post image
        holder.binding.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display clicked position in a Toast
                Toast.makeText(context, "Clicked position: " + position, Toast.LENGTH_SHORT).show();

                // Open ZoomActivity with post details
                Intent intent = new Intent(context, ZoomActivity.class);
                intent.putExtra("Position", position); // Pass the integer position

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the size of your dataset (list of posts)
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        // ViewBinding for each item in the RecyclerView
        PostdashboardBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PostdashboardBinding.bind(itemView);
        }
    }
}
