package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gokulsundar4545.connectwithpeople.Adapter.UserAdapter;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    ArrayList<User> list = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    UserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        // Initialize FirebaseAuth and FirebaseDatabase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        adapter = new UserAdapter(getContext(), list, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.userRv.setLayoutManager(layoutManager);

        getAllUser(); // Call method to fetch users from Firebase

        return binding.getRoot();
    }

    private void getAllUser() {
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    User user = datasnapshot.getValue(User.class);
                    user.setUserID(datasnapshot.getKey());

                    // Exclude current user from the list
                    if (auth.getCurrentUser() != null && !datasnapshot.getKey().equals(auth.getCurrentUser().getUid())) {
                        list.add(user);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

        binding.userRv.setAdapter(adapter);
    }
}
