package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;

import com.gokulsundar4545.connectwithpeople.Adapter.UserAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.UserAdapterLike;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PostLikedByActivity extends AppCompatActivity {

    String postId;
    private RecyclerView recyclerView;

    ArrayList<User> userList;
    UserAdapterLike userAdapter;

    FirebaseDatabase database;

    SwipeRefreshLayout Refresh1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_liked_by);

        recyclerView=findViewById(R.id.friendrv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Refresh1=findViewById(R.id.swaplbe);


        userList=new ArrayList<>(  );


        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        database=FirebaseDatabase.getInstance();


        database.getReference()
                .child("posts")
                .child(postId)
                .child("likes").addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    String hisuid=""+snapshot1.getRef().getKey();

                    getUsers(hisuid);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        Refresh1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener( ) {
            @Override
            public void onRefresh() {
                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("likes").addValueEventListener(new ValueEventListener( ) {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            String hisuid=""+snapshot1.getRef().getKey();

                            getUsers(hisuid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

                Refresh1.setRefreshing(false);
            }
        });


    }

    private void getUsers(String hisuid) {
        DatabaseReference reference=FirebaseDatabase.getInstance( ).getReference("Users");
        reference.orderByChild("uid").equalTo(hisuid)
                .addValueEventListener(new ValueEventListener( ) {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            User user=ds.getValue( User.class );
                            userList.add(user);
                        }

                        userAdapter=new UserAdapterLike(getApplicationContext(),userList,false);
                        recyclerView.setAdapter(userAdapter);
                    }


                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }


}