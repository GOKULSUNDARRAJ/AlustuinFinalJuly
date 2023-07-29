package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gokulsundar4545.connectwithpeople.Adapter.viewPagerAdapter;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.material.tabs.TabLayout;

public class NotificationFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;

    public NotificationFragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_notification2, container, false);

        return view;
    }
}