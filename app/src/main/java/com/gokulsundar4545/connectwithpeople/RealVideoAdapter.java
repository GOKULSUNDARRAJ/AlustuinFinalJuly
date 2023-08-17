package com.gokulsundar4545.connectwithpeople;

import static com.gokulsundar4545.connectwithpeople.TextEditorDialogFragment.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.gokulsundar4545.connectwithpeople.Fragment.BottomSheetFragmentforlikes;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.Model.VedioMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RealVideoAdapter extends RecyclerView.Adapter<RealVideoAdapter.RealVideoViewHolder> {


    private static final String PREF_NAME = "switchPreferences";
    private static final String SWITCH_KEY = "switchState";


    private List<VedioMode> videoList;
    private Context context;
    private Handler handler = new Handler();
    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ViewPager2 recyclerView;
    private int completionCount = 0;
    SharedPreferences sharedPreferences;
    public RealVideoAdapter(Context context, List<VedioMode> videoList, ViewPager2 recyclerView, SharedPreferences sharedPreferences) {
        this.context = context;
        this.videoList = videoList;
        this.recyclerView = recyclerView;
        this.sharedPreferences=sharedPreferences;
    }

    @NonNull
    @Override
    public RealVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reels, parent, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        return new RealVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RealVideoViewHolder holder, int position) {
        VedioMode video = videoList.get(position);




        // Add listener to set the favorite icon based on whether the user is in favorites
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(userId).child("youfollowing");
            userFavoritesRef.orderByChild("id").equalTo(video.getVedioBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // User is in favorites, set unfavorite icon
                        holder.followBtn.setText("following");
                        holder.followBtn.setTextColor(context.getResources().getColor(R.color.white));
                        holder.followBtn.setBackgroundResource(R.drawable.whiteline5);


                    } else {
                        // User is not in favorites, set favorite icon
                        holder.followBtn.setText("follow");
                        holder.followBtn.setBackgroundResource(R.drawable.whiteline5);
                        holder.followBtn.setTextColor(context.getResources().getColor(R.color.white));


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to update favorite icon");
                }
            });
        }



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("vedio").child(video.getVedioId()).child("comments");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int commentCount = 0;
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    if (commentSnapshot.getValue(Comment.class) != null) {
                        commentCount++;
                    }
                }


                holder.commentcount.setText(""+commentCount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });





// Define the DatabaseReference
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("vedio");

// Assuming the video ID is "ItmbIBb6FJSMLjPgwI4ln0rGtDH2" and the current user ID
        String videoId = video.getVedioId();
        String currentUserId =FirebaseAuth.getInstance().getCurrentUser().getUid();

// Reference to the specific video and likes
        databaseReference2.child(videoId).child("likes").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the current user has liked the video
                Boolean hasLiked = dataSnapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(hasLiked)) {
                    // Show a Toast if the current user has liked the video

                    holder.like.setImageResource(R.drawable.ic_baseline_favorite_24); // Update icon to liked
                } else {
                    // Show a Toast if the current user has not liked the video
                    holder.like.setImageResource(R.drawable.baseline_favorite_border_24); // Update icon to default (unliked)
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(context, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


// Define the Firebase Realtime Database reference
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("vedio");

// Assuming the video ID is "-O3CZkyjAY9M7-AlrZWa"
        String videoId2 = video.getVedioId();



        databaseReference3.child(videoId2).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the number of likes by counting the children
                long likeCount = dataSnapshot.getChildrenCount();
                holder.likescount.setText(""+likeCount);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(context, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.videoView.setVideoPath(video.getVedioUrl());
        holder.caption.setText(video.getVedioDescription());
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                holder.videoView.start();
                holder.shade1.setVisibility(View.VISIBLE);
                holder.shade2.setVisibility(View.GONE);
                updateProgressBar(holder); // Start updating the ProgressBar

            }
        });



        boolean switchState = sharedPreferences.getBoolean("switchState", false);





        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {


                if (switchState) {
                    completionCount++;
                    if (completionCount >= 3) {
                        int nextPosition = holder.getAdapterPosition() + 1;
                        if (nextPosition < videoList.size()) {
                            recyclerView.setCurrentItem(nextPosition, true);
                        } else {
                            recyclerView.setCurrentItem(0, true); // Go back to the first video if it's the last one
                        }
                        completionCount = 0;
                    } else {
                        holder.videoView.start();
                    }
                } else {

                    holder.videoView.start();
                }

            }
        });

        // Handle touch events for long press
        holder.videoView.setOnTouchListener(new View.OnTouchListener() {
            private Handler handler = new Handler();
            private Runnable longPressRunnable;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Start a long press detection
                        longPressRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (holder.videoView.isPlaying()) {
                                    holder.videoView.pause();

                                }
                            }
                        };
                        handler.postDelayed(longPressRunnable, 500); // 500ms for long press
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Cancel the long press detection
                        handler.removeCallbacks(longPressRunnable);
                        if (!holder.videoView.isPlaying()) {
                            holder.videoView.start();
                            updateProgressBar(holder);
                        }
                        return true;
                }
                return false;
            }
        });



        Auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();


        database.getReference().child("Users").child(video.getVedioBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    User user = snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .into(holder.profile);
                    holder.username.setText(user.getName());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.like.setOnClickListener(view -> toggleLike( video.getVedioId(),holder.like));


        holder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite(holder.followBtn,video.getVedioBy(),position);
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragmentforrealcom bottomSheetFragment = new BottomSheetFragmentforrealcom(video.getVedioId());
                bottomSheetFragment.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });



    }

    private void updateProgressBar(RealVideoViewHolder holder) {
        final int videoDuration = holder.videoView.getDuration();
        holder.progressBar.setMax(videoDuration);

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (holder.videoView.isPlaying()) {
                    int currentPosition = holder.videoView.getCurrentPosition();
                    holder.progressBar.setProgress(currentPosition);
                    // Update every 50 milliseconds for smoother progress
                    handler.postDelayed(this, 50); // More frequent updates for smoothness
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class RealVideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ConstraintLayout shade1, shade2;
        ProgressBar progressBar;
        CircleImageView profile;
        TextView username,caption,followBtn;
        ImageView like;
        TextView likescount,commentcount;
        ImageView comment;

        public RealVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            shade1 = itemView.findViewById(R.id.shade1);
            shade2 = itemView.findViewById(R.id.shade2);
            progressBar = itemView.findViewById(R.id.progress_bar);
            profile=itemView.findViewById(R.id.profile_image);
            username=itemView.findViewById(R.id.username);
            caption=itemView.findViewById(R.id.vedioname);
            followBtn=itemView.findViewById(R.id.followbtn);
            like=itemView.findViewById(R.id.like);
            likescount=itemView.findViewById(R.id.likecount);
            comment=itemView.findViewById(R.id.comment);
            commentcount=itemView.findViewById(R.id.commentcount);
        }
    }




    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    private void toggleLike(String videoId, ImageView likeIcon) {
        DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("vedio").child(videoId).child("likes");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        videoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(currentUserId)) {
                    // User has already liked the video, so unlike it
                    videoRef.child(currentUserId).removeValue();
                    likeIcon.setImageResource(R.drawable.baseline_favorite_border_24); // Update icon to default (unliked)
                } else {
                    // User has not liked the video, so like it
                    videoRef.child(currentUserId).setValue(true);
                    likeIcon.setImageResource(R.drawable.ic_baseline_favorite_24); // Update icon to liked
                }

                // List all likes after toggling
                listAllLikes(videoId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Log.e("ToggleLike", "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void listAllLikes(String videoId) {
        DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("vedio").child(videoId).child("likes");

        videoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear previous list if needed
                    List<String> likedUserIds = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userId = snapshot.getKey();
                        likedUserIds.add(userId);
                    }

                    // Now `likedUserIds` contains all user IDs who liked the video
                    // You can use this list as needed (e.g., display it in the UI, etc.)
                    Log.d("ListAllLikes", "Liked user IDs: " + likedUserIds.toString());
                } else {
                    Log.d("ListAllLikes", "No likes yet.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Log.e("ListAllLikes", "Database error: " + databaseError.getMessage());
            }
        });
    }


    private void toggleFavorite(TextView followBtn, String user, int position) {
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
                        followBtn.setBackgroundResource(R.drawable.whiteline5);
                        followBtn.setTextColor(context.getResources().getColor(R.color.white));
                        showToast("Started unfollowing");
                    } else {
                        // User is not in favorites, add them
                        String followUid = userFavoritesRef.push().getKey(); // Generate a unique key for the follow
                        userFavoritesRef.child(followUid).child("id").setValue(user);
                        followBtn.setText("following");
                        followBtn.setBackgroundResource(R.drawable.whiteline5);
                        followBtn.setTextColor(context.getResources().getColor(R.color.white));
                        showToast("Started following");
                    }

                    // Notify adapter of item change for the specific position
                    notifyItemChanged(position);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to update favorites");
                }
            });

            // Also update the follow status for the other user's followers
            DatabaseReference userFollowersRef = databaseReference.child("Users").child(user).child("yourfollowers");
            userFollowersRef.orderByChild("id").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Current user is already a follower, remove them
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            childSnapshot.getRef().removeValue();
                        }
                    } else {
                        // Current user is not a follower, add them
                        String followUid = userFollowersRef.push().getKey(); // Generate a unique key for the follow
                        userFollowersRef.child(followUid).child("id").setValue(userId);
                    }

                    // Notify adapter of item change for the specific position
                    notifyItemChanged(position);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to update followers");
                }
            });
        }
    }





}