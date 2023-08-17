package com.gokulsundar4545.connectwithpeople.Fragment;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.CropImageActivity;
import com.gokulsundar4545.connectwithpeople.CropImageActivitystory;
import com.gokulsundar4545.connectwithpeople.NewActivity;
import com.gokulsundar4545.connectwithpeople.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CameraFragmentstory extends Fragment {
    private static final String TAG = "CameraFragmentstory";
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 300;

    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private String cameraId;
    private int currentCameraFacing = CameraCharacteristics.LENS_FACING_BACK;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private File videoFile;


    private SharedPreferences sharedPreferences;
    private static final int REQUEST_PERMISSION_CODE = 200;
    ImageView Create;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camerastory, container, false);
        textureView = view.findViewById(R.id.texture_view);
        textureView.setSurfaceTextureListener(surfaceTextureListener);


        Create=view.findViewById(R.id.Create);

        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        sharedPreferences = getActivity().getSharedPreferences("ModifiedImagePrefs2", Context.MODE_PRIVATE);
        String modifiedImageUriString = sharedPreferences.getString("modified_image_uri2", null);

        ImageView shareButton = view.findViewById(R.id.share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        ImageView turnFrontButton = view.findViewById(R.id.turnfront);
        turnFrontButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        ImageView gallary = view.findViewById(R.id.gallary);
        gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBottomSheetDialoggallary bottomSheetDialog = new CustomBottomSheetDialoggallary();
                bottomSheetDialog.show(((FragmentActivity) getContext()).getSupportFragmentManager(), bottomSheetDialog.getTag());
            }
        });

        ImageView custom = view.findViewById(R.id.custom);
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragment2 bottomSheetFragment = new BottomSheetFragment2();
                bottomSheetFragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        Glide.with(this).load(modifiedImageUriString).into(custom);

        ImageView vedio = view.findViewById(R.id.vedio);

        vedio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "Long click detected");
                if (!isRecording) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                    } else {
                        startRecording(); // Ensure this method is properly implemented
                    }

                }
                return true;
            }
        });


        vedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    setupMediaRecorder();
                } else {
                    requestPermission();
                }
            }
        });



        return view;
    }

    private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
            // Handle size changes if necessary
        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
            // Handle texture updates if necessary
        }
    };

    private void openCamera() {
        CameraManager manager = (CameraManager) requireActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                return;
            }

            // Find available cameras and open the default one
            String[] cameraIds = manager.getCameraIdList();
            for (String id : cameraIds) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == currentCameraFacing) {
                    cameraId = id;
                    break;
                }
            }

            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    };

    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
            Surface surface = new Surface(texture);
            final CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);
            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    captureSession = session;
                    try {
                        builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        session.setRepeatingRequest(builder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Toast.makeText(getContext(), "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void captureImage() {
        if (textureView == null) {
            return;
        }

        Bitmap bitmap = textureView.getBitmap(textureView.getWidth(), textureView.getHeight());

        // Save captured bitmap to a file
        String imagePath = saveBitmapToFile(bitmap);

        if (imagePath != null) {
            // Display image file path in a Toast
            Toast.makeText(getContext(), "Image saved: " + imagePath, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), CropImageActivitystory.class);
            intent.putExtra("image_urlstory", imagePath);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private String saveBitmapToFile(Bitmap bitmap) {
        try {
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "IMG_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void switchCamera() {
        currentCameraFacing = (currentCameraFacing == CameraCharacteristics.LENS_FACING_BACK) ? CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK;
        closeCamera();
        openCamera();
    }

    private void closeCamera() {
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        // Release other resources like MediaRecorder if necessary
    }


    private void startRecording() {
        if (cameraDevice == null || !textureView.isAvailable()) {
            Toast.makeText(getContext(), "Camera is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            closePreviewSession();

            mediaRecorder = new MediaRecorder();
            setUpMediaRecorder();

            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
            Surface surface = new Surface(texture);

            CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            surfaces.add(surface);
            captureRequestBuilder.addTarget(surface);
            surfaces.add(mediaRecorder.getSurface());
            captureRequestBuilder.addTarget(mediaRecorder.getSurface());

            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    captureSession = session;
                    try {
                        captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                        mediaRecorder.start();
                        isRecording = true;
                        Log.d(TAG, "Recording started");
                    } catch (CameraAccessException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.e(TAG, "Failed to start video capture session");
                }
            }, null);

        } catch (CameraAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            try {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = false;
                Log.d(TAG, "Recording stopped");

                Toast.makeText(getContext(), "Video saved: " + videoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), NewActivity.class);
                intent.putExtra("video_path", videoFile.getAbsolutePath());
                startActivity(intent);
            } catch (RuntimeException stopException) {
                Log.e(TAG, "Error stopping media recorder: " + stopException.getMessage());
            }
        }
    }

    private void setUpMediaRecorder() throws IOException {
        videoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "VID_" + System.currentTimeMillis() + ".mp4");

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
        mediaRecorder.setVideoEncodingBitRate(10000000);
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoSize(textureView.getWidth(), textureView.getHeight());
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.prepare();
    }

    private void closePreviewSession() {
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(getContext(), "Audio recording permission denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMediaRecorder();
            } else {
                Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }





    private boolean checkPermission() {
        int cameraPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        int audioPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO);
        int storagePermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                audioPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION_CODE);
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        textureView.setSurfaceTextureListener(textureListener);
    }

    public void onRecordButtonClick(View view) {
        if (isRecording) {
            // Stop recording
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            Toast.makeText(getContext(), "Recording stopped. Video saved: " + videoFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } else {
            // Start recording
            if (prepareMediaRecorder()) {
                try {
                    mediaRecorder.start();
                    isRecording = true;
                    Toast.makeText(getContext(), "Recording started", Toast.LENGTH_SHORT).show();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Failed to prepare MediaRecorder", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean prepareMediaRecorder() {
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        videoFile = getOutputMediaFile();
        if (videoFile == null) {
            return false;
        }

        mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
        mediaRecorder.setVideoEncodingBitRate(10000000);
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoSize(640, 480);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            setupMediaRecorder();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "VID_" + timeStamp + ".mp4");
    }




}

