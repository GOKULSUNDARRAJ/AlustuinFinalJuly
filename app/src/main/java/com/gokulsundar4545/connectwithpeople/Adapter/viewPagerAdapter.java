package com.gokulsundar4545.connectwithpeople.Adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.gokulsundar4545.connectwithpeople.Fragment.Notification2Fragment;
import com.gokulsundar4545.connectwithpeople.Fragment.RequestFragment;

public class viewPagerAdapter extends FragmentPagerAdapter {


    public viewPagerAdapter(@NonNull  FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Notification2Fragment();
            case 1:
                return new RequestFragment();

            default:
                return new Notification2Fragment();
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable

    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
        if (position==0){
            title="Notification";
        }else if (position==1) {
            title = "Required";
        }else {
            title="Not";
        }
        return title;
    }
}
