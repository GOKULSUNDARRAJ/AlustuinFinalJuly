package com.gokulsundar4545.connectwithpeople;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;

public class
MainActivity4 extends AppCompatActivity {

    private static final int REQUEST_VIDEO_PERMISSIONS = 1;
    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TextureView textureView;
    private Button btnRecord;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        textureView = findViewById(R.id.textureView);
        btnRecord = findViewById(R.id.btnRecord);

        // Request necessary permissions
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, VIDEO_PERMISSIONS, REQUEST_VIDEO_PERMISSIONS);
        }

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });
    }

    private boolean hasPermissions() {
        for (String permission : VIDEO_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        // Set up MediaRecorder with required configurations
        setupMediaRecorder();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            btnRecord.setText("Stop Recording");
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            btnRecord.setText("Record Video");
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupMediaRecorder() {
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setVideoEncodingBitRate(10000000);
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoSize(640, 480);

        // Set audio source and encoder
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);  // Or use MIC if needed
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);    // Adjust as per your requirements

        mediaRecorder.setOutputFile(getVideoFilePath());

        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        if (surfaceTexture != null) {
            Surface surface = new Surface(surfaceTexture);
            mediaRecorder.setPreviewDisplay(surface);
        } else {
            Toast.makeText(this, "Failed to get SurfaceTexture", Toast.LENGTH_SHORT).show();
        }
    }



    private String getVideoFilePath() {
        // Define where to store the recorded video
        // Example: return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/yourvideo.mp4";
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_VIDEO_PERMISSIONS) {
            boolean allGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                // Permissions granted, proceed to setup camera
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopRecording();
        }
    }
}
