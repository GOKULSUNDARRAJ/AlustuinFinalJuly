package com.gokulsundar4545.connectwithpeople.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.Model.VedioMode;
import com.gokulsundar4545.connectwithpeople.R;

import com.gokulsundar4545.connectwithpeople.databinding.FragmentAddBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class AddPostFragment extends Fragment {

    FragmentAddBinding binding;
    Uri uri;


    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage Storage;

    ProgressDialog progressDialog;


    public static final  int PICK_VEDIO=1;

    VideoView videoView;
    EditText editText;
    Button button;

    ImageView choosvedio;

    private Uri VedioUri;
    MediaController mediaController;

    DatabaseReference databaseReference;
    VedioMode member;
    UploadTask uploadTask;




    public AddPostFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        member=new VedioMode();





        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        Storage=FirebaseStorage.getInstance();
        progressDialog=new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentAddBinding.inflate(inflater, container, false);

        mediaController =new MediaController(getContext());
        binding.postvedio.setMediaController(mediaController);
        binding.postvedio.start();




         binding.addvedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent,PICK_VEDIO);
            }
        });



        binding.profileImage.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Fragment proFra=new ProfileFragment();
                 FragmentTransaction fm=getActivity().getSupportFragmentManager().beginTransaction();
                 fm.replace(R.id.container,proFra).commit();
             }
         });


        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Post Uploading");
        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(true);
        database.getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    User user=snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .into(binding.profileImage);
                    binding.name.setText(user.getName());
                    binding.profession.setText(user.getProfission());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadPost();
            }
        });
        binding.vediobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadVedio();
            }
        });



        binding.postdescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String description=binding.postdescription.getText().toString();
                if (!description.isEmpty()){
                    binding.postbtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_input_bg));
                    binding.postbtn.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.postbtn.setEnabled(true);
                    binding.vediobtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_input_bg));
                    binding.vediobtn.setTextColor(getContext().getResources().getColor(R.color.white));
                    binding.vediobtn.setEnabled(true);
                }else {
                    binding.postbtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.active_input_bg));
                    binding.postbtn.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.postbtn.setEnabled(false);
                    binding.vediobtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.active_input_bg));
                    binding.vediobtn.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.vediobtn.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });
        return binding.getRoot();
    }

    private void UploadVedio() {


        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Reels Uploading");
        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(true);

        progressDialog.show();

        String vedioname=binding.postdescription.getText().toString().trim();

        if (VedioUri!=null || !TextUtils.isEmpty(vedioname)){
            final StorageReference reference=Storage.getReference().child("Vedio")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(new Date().getTime()+"");
            reference.putFile(VedioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            VedioMode post=new VedioMode();

                            post.setVedioUrl(uri.toString());
                            post.setVedioBy(FirebaseAuth.getInstance().getUid());
                            post.setVedioDescription(binding.postdescription.getText().toString());
                            post.setVedioposterAt(new Date().getTime());

                            database.getReference().child("vedio")
                                    .push()
                                    .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {


                                    progressDialog.dismiss();
                                    binding.vediobtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.btn_input_ww));
                                    binding.vediobtn.setTextColor(getContext().getResources().getColor(R.color.white));
                                    binding.vediobtn.setEnabled(false);
                                    Toast.makeText(getContext(), "Vedio Posted Successfully", Toast.LENGTH_SHORT).show();
                                    VedioViewFragment pf=new VedioViewFragment();

                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.fram_layout,pf).commit();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    private void UploadPost(){
        progressDialog.show();
        final StorageReference reference=Storage.getReference().child("posts")
                .child(FirebaseAuth.getInstance().getUid())
                .child(new Date().getTime()+"");
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Post post=new Post();

                        post.setPostImg(uri.toString());
                        post.setPostedBy(FirebaseAuth.getInstance().getUid());
                        post.setPostDescription(binding.postdescription.getText().toString());
                        post.setPosterAt(new Date().getTime());

                        database.getReference().child("posts")
                                .push()
                                .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                progressDialog.dismiss();
                                binding.postbtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.btn_input_ww));
                                binding.postbtn.setTextColor(getContext().getResources().getColor(R.color.white));
                                binding.postbtn.setEnabled(false);

                                Toast.makeText(getContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();
                                HomeFragment pf=new HomeFragment();

                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fram_layout,pf).commit();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData()!=null){
            uri=data.getData();
            binding.postimage.setImageURI(uri);
            binding.postbtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.btn_input_bg));
            binding.postbtn.setTextColor(getContext().getResources().getColor(R.color.white));
            binding.postbtn.setEnabled(true);
        }else {

        }

        if (requestCode==PICK_VEDIO || requestCode==RESULT_OK ||
                    data!=null || data.getData() !=null){
                VedioUri=data.getData();
                binding.postvedio.setVideoURI(VedioUri);
                binding.vediobtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.btn_input_bg));
                binding.vediobtn.setTextColor(getContext().getResources().getColor(R.color.white));
                binding.vediobtn.setEnabled(true);



            }

    }
}