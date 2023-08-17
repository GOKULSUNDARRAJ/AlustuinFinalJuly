package com.gokulsundar4545.connectwithpeople.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Adapter.VedioCropActivity;
import com.gokulsundar4545.connectwithpeople.Adapter.VideoGalleryAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.VideoUtils;
import com.gokulsundar4545.connectwithpeople.R;

import java.util.List;

public class VideoGalleryFragment extends Fragment implements VideoGalleryAdapter.OnVideoClickListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private String selectedVideoPath;
    TextView btnDone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_gallery, container, false);
        recyclerView = view.findViewById(R.id.friendrv);
        btnDone = view.findViewById(R.id.down);
        btnDone.setOnClickListener(v -> handleDoneButtonClick());

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            loadGallery();
        }
        return view;
    }

    private void loadGallery() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<String> videoPaths = VideoUtils.getAllVideos(getContext());
        VideoGalleryAdapter videoAdapter = new VideoGalleryAdapter(getContext(), videoPaths, this);
        recyclerView.setAdapter(videoAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadGallery();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onVideoClick(String videoPath) {
        selectedVideoPath = videoPath;
        btnDone.setVisibility(View.VISIBLE);
    }

    private void handleDoneButtonClick() {
        if (selectedVideoPath != null) {
            saveVideoPathToSharedPreferences(selectedVideoPath);
            Intent intent = new Intent(getActivity(), VedioCropActivity.class);
            intent.putExtra("video_path", selectedVideoPath);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Please select a video", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveVideoPathToSharedPreferences(String videoPath) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("VideoPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("video_path", videoPath);
        editor.apply();
        Toast.makeText(getContext(), "Video path saved", Toast.LENGTH_SHORT).show();
    }
}
