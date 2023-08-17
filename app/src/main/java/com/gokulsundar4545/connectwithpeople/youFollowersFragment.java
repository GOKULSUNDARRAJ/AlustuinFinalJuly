package com.gokulsundar4545.connectwithpeople;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class youFollowersFragment extends Fragment {


    private RecyclerView recyclerView;
    private FollowersAdapter adapter;
    private List<Followers> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_you_followers, container, false);
        recyclerView =view.findViewById(R.id.friendrv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        followingList = new ArrayList<>();
        adapter = new FollowersAdapter(getContext(), followingList);
        recyclerView.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userFavoritesRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(userId).child("yourfollowers");

            userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    followingList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Followers following = dataSnapshot.getValue(Followers.class);
                        followingList.add(following);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to get follow count", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }
}