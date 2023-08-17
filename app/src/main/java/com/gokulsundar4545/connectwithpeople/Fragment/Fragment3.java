package com.gokulsundar4545.connectwithpeople.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gokulsundar4545.connectwithpeople.Adapter.StatusVH;
import com.gokulsundar4545.connectwithpeople.Model.ListModel;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.TedPermission;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Fragment3 extends Fragment implements View.OnClickListener {

    TextView taptoaddtv,mystatustv;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference statusref,chatlist;
    String uid,url,time,delete;
    ImageView iv_mystatus;
    private Uri imageuri;
    RecyclerView recyclerView;

    private static final int IMAGE_PICK_GALARY_CODE = 400;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_3, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        taptoaddtv=getActivity().findViewById(R.id.tabtoadd_tv);
        mystatustv=getActivity().findViewById(R.id.mystatus_tv);
        iv_mystatus=getActivity().findViewById(R.id.iv_mysatatus);
        recyclerView=getActivity().findViewById(R.id.rv_f3);

        statusref=database.getReference("laststatus");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();

        taptoaddtv.setOnClickListener(this);
        mystatustv.setOnClickListener(this);

        statusref.keepSynced(true);
        chatlist=database.getReference("chat list").child(uid);


        checkpermission();

        fetchStatus();


    }

    private void fetchStatus() {

        statusref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    url=snapshot.child("image").getValue().toString();
                    time=snapshot.child("time").getValue().toString();
                    delete=snapshot.child("delete").getValue().toString();

                    Picasso.get().load(url).into(iv_mystatus);
                    taptoaddtv.setText(time);
                    iv_mystatus.setPadding(0,0,0,0);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void checkpermission() {



       TedPermission.with(getActivity())
               .setPermissionListener(new com.gun0912.tedpermission.PermissionListener( ) {
                   @Override
                   public void onPermissionGranted() {

                   }

                   @Override
                   public void onPermissionDenied(List<String> deniedPermissions) {

                   }
               }).setPermissions(Manifest.permission.CAMERA)
               .check();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tabtoadd_tv:
                openCameraBs();

            case R.id.mystatus_tv:
                openCameraBs();

        }

    }

    private void openCameraBs() {
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.camera_bs);

        TextView openCamera=dialog.findViewById(R.id.open_camera);
        TextView opengallery=dialog.findViewById(R.id.open_gallery);

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),UploadImage.class);
                startActivity(intent);
            }
        });

        opengallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickimage();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void pickimage() {


        Intent intent=new Intent(Intent.ACTION_PICK
                , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALARY_CODE);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        try {
            if (requestCode==IMAGE_PICK_GALARY_CODE || resultCode==RESULT_OK ){
                imageuri=data.getData();

                    String url =imageuri.toString();
                    Intent intent=new Intent(getContext(),ImageActivity.class);
                    intent.putExtra("u",url);
                    startActivity(intent);
            }

        }catch (Exception e){
            e.printStackTrace();

        }

    }


    @Override
    public void onStart() {
        super.onStart( );
    }
}