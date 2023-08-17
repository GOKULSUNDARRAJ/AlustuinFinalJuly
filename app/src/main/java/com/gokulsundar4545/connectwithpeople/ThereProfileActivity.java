package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gokulsundar4545.connectwithpeople.Adapter.MypostAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.TherepostAdapter;
import com.gokulsundar4545.connectwithpeople.Model.Follow;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.databinding.ActivityComment2Binding;
import com.gokulsundar4545.connectwithpeople.databinding.ActivityThereProfileBinding;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ThereProfileActivity extends AppCompatActivity {


    ArrayList<Follow> list;

    ActivityThereProfileBinding binding;

    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;


    ShimmerRecyclerView dashboardRv;

    DatabaseReference PostRef;

    String uid;
    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityThereProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");



        PostRef=FirebaseDatabase.getInstance().getReference().child("posts");
        PostRef.orderByChild("postedBy").equalTo(uid).addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    count=(int) snapshot.getChildrenCount();

                    binding.textView18.setText(Integer.toString(count)+"");
                }else {
                    binding.textView18.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        Query query=FirebaseDatabase.getInstance( ).getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot ds:snapshot.getChildren()){
                    String name=""+ds.child("name").getValue();
                    String professional=""+ds.child("profission").getValue();
                    String profile=""+ds.child("Profile_photo").getValue();
                    String followcount=""+ds.child("followerCount").getValue(  );

                    binding.textView3.setText(name);
                    binding.username.setText(name);
                    binding.profession.setText("@"+professional);
                    binding.textView16.setText(followcount);

                    try {
                        Picasso.get()
                                .load(profile)
                                .placeholder(R.drawable.profile)
                                .into(binding.pro);
                    }catch (Exception e){

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        dashboardRv=findViewById(R.id.dashboardRv);

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener( ) {
            @Override
            public void onRefresh() {
                loadHisPosts();
                binding.refresh.setRefreshing(false);
            }
        });

        loadHisPosts();


    }

    private void loadHisPosts() {


        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
        dashboardRv.setLayoutManager(gridLayoutManager);


        ArrayList<Post> dashboardlist = new ArrayList<>();

        DatabaseReference reference=FirebaseDatabase.getInstance( ).getReference("posts" );

        TherepostAdapter dashboardAdapter = new TherepostAdapter(dashboardlist,this);
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