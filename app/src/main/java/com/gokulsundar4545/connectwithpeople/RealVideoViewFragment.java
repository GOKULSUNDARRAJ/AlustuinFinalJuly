package com.gokulsundar4545.connectwithpeople;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gokulsundar4545.connectwithpeople.Model.VedioMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RealVideoViewFragment extends Fragment {

    private ViewPager2 viewPager2;
    private RealVideoAdapter realVideoAdapter;
    private List<VedioMode> videoList = new ArrayList<>();
    private DatabaseReference videoDatabaseRef;

    public RealVideoViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Database reference
        videoDatabaseRef = FirebaseDatabase.getInstance().getReference().child("vedio");
        fetchVideoData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vedio_view, container, false);
        viewPager2 = view.findViewById(R.id.view_pager_2);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("switchPreferences", Context.MODE_PRIVATE);
        realVideoAdapter =new RealVideoAdapter(getContext(),videoList,viewPager2,sharedPreferences);
        viewPager2.setAdapter(realVideoAdapter);
        return view;
    }

    private void fetchVideoData() {
        videoDatabaseRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Log.d("RealVideoAdapter", "Raw Data: " + postSnapshot.getKey());
                    VedioMode video = postSnapshot.getValue(VedioMode.class);
                    if (video != null) {
                        video.setVedioId(postSnapshot.getKey()); // Set the push ID
                        Log.d("RealVideoAdapter", "Video ID: " + video.getVedioId());
                        Log.d("RealVideoAdapter", "Push ID: " + video.getVedioId()); // Log the push ID
                    } else {
                        Log.d("RealVideoAdapter", "Video is null");
                    }
                    videoList.add(video);
                }
                if (realVideoAdapter != null) {
                    realVideoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                // Handle possible errors.
            }
        });
    }

}
