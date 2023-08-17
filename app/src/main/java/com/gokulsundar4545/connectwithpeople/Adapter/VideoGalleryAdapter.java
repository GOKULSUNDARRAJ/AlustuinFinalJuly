package com.gokulsundar4545.connectwithpeople.Adapter;

import static androidx.camera.core.CameraXThreads.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.R;

import java.io.File;
import java.util.List;

public class VideoGalleryAdapter extends RecyclerView.Adapter<VideoGalleryAdapter.ViewHolder> {

    private Context context;
    private List<String> videoPaths;
    private OnVideoClickListener clickListener;
    private int selectedItemPosition = RecyclerView.NO_POSITION; // Initially no item selected

    public VideoGalleryAdapter(Context context, List<String> videoPaths, OnVideoClickListener clickListener) {
        this.context = context;
        this.videoPaths = videoPaths;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String videoPath = videoPaths.get(position);

        // Load video thumbnail using Glide
        Glide.with(context)
                .load(Uri.fromFile(new File(videoPath))) // Assuming videoPath is the path to the video file
                .into(holder.imageView);

        // Item click listener to handle item selection
        holder.itemView.setOnClickListener(v -> {
            // Update selection and notify changes
            int previousSelectedItem = selectedItemPosition;
            selectedItemPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelectedItem);
            notifyItemChanged(selectedItemPosition);

            // Inform listener of the selection
            if (clickListener != null) {
                clickListener.onVideoClick(videoPath);
            }
        });

        // Update the visibility of the checkmark based on selection
        if (position == selectedItemPosition) {
            holder.check.setVisibility(View.VISIBLE);
        } else {
            holder.check.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return videoPaths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView check;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            check = itemView.findViewById(R.id.check);
        }
    }

    public interface OnVideoClickListener {
        void onVideoClick(String videoPath);
    }
}
