package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.Fragment.AddPostFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.AllPostFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.CameraFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.ChatListFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.HomeFragment;
import com.gokulsundar4545.connectwithpeople.Fragment.NewSearchFragment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.iammert.library.readablebottombar.ReadableBottomBar;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements TransitionHandler {

    ActivityMainBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;


    private long time;

    static final float END_SCALE = 0.7f;
    String mUID="";

    FirebaseAuth Auth;
    FirebaseStorage storage;
    DatabaseReference PostRef;
    String firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setStatusBarColor(getResources().getColor(android.R.color.white));


        HomeFragment fragment=new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fram_layout,fragment)
                .commit();




        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();





        Auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        firebaseUser=Auth.getCurrentUser().getUid();



        ImageView home;
        home=findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new HomeFragment());
                setStatusBarColor(getResources().getColor(android.R.color.white));
                int color = ContextCompat.getColor(MainActivity.this, R.color.white);
                binding.cardproduct.setBackgroundColor(color);
                binding.home.setColorFilter(Color.BLACK);
                binding.reels.setColorFilter(Color.BLACK);
                binding.add.setColorFilter(Color.BLACK);
                binding.user.setColorFilter(Color.BLACK);

            }
        });

        ImageView reels;
        reels=findViewById(R.id.reels);
        reels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new RealVideoViewFragment());
                setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
                int color = ContextCompat.getColor(MainActivity.this, R.color.black);
                binding.cardproduct.setBackgroundColor(color);
                binding.home.setColorFilter(Color.WHITE);
                binding.reels.setColorFilter(Color.WHITE);
                binding.add.setColorFilter(Color.WHITE);
                binding.user.setColorFilter(Color.WHITE);



            }
        });

        ImageView add;
        add=findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             startActivity(new Intent(view.getContext(), MainCameraActivity.class));
             overridePendingTransition(R.anim.slid_from_left, R.anim.slid_to_right);
                setStatusBarColor(getResources().getColor(android.R.color.white));
                int color = ContextCompat.getColor(MainActivity.this, R.color.white);
                binding.cardproduct.setBackgroundColor(color);
                binding.home.setColorFilter(Color.BLACK);
                binding.reels.setColorFilter(Color.BLACK);
                binding.add.setColorFilter(Color.BLACK);
                binding.user.setColorFilter(Color.BLACK);

            }
        });

        ImageView user;
        user=findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new AllPostFragment());
                setStatusBarColor(getResources().getColor(android.R.color.white));
                int color = ContextCompat.getColor(MainActivity.this, R.color.white);
                binding.cardproduct.setBackgroundColor(color);
                binding.home.setColorFilter(Color.BLACK);
                binding.reels.setColorFilter(Color.BLACK);
                binding.add.setColorFilter(Color.BLACK);
                binding.user.setColorFilter(Color.BLACK);

            }
        });

        ImageView profile;
        profile=findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new ProfileFragment());
                setStatusBarColor(getResources().getColor(android.R.color.white));
                int color = ContextCompat.getColor(MainActivity.this, R.color.white);
                binding.cardproduct.setBackgroundColor(color);
                binding.home.setColorFilter(Color.BLACK);
                binding.reels.setColorFilter(Color.BLACK);
                binding.add.setColorFilter(Color.BLACK);
                binding.user.setColorFilter(Color.BLACK);

            }
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




        database.getReference().child("Users").child(Auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if (snapshot.exists()){

                    User user=snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(binding.profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    protected void onResume() {


        super.onResume();
    }



    @Override
    public void onBackPressed() {


//        if (binding.bottomNavigationView.getSelectedItemId()==R.id.home){
//            super.onBackPressed();
//            finish();
//        }else {
//            binding.bottomNavigationView.setSelectedItemId(R.id.home);
//
//        }


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






    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(color);
        }
    }



    @Override
    public void performTransition() {
        overridePendingTransition(R.anim.slide_from_bottom, R.anim.slid_to_top);
    }

}