package com.gokulsundar4545.connectwithpeople;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
public class UserStoriesAdapter extends RecyclerView.Adapter<UserStoriesAdapter.UserStoryViewHolder> {
    private List<UserStory> userStories;
    private Set<Integer> selectedItems = new HashSet<>();

    public UserStoriesAdapter(List<UserStory> userStories) {
        this.userStories = userStories;
    }

    @NonNull
    @Override
    public UserStoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_story, parent, false);
        return new UserStoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserStoryViewHolder holder, int position) {
        UserStory userStory = userStories.get(position);
        if (userStory != null) {
            holder.bind(userStory, position);
        }
    }

    @Override
    public int getItemCount() {
        return userStories.size();
    }

    public List<UserStory> getSelectedStories() {
        List<UserStory> selectedStories = new ArrayList<>();
        for (Integer position : selectedItems) {
            selectedStories.add(userStories.get(position));
        }
        return selectedStories;
    }

    public class UserStoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageStory;
        TextView caption;
        ImageView overlay;

        public UserStoryViewHolder(View itemView) {
            super(itemView);
            imageStory = itemView.findViewById(R.id.imagestory);
            caption = itemView.findViewById(R.id.caption);
            overlay = itemView.findViewById(R.id.secelctiamge);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (selectedItems.contains(position)) {
                        selectedItems.remove(position);
                    } else {
                        selectedItems.add(position);
                    }
                    notifyItemChanged(position);
                }
            });
        }

        public void bind(UserStory userStory, int position) {
            if (userStory.getImage() != null && !userStory.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(userStory.getImage())
                        .into(imageStory);
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.placeholder)
                        .into(imageStory);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd \nMMMM", Locale.getDefault());
            String formattedDate = sdf.format(new Date(userStory.getStoryAt()));
            caption.setText(formattedDate);

            overlay.setVisibility(selectedItems.contains(position) ? View.VISIBLE : View.GONE);
        }
    }
}
