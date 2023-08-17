package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.Fragment.AddPostFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.ChatListFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.HomeFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.Notification2Fragment;
import com.gokulsundar4545.connectwithpeople.Fragment.ProfileFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.SearchFragment;

import com.gokulsundar4545.connectwithpeople.Fragment.VedioViewFragment;

import com.gokulsundar4545.connectwithpeople.Model.User;


import com.gokulsundar4545.connectwithpeople.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity  {

    ActivityMainBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;


    private long time;

    static final float END_SCALE = 0.7f;
    String mUID="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        HomeFragment fragment=new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fram_layout,fragment)
                .commit();
        setSupportActionBar(binding.toolbar);

        binding.message.setVisibility(View.GONE);

        binding.search123.setVisibility(View.GONE);
        binding.toolbar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.toolbar.setVisibility(View.GONE);


        binding.bottomNavigationView.setBackground(null);



        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    binding.message.setVisibility(View.GONE);

                    binding.search123.setVisibility(View.GONE);

                    binding.toolbar.setVisibility(View.GONE);
                    break;

                case R.id.shorts:
                    replaceFragment(new VedioViewFragment());
                    binding.toolbar.setVisibility(View.GONE);
                    binding.message.setVisibility(View.GONE);

                    binding.search123.setVisibility(View.GONE);


                    break;

                case R.id.add:
                    replaceFragment(new AddPostFragment());
                    binding.toolbar.setVisibility(View.GONE);
                    binding.message.setVisibility(View.GONE);

                    binding.search123.setVisibility(View.GONE);

                    break;

                case R.id.chat:
                    MainActivity.this.setTitle("");
                    binding.toolbar.setVisibility(View.VISIBLE);
                    binding.message.setVisibility(View.VISIBLE);

                    binding.search123.setVisibility(View.VISIBLE);
                    binding.toolbar.setVisibility(View.VISIBLE);
                    replaceFragment(new SearchFragment());
                    break;

                case R.id.profile:
                    binding.message.setVisibility(View.GONE);
                    binding.search123.setVisibility(View.GONE);
                    replaceFragment(new ProfileFragment());
                    binding.toolbar.setVisibility(View.GONE);
                    break;


            }
            return true;
        });



        


       String emial = (String)  MyPref.getFromPrefs(this,MyPref.EMAIL,"");
       String token = (String)  MyPref.getFromPrefs(this,MyPref.TOKEN,"");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Users");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        User post = ds.getValue(User.class);

                        if (post  != null && post.getEmail() != null
                                && post.getEmail().equalsIgnoreCase(emial)) {
                            usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("token").setValue(token);
                        }



                        Log.d("TAG", "post: "  + post.getEmail());
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        usersRef.addListenerForSingleValueEvent(eventListener);










    }

    @Override
    protected void onResume() {


        super.onResume();
    }



    @Override
    public void onBackPressed() {


        if (binding.bottomNavigationView.getSelectedItemId()==R.id.home){
            super.onBackPressed();
            finish();
        }else {
            binding.bottomNavigationView.setSelectedItemId(R.id.home);

        }


        if (time+1000> System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(this, "Press Again", Toast.LENGTH_SHORT).show( );
        }

        time=System.currentTimeMillis();

    }






    private  void checkUseStatus(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){
            mUID=user.getUid();

            SharedPreferences sharedPreferences=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();
        }




    }






    private  void replaceFragment(Fragment fragment){
        FragmentManager transaction=getSupportFragmentManager();
        FragmentTransaction transaction1=transaction.beginTransaction();
        transaction1.replace(R.id.fram_layout,fragment,null);
        transaction1.commit();

    }












}