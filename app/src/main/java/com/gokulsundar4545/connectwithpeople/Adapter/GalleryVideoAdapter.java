package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.R;

import java.util.List;

public class GalleryVideoAdapter extends RecyclerView.Adapter<GalleryVideoAdapter.ViewHolder> {

    private Context context;
    private List<String> videoPaths;
    private OnVideoClickListener onVideoClickListener;

    public interface OnVideoClickListener {
        void onVideoClick(String videoUrl);
    }

    public GalleryVideoAdapter(Context context, List<String> videoPaths, OnVideoClickListener onVideoClickListener) {
        this.context = context;
        this.videoPaths = videoPaths;
        this.onVideoClickListener = onVideoClickListener;
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
        holder.thumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND));

        holder.itemView.setOnClickListener(v -> {
            if (onVideoClickListener != null) {
                onVideoClickListener.onVideoClick(videoPath);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoPaths.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.image_view);
        }
    }
}
