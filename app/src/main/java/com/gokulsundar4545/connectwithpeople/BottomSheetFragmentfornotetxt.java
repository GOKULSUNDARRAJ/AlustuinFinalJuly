package com.gokulsundar4545.connectwithpeople;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gokulsundar4545.AddnotesActivity;
import com.gokulsundar4545.connectwithpeople.Fragment.GalleryFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.VideoGalleryFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragmentfornotetxt extends BottomSheetDialogFragment {

    AddnotesActivity addnotesActivity;

    public BottomSheetFragmentfornotetxt(AddnotesActivity addnotesActivity) {
        this.addnotesActivity = addnotesActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layoutfornotestxt, container, false);



        return view;
    }


}
