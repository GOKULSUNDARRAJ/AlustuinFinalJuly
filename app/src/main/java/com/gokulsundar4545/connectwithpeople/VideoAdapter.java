package com.gokulsundar4545.connectwithpeople;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<File> videoFiles;
    private Context context;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private OnVideoClickListener onVideoClickListener;
    private int selectedItemPosition = RecyclerView.NO_POSITION; // Initially no item selected

    public VideoAdapter(Context context, List<File> videoFiles, OnVideoClickListener listener) {
        this.context = context.getApplicationContext();
        this.videoFiles = videoFiles;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("videos");
        this.storageReference = FirebaseStorage.getInstance().getReference("videos");
        this.onVideoClickListener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        File videoFile = videoFiles.get(position);
        // Update the visibility of the checkmark based on selection
        if (position == selectedItemPosition) {
            holder.check.setVisibility(View.VISIBLE);
        } else {
            holder.check.setVisibility(View.GONE);
        }
        // Set thumbnail for the video
        Bitmap thumbnail = getVideoThumbnail(videoFile.getPath());
        if (thumbnail != null) {
            holder.videoThumbnail.setImageBitmap(thumbnail);
        } else {
            holder.videoThumbnail.setImageResource(R.drawable.ic_video_placeholder); // Placeholder image
        }

        holder.itemView.setOnClickListener(v -> {

            int previousSelectedItem = selectedItemPosition;
            selectedItemPosition = position;

            // Notify changes
            notifyItemChanged(previousSelectedItem);
            notifyItemChanged(selectedItemPosition);


            if (onVideoClickListener != null) {
                onVideoClickListener.onVideoClick(videoFile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        ImageView videoThumbnail;
        ImageView check;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.image_view);
            check = itemView.findViewById(R.id.check);
        }
    }

    private Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(videoPath);
            return retriever.getFrameAtTime(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            retriever.release();
        }
    }

    public interface OnVideoClickListener {
        void onVideoClick(File videoPath);
    }
}
