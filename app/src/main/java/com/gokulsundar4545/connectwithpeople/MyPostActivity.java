package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gokulsundar4545.connectwithpeople.Adapter.PostAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.PostAdaptrMy;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class MyPostActivity extends AppCompatActivity {

    SwipeRefreshLayout Refresh;
    ShimmerRecyclerView dashboardRv;
    FirebaseDatabase database;
    ArrayList<Post> dashboardlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        Refresh=findViewById(R.id.refresh);


        database = FirebaseDatabase.getInstance();
        dashboardRv = findViewById(R.id.dashboardRv);
        dashboardlist = new ArrayList<>();


        PostAdaptrMy dashboardAdapter = new PostAdaptrMy(dashboardlist, this);
        LinearLayoutManager layoutManager11 = new LinearLayoutManager(this);
        layoutManager11.setReverseLayout(true);
        layoutManager11.setStackFromEnd(true);
        dashboardRv.setLayoutManager(layoutManager11);
        dashboardRv.setNestedScrollingEnabled(false);

        FirebaseUser firebaseUser= FirebaseAuth.getInstance( ).getCurrentUser( );

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dashboardlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());

                    if (firebaseUser.getUid().equals(post.getPostedBy())){
                        dashboardlist.add(post);
                    }

                }
                dashboardRv.setAdapter(dashboardAdapter);
                dashboardRv.hideShimmerAdapter();
                dashboardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener( ) {
            @Override
            public void onRefresh() {
                database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dashboardlist.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            post.setPostId(dataSnapshot.getKey());

                            if (firebaseUser.getUid().equals(post.getPostedBy())){
                                dashboardlist.add(post);
                            }

                        }
                        dashboardRv.setAdapter(dashboardAdapter);
                        dashboardRv.hideShimmerAdapter();
                        dashboardAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Refresh.setRefreshing(false);
            }
        });
    }
}