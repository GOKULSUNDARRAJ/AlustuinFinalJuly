package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gokulsundar4545.connectwithpeople.Adapter.NotificationAdapter;
import com.gokulsundar4545.connectwithpeople.Model.Notification;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Notification2Fragment extends Fragment {

    ShimmerRecyclerView  recyclerView;
    ArrayList<Notification> list;

    FirebaseDatabase database;

    public Notification2Fragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view=inflater.inflate(R.layout.fragment_notification2, container, false);

       database=FirebaseDatabase.getInstance();

       recyclerView=view.findViewById(R.id.notificatioRv);
       recyclerView.showShimmerAdapter();

       list=new ArrayList<>();



        NotificationAdapter notificationAdapter=new NotificationAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);


        database.getReference()
                .child("notification")
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {

                        list.clear();
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            Notification notification=dataSnapshot.getValue(Notification.class);

                            notification.setNotificationId(dataSnapshot.getKey());
                            list.add(notification);
                        }
                        recyclerView.hideShimmerAdapter();
                        recyclerView.setAdapter(notificationAdapter);
                        notificationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
       return view;
    }
}