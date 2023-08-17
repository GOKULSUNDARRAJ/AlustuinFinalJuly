package com.gokulsundar4545.connectwithpeople.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.gokulsundar4545.connectwithpeople.CropImageActivity;
import com.gokulsundar4545.connectwithpeople.CropImageActivitystory;
import com.gokulsundar4545.connectwithpeople.NewActivity;
import com.gokulsundar4545.connectwithpeople.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CameraFragment2 extends Fragment {
    private static final String TAG = "CameraFragment";
    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private Bitmap capturedBitmap;
    private String cameraId;
    private int currentCameraFacing = CameraCharacteristics.LENS_FACING_BACK; // Default to back camera

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private File videoFile;
    ImageView Create;

    @SuppressLint("MissingInflatedId")
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


        ImageView custom=view.findViewById(R.id.custom);

        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragment2 bottomSheetFragment = new BottomSheetFragment2();
                bottomSheetFragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        ImageView vedio=view.findViewById(R.id.vedio);





        vedio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (!isRecording) {
                    startRecording();

                }
                return true;
            }
        });

        vedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });




        BottomSheetFragment2 bottomSheetFragment = new BottomSheetFragment2();
        bottomSheetFragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
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
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 200);
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
            intent.putExtra("image_url", imagePath);
            startActivity(intent);


        } else {
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }

        // Example: You can display the captured bitmap in an ImageView or perform other actions
//        ImageView capturedImageView = requireView().findViewById(R.id.captured_image_view);
//        capturedImageView.setImageBitmap(bitmap);
//        capturedImageView.setVisibility(View.VISIBLE);

        // Optionally, you can save the bitmap to storage or perform other operations here
    }


    private String saveBitmapToFile(Bitmap bitmap) {
        // Example directory where the images will be stored
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YourAppImages");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                return null;
            }
        }

        // Create a file to save the image
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(directory, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            return imageFile.getAbsolutePath(); // Return the absolute path of the image file
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void switchCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }

        currentCameraFacing = (currentCameraFacing == CameraCharacteristics.LENS_FACING_BACK) ?
                CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK;

        openCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;

        }
        closeCamera();
        stopRecording();
    }


    private void startRecording() {
        if (cameraDevice == null || !textureView.isAvailable()) {
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
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            Toast.makeText(getContext(), "Video saved: " + videoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

//            // Example: You can display the captured video in a VideoView or perform other actions
//            VideoView capturedVideoView = requireView().findViewById(R.id.captured_video_view);
//            capturedVideoView.setVideoURI(Uri.fromFile(videoFile));
//            capturedVideoView.setVisibility(View.VISIBLE);
//            capturedVideoView.start();

            Intent intent = new Intent(getContext(), NewActivity.class);
            intent.putExtra("video_path", videoFile.getAbsolutePath());
            startActivity(intent);




            // Optionally, you can save the video to storage or perform other operations here
        }
    }

    private void setUpMediaRecorder() throws IOException {
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        videoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "YourAppVideos/video.mp4");
        if (!videoFile.getParentFile().exists()) {
            videoFile.getParentFile().mkdirs();
        }

        mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
        mediaRecorder.setVideoEncodingBitRate(10000000);
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoSize(textureView.getWidth(), textureView.getHeight());
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.prepare();
    }

    // Other methods (openCamera, switchCamera, onRequestPermissionsResult, onPause) as before...



    private void closeCamera() {
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void closePreviewSession() {
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
    }
}
