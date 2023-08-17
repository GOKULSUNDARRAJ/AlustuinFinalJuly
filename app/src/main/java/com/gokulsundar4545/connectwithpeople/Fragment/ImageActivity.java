package com.gokulsundar4545.connectwithpeople.Fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.EditUserProfile;
import com.gokulsundar4545.connectwithpeople.Model.StatusModel;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ImageActivity extends AppCompatActivity {

    ImageView imageView;
    TextView openCameratv;
    private  String currentphotopath;
    String uid;
    EditText captionEt;
    FloatingActionButton sendbtn;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference statusRef,laststatus;

    StatusModel model;
    Bitmap bitmap;
    ProgressBar pb;
    String imageuri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        model=new StatusModel();

        imageView=findViewById(R.id.iv_ss);
        openCameratv=findViewById(R.id.open_aam_ss);
        captionEt=findViewById(R.id.caprion_ss);
        sendbtn=findViewById(R.id.sendbtn_ss);
        pb=findViewById(R.id.pb_ss);
        statusRef=database.getReference("Status");
        laststatus=database.getReference("laststatus");

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            imageuri=bundle.getString("u");
            Picasso.get().load(imageuri).into(imageView);
        }
        
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStatus();
            }
        });

    }

    private void saveStatus() {

        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Sending image....");
        progressDialog.show();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        StorageReference storageReference;
        storageReference=storage.getReference("satatusimaeges");

        final StorageReference reference=storageReference.child(System.currentTimeMillis()+"");

        UploadTask uploadTask=reference.putFile(Uri.parse(imageuri));

        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return null;
            }
        })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUri=task.getResult();
                            Calendar callfordate=Calendar.getInstance();
                            SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM");
                            final String savedate=currentDate.format(callfordate.getTime());

                            Calendar callfortime=Calendar.getInstance();
                            SimpleDateFormat currenttime=new SimpleDateFormat("HH:mm:a");

                            final String savetime=currenttime.format(callfortime.getTime());
                            model.setDelete(String.valueOf(System.currentTimeMillis()));
                            model.setImage(downloadUri.toString());

                            progressDialog.dismiss();
                            model.setCaption(captionEt.getText().toString().trim());
                            model.setUid(uid);
                            model.setTime(savedate+" "+savetime);

                            String key=statusRef.push().getKey();
                            statusRef.child(uid).child(key).setValue(model);





                            model.setDelete(String.valueOf(System.currentTimeMillis()));
                            model.setImage(downloadUri.toString());
                            model.setCaption(captionEt.getText().toString().trim());
                            model.setUid(uid);
                            model.setTime(savedate+" "+savetime);

                            laststatus.child(uid).setValue(model);


                            Toast.makeText(ImageActivity.this, "Status Upload", Toast.LENGTH_SHORT).show();

                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent i=new Intent(ImageActivity.this, EditUserProfile.class);
                                    startActivity(i);
                                    finish();
                                }
                            },1000);

                        }
                    }
                });

    }
}