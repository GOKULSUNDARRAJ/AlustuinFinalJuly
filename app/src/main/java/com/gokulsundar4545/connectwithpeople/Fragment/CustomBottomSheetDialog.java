package com.gokulsundar4545.connectwithpeople.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gokulsundar4545.Client;
import com.gokulsundar4545.Data;
import com.gokulsundar4545.Sender;
import com.gokulsundar4545.connectwithpeople.APIService;
import com.gokulsundar4545.connectwithpeople.Adapter.FollowersAdaptershare;
import com.gokulsundar4545.connectwithpeople.Model.Follow;
import com.gokulsundar4545.connectwithpeople.MyResponse;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomBottomSheetDialog extends BottomSheetDialogFragment implements FollowersAdaptershare.OnProfileImageClickListener, FollowersAdaptershare.OnSelectedUIDsListener {

    private RecyclerView recyclerView;
    private FollowersAdaptershare adapter;
    private String postId;
    private Context context;
    private String postDescription;
    private String postImg;
    private int adapterPosition;
    private ArrayList<String> selectedUIDs = new ArrayList<>();
    private ArrayList<String> selectedtoken = new ArrayList<>();
    private Button buttonShare;
    private ImageView share, download, whatsapp;

    public CustomBottomSheetDialog(String postId, Context context, String postDescription, String postImg, int adapterPosition) {
        this.postId = postId;
        this.context = context;
        this.postDescription = postDescription;
        this.postImg = postImg;
        this.adapterPosition = adapterPosition;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_share, container, false);
        recyclerView = view.findViewById(R.id.friendrv);

        ArrayList<Follow> list = new ArrayList<>(); // Initialize with your data source

        adapter = new FollowersAdaptershare(list, getContext(), this);
        adapter.setOnSelectedUIDsListener(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        fetchData(); // Fetch your data to populate list

        buttonShare = view.findViewById(R.id.letuserloginbtn);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedUIDs.isEmpty()) {

                    sendMessage(postDescription, postImg, selectedUIDs, selectedtoken);

                } else {
                    Toast.makeText(getContext(), "Select user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        share = view.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareContent();
            }
        });

        download = view.findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndDownloadImage();
            }
        });

        whatsapp = view.findViewById(R.id.whatsapp);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareToWhatsApp(postImg, postDescription);
            }
        });

        return view;
    }

    private void fetchData() {
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("youfollowing");

        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Follow> followerList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Follow follow = dataSnapshot.getValue(Follow.class);
                    followerList.add(follow);
                }
                adapter.updateList(followerList); // Update adapter's dataset
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CustomBottomSheet", "Failed to fetch followers: " + error.getMessage());
            }
        });
    }

    private void shareContent() {
        // Implement your share content functionality here
        // Example: Sharing image and text using Intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        Uri uri = Uri.parse(postImg); // assuming postImg is the image URI

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, postDescription);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void checkPermissionAndDownloadImage() {
        // Implement your permission check and download image functionality here
        // Example: Downloading image using Glide and saving to gallery
        Glide.with(requireContext())
                .asBitmap()
                .load(postImg)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Save bitmap to gallery
                        saveImageToGallery(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Implement method if needed
                    }
                });
    }

    private void saveImageToGallery(Bitmap bitmap) {
        // Save bitmap to gallery
        String filename = "image_" + System.currentTimeMillis() + ".jpg";
        File storageDir = new File(requireContext().getExternalFilesDir(null), "images");

        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, filename);
            try {
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                // Add image to gallery
                MediaStore.Images.Media.insertImage(requireContext().getContentResolver(),
                        imageFile.getAbsolutePath(), imageFile.getName(), imageFile.getName());

                Toast.makeText(requireContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Failed to create directory", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareToWhatsApp(String imageUrl, String text) {
        // Implement sharing to WhatsApp functionality here
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, text);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUrl));
            whatsappIntent.setType("image/*");
        }

        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(String message, String imageUrl, ArrayList<String> selectedUIDs, ArrayList<String> selectedTokens) {
        APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = user.getUid();

        // Combined message HashMap
        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("sender", myUid);
        messageMap.put("timestamp", timestamp);
        messageMap.put("isseen", false);
        messageMap.put("position", String.valueOf(adapterPosition));
        messageMap.put("type", "both"); // Set type to "both"

        // Check if message is text
        if (message != null && !message.trim().isEmpty()) {
            messageMap.put("message", message);
        }

        // Check if message is image URL
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            messageMap.put("messageimage", imageUrl);
        }

        // Save message to Firebase Database for each recipient
        for (int i = 0; i < selectedUIDs.size(); i++) {
            String hisUid = selectedUIDs.get(i);
            messageMap.put("receiver", hisUid);

            databaseReference.child("Chat").push().setValue(messageMap)
                    .addOnSuccessListener(aVoid -> {
                        updateChatList(myUid, hisUid);

                    })
                    .addOnFailureListener(e -> {
                        Log.e("TAG", "Failed to send message: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
                    });

            // Send notification via FCM for each recipient
            String hisToken = selectedTokens.get(i);
            sendNotification(apiService, myUid, hisUid, hisToken, "New Message or Image");
        }


        buttonShare.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Message sent", Toast.LENGTH_SHORT).show();

        // Log and handle responses if needed for FCM notifications
    }

    // Utility method to send FCM notification
    private void sendNotification(APIService apiService, String myUid, String hisUid, String hisToken, String messageContent) {
        Data data = new Data(myUid, R.drawable.logo,
                messageContent,
                "New Message or Image",
                hisUid);
        Sender sender = new Sender(data, hisToken);

        apiService.sendNotification(sender)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        Log.e("tag", "response.body().success: " + response.body());
                        Log.e("tag", "response.code(): " + response.code());
                        if (response.code() == 200) {
                            Log.e("tag", "response.body().success: " + response.body().success);
                            if (response.body().success != 1) {
                                // Handle failure scenario if needed
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Log.e("tag", "onFailure: " + t.getMessage());
                    }
                });
    }

    private void updateChatList(String myUid, String hisUid) {
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist").child(myUid).child(hisUid);
        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef1.child("id").setValue(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "Failed to update chat list: " + error.getMessage());
            }
        });

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist").child(hisUid).child(myUid);
        chatRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef2.child("id").setValue(myUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "Failed to update chat list: " + error.getMessage());
            }
        });
    }


    @Override
    public void onProfileImageClick(String followedBy, String token) {
        // Handle profile image click events here
    }

    @Override
    public void onSelectedUIDs(ArrayList<String> selectedUIDs, ArrayList<String> selectedTokens) {

        this.selectedUIDs=selectedUIDs;
        this.selectedtoken=selectedTokens;

        buttonShare.setVisibility(View.VISIBLE);

        // Display a Toast message with the selected user IDs and tokens
        StringBuilder messageBuilder = new StringBuilder("Selected Users:\n");
        for (int i = 0; i < selectedUIDs.size(); i++) {
            messageBuilder.append("UID: ").append(selectedUIDs.get(i)).append("\n");
            messageBuilder.append("Token: ").append(selectedTokens.get(i)).append("\n\n");
        }
        Toast.makeText(getContext(), messageBuilder.toString(), Toast.LENGTH_LONG).show();
    }

}
