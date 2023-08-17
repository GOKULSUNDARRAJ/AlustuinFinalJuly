package com.gokulsundar4545.connectwithpeople.Fragment;

import static android.content.Intent.getIntent;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gokulsundar4545.connectwithpeople.Adapter.UserAdapterLike;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BottomSheetFragmentforlikes extends BottomSheetDialogFragment {
    String postId;
    private RecyclerView recyclerView;

    ArrayList<User> userList;
    UserAdapterLike userAdapter;

    FirebaseDatabase database;

    SwipeRefreshLayout Refresh1;
    ImageView close;

    public BottomSheetFragmentforlikes(String postId) {
        this.postId = postId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout_likes, container, false);

        recyclerView=view.findViewById(R.id.friendrv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        Refresh1=view.findViewById(R.id.swaplbe);
        close=view.findViewById(R.id.imageView7);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        userList=new ArrayList<>(  );



        database=FirebaseDatabase.getInstance();


        database.getReference()
                .child("posts")
                .child(postId)
                .child("likes").addValueEventListener(new ValueEventListener( ) {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            String hisuid=""+snapshot1.getRef().getKey();

                            getUsers(hisuid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        Refresh1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener( ) {
            @Override
            public void onRefresh() {
                database.getReference()
                        .child("posts")
                        .child(postId)
                        .child("likes").addValueEventListener(new ValueEventListener( ) {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                userList.clear();
                                for (DataSnapshot snapshot1:snapshot.getChildren()){
                                    String hisuid=""+snapshot1.getRef().getKey();

                                    getUsers(hisuid);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });

                Refresh1.setRefreshing(false);
            }
        });

        return view;
    }



    private void getUsers(String hisuid) {
        DatabaseReference reference=FirebaseDatabase.getInstance( ).getReference("Users");
        reference.orderByChild("uid").equalTo(hisuid)
                .addValueEventListener(new ValueEventListener( ) {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            User user=ds.getValue( User.class );
                            userList.add(user);
                        }

                        userAdapter=new UserAdapterLike(getApplicationContext(),userList,false);
                        recyclerView.setAdapter(userAdapter);
                    }


                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

}
