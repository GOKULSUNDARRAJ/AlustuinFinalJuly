package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.gokulsundar4545.connectwithpeople.Fragment.ChatListFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.HomeFragment;

public class MessageactivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messageactivity);
        setStatusBarColor(getResources().getColor(android.R.color.white));

        replaceFragment(new ChatListFragment());
    }


    private  void replaceFragment(Fragment fragment){
        FragmentManager transaction=getSupportFragmentManager();
        FragmentTransaction transaction1=transaction.beginTransaction();
        transaction1.replace(R.id.fram_layout,fragment,null);
        transaction1.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slid_from_left, R.anim.slid_to_right);
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(color);
        }
    }
}