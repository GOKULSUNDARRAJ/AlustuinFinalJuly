package com.gokulsundar4545.connectwithpeople.Fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.gokulsundar4545.connectwithpeople.Adapter.CombinedAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.SearchPostAdapter;
import com.gokulsundar4545.connectwithpeople.CombinedAdapterforall;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.SearchUserActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class AllPostFragment extends Fragment {

    private RecyclerView recyclerView;
    private CombinedAdapterforall searchPostAdapter;
    private ArrayList<Post> postList; // Change to ArrayList<Post>
    private DatabaseReference databaseReference;

    ConstraintLayout search;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_post, container, false);
        recyclerView = view.findViewById(R.id.userRv);
        search=view.findViewById(R.id.bottom);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), SearchUserActivity.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(intent,b);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("posts");

        // Initialize RecyclerView and its components
        postList = new ArrayList<>(); // Initialize as ArrayList<Post>
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        searchPostAdapter = new CombinedAdapterforall(postList, requireContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(searchPostAdapter);

        // Set up ValueEventListener for fetching data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setPostId(dataSnapshot.getKey());
                        postList.add(post);

                    }
                }


                searchPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
            }
        });

        final int[] state = new int[1];
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                state[0] = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && (state[0] == 0 || state[0] == 2)) {
                    hidetoolbar();
                } else if (dy < -10 || isRecyclerViewAtTop(recyclerView)) {
                    showtoolbar();
                }
            }
        });

    }

    private void hidetoolbar() {
        search.setVisibility(View.GONE);

    }

    private void showtoolbar() {
        search.setVisibility(View.VISIBLE);

    }

    private boolean isRecyclerViewAtTop(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(-1);
    }
}
