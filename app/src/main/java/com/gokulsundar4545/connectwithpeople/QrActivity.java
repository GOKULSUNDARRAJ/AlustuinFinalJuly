package com.gokulsundar4545.connectwithpeople;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QrActivity extends AppCompatActivity {
    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    TextView name;
    ImageView scan;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        setStatusBarColor(getResources().getColor(android.R.color.white));

        name=findViewById(R.id.name);
        Auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();


        database.getReference().child("Users").child(Auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user=snapshot.getValue(User.class);
                    name.setText(user.getName());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        scan=findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),BarcodeScannerActivity.class));

            }
        });




        ImageView imageViewQR = findViewById(R.id.song_cover_image_view);

        Bitmap qrBitmap = generateQRCode(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (qrBitmap != null) {
            Glide.with(this)
                    .load(qrBitmap)
                    .apply(new RequestOptions().transform(new RoundedCorners(10))) // Optional: Add rounded corners to the QR code
                    .into(imageViewQR);
        } else {
            // Handle QR code generation failure
        }

    }

    // Method to generate QR code from text
    private Bitmap generateQRCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(color);
        }
    }

}

