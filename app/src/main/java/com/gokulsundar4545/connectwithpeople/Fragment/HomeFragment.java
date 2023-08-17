package com.gokulsundar4545.connectwithpeople.Fragment;

import static com.gokulsundar4545.ClsGlobal.isDateGreaterThen24Hours;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.animation.content.Content;
import com.cooltechworks.views.shimmer.ShimmerAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gokulsundar4545.connectwithpeople.Adapter.PostAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.StoryAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.StorynewAdapter;
import com.gokulsundar4545.connectwithpeople.EditUserProfile;
import com.gokulsundar4545.connectwithpeople.LoginActivity;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.Story;
import com.gokulsundar4545.connectwithpeople.Model.Storynew;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.Model.UserStories;
import com.gokulsundar4545.connectwithpeople.PayMentActivity;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {


    DrawerLayout drawerLayout1;
    NavigationView navigationView;

    static final float END_SCALE = 0.7f;
    LinearLayout content;

    ShimmerRecyclerView dashboardRv,StroyRv;

    ArrayList<Story> Storylist;
    FirebaseAuth auth;
    FirebaseDatabase database;
    SwipeRefreshLayout Refresh;
    FirebaseStorage storage;

    ArrayList<Post> dashboardlist;

    RoundedImageView addStoryImage;
    ProgressDialog progressDialog;

    TextView textView4;

    DatabaseReference NotificationRef;

    String CurrentUser;
    private int count;

    TextView comment;
    FirebaseAuth Auth;

    ImageView menuIcon1, notification,chat,star,status;
    de.hdodenhof.circleimageview.CircleImageView profile,imageView2;

    private RecyclerView recyclerView_story;
    private StorynewAdapter storyAdapter;
    private List<Storynew> storynewList;


    ActivityResultLauncher<String> galleryLauncher;

    public HomeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());

        Auth=FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        profile = view.findViewById(R.id.profile_image);

        drawerLayout1 = view.findViewById(R.id.drawer_layouy);
        navigationView = view.findViewById(R.id.navigation_view);
        content = view.findViewById(R.id.content);
        notification = view.findViewById(R.id.notification);
        chat=view.findViewById(R.id.chat);
        star=view.findViewById(R.id.start);


        comment=view.findViewById(R.id.comment);
        NotificationRef=FirebaseDatabase.getInstance( ).getReference( ).child("notification").child(Auth.getUid());
        NotificationRef.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    count=(int) snapshot.getChildrenCount();
                    comment.setText(Integer.toString(count));


                }else {
                    comment.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        Refresh=view.findViewById(R.id.refresh);

        textView4=view.findViewById(R.id.textView4);
        imageView2=view.findViewById(R.id.imageView2);

        TextPaint textPaint=textView4.getPaint();
        float width=textPaint.measureText(getResources().getString(R.string.app_name));
        Shader shader=new LinearGradient(0,0,width,textView4.getTextSize(),
                new int[]{

                        Color.parseColor("#5D6D7E"),

                        Color.parseColor("#5D6D7E")
                },null,Shader.TileMode.CLAMP);
        textView4.getPaint().setShader(shader);

        menuIcon1 = view.findViewById(R.id.menu_icon);
        menuIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout1.isDrawerVisible(GravityCompat.START))
                    drawerLayout1.closeDrawer(GravityCompat.START);
                else drawerLayout1.openDrawer(GravityCompat.START);
            }
        });


        animationNavigationDrawer();

        Nview();

        dashboardRv = view.findViewById(R.id.dashboardRv);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();




        StroyRv=view.findViewById(R.id.storyRV);
        StroyRv.showShimmerAdapter();

        Storylist = new ArrayList<>();

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Story Uploading");
        progressDialog.setMessage("Please wait while Story Uploading!........");
        progressDialog.setCancelable(false);


        StoryAdapter adapter=new StoryAdapter(Storylist,getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        StroyRv.setLayoutManager(linearLayoutManager);
        StroyRv.setNestedScrollingEnabled(false);


        database.getReference()
                .child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                Storylist.clear();
                if (snapshot.exists()){
                    for (DataSnapshot storySnapshot:snapshot.getChildren()){
                        Story story=new Story();
                        story.setStoryBy(storySnapshot.getKey());
                        story.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));

                        ArrayList<UserStories> Stories=new ArrayList<>();

                        for (DataSnapshot Snapshot1:storySnapshot.child("userStories").getChildren()){

                            UserStories userStories=Snapshot1.getValue(UserStories.class);
                            if (userStories != null){
                                Log.e("tag","isDateGreaterThen24Hours(userStories.getStoryAt()): "
                                        + isDateGreaterThen24Hours(userStories.getStoryAt()));

                                if (!isDateGreaterThen24Hours(userStories.getStoryAt())){
                                    Stories.add(userStories);
                                }else {
                                    // Remove record from firebase realtime database login here.
                                }
                            }

                        }
                        story.setStories(Stories);
                        Storylist.add(story);
                    }
                    StroyRv.setAdapter(adapter);
                    StroyRv.hideShimmerAdapter();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });




        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    User user = snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(profile);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(imageView2);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        status=view.findViewById(R.id.status);

        status.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                 Fragment3 pf = new Fragment3();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pf).commit();
            }
        });


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChatListFragment pf = new ChatListFragment();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pf).commit();

            }
        });




        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileFragment pf = new ProfileFragment();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pf).commit();

            }
        });


        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Notification2Fragment pay = new Notification2Fragment();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pay).commit();

            }
        });

        addStoryImage=view.findViewById(R.id.postimage);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddStatus pay = new AddStatus();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pay).commit();

            }
        });


        dashboardlist = new ArrayList<>();


        PostAdapter dashboardAdapter = new PostAdapter(dashboardlist, getContext());
        LinearLayoutManager layoutManager11 = new LinearLayoutManager(getContext());
        layoutManager11.setReverseLayout(true);
        layoutManager11.setStackFromEnd(true);
        dashboardRv.setLayoutManager(layoutManager11);
        dashboardRv.showShimmerAdapter();
        dashboardRv.setNestedScrollingEnabled(false);

        Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener( ) {
            @Override
            public void onRefresh() {
                database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dashboardlist.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            post.setPostId(dataSnapshot.getKey());
                            dashboardlist.add(post);
                        }
                        dashboardRv.setAdapter(dashboardAdapter);
                        dashboardRv.hideShimmerAdapter();
                        dashboardAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Refresh.setRefreshing(false);
            }
        });


        dashboardRv.showShimmerAdapter();
        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dashboardlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    dashboardlist.add(post);
                }
                dashboardRv.setAdapter(dashboardAdapter);
                dashboardRv.hideShimmerAdapter();
                dashboardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        return view;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.editprofile:
                Intent intent1 = new Intent(getContext(), EditUserProfile.class);
                startActivity(intent1);
                break;
            case R.id.Reward:
                Intent intent = new Intent(getContext(), PayMentActivity.class);
                startActivity(intent);
                break;

            case R.id.logout:
                Logout();
                break;


        }
        return true;
    }

    private void Nview() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.editprofile);

    }


    private void animationNavigationDrawer() {

        drawerLayout1.setScrimColor(getResources().getColor(R.color.white));

    }

    private void Logout() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you want to Logout!");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                auth.signOut();
                Intent intent = new Intent(getContext(), StartActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();


            }
        });
        alertDialog.show();


    }

    private void UpdateUserProfile(String count) {

        HashMap user=new HashMap();
        user.put("NotificationCount",count);

        FirebaseAuth Auth=FirebaseAuth.getInstance();
        FirebaseUser CurrentUser=Auth.getCurrentUser();

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(CurrentUser.getUid()).updateChildren(user).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull @NotNull Task task) {

                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Profile Updated Successful", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}