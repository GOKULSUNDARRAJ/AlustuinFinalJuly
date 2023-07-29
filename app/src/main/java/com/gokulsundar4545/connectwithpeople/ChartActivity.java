package com.gokulsundar4545.connectwithpeople;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.gokulsundar4545.Client;
import com.gokulsundar4545.Data;
import com.gokulsundar4545.Sender;
import com.gokulsundar4545.connectwithpeople.Adapter.ChatAdapter;
import com.gokulsundar4545.connectwithpeople.Model.ModelChat;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChartActivity extends AppCompatActivity {


    FirebaseAuth auth;
    String hisUid;
    String myuid;
    String hisImage;
    String hisToken;
    TextView nametv, hisonline;
    de.hdodenhof.circleimageview.CircleImageView profile;

    FirebaseDatabase database;
    RecyclerView recyclerView;
    DatabaseReference reference;



    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    private static final int IMAGE_PICK_CAMER_CODE = 300;
    private static final int IMAGE_PICK_GALARY_CODE = 400;


    String[] cameraPermission;
    String[] storagePermission;

    Uri image_rui = null;

    APIService apiService;
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat> chatList;
    ChatAdapter chatAdapter;

    ImageButton sendbtn, attachbtn1;

    EditText message1;

    ImageView back;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart2);


        readMessage();
        seenMessage();


        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        sendbtn = findViewById(R.id.sendbutton);

        recyclerView = findViewById(R.id.ChatRecycle);
        message1 = findViewById(R.id.messageEd);
        profile = findViewById(R.id.profile_image);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        nametv = findViewById(R.id.hisname);
        hisonline = findViewById(R.id.hisonline);

        attachbtn1 = findViewById(R.id.attachbtn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUId");
        myuid = intent.getStringExtra("myUId");
        hisToken = intent.getStringExtra("hisToken");
        Log.e("tag","hisUid: " +hisUid);
        Log.e("tag","myuid: " +myuid);
        Log.e("tag","hisToken: " +hisToken);
        Query Userquery = reference.orderByChild("uid").equalTo(hisUid);

        Userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String name = "" + ds.child("name").getValue();
                    hisImage = "" + ds.child("Profile_photo").getValue();
                    String typingstatus = "" + ds.child("typingstatus").getValue();


                    if (typingstatus.equals(myuid)) {
                        hisonline.setText("Typing....");
                    } else {
                        try {
                            String onlinestatus = "" + ds.child("onlinestatus").getValue();
                            if (onlinestatus.equals("online")) {
                                hisonline.setText(onlinestatus);
                            } else {

                                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                                cal.setTimeInMillis(Long.parseLong(onlinestatus));
                                String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                                hisonline.setText("Last seen at :" + dateTime);


                            }

                        } catch (Exception e) {

                        }

                    }


                    nametv.setText(name);
                    try {
                        Picasso.get()
                                .load(hisImage)
                                .into(profile);
                    } catch (Exception e) {
                        Picasso.get()
                                .load(R.drawable.profile)
                                .into(profile);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = message1.getText().toString().trim();

                if (TextUtils.isEmpty(message)) {
                    Snackbar.make(view, "cant sent empty message", Snackbar.LENGTH_LONG)
                            .show();

                } else {
                    sendMessage(message);
                }
                message1.setText("");
            }
        });


        attachbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowImagePickDialog();
            }
        });





        message1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 0) {
                    chechtypingStatus("noOne");
                } else {
                    chechtypingStatus(hisUid);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void ShowImagePickDialog() {


        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image From");


        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {

                    if (!checkCameraPermission()){
                        requsetCameraPermission();
                    }else {
                       pickFromCamera();

                    }
                }

                if (i == 1) {

                    if (!checkStoragePermission()){
                        requsetStoragePermission();
                    }else {
                        picKFromGallery();
                    }

                }
            }
        });

        builder.show();

    }


    private void sendImageMessage(Uri image_rui) throws IOException {

        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Sending image....");
        progressDialog.show();

        String timeStamp =""+System.currentTimeMillis();
        String fileNameAndPath="ChatImage/"+"post_"+timeStamp;

        Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_rui);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] data=baos.toByteArray();

        StorageReference ref= FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String downloadUri=uriTask.getResult().toString();

                        if (uriTask.isSuccessful()){
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("sender",myuid);
                            hashMap.put("receiver",hisUid);
                            hashMap.put("message",downloadUri);
                            hashMap.put("timestamp",timeStamp);
                            hashMap.put("type","image");
                            hashMap.put("isseen",false);

                            databaseReference.child("Chat").push().setValue(hashMap);


                            DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference("Users").child(myuid);
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user=snapshot.getValue(User.class);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            final DatabaseReference chatRef1=FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(myuid)
                                    .child(hisUid);
                            chatRef1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                    if (!snapshot.exists()){
                                        chatRef1.child("id").setValue(hisUid);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull  DatabaseError error) {

                                }
                            });


                            final DatabaseReference chatRef2=FirebaseDatabase.getInstance().getReference("Chatlist")
                                    .child(hisUid)
                                    .child(myuid);
                            chatRef2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                    if (!snapshot.exists()){
                                        chatRef2.child("id").setValue(myuid);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull  DatabaseError error) {

                                }
                            });
                        }
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                });


    }


    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requsetStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }



    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result & result1;
    }

    private void requsetCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission, CAMERA_REQUEST_CODE);
    }



    private void chechOnlineStatus(String status) {

        DatabaseReference dcRef=FirebaseDatabase.getInstance().getReference("Users").child(myuid);
        HashMap<String,Object> map=new HashMap<>();
        map.put("onlinestatus",status);
        dcRef.updateChildren(map);
    }

    private void chechtypingStatus(String typing) {

        DatabaseReference dcRef=FirebaseDatabase.getInstance().getReference("Users").child(myuid);
        HashMap<String,Object> map=new HashMap<>();
        map.put("typingstatus",typing);
        dcRef.updateChildren(map);
    }

    private void seenMessage() {
        userRefForSeen=FirebaseDatabase.getInstance().getReference("Chat");
        seenListener=userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myuid) && chat.getSender().equals(hisUid)){
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("isseen",true);
                        ds.getRef().updateChildren(map);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }


    private void readMessage() {
        chatList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myuid) && chat.getSender().equals(hisUid)
                        || chat.getReceiver().equals(hisUid) && chat.getSender().equals(myuid)){
                            chatList.add(chat);
                    }
                }

                Log.e("tag","chatList: " + chatList.size());
                chatAdapter=new ChatAdapter(ChartActivity.this , chatList , hisImage);
                chatAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp=String.valueOf(System.currentTimeMillis());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        myuid=user.getUid();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",myuid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("onlinestatus","onlinestatus");
        hashMap.put("timestamp",timestamp);
        hashMap.put("isseen",false);
        hashMap.put("type","text");

        databaseReference.child("Chat").push().setValue(hashMap);

        Data data = new Data(myuid, R.drawable.logo,
                ": "+message, "New Message",
                hisUid);

        Sender sender = new Sender(data, hisToken);

        apiService.sendNotification(sender)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        Log.e("tag","response.body().success: " +response.body());
                        Log.e("tag","response.code(): " +response.code());
                        if (response.code() == 200){
                            Log.e("tag","response.body().success: " +response.body().success);
                            if (response.body().success != 1){
                                //Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Log.e("tag","onFailure: " +t.getMessage());
                    }
                });

        final DatabaseReference chatRef1=FirebaseDatabase.getInstance()
                .getReference("Chatlist")
                .child(myuid)
                .child(hisUid);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef1.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });


        final DatabaseReference chatRef2=FirebaseDatabase.getInstance()
                .getReference("Chatlist")
                .child(myuid)
                .child(hisUid);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef2.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }



    @Override
    protected void onResume() {
        chechOnlineStatus("online");
        Status("online");
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status("offline");
        String timestamp=String.valueOf(System.currentTimeMillis());
        chechOnlineStatus(timestamp);
        chechtypingStatus("noOne");
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onStart() {
        chechOnlineStatus("online");
        super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if (grantResults.length>0){
                    boolean cameraAccepted=grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1] ==PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();

                    }else{
                        Toast.makeText(this, "Camera & Storage Permission Are neccesssary", Toast.LENGTH_SHORT).show();
                    }
                }else {

                }

                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length>0) {


                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        picKFromGallery();
                    } else {
                        Toast.makeText(this, "Storage Permission Are neccesssary", Toast.LENGTH_SHORT).show();
                    }
                }else {

                }
                break;
        }
    }

    private void picKFromGallery() {

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALARY_CODE);
    }

    private void pickFromCamera() {

        ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_rui);
        startActivityForResult(intent,IMAGE_PICK_CAMER_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {

        if (resultCode==RESULT_OK){
            if (requestCode==IMAGE_PICK_GALARY_CODE){
                image_rui=data.getData();

                try {
                    sendImageMessage(image_rui);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode==IMAGE_PICK_CAMER_CODE){
                try {
                    sendImageMessage(image_rui);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void Status(String status) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        databaseReference.updateChildren(hashMap);
    }


}


