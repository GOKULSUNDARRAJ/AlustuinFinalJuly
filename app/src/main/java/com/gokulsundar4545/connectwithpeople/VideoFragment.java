package com.gokulsundar4545.connectwithpeople;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Adapter.VedioCropActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class VideoFragment extends Fragment implements VideoAdapter.OnVideoClickListener {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final String TAG = "VideoFragment";

    private TextView btnDone;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<File> videoFiles;
    private File selectedVideoFile;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        recyclerView = view.findViewById(R.id.friendrv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setNestedScrollingEnabled(true);
        btnDone = view.findViewById(R.id.down); // Assuming you have a "Done" button

        // Check and request permission if needed
        checkStoragePermission();
        btnDone.setOnClickListener(v -> handleDoneButtonClick());
        return view;
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
        } else {
            // Permission already granted
            displayVideoFiles();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                displayVideoFiles();
            } else {
                // Permission denied
                Toast.makeText(getContext(), "Permission denied to access external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO && resultCode == getActivity().RESULT_OK && data != null) {
            Uri videoUri = data.getData();
            if (videoUri != null) {
                saveVideoToInternalStorage(videoUri);
                displayVideoFiles(); // Refresh the list
            }
        }
    }

    private void displayVideoFiles() {
        videoFiles = getVideoFiles();
        videoAdapter = new VideoAdapter(getContext(), videoFiles, this);
        recyclerView.setAdapter(videoAdapter);

    }

    private List<File> getVideoFiles() {
        List<File> videoFiles = new ArrayList<>();
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (downloadsDir.exists() && downloadsDir.isDirectory()) {
            File[] files = downloadsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".mp4")) {
                        // Check video duration
                        long duration = getVideoDuration(file);
                        if (duration <= 25000) { // 15 seconds in milliseconds
                            videoFiles.add(file);
                        }
                    }
                }
            }
        } else {
            Log.e(TAG, "Downloads directory does not exist or is not a directory");
            Toast.makeText(getContext(), "Downloads directory does not exist or is not a directory", Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "Video files found: " + videoFiles.size());
        return videoFiles;
    }

    private long getVideoDuration(File videoFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        long duration = -1;
        try {
            retriever.setDataSource(videoFile.getAbsolutePath());
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (durationStr != null) {
                duration = Long.parseLong(durationStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return duration;
    }


    private void saveVideoToInternalStorage(Uri videoUri) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(videoUri);
            File videoDir = new File(getContext().getFilesDir(), "videos");
            if (!videoDir.exists()) {
                videoDir.mkdirs(); // Create directory if it doesn't exist
            }
            File videoFile = new File(videoDir, "video_" + System.currentTimeMillis() + ".mp4");
            FileOutputStream outputStream = new FileOutputStream(videoFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            Toast.makeText(getContext(), "Video saved to internal storage", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save video", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onVideoClick(File videoPath) {
        // Handle the video path here
        Toast.makeText(getContext(), "Clicked video path: " + videoPath, Toast.LENGTH_SHORT).show();
        btnDone.setVisibility(View.VISIBLE);
        selectedVideoFile = videoPath; // Store selected video file
    }

    private void handleDoneButtonClick() {
        if (selectedVideoFile != null) {
            // Convert File to Uri using FileProvider
            Uri videoUri = FileProvider.getUriForFile(
                    getContext(),
                    "com.gokulsundar4545.connectwithpeople.fileprovider", // Replace with your provider authority
                    selectedVideoFile
            );

            // Start VedioCropActivity and pass selected video file Uri
            Intent intent = new Intent(getActivity(), VedioCropActivity.class);
            intent.putExtra("vidiourl", selectedVideoFile.getPath());
            intent.putExtra("vidiourl1", videoUri.toString());
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Please select a video", Toast.LENGTH_SHORT).show();
        }
    }
}