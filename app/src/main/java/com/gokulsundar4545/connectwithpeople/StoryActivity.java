package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.Model.Story;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.Model.UserStories;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

import static com.gokulsundar4545.ClsGlobal.isDateGreaterThen24Hours;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int count=0;
    long pressTime=0L;
    long limit=500L;

    StoriesProgressView storiesProgressView;
    ImageView image;
    CircleImageView Profile;
    TextView story_username;

    List<String> images;
    List<String> storyids;
    String userid;

    private View.OnTouchListener onTouchListener=new View.OnTouchListener( ) {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pressTime=System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now=System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit<now-pressTime;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storiesProgressView=findViewById(R.id.stories);
        image=findViewById(R.id.image);
        Profile=findViewById(R.id.profile_image);
        story_username=findViewById(R.id.story_username);

        userid=getIntent().getStringExtra("userid");
        getStorise(userid);
        userInfo(userid);
        View reverse=findViewById(R.id.revers);

        reverse.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        View skip=findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });

       skip.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onNext() {

        Glide.with(getApplicationContext()).load(images.get(++count))
                .into(image);
    }

    @Override
    public void onPrev() {
        if ((count-1)<0) return;
        Glide.with(getApplicationContext()).load(images.get(--count))
                .into(image);
    }

    @Override
    public void onComplete() {

        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy( );
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause( );
    }

    @Override
    protected void onRestart() {
        storiesProgressView.resume();
        super.onRestart( );
    }

    private void getStorise(String userid){
        images =new ArrayList<>(  );
        storyids=new ArrayList<>(  );
        DatabaseReference reference= FirebaseDatabase.getInstance( ).getReference("Story" ).child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                images.clear();
                storyids.clear();

                for (DataSnapshot Snapshot1:snapshot.child("userStories").getChildren()){

                    UserStories userStories=Snapshot1.getValue(UserStories.class);
                    if (userStories != null){
                        Picasso.get()
                                .load(userStories.getImage())
                                .into(image);
                    }

                }

                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);



            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void userInfo(String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance( ).getReference( "Users")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user=snapshot.getValue( User.class );
                story_username.setText(user.getName());
                Picasso.get()
                        .load(user.getProfile_photo())
                        .into(Profile);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}