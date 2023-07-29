package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gokulsundar4545.connectwithpeople.Fragment.HomeFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.InternshipFragment;

import com.gokulsundar4545.connectwithpeople.Fragment.AddVedioFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.Notification2Fragment;
import com.gokulsundar4545.connectwithpeople.Fragment.ProfileFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.VedioViewFragment;
import com.gokulsundar4545.connectwithpeople.databinding.ActivityNextBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.iammert.library.readablebottombar.ReadableBottomBar;

public class NextActivity extends AppCompatActivity  {

    ActivityNextBinding binding;
    FirebaseAuth Auth=FirebaseAuth.getInstance();



    ConstraintLayout content;
    static final float END_SCALE = 0.7f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNextBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        content = findViewById(R.id.content);







        setSupportActionBar(binding.toolbar);
        NextActivity.this.setTitle("My Profile");

        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        binding.toolbar.setVisibility(View.GONE);
        transaction.replace(R.id.container,new HomeFragment());
        transaction.commit();





        binding.readablebottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {

                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();

                switch (i){
                    case 0:
                        Intent intent=new Intent(NextActivity.this,MainActivity.class);
                        startActivity(intent);

                        break;

                    case 1:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container,new Notification2Fragment());
                        break;


                    case 2:
                        binding.toolbar.setVisibility(View.VISIBLE);
                        transaction.replace(R.id.container,new ProfileFragment());
                        break;
                    case 3:
                        binding.toolbar.setVisibility(View.GONE);
                        transaction.replace(R.id.container,new AddVedioFragment());
                        break;
                }
                transaction.commit();
            }
        });
    }





    @Override
    public void onBackPressed() {

    }











}