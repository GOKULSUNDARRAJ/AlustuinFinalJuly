package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gokulsundar4545.Data;
import com.gokulsundar4545.connectwithpeople.Model.StatusModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class ShowStatus extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    List<String> image;
    List<String> uid;
    List<String> caption;
    List<String> delete;
    List<String> time;

    FirebaseFirestore dp=FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    StatusModel statusModel;
    StoriesProgressView storiesProgressView;
    String userid;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference statusref,laststatus;

    int couter=0;

    ImageView s_iv,useriv;
    TextView tvname,storyTv,captionTv,timeTv;


    private View.OnTouchListener onTouchListener=new View.OnTouchListener( ) {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_status);

        storiesProgressView=findViewById(R.id.stories);

        s_iv=findViewById(R.id.iv_status_show);
        useriv=findViewById(R.id.iv_mysatatus);

        tvname=findViewById(R.id.mystatus_tv);
        timeTv=findViewById(R.id.time_tvstatus);
        captionTv=findViewById(R.id.caption);
        storyTv=findViewById(R.id.statuscount);



        laststatus=database.getReference("laststatus");

        statusModel=new StatusModel();


        View reverse=findViewById(R.id.viewnext);
        reverse.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });

        reverse.setOnTouchListener(onTouchListener);


        reverse.setOnLongClickListener(new View.OnLongClickListener( ) {
            @Override
            public boolean onLongClick(View v) {
                storiesProgressView.pause();
                return false;
            }
        });

        reverse.setOnTouchListener(onTouchListener);


        View skip=findViewById(R.id.viewprev);
        reverse.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });

        skip.setOnTouchListener(onTouchListener);



        skip.setOnLongClickListener(new View.OnLongClickListener( ) {
            @Override
            public boolean onLongClick(View v) {
                storiesProgressView.pause();
                return false;
            }
        });

        skip.setOnTouchListener(onTouchListener);


        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            userid=extras.getString("uid");

        }else {

        }

        statusref=database.getReference("Status").child(userid);


    }


    @Override
    protected void onStart() {
        super.onStart( );

        try {
            getStories(userid);

        }catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show( );
        }
    }

    private void getStories(String userid) {

        image=new ArrayList<>(  );
        uid=new ArrayList<>(  );
        delete=new ArrayList<>(  );
        caption=new ArrayList<>(  );
        time=new ArrayList<>(  );

        statusref.addListenerForSingleValueEvent(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                image.clear();
                uid.clear();
                delete.clear();
                caption.clear();
                time.clear();


                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    StatusModel statusModel=snapshot1.getValue( StatusModel.class );

                    long timecurrent=System.currentTimeMillis();

                    image.add(statusModel.getImage());
                    uid.add(statusModel.getUid());
                    time.add(statusModel.getTime());
                    delete.add(statusModel.getDelete());
                    caption.add(statusModel.getCaption());


                }

                storiesProgressView.setStoriesCount(image.size());
                storiesProgressView.setStoriesListener(ShowStatus.this);
                storiesProgressView.startStories(couter);
                storiesProgressView.setStoryDuration(5000L);

                s_iv.setVisibility(View.VISIBLE);

                captionTv.setText(caption.get(couter));
                Picasso.get().load(image.get(couter))
                        .into(s_iv);

                timeTv.setText(time.get(couter));

                fetchuserInfo(uid.get(couter));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void fetchuserInfo(String s) {

    }

    @Override
    public void onNext() {

    }

    @Override
    public void onPrev() {

        if ((couter-1)<0) return;
        Picasso.get().load(image.get(--couter)).into(s_iv);
        captionTv.setText(caption.get(couter));

    }

    @Override
    public void onComplete() {
        finish();

    }
}