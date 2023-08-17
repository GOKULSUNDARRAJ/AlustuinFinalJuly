package com.gokulsundar4545.connectwithpeople.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Adapter.GalleryUtilsvedio;
import com.gokulsundar4545.connectwithpeople.Adapter.GalleryVideoAdapter;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.VedioEditactivity;

import java.util.List;

public class GalleryFragmentVedio extends Fragment implements GalleryVideoAdapter.OnVideoClickListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_VIDEO_CAPTURE = 200;

    private RecyclerView recyclerView;
    private GalleryVideoAdapter videoAdapter;
    private String selectedVideoUri; // To store selected video URI

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_video, container, false);
        recyclerView = view.findViewById(R.id.friendrv);


        // Check and request READ_EXTERNAL_STORAGE permission if not granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            loadGallery(); // Permission already granted, load the gallery
        }


        ImageView down=view.findViewById(R.id.down);

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleVideoSelection();
            }
        });

        return view;
    }

    private void loadGallery() {
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Get list of all video paths
        List<String> videoPaths = GalleryUtilsvedio.getAllVideos(requireContext());
        videoAdapter = new GalleryVideoAdapter(requireContext(), videoPaths, this); // Pass 'this' as the listener
        recyclerView.setAdapter(videoAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadGallery(); // Permission granted, load the gallery
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onVideoClick(String videoUri) {
        selectedVideoUri = videoUri; // Store selected video URI

    }

    private void captureVideo() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Set extra for maximum video duration (in seconds)
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        } else {
            Toast.makeText(requireContext(), "No app to handle video capture", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == requireActivity().RESULT_OK) {
            // Get the video URI
            if (data != null && data.getData() != null) {
                selectedVideoUri = data.getData().toString();

            } else {
                Toast.makeText(requireContext(), "Failed to capture video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleVideoSelection() {
        if (selectedVideoUri != null) {
            // Start NewActivity and pass selected video URI
            Intent intent = new Intent(requireActivity(), VedioEditactivity.class);
            intent.putExtra("video_uri", selectedVideoUri);
            Toast.makeText(getContext(), selectedVideoUri, Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Please select a video", Toast.LENGTH_SHORT).show();
        }
    }
}
