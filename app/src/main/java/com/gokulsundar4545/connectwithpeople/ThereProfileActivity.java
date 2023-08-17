package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gokulsundar4545.connectwithpeople.Adapter.TherepostAdapter;
import com.gokulsundar4545.connectwithpeople.Model.Follow;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.databinding.ActivityThereProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ThereProfileActivity extends AppCompatActivity {


    ArrayList<Follow> list;

    ActivityThereProfileBinding binding;

    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;


    ShimmerRecyclerView dashboardRv;

    DatabaseReference PostRef;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    String uid;
    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityThereProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(userId);

            DatabaseReference userDatabaseReference2 = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(uid);

            DatabaseReference yourFollowersRef = userDatabaseReference.child("yourfollowers");
            DatabaseReference youFollowingRef = userDatabaseReference.child("youfollowing");

            // Fetch "yourfollowers" IDs
            yourFollowersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> yourFollowersIds = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String id = dataSnapshot.child("id").getValue(String.class);
                        if (id != null) {
                            yourFollowersIds.add(id);
                        }
                    }

                    // Fetch "youfollowing" IDs
                    youFollowingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<String> youFollowingIds = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String id = dataSnapshot.child("id").getValue(String.class);
                                if (id != null) {
                                    youFollowingIds.add(id);
                                }
                            }

                            // Check if both IDs are in their respective lists
                            boolean isMutualFollow = yourFollowersIds.contains(uid) && youFollowingIds.contains(uid);

                            // Fetch the account type
                            userDatabaseReference2.child("accountType").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String accountType = snapshot.getValue(String.class);

                                    if ("private".equals(accountType) && isMutualFollow) {
                                        Toast.makeText(ThereProfileActivity.this, "Mutual Follow and Private Account", Toast.LENGTH_LONG).show();
                                        binding.mutalfllow.setVisibility(View.VISIBLE);
                                        TextView textView = findViewById(R.id.dashboardRv1);
                                        textView.setVisibility(View.VISIBLE);
                                        binding.dashboardRv.setVisibility(View.VISIBLE);
                                        binding.textView25.setVisibility(View.VISIBLE);


                                    } else if ("public".equals(accountType)) {
                                        Toast.makeText(ThereProfileActivity.this, "Mutual not Follow and public Account", Toast.LENGTH_LONG).show();
                                        binding.mutalfllow.setVisibility(View.VISIBLE);
                                        TextView textView = findViewById(R.id.dashboardRv1);
                                        textView.setVisibility(View.VISIBLE);
                                        binding.dashboardRv.setVisibility(View.VISIBLE);
                                        binding.textView25.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(ThereProfileActivity.this, "Not Following or Not Mutual", Toast.LENGTH_LONG).show();
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ThereProfileActivity.this, "Failed to load account type", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ThereProfileActivity.this, "Failed to get follow count", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ThereProfileActivity.this, "Failed to get follow count", Toast.LENGTH_SHORT).show();
                }
            });
        }



        binding.biolayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragmentforbiothere bottomSheetFragment = new BottomSheetFragmentforbiothere(uid);
                bottomSheetFragment.show((ThereProfileActivity.this).getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        binding.recyclerview12.setLayoutManager(new LinearLayoutManager(ThereProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
        List<String> keysList = new ArrayList<>(); // Replace with your actual data list

        // Corrected usage with parameters swapped
        StoryHighlightsAdapter adapter = new StoryHighlightsAdapter( keysList,ThereProfileActivity.this, uid);


        binding.recyclerview12.setAdapter(adapter);


        DatabaseReference highlightsRef = FirebaseDatabase.getInstance().getReference()
                .child("storieshighlites") // Updated name here
                .child(uid)
                .child("userhighlites");

        highlightsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> keysList = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    keysList.add(key);  // Add each key to the list

                }

                // Now, you can pass keysList to your RecyclerView adapter
                StoryHighlightsAdapter adapter = new StoryHighlightsAdapter(keysList,ThereProfileActivity.this, uid);
                binding.recyclerview12.setAdapter(adapter); // Update setting adapter

                if (keysList.isEmpty()){
                    binding.mutalfllow.setVisibility(View.GONE);
                }
                // Show Toast with item count
                Toast.makeText(ThereProfileActivity.this, "Items loaded: " + keysList.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
                Log.e("FirebaseError", "Error getting child keys: " + databaseError.getMessage());
            }
        });




        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        binding.textView21.setOnClickListener(view -> toggleFavorite(binding.textView21, uid));
        PostRef=FirebaseDatabase.getInstance().getReference().child("posts");
        PostRef.orderByChild("postedBy").equalTo(uid).addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    count=(int) snapshot.getChildrenCount();

                    binding.textView18.setText(Integer.toString(count)+"");
                }else {
                    binding.textView18.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        FirebaseUser currentUser1 = firebaseAuth.getCurrentUser();
        if (currentUser1 != null) {
            String userId = currentUser1.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(userId).child("youfollowing");
            userFavoritesRef.orderByChild("id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // User is in favorites, set unfavorite icon
                        binding.textView21.setText("following");
                        binding.textView21.setTextColor(ThereProfileActivity.this.getResources().getColor(R.color.black));
                        binding.textView21.setBackgroundResource(R.drawable.unfollow);
                    } else {
                        // User is not in favorites, set favorite icon
                        binding.textView21.setText("follow");
                        binding.textView21.setBackgroundResource(R.drawable.follow);
                        binding.textView21.setTextColor(ThereProfileActivity.this.getResources().getColor(R.color.white));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to update favorite icon");
                }
            });
        }


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        // Retrieve user data from Firebase Realtime Database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user data from snapshot

                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    String links1 = dataSnapshot.child("links").getValue(String.class);

                    binding.profession.setText(bio);

                    binding.textView24.setText(links1);

                    try {
                        if (links1.isEmpty()){
                            binding.linkslayout.setVisibility(View.GONE);

                        }else {
                            binding.linkslayout.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){

                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(ThereProfileActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });




        Query query=FirebaseDatabase.getInstance( ).getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot ds:snapshot.getChildren()){
                    String name=""+ds.child("name").getValue();
                    String professional=""+ds.child("profission").getValue();
                    String profile=""+ds.child("Profile_photo").getValue();
                    String followcount=""+ds.child("followerCount").getValue(  );
                    String hisuid=""+ds.child("uid").getValue();
                    String token=""+ds.child("token").getValue();

                    binding.textView3.setText(name);
                    binding.username.setText(name);


                    try {
                        Picasso.get()
                                .load(profile)
                                .placeholder(R.drawable.profile)
                                .into(binding.pro);
                    }catch (Exception e){

                    }


                    binding.textView25.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(getApplicationContext(), ChartActivity.class);
                            intent.putExtra("hisUId",hisuid);
                            intent.putExtra("myUId",FirebaseAuth.getInstance().getCurrentUser().getUid());
                            intent.putExtra("hisToken",token);
                        view.getContext().startActivity(intent);

                        }
                    });


                    binding.following.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(getApplicationContext(), ThereFollowingActivity.class);
                            intent.putExtra("hisUId",uid);
                            startActivity(intent);

                        }
                    });

                    binding.followes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(getApplicationContext(), ThereFollowersActivity.class);
                            intent.putExtra("hisUId",uid);
                            startActivity(intent);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser2 = FirebaseAuth.getInstance().getCurrentUser();;
        if (currentUser2 != null) {
            String userId = currentUser2.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(uid).child("youfollowing");

            // Get the count of "youfollowing" node children
            userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long count = snapshot.getChildrenCount(); // Get the count
                    binding.textView17.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to get follow count");
                }
            });


        }

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(uid).child("yourfollowers");

            // Get the count of "youfollowing" node children
            userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long count = snapshot.getChildrenCount(); // Get the count
                    binding.textView16.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to get follow count");
                }
            });


        }


        dashboardRv=findViewById(R.id.dashboardRv);



        loadHisPosts();


    }

    private void loadHisPosts() {


        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
        dashboardRv.setLayoutManager(gridLayoutManager);


        ArrayList<Post> dashboardlist = new ArrayList<>();

        DatabaseReference reference=FirebaseDatabase.getInstance( ).getReference("posts" );

        TherepostAdapter dashboardAdapter = new TherepostAdapter(dashboardlist,this);
        Query query=reference.orderByChild("postedBy").equalTo(uid);

        query.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dashboardlist.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Post post=ds.getValue(Post.class);
                    dashboardlist.add(post);

                    dashboardRv.setAdapter(dashboardAdapter);

                }




            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }





    private void toggleFavorite(TextView followBtn, String user) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(userId).child("youfollowing");
            userFavoritesRef.orderByChild("id").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // User is already in favorites, remove them
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            childSnapshot.getRef().removeValue();
                        }
                        followBtn.setText("follow");
                        followBtn.setBackgroundResource(R.drawable.follow);
                        followBtn.setTextColor(ThereProfileActivity.this.getResources().getColor(R.color.white));
                        showToast("Started following");
                    } else {
                        // User is not in favorites, add them
                        String followUid = userFavoritesRef.push().getKey(); // Generate a unique key for the follow
                        userFavoritesRef.child(followUid).child("id").setValue(user);
                        followBtn.setText("following");
                        followBtn.setBackgroundResource(R.drawable.unfollow);
                        followBtn.setTextColor(ThereProfileActivity.this.getResources().getColor(R.color.black));
                        showToast("Started unfollowing");
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to update favorites");
                }
            });





            DatabaseReference userFavoritesRef2 = databaseReference.child("Users").child(user).child("yourfollowers");
            userFavoritesRef2.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // User is already in favorites, remove them
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            childSnapshot.getRef().removeValue();
                        }

                    } else {
                        // User is not in favorites, add them
                        String followUid = userFavoritesRef2.push().getKey(); // Generate a unique key for the follow
                        userFavoritesRef2.child(followUid).child("id").setValue(userId);

                    }

                    // Notify adapter of item change for the specific position

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to update favorites");
                }
            });
        }
    }
}