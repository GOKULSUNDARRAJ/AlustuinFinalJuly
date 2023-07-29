package com.gokulsundar4545.connectwithpeople.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.gokulsundar4545.connectwithpeople.CommentActivity;
import com.gokulsundar4545.connectwithpeople.Model.Comment;
import com.gokulsundar4545.connectwithpeople.Model.Notification;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.databinding.Notification2SampleBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.gokulsundar4545.connectwithpeople.R.drawable.ic_launcher_background;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

    ArrayList<Notification> list;
    Context context;

    public NotificationAdapter(ArrayList<Notification> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.notification2_sample,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  NotificationAdapter.viewHolder holder, int position) {

        Notification notification=list.get(position);




        String type=notification.getType();
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(notification.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull  DataSnapshot snapshot) {

                        User user=snapshot.getValue(User.class);
                        Picasso.get()
                                .load(user.getProfile_photo())
                                .into(holder.binding.profileImage);

                        String text= TimeAgo.using(notification.getNotificationAt());
                        holder.binding.time.setText(text);


                        if(type.equals("like")){
                            holder.binding.notification.setText(Html.fromHtml("<b>"+user.getName()+"</b>" +" "+ "Liked your Post"));

                        }else if (type.equals("follows")){
                            holder.binding.notification.setText(Html.fromHtml("<b>"+user.getName()+"</b>" +" "+ "Start follows you"));
                        }else {
                            holder.binding.notification.setText(Html.fromHtml("<b>"+user.getName() +"</b>"+" "+ "Commented on your post"));
                        }

                    }



                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

        holder.binding.openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference()
                        .child("notification")
                        .child(notification.getPostBy())
                        .child(notification.getNotificationId())
                        .child("checkOpen")
                        .setValue(true);

                if (!type.equals("follows")){
                    holder.binding.eye.setBackgroundResource(R.drawable.ticks);
                    Intent intent=new Intent(context, CommentActivity.class);
                    intent.putExtra("postId",notification.getPostId());
                    intent.putExtra("postedBy",notification.getPostBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }

            }
        });

        holder.binding.openNotification.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.binding.openNotification.setBackgroundColor(Color.DKGRAY);
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Do you want to delete the Notification?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FirebaseDatabase.getInstance().getReference()
                                .child("notification")
                                .child(notification.getPostBy())
                                .child(notification.getNotificationId())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(context, "Notification Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull  Exception e) {

                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        holder.binding.openNotification.setBackgroundColor(Color.WHITE);
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
                return true;
            }
        });

        Boolean chexkOpen=notification.isCheckOpen();
        if (chexkOpen==true){
            holder.binding.eye.setBackgroundResource(R.drawable.ticks);
        }else {
            holder.binding.eye.setBackgroundResource(R.drawable.ic_baseline_done_all_24);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        Notification2SampleBinding binding;

        public viewHolder(@NonNull  View itemView) {
            super(itemView);

            binding= Notification2SampleBinding.bind(itemView);


        }
    }
}
