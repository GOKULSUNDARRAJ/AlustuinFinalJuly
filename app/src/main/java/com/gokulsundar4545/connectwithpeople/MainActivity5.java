package com.gokulsundar4545.connectwithpeople;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity5 extends AppCompatActivity {

    private static final int REQUEST_READ_CALL_LOG = 100;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView btnAccessCallLog = findViewById(R.id.login);
        btnAccessCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCallLogPermission();
            }
        });
    }

    private void requestCallLogPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    REQUEST_READ_CALL_LOG);
        } else {
            // Permission has already been granted
            accessCallHistory();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CALL_LOG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                accessCallHistory();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied to read call log", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void accessCallHistory() {
        Uri callLogUri = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[]{
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        Cursor cursor = getContentResolver().query(callLogUri, projection, null, null, CallLog.Calls.DATE + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(CallLog.Calls._ID));
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));

                // Convert timestamp to readable date
                Date callDate = new Date(Long.parseLong(date));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String formattedDate = dateFormat.format(callDate);

                // Get current user ID
                String userId = mAuth.getCurrentUser().getUid();

                // Create a CallLogEntry object
                CallLogEntry callLogEntry = new CallLogEntry(id, number, type, formattedDate, duration);

                // Store call log in Firebase Realtime Database
                mDatabase.child("call_logs").child(userId).push().setValue(callLogEntry)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Firebase", "Call log stored successfully.");
                            } else {
                                Log.e("Firebase", "Failed to store call log.", task.getException());
                            }
                        });
            }
            cursor.close();
        }
    }

    // CallLogEntry class to represent a call log entry
    public static class CallLogEntry {
        public String id;
        public String number;
        public String type;
        public String date;
        public String duration;

        public CallLogEntry() {
            // Default constructor required for calls to DataSnapshot.getValue(CallLogEntry.class)
        }

        public CallLogEntry(String id, String number, String type, String date, String duration) {
            this.id = id;
            this.number = number;
            this.type = type;
            this.date = date;
            this.duration = duration;
        }
    }
}
