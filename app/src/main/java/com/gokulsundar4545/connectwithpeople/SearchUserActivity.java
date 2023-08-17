package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.gokulsundar4545.connectwithpeople.Fragment.CameraFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.SearchFragment;

public class SearchUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        replaceFragment(new SearchFragment());

    }


    private  void replaceFragment(Fragment fragment){
        FragmentManager transaction=getSupportFragmentManager();
        FragmentTransaction transaction1=transaction.beginTransaction();
        transaction1.replace(R.id.fram_layout,fragment,null);
        transaction1.commit();

    }
}