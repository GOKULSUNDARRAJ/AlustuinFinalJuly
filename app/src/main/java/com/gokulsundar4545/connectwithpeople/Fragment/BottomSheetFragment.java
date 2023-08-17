package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.VideoFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // Setup ViewPager
        setupViewPager();

        // Connect TabLayout with ViewPager
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager() {
        BottomSheetPagerAdapter adapter = new BottomSheetPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new GalleryFragment(), "Images");
        adapter.addFragment(new VideoFragment(), "Videos");
        viewPager.setAdapter(adapter);
    }

    private static class BottomSheetPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        BottomSheetPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
