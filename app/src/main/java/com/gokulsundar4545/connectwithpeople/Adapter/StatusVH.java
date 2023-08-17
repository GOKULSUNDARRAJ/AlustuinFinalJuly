package com.gokulsundar4545.connectwithpeople.Adapter;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class StatusVH extends RecyclerView.ViewHolder {
    ImageView statusiv;
    TextView nametv,timetv;
    String urlresult,deleteresult,timeresult;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference statusref,laststatus;
    LinearLayout ll_ss;
    public StatusVH(@NonNull @NotNull View itemView) {
        super(itemView);


    }


    public void  fetechStatus(FragmentActivity application, String key1, String key2, String time,
                              String lastm,
                              String name,
                              String url,
                              String uid){
        statusref=database.getReference("Status");
        laststatus=database.getReference("laststatus");

        ll_ss=itemView.findViewById(R.id.ll_status_item);
        statusiv=itemView.findViewById(R.id.iv_mysatatus);
        nametv=itemView.findViewById(R.id.mystatus_tv_item);
        timetv=itemView.findViewById(R.id.time_tvstatus_item);

        laststatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.hasChild(uid)){

                    statusref.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                urlresult=snapshot.child("image").getValue().toString();
                                timeresult=snapshot.child("time").getValue().toString();
                                deleteresult=snapshot.child("delete").getValue().toString();

                                Picasso.get().load(url).into(statusiv);
                                timetv.setText(timeresult);
                                statusiv.setPadding(0,0,0,0);
                                nametv.setText(name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }else {
                    ll_ss.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }
}
