package com.gokulsundar4545.connectwithpeople.Fragment;

import static com.gokulsundar4545.ClsGlobal.isDateGreaterThen24Hours;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.animation.content.Content;
import com.cooltechworks.views.shimmer.ShimmerAdapter;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gokulsundar4545.connectwithpeople.Adapter.CombinedAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.PostAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.StoryAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.StorynewAdapter;
import com.gokulsundar4545.connectwithpeople.EditImageActivity;
import com.gokulsundar4545.connectwithpeople.EditUserProfile;
import com.gokulsundar4545.connectwithpeople.ImageEditActivity;
import com.gokulsundar4545.connectwithpeople.LoginActivity;
import com.gokulsundar4545.connectwithpeople.MainActivity5;
import com.gokulsundar4545.connectwithpeople.MessageactivityActivity;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.Story;
import com.gokulsundar4545.connectwithpeople.Model.Storynew;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.Model.UserStories;
import com.gokulsundar4545.connectwithpeople.PayMentActivity;
import com.gokulsundar4545.connectwithpeople.PhotoEditActivity;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.StartActivity;
import com.gokulsundar4545.connectwithpeople.StoryActivityadd;
import com.gokulsundar4545.connectwithpeople.VideoFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
    private MediaPlayer mediaPlayer;

    ActivityResultLauncher<String> galleryLauncher;

    public HomeFragment() {

    }

    ConstraintLayout constraintLayout3;
    HorizontalScrollView horizontalScrollView;

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

        constraintLayout3=view.findViewById(R.id.constraintLayout3);
        horizontalScrollView=view.findViewById(R.id.horizontalScrollView);

        TextView textView4=view.findViewById(R.id.textView4);
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(view.getContext(), ImageEditActivity.class));
            }
        });

        comment=view.findViewById(R.id.comment);
        NotificationRef=FirebaseDatabase.getInstance().getReference().child("notification1").child(Auth.getUid());
        NotificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return; // Check if the fragment is attached

                if (snapshot.exists()) {
                    long count = snapshot.getChildrenCount(); // Get number of notifications

                    // Update UI to show notification count
                    if (Long.toString(count).contains("0")) {
                        comment.setVisibility(View.GONE);
                    } else {
                        comment.setText(Long.toString(count));
                        comment.setVisibility(View.VISIBLE);
                    }

                    // Update notification count in the current user's data
                    DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(Auth.getUid());
                    currentUserRef.child("notificationCount").setValue(count);
                } else {
                    if (isAdded()) { // Check again if the fragment is attached
                        comment.setText("0"); // No notifications found

                    }

                    // Reset notification count in the current user's data if needed
                    DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(Auth.getUid());
                    currentUserRef.child("notificationCount").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) { // Check if the fragment is attached
                    // Handle any errors that occur
                    Log.e("Firebase", "Error fetching notifications", error.toException());
                    Toast.makeText(requireContext(), "Error fetching notifications", Toast.LENGTH_SHORT).show();
                }
            }
        });




        textView4=view.findViewById(R.id.textView4);
        imageView2=view.findViewById(R.id.imageView2);


        profile.setOnClickListener(new View.OnClickListener() {
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


        StoryAdapter adapter=new StoryAdapter(Storylist,getContext(),getActivity());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        StroyRv.setLayoutManager(linearLayoutManager);
        StroyRv.setNestedScrollingEnabled(false);


        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Storylist.clear(); // Clear the existing list
                if (snapshot.exists()) {
                    for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                        Story story = new Story();
                        story.setStoryBy(storySnapshot.getKey());

                        Long postedBy = storySnapshot.child("postedBy").getValue(Long.class);
                        if (postedBy != null) {
                            story.setStoryAt(postedBy);
                        } else {
                            story.setStoryAt(0L); // or handle this case appropriately
                        }

                        ArrayList<UserStories> stories = new ArrayList<>();

                        for (DataSnapshot snapshot1 : storySnapshot.child("userStories").getChildren()) {
                            UserStories userStories = snapshot1.getValue(UserStories.class);
                            if (userStories != null) {
                                Log.e("tag", "isDateGreaterThen24Hours(userStories.getStoryAt()): "
                                        + isDateGreaterThen24Hours(userStories.getStoryAt()));

                                if (!isDateGreaterThen24Hours(userStories.getStoryAt())) {
                                    stories.add(userStories);
                                } else {
                                    // Remove record from Firebase Realtime Database
                                    String storyKey = storySnapshot.getKey();
                                    String userStoryKey = snapshot1.getKey();
                                    DatabaseReference storyRef = database.getReference()
                                            .child("stories")
                                            .child(storyKey)
                                            .child("userStories")
                                            .child(userStoryKey);

                                    storyRef.removeValue(); // Remove the user story from database
                                }
                            }
                        }

                        // Check if stories list is empty after processing
                        if (stories.isEmpty()) {
                            // Remove postedBy if there are no user stories
                            DatabaseReference postedByRef = database.getReference()
                                    .child("stories")
                                    .child(storySnapshot.getKey())
                                    .child("postedBy");
                            postedByRef.removeValue();
                        } else {
                            story.setStories(stories);
                            Storylist.add(story);
                        }
                    }
                    StroyRv.setAdapter(adapter);
                    StroyRv.hideShimmerAdapter();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Log.e("DatabaseError", error.getMessage());
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







        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              startActivity(new Intent(getContext(), MessageactivityActivity.class));


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
                // Get reference to the current user's notification1 node
                DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("notification1").child(Auth.getUid());

                // Remove the notification1 node
                notificationRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Successfully deleted notification1 node

                                // Optionally, update UI or perform any other actions after deletion
                                // For example, clear UI elements or update notification count to zero
                                comment.setText("0"); // Update UI example
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to delete notification1 node
                                Log.e("Firebase", "Error deleting notification", e);
                                Toast.makeText(requireContext(), "Failed to delete notification", Toast.LENGTH_SHORT).show();
                            }
                        });


                Notification2Fragment pay = new Notification2Fragment();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pay).commit();

            }
        });



        addStoryImage=view.findViewById(R.id.postimage);
        addStoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), StoryActivityadd.class);
                startActivity(intent);

            }
        });


        dashboardlist = new ArrayList<>();

        mediaPlayer = new MediaPlayer();
        CombinedAdapter dashboardAdapter = new CombinedAdapter(dashboardlist,getContext(),mediaPlayer);
        LinearLayoutManager layoutManager11 = new LinearLayoutManager(getContext());
        layoutManager11.setReverseLayout(true);
        layoutManager11.setStackFromEnd(true);
        dashboardRv.setLayoutManager(layoutManager11);
        dashboardRv.showShimmerAdapter();
        dashboardRv.setNestedScrollingEnabled(false);




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

        final int[] state = new int[1];

        dashboardRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                state[0] = newState;
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0 && (state[0] == 0 || state[0] == 2)) {
                    hidetoolbar();
                } else if (dy < -10 || isRecyclerViewAtTop(recyclerView)) {
                    showtoolbar();
                }
            }
        });

        return view;
    }

    private void hidetoolbar() {
        constraintLayout3.setVisibility(View.GONE);
        horizontalScrollView.setVisibility(View.GONE);

    }

    private void showtoolbar() {
        constraintLayout3.setVisibility(View.VISIBLE);
        if (isRecyclerViewAtTop(dashboardRv)) {
            horizontalScrollView.setVisibility(View.VISIBLE);
        } else {
            horizontalScrollView.setVisibility(View.GONE);
        }

    }

    private boolean isRecyclerViewAtTop(RecyclerView recyclerView) {
        return !recyclerView.canScrollVertically(-1);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}