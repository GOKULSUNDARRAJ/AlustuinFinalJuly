package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gokulsundar4545.connectwithpeople.Adapter.MypostAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.TherepostAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.TherepostAdapterClick;
import com.gokulsundar4545.connectwithpeople.Model.Follow;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ClickTherePostActivity extends AppCompatActivity {


    ArrayList<Follow> list;


    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;


    ShimmerRecyclerView dashboardRv;


    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_there_post);

        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");






        dashboardRv=findViewById(R.id.dashboardRv);

        loadHisPosts();


    }

    private void loadHisPosts() {


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        dashboardRv.setLayoutManager(linearLayoutManager);


        ArrayList<Post> dashboardlist = new ArrayList<>();

        DatabaseReference reference=FirebaseDatabase.getInstance( ).getReference("posts" );

        TherepostAdapterClick dashboardAdapter = new TherepostAdapterClick(dashboardlist,this);
        Query query=reference.orderByChild("postedBy").equalTo(uid);

        query.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dashboardlist.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Post post=ds.getValue(Post.class);
                    dashboardlist.add(post);

                    dashboardRv.setAdapter(dashboardAdapter);

                }




            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}