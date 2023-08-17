package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gokulsundar4545.connectwithpeople.Adapter.ChatlistAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.UserAdapter;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListFragment extends Fragment {


    ArrayList<Chatlist> userlist;
    ArrayList<User> mUser;
    RecyclerView recyclerView;
    ChatlistAdapter mAdapter;
    FirebaseAuth auth;

    ImageView back;
    SwipeRefreshLayout swaplbe;

    CircleImageView profileimage;

    DatabaseReference reference;
    FirebaseUser firebaseuser;
    FirebaseDatabase database;

    TextView name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_chat, container, false);
        database = FirebaseDatabase.getInstance();

        profileimage=view.findViewById(R.id.profile_image);
        auth=FirebaseAuth.getInstance();
        firebaseuser=FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=view.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        swaplbe=view.findViewById(R.id.swaplbe);


        back=view.findViewById(R.id.imageView7);
        back.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                HomeFragment pf = new HomeFragment();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pf).commit();
            }
        });

        name=view.findViewById(R.id.name);

        userlist=new ArrayList<>();

        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    User user = snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(profileimage);

                    name.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        swaplbe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener( ) {
            @Override
            public void onRefresh() {
                reference= FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseuser.getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                        userlist.clear();
                        for (DataSnapshot ds:snapshot.getChildren()) {
                            Chatlist chatlist = ds.getValue(Chatlist.class);
                            userlist.add(chatlist);
                        }
                        ChatListing();
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

                swaplbe.setRefreshing(false);
            }
        });
        reference= FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                userlist.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                   Chatlist chatlist = ds.getValue(Chatlist.class);
                    userlist.add(chatlist);
                }
                ChatListing();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
        return view;
    }

    private void ChatListing() {
        mUser=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                mUser.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    User users=ds.getValue(User.class);

                    for (Chatlist chatlist:userlist){

                        if (users.getUid().equals(chatlist.getId())){
                            mUser.add(users);
                        }
                    }
                }

                mAdapter=new ChatlistAdapter(getContext(),mUser,true);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onPause() {
        super.onPause( );
        Status("offline");
    }

    @Override
    public void onResume() {
        Status("Online");
        super.onResume( );
    }

    private void Status(String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        databaseReference.updateChildren(hashMap);
    }


}