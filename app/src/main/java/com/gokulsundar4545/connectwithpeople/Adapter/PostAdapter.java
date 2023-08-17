package com.gokulsundar4545.connectwithpeople.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.Data;
import com.gokulsundar4545.Sender;
import com.gokulsundar4545.connectwithpeople.CommentActivity;
import com.gokulsundar4545.connectwithpeople.EditUserProfile;
import com.gokulsundar4545.connectwithpeople.Fragment.BottomSheetFragment2;
import com.gokulsundar4545.connectwithpeople.Fragment.BottomSheetFragmentforlikes;
import com.gokulsundar4545.connectwithpeople.Fragment.CustomBottomSheetDialog;
import com.gokulsundar4545.connectwithpeople.Model.Notification;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.MyResponse;
import com.gokulsundar4545.connectwithpeople.PostLikedByActivity;
import com.gokulsundar4545.connectwithpeople.R;

import com.gokulsundar4545.connectwithpeople.StartActivity;
import com.gokulsundar4545.connectwithpeople.ThereProfileActivity;

import com.gokulsundar4545.connectwithpeople.TransitionHandler;
import com.gokulsundar4545.connectwithpeople.databinding.DashboardRvBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.viewHolder> {

    ArrayList<Post> list;
    Context context;

    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference PostRef;
    String myuid;
    private int playingPosition = -1; // Track which item is currently playing
    private MediaPlayer mediaPlayer;

    public PostAdapter(ArrayList<Post> list, Context context, MediaPlayer mediaPlayer) {
        this.list = list;
        this.context = context;
        this.mediaPlayer = mediaPlayer;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.dashboard_rv, parent, false);
        return new viewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Post model = list.get(position);

        Auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();


        database.getReference().child("Users").child(Auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    User user = snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .into(holder.binding.pro);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        

        Picasso.get()
                .load(model.getPostImg())
                .into(holder.binding.postimage);


        holder.binding.textView6.setText("View All" + " " + model.getCommentCount() + " " + "Comments");
        holder.binding.description.setText("# " + model.getPostDescription() + " ");
        holder.binding.likedby.setText("likes" + " " + model.getPostLike() + "");

        String pId = model.getPostId();

        holder.binding.menubar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.deleteoption1);

                LinearLayout shareoption = dialog.findViewById(R.id.deleteLayout);
                shareoption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.binding.postimage.getDrawable();
                        if (bitmapDrawable == null) {
                            String des = holder.binding.description.getText().toString();
                            shareTextOnly(des);
                        } else {
                            String des = holder.binding.description.getText().toString();
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            shareTextandImage(des, bitmap);
                        }
                    }
                });
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);

            }
        });



        holder.binding.comment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", model.getPostId());
                intent.putExtra("postedBy", model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle();
                context.startActivity(intent, b);
            }
        });

        holder.binding.textView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", model.getPostId());
                intent.putExtra("postedBy", model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle();
                context.startActivity(intent, b);
            }
        });

        holder.binding.likedby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                // Check if the context is an instance of FragmentActivity
                if (context instanceof FragmentActivity) {
                    BottomSheetFragmentforlikes bottomSheetFragment = new BottomSheetFragmentforlikes(pId);
                    bottomSheetFragment.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheetFragment.getTag());


                } else {
                    // Handle case where context is not FragmentActivity (optional)
                    // You can log an error or handle the situation as needed
                }
            }
        });


        holder.binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid", model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Use context instead of getActivity()
                Bundle b = ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle();
                context.startActivity(intent, b);

                // Ensure context implements TransitionHandler
                if (context instanceof TransitionHandler) {
                    ((TransitionHandler) context).performTransition();
                }
            }
        });


        holder.binding.postimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        boolean isPlaying = position == playingPosition && mediaPlayer.isPlaying();

        // Update UI based on the playback state
        if (isPlaying) {
            holder.binding.pause.setVisibility(View.VISIBLE);
            holder.binding.play.setVisibility(View.GONE);
        } else {
            holder.binding.pause.setVisibility(View.GONE);
            holder.binding.play.setVisibility(View.VISIBLE);
        }
        holder.binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.pause.setVisibility(View.VISIBLE);
                holder.binding.play.setVisibility(View.GONE);
                // Reset previously playing item
                if (playingPosition != -1 && playingPosition != position) {
                    notifyItemChanged(playingPosition);
                }

                // Update the current playing position
                playingPosition = position;

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(model.getSongurl());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                            holder.binding.pause.setVisibility(View.VISIBLE);
                            holder.binding.play.setVisibility(View.GONE);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.binding.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    holder.binding.play.setVisibility(View.VISIBLE);
                    holder.binding.pause.setVisibility(View.GONE);
                }
            }
        });




        holder.binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Clicked position: " + holder.getAdapterPosition(), Toast.LENGTH_SHORT).show();
                CustomBottomSheetDialog bottomSheetDialog = new CustomBottomSheetDialog(model.getPostId(), context, model.getPostDescription(), model.getPostImg(),holder.getAdapterPosition());
                bottomSheetDialog.show(((FragmentActivity) context).getSupportFragmentManager(), bottomSheetDialog.getTag());
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Users").
                child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User user = snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile_photo())
                                .into(holder.binding.profileImage);

                        holder.binding.username.setText(user.getName());
                        holder.binding.about.setText(model.getSongurl());
                        holder.binding.about.setSelected(true);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        FirebaseDatabase.getInstance().getReference()
                .child("posts")
                .child(model.getPostId())
                .child("likes")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_favorite_24, 0, 0, 0);

                        } else {
                            holder.binding.like.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    FirebaseDatabase.getInstance().getReference()
                                            .child("posts")
                                            .child(model.getPostId())
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("posts")
                                                            .child(model.getPostId())
                                                            .child("postLike")
                                                            .setValue(model.getPostLike() + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    holder.binding.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_favorite_24, 0, 0, 0);

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setPostId(model.getPostId());
                                                                    notification.setPostBy(model.getPostedBy());
                                                                    notification.setType("like");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification")
                                                                            .child(model.getPostedBy())
                                                                            .push()
                                                                            .setValue(notification);

                                                                    Notification notification1 = new Notification();
                                                                    notification1.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification1.setNotificationAt(new Date().getTime());
                                                                    notification1.setPostId(model.getPostId());
                                                                    notification1.setPostBy(model.getPostedBy());
                                                                    notification1.setType("like");

                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("notification1")
                                                                            .child(model.getPostedBy())
                                                                            .push()
                                                                            .setValue(notification);
                                                                }
                                                            });

                                                }
                                            });

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void shareTextandImage(String des, Bitmap bitmap) {
        String shareBody = des;
        Uri uri = saveImageToShare(bitmap);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        intent.setType("image/png");
        context.startActivity(Intent.createChooser(intent, "Share Via"));


    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.gokulsundar4545.connectwithpeople.firebaseapp.fileprovider", file);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly(String des) {
        String shareBody = des;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(intent, "Share Via"));


    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {


        DashboardRvBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = DashboardRvBinding.bind(itemView);


        }
    }


}
