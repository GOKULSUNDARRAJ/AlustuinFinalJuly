package com.gokulsundar4545.connectwithpeople;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.databinding.ActivityPayMentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;


public class PayMentActivity extends AppCompatActivity {

    private ActivityPayMentBinding binding;
    public static final String GPAY_PACKAGE_NAME="com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE=103;
    String amount;
    String name="Hotel X 8";
    String upId="gpay-11201146045@okbizaxis";
    String transcation="Transcation for Hindustan IT";
    String status;
    Uri uri;

    ImageView profile;



    private static boolean isAppInstalled(Context context, String packagename) {
        try {
            context.getPackageManager().getApplicationInfo(packagename,0);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();


            return false;
        }
    }



    private static Uri getUpIPaymentUri(String name,String upId,String transcation,String amount) {
        return new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa",upId)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",transcation)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("cu","INR")
                .build();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPayMentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        profile=findViewById(R.id.profile_image);

        FirebaseDatabase database;
        FirebaseAuth auth;
        auth= FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();



        database.getReference().child("Users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    User user=snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .into(profile);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });



        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });





    }

    private void showDialog() {
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.amountoption);

        LinearLayout google=dialog.findViewById(R.id.googlepayLayout);
        LinearLayout payth=dialog.findViewById(R.id.paythLayout);
        LinearLayout phone=dialog.findViewById(R.id.phonepayLayout);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount=binding.amountEd1.getEditText().toString().trim();

                if (!amount.isEmpty()){
                    uri=getUpIPaymentUri(name,upId,transcation,amount);
                    payWithPay();
                }else {
                    binding.amountEd1.setError("Ammount is Required");
                    binding.amountEd1.requestFocus();
                }


            }
        });

        payth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount=binding.amountEd1.getEditText().toString().trim();

                if (!amount.isEmpty()){
                    uri=getUpIPaymentUri(name,upId,transcation,amount);
                    payWithPay();
                }else {
                    binding.amountEd1.setError("Ammount is Required");
                    binding.amountEd1.requestFocus();
                }


            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount=binding.amountEd1.getEditText().toString().trim();

                if (!amount.isEmpty()){
                    uri=getUpIPaymentUri(name,upId,transcation,amount);
                    payWithPay();
                }else {
                    binding.amountEd1.setError("Ammount is Required");
                    binding.amountEd1.requestFocus();
                }



            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations=R.style.DialoAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void payWithPay() {

        if (isAppInstalled(this,GPAY_PACKAGE_NAME)){
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
//            intent.setPackage(GPAY_PACKAGE_NAME);
            startActivityForResult(intent,GOOGLE_PAY_REQUEST_CODE);

        }else {
            Toast.makeText(this, "Please Install GPay", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            status=data.getStringExtra("Status").toLowerCase();

        }

        if ((RESULT_OK==resultCode) && status.equals("success")){
            Toast.makeText(this, "Transcation Successful", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Transcation Fails", Toast.LENGTH_SHORT).show();

        }
    }






}