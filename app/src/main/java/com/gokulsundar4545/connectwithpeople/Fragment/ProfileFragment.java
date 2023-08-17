package com.gokulsundar4545.connectwithpeople.Fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gokulsundar4545.connectwithpeople.Adapter.FollowersAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.MypostAdapter;
import com.gokulsundar4545.connectwithpeople.Adapter.PostAdapter;
import com.gokulsundar4545.connectwithpeople.ChartActivity;
import com.gokulsundar4545.connectwithpeople.CommentActivity;
import com.gokulsundar4545.connectwithpeople.EditUserProfile;
import com.gokulsundar4545.connectwithpeople.LoginActivity;
import com.gokulsundar4545.connectwithpeople.Model.Follow;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.PayMentActivity;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.StartActivity;
import com.gokulsundar4545.connectwithpeople.StartActivityFirst;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    ArrayList<Follow> list;
    FragmentProfileBinding binding;

    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference PostRef;



    ShimmerRecyclerView dashboardRv;

    ArrayList<Post> dashboardlist;
    String firebaseUser;

    private int count=0;
    public ProfileFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        firebaseUser=Auth.getCurrentUser().getUid();

        PostRef=FirebaseDatabase.getInstance().getReference("post");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentProfileBinding.inflate(inflater,container,false);

        dashboardlist = new ArrayList<>();



        MypostAdapter dashboardAdapter = new MypostAdapter(dashboardlist, getContext());
       // LinearLayoutManager layoutManager11 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);

        //layoutManager11.setReverseLayout(true);
        //layoutManager11.setStackFromEnd(true);
        //binding.dashboardRv.setLayoutManager(layoutManager11);
        binding.dashboardRv.setNestedScrollingEnabled(false);

        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),3);
        binding.dashboardRv.setLayoutManager(gridLayoutManager);


        binding.textView21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),EditUserProfile.class);
                Bundle b= ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(intent,b);

            }
        });


        PostRef=FirebaseDatabase.getInstance().getReference().child("posts");
        PostRef.orderByChild("postedBy").equalTo(firebaseUser).addValueEventListener(new ValueEventListener( ) {
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


        binding.menuicon.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                final Dialog dialog=new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.options);

                LinearLayout shareoption=dialog.findViewById(R.id.Settings);
                shareoption.setOnClickListener(new View.OnClickListener( ) {
                    @Override
                    public void onClick(View v) {
                        SettindsFragment pf = new SettindsFragment();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fram_layout, pf).commit();

                        dialog.dismiss();

                    }
                });

                LinearLayout about=dialog.findViewById(R.id.about);
                about.setOnClickListener(new View.OnClickListener( ) {
                    @Override
                    public void onClick(View v) {
                        About pf = new About();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fram_layout, pf).commit();

                        dialog.dismiss();

                    }
                });

                LinearLayout updat=dialog.findViewById(R.id.editprofile);
                updat.setOnClickListener(new View.OnClickListener( ) {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(v.getContext(),EditUserProfile.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                LinearLayout logout=dialog.findViewById(R.id.logout);
                logout.setOnClickListener(new View.OnClickListener( ) {
                    @Override
                    public void onClick(View v) {



                        Logout();
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations=R.style.DialoAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });
        binding.textView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FlowersFragment pf = new FlowersFragment();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pf).commit();

            }
        });

        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Camera", Toast.LENGTH_SHORT).show();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fram_layout, new AddPostFragment()).addToBackStack(null).commit();

            }
        });

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener( ) {
            @Override
            public void onRefresh() {
                FirebaseUser firebaseUser=FirebaseAuth.getInstance( ).getCurrentUser( );
                database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dashboardlist.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {


                            Post post = dataSnapshot.getValue(Post.class);
                            post.setPostId(dataSnapshot.getKey());
                            if (firebaseUser.getUid().equals(post.getPostedBy())){
                                dashboardlist.add(post);
                            }

                        }
                        binding.dashboardRv.setAdapter(dashboardAdapter);
                        binding.dashboardRv.hideShimmerAdapter();
                        dashboardAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                binding.refresh.setRefreshing(false);
            }
        });

        FirebaseUser firebaseUser=FirebaseAuth.getInstance( ).getCurrentUser( );
        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dashboardlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {


                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    if (firebaseUser.getUid().equals(post.getPostedBy())){
                        dashboardlist.add(post);
                    }

                }
                binding.dashboardRv.setAdapter(dashboardAdapter);
                binding.dashboardRv.hideShimmerAdapter();
                dashboardAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        database.getReference().child("Users").child(Auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if (snapshot.exists()){

                    User user=snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getCover_photo())
                            .into(binding.coverphoto);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .into(binding.pro);
                    binding.textView3.setText(user.getName());
                    binding.textView5.setText(user.getProfission());
                    binding.textView16.setText(user.getFollowerCount()+"");
                    binding.username.setText(user.getName());
                    binding.profession.setText("@"+user.getProfission());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        binding.changecoverphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,11);
            }
        });

        binding.verifiedAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,22);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==11){
            if (data.getData()!=null){
                Uri uri=data.getData();
                binding.coverphoto.setImageURI(uri);

                final StorageReference reference=storage.getReference().child("Cover_photo").child(FirebaseAuth.getInstance().getUid());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Cover photo Saved", Toast.LENGTH_SHORT).show();

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                database.getReference().child("Users").child(Auth.getUid()).child("Cover_photo").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        }
        else {
            if (data.getData()!=null){
                Uri uri=data.getData();
                binding.pro.setImageURI(uri);

                final StorageReference reference=storage.getReference().child("Profile_photo").child(FirebaseAuth.getInstance().getUid());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Profile photo Saved", Toast.LENGTH_SHORT).show();

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                database.getReference().child("Users").child(Auth.getUid()).child("Profile_photo").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        }

    }




    private void Logout() {
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();

        Dialog dialog=new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT ));


        TextView login=dialog.findViewById(R.id.logout);
        TextView cancel=dialog.findViewById(R.id.cancel);


        login.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getContext(), StartActivityFirst.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();



    }

}