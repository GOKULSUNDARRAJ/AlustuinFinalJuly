package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

public class ThereFollowingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_following);

        Intent intent = getIntent();
        String hisUid = intent.getStringExtra("hisUId");

        // Create an instance of ThereFollowingFragment and set arguments
        thereFollowingFragment thereFollowingFragment = new thereFollowingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("hisUId", hisUid);
        thereFollowingFragment.setArguments(bundle);

        // Replace the fragment
        replaceFragment(thereFollowingFragment);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fram_layout, fragment, null);
        fragmentTransaction.commit();
    }
}
