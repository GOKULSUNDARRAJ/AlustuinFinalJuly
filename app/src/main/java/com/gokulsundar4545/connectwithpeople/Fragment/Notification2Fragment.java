package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    ImageView back;

    SwipeRefreshLayout refreshLayout;

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

       back=view.findViewById(R.id.imageView6);

       database=FirebaseDatabase.getInstance();

       recyclerView=view.findViewById(R.id.notificatioRv);
       recyclerView.showShimmerAdapter();

       refreshLayout=view.findViewById(R.id.refresh);


       list=new ArrayList<>();



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HomeFragment pf = new HomeFragment();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pf).commit();

            }
        });


        NotificationAdapter notificationAdapter=new NotificationAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setNestedScrollingEnabled(false);


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


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener( ) {
            @Override
            public void onRefresh() {

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

                refreshLayout.setRefreshing(false);
            }
        });
       return view;
    }
}