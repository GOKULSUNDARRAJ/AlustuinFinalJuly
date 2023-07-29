package com.gokulsundar4545.connectwithpeople.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.VedioMode;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Member;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class AddVedioFragment extends Fragment {

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
    FirebaseStorage Storage;



    FirebaseAuth auth;
    FirebaseDatabase database;

    ProgressDialog progressDialog;





    public AddVedioFragment() {
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
        View view=inflater.inflate(R
                .layout.fragment_addvedio, container, false);

        choosvedio=view.findViewById(R.id.addvedio);


        videoView=view.findViewById(R.id.postvedio);
        editText=view.findViewById(R.id.vediodescription);
        button=view.findViewById(R.id.postbtn);

        mediaController =new MediaController(getContext());
        videoView.setMediaController(mediaController);
        videoView.start();


        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Post Uploading");
        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(true);

        choosvedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent,PICK_VEDIO);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                UploadVedio();
            }
        });


        return view;
    }

    private void UploadVedio() {



        String vedioname=editText.getText().toString().trim();

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
                            post.setVedioDescription(editText.getText().toString());
                            post.setVedioposterAt(new Date().getTime());

                            database.getReference().child("vedio")
                                    .push()
                                    .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {


                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Vedio Posted Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==PICK_VEDIO || requestCode==RESULT_OK ||
        data!=null || data.getData() !=null){
            VedioUri=data.getData();
            videoView.setVideoURI(VedioUri);

        }
    }



}