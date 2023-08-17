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

import com.gokulsundar4545.connectwithpeople.Adapter.GalleryUtils;
import com.gokulsundar4545.connectwithpeople.Adapter.GalleryUtils2;
import com.gokulsundar4545.connectwithpeople.Adapter.Galleryadapterforstory;
import com.gokulsundar4545.connectwithpeople.EditImageActivitystory;
import com.gokulsundar4545.connectwithpeople.R;

import java.util.Collections;
import java.util.List;

public class GalleryFragmentstory extends Fragment implements Galleryadapterforstory.OnImageClickListener {

    private static final String PREF_NAME = "MyPreferences";
    private SharedPreferences sharedPreferences;

    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView recyclerView;
    private Uri selectedImageUrl; // To store selected image URL
    TextView btnDone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_gallary, container, false);
        recyclerView = view.findViewById(R.id.friendrv);
        btnDone = view.findViewById(R.id.down); // Assuming you have a "Done" button
        btnDone.setOnClickListener(v -> handleDoneButtonClick());

        sharedPreferences = getActivity().getSharedPreferences("ModifiedImagePrefsstory", MODE_PRIVATE); // Initialize SharedPreferences

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            loadGallery();
        }

        return view;
    }

    private void loadGallery() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<Uri> imagePaths = GalleryUtils2.getAllImages(getContext());

        // Reverse the imagePaths list
        Collections.reverse(imagePaths);

        Galleryadapterforstory galleryAdapter = new Galleryadapterforstory(getContext(), imagePaths, this); // Pass 'this' as the listener
        recyclerView.setAdapter(galleryAdapter);
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
    public void onImageClick(Uri imageUrl) {
        selectedImageUrl = imageUrl; // Store selected image URL
        btnDone.setVisibility(View.VISIBLE);
    }

    private void handleDoneButtonClick() {
        if (selectedImageUrl != null) {
            // Save the selected image URL in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("modified_image_uristory", selectedImageUrl.toString());
            editor.apply(); // or editor.commit();

            // Start NewActivity and pass selected image URL
            Intent intent = new Intent(getActivity(), EditImageActivitystory.class);
            intent.putExtra("image_urlstory", selectedImageUrl.toString());
            startActivity(intent);

            Toast.makeText(getContext(), selectedImageUrl.toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }
}
