package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private Context context;
    private List<String> imagePaths;
    private OnImageClickListener clickListener;
    private int selectedItemPosition = RecyclerView.NO_POSITION; // Initially no item selected

    public GalleryAdapter(Context context, List<String> imagePaths, OnImageClickListener clickListener) {
        this.context = context;
        this.imagePaths = imagePaths;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);
        Glide.with(context).load(imagePath).into(holder.imageView);

        // Update the visibility of the checkmark based on selection
        if (position == selectedItemPosition) {
            holder.check.setVisibility(View.VISIBLE);
        } else {
            holder.check.setVisibility(View.GONE);
        }

        // Item click listener to handle item selection
        holder.itemView.setOnClickListener(v -> {
            // Update selection
            int previousSelectedItem = selectedItemPosition;
            selectedItemPosition = position;

            // Notify changes
            notifyItemChanged(previousSelectedItem);
            notifyItemChanged(selectedItemPosition);

            // Inform listener of the selection
            if (clickListener != null) {
                clickListener.onImageClick(imagePath);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
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

    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }
}
