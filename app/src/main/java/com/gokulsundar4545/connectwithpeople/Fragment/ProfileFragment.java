package com.gokulsundar4545.connectwithpeople.Fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import com.gokulsundar4545.connectwithpeople.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    ArrayList<Follow> list;
    FragmentProfileBinding binding;

    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;


    ShimmerRecyclerView dashboardRv;

    ArrayList<Post> dashboardlist;

    public ProfileFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();

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

        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2);
        binding.dashboardRv.setLayoutManager(gridLayoutManager);


        binding.textView21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),EditUserProfile.class);
                Bundle b= ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(intent,b);

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

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dashboardlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    dashboardlist.add(post);
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
                    binding.profession.setText("#"+user.getProfission());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


         list=new ArrayList<>();


        FollowersAdapter adapter=new FollowersAdapter(list,getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);

        binding.friendrv.setLayoutManager(linearLayoutManager);
        binding.friendrv.setAdapter(adapter);

        database.getReference().child("Users")
                .child(Auth.getUid())
                .child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Follow follow=dataSnapshot.getValue(Follow.class);
                    list.add(follow);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

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






}