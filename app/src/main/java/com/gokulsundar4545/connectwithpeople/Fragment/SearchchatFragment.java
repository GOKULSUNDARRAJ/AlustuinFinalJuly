package com.gokulsundar4545.connectwithpeople.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gokulsundar4545.connectwithpeople.Adapter.ChatlistAdapter2;
import com.gokulsundar4545.connectwithpeople.Model.Chatlist;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchchatFragment extends Fragment {

    ArrayList<Chatlist> userlist;
    ArrayList<User> mUser, filteredUsers;
    RecyclerView recyclerView;
    ChatlistAdapter2 mAdapter;
    FirebaseAuth auth;
    ImageView back;
    SwipeRefreshLayout swaplbe;
    CircleImageView profileimage;
    DatabaseReference reference;
    FirebaseUser firebaseuser;
    FirebaseDatabase database;
    ImageView imageView9;
    EditText searchEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchchat, container, false);
        database = FirebaseDatabase.getInstance();
        profileimage = view.findViewById(R.id.profile_image);
        auth = FirebaseAuth.getInstance();
        firebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swaplbe = view.findViewById(R.id.swaplbe);
        imageView9 = view.findViewById(R.id.imageView9);
        searchEditText = view.findViewById(R.id.name);

        imageView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatListFragment pf = new ChatListFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pf).commit();
            }
        });

        back = view.findViewById(R.id.imageView7);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment pf = new HomeFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pf).commit();
            }
        });

        userlist = new ArrayList<>();
        filteredUsers = new ArrayList<>();

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(profileimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        swaplbe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadChatList();
                swaplbe.setRefreshing(false);
            }
        });

        loadChatList();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void loadChatList() {
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userlist.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chatlist chatlist = ds.getValue(Chatlist.class);
                    userlist.add(chatlist);
                }
                ChatListing();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void ChatListing() {
        mUser = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    for (Chatlist chatlist : userlist) {
                        if (user.getUid().equals(chatlist.getId())) {
                            mUser.add(user);
                            break;
                        }
                    }
                }

                Collections.sort(mUser, new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        Long time1 = Long.parseLong(u1.getLasttime());
                        Long time2 = Long.parseLong(u2.getLasttime());
                        return time2.compareTo(time1);
                    }
                });

                filteredUsers.addAll(mUser);  // Initially, filtered list contains all users
                mAdapter = new ChatlistAdapter2(getContext(), filteredUsers, true);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void filter(String text) {
        filteredUsers.clear();
        for (User user : mUser) {
            if (user.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredUsers.add(user);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        Status("offline");
    }

    @Override
    public void onResume() {
        Status("Online");
        super.onResume();
    }

    private void Status(String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        databaseReference.updateChildren(hashMap);
    }
}
