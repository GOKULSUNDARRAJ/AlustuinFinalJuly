package com.gokulsundar4545.connectwithpeople.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
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

import com.gokulsundar4545.connectwithpeople.Adapter.GalleryAdapterforstoryedit;
import com.gokulsundar4545.connectwithpeople.Adapter.GalleryUtils2;
import com.gokulsundar4545.connectwithpeople.CropImageActivity;
import com.gokulsundar4545.connectwithpeople.ImageEditActivity;
import com.gokulsundar4545.connectwithpeople.R;

import java.util.Collections;
import java.util.List;

public class GalleryFragmentforstoryedit extends Fragment implements GalleryAdapterforstoryedit.OnImageClickListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private Uri selectedImageUri; // To store selected image URI
    private TextView btnDone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_gallary, container, false);
        recyclerView = view.findViewById(R.id.friendrv);
        btnDone = view.findViewById(R.id.down); // Assuming you have a "Done" button
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

        List<Uri> imageUris = GalleryUtils2.getAllImages(getContext());

        // Reverse the imageUris list
        Collections.reverse(imageUris);

        GalleryAdapterforstoryedit galleryAdapter = new GalleryAdapterforstoryedit(getContext(), imageUris, this); // Pass 'this' as the listener
        recyclerView.setAdapter(galleryAdapter);

        // Check if imageUris list has at least one item
        if (!imageUris.isEmpty()) {
            // Get the image URI at index 0
            Uri imageUri = imageUris.get(0);
            saveSelectedImageUri(imageUri);
        }
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
    public void onImageClick(String imageUrl) {
        selectedImageUri = Uri.parse(imageUrl); // Store selected image URI
        btnDone.setVisibility(View.VISIBLE);
    }

    private void handleDoneButtonClick() {
        if (selectedImageUri != null) {

            startActivity(new Intent(getContext(), ImageEditActivity.class));
            Toast.makeText(getContext(), ""+selectedImageUri, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSelectedImageUri(Uri imageUri) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("selectimagepre", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectimage", imageUri.toString()); // Convert Uri to String
        editor.apply();

        Toast.makeText(getContext(), imageUri.toString(), Toast.LENGTH_SHORT).show();
    }
}
