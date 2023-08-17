package com.gokulsundar4545.connectwithpeople.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.EditUserProfile;
import com.gokulsundar4545.connectwithpeople.Model.StatusModel;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UploadImage extends AppCompatActivity {

    ImageView imageView;
    TextView openCameratv;
    String currentphotopath;
    String uid;
    EditText captionEt;
    FloatingActionButton sendbtn;
    private static final int CAMERA_REQUEST_CODE = 100;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference statusRef,laststatus;

    StatusModel model;
    Uri imageuri = null;

    Bitmap bitmap;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

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

        openCameratv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filename="photo";
                File storageDirectory=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                try {

                    ContentValues cv=new ContentValues();
                    cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
                    cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
                    imageuri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
                    startActivityForResult(intent,CAMERA_REQUEST_CODE);

                }catch (Exception e){
                    Toast.makeText(UploadImage.this, "Error", Toast.LENGTH_SHORT).show();

                }
            }
        });


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveStatus(imageuri);
                    Toast.makeText(UploadImage.this, "Please Wait", Toast.LENGTH_SHORT).show( );
                } catch (IOException e) {
                    e.printStackTrace( );
                }
            }
        });
    }

    private void saveStatus(Uri imageuri) throws IOException {


        try {


            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Sending image....");
            progressDialog.show( );

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver( ), imageuri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream( );
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray( );

            String timeStamp = "" + System.currentTimeMillis( );

            String fileNameAndPath = "satatusimages" + timeStamp;

            StorageReference ref = FirebaseStorage.getInstance( ).getReference( ).child(fileNameAndPath);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>( ) {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss( );
                            Task<Uri> uriTask = taskSnapshot.getStorage( ).getDownloadUrl( );
                            while (!uriTask.isSuccessful( )) ;
                            String downloadUri = uriTask.getResult( ).toString( );

                            if (uriTask.isSuccessful( )) {
                                pb.setVisibility(View.VISIBLE);
                                Calendar callfordate = Calendar.getInstance( );
                                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM");
                                final String savedate = currentDate.format(callfordate.getTime( ));

                                Calendar callfortime = Calendar.getInstance( );
                                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:a");

                                final String savetime = currenttime.format(callfortime.getTime( ));
                                model.setDelete(String.valueOf(System.currentTimeMillis( )));
                                model.setImage(downloadUri.toString( ));
                                model.setCaption(captionEt.getText( ).toString( ).trim( ));
                                model.setUid(uid);
                                model.setTime(savedate + " " + savetime);

                                String key = statusRef.push( ).getKey( );
                                statusRef.child(uid).child(key).setValue(model);


                                model.setDelete(String.valueOf(System.currentTimeMillis( )));
                                model.setImage(downloadUri.toString( ));
                                model.setCaption(captionEt.getText( ).toString( ).trim( ));
                                model.setUid(uid);
                                model.setTime(savedate + " " + savetime);

                                laststatus.child(uid).setValue(model);

                                pb.setVisibility(View.GONE);

                                Toast.makeText(UploadImage.this, "Status Upload", Toast.LENGTH_SHORT).show( );

                                Handler handler = new Handler( );
                                handler.postDelayed(new Runnable( ) {
                                    @Override
                                    public void run() {

                                        Intent i = new Intent(UploadImage.this, EditUserProfile.class);
                                        startActivity(i);
                                        finish( );
                                    }
                                }, 1000);


                            }
                        }
                    })

                    .addOnFailureListener(new OnFailureListener( ) {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }catch(Exception e){

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CAMERA_REQUEST_CODE){

            imageView.setImageURI(imageuri);


        }



    }



}