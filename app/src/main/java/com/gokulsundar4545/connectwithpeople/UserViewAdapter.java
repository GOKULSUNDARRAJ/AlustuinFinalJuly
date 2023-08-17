package com.gokulsundar4545.connectwithpeople;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.ViewHolder> {

    private List<UserUidModel> userUidList;

    public UserViewAdapter(List<UserUidModel> userUidList) {
        this.userUidList = userUidList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_uid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserUidModel userUid = userUidList.get(position);
        holder.bind(userUid);
    }

    @Override
    public int getItemCount() {
        return userUidList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView currentUserUidTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            currentUserUidTextView = itemView.findViewById(R.id.name); // Replace with your TextView id
        }

        public void bind(UserUidModel userUid) {
            currentUserUidTextView.setText(userUid.getCurrentUserUid());
        }
    }
}
