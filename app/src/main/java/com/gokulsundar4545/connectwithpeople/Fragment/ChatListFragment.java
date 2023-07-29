package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {


    ArrayList<Chatlist> userlist;
    ArrayList<User> mUser;
    RecyclerView recyclerView;
    UserAdapter mAdapter;
    FirebaseAuth auth;



    DatabaseReference reference;
    FirebaseUser firebaseuser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_chat, container, false);

        auth=FirebaseAuth.getInstance();
        firebaseuser=FirebaseAuth.getInstance().getCurrentUser();
        recyclerView=view.findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userlist=new ArrayList<>();

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

                mAdapter=new UserAdapter(getContext(),mUser,true);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }



}