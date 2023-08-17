package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.R;

public class DisplayImageFragment extends Fragment {

    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_image, container, false);
        imageView = view.findViewById(R.id.image_view);

        Bundle args = getArguments();
        if (args != null) {
            String imageUrl = args.getString("image_url");
            if (imageUrl != null) {
                Glide.with(this).load(imageUrl).into(imageView);
            }
        }

        return view;
    }
}
