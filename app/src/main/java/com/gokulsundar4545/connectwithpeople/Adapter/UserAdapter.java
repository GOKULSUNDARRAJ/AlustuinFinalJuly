package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.ChartActivity;
import com.gokulsundar4545.connectwithpeople.Model.Follow;
import com.gokulsundar4545.connectwithpeople.Model.ModelChat;
import com.gokulsundar4545.connectwithpeople.Model.Notification;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.databinding.UserSampleBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder> {

    Context context;
    ArrayList<User> list1;
    FirebaseAuth firebaseAuth;
    boolean ischat;

    String thelastmsg;


    public UserAdapter(Context context, ArrayList<User> list,boolean ischat) {
        this.context = context;
        this.list1 = list;
        this.ischat=ischat;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_sample,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  UserAdapter.viewHolder holder, int position) {


        final String hisUid=list1.get(position).getUid();
        firebaseAuth=FirebaseAuth.getInstance();




        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        String myuid=user1.getUid();



        holder.binding.ChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ChartActivity.class);
                intent.putExtra("hisUId",hisUid);
                intent.putExtra("myUId",myuid);
                intent.putExtra("hisToken",list1.get(position).getToken());
                context.startActivity(intent);

            }
        });

        User user=list1.get(position);


        if (ischat){
            if (user.getStatus().equals("online")){
                holder.binding.online.setVisibility(View.VISIBLE);
                holder.binding.offline.setVisibility(View.GONE);
            }else {
                holder.binding.offline.setVisibility(View.VISIBLE);
                holder.binding.online.setVisibility(View.GONE);


            }
        }else {
            holder.binding.offline.setVisibility(View.GONE);
            holder.binding.online.setVisibility(View.GONE);


        }



        if (ischat){
            LastMessage(user.getUid(),holder.lastMsg);
        }else {
            holder.lastMsg.setVisibility(View.GONE);
        }

        Picasso.get()
                .load(user.getProfile_photo())
                .into(holder.binding.profileImage);

        holder.binding.name.setText(user.getName());




        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(user.getUid())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {

                if (snapshot.exists()){
                    holder.binding.followbtn.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.follow_bg));
                    holder.binding.followbtn.setText("Following");
                    holder.binding.followbtn.setTextColor(context.getResources().getColor(R.color.black));
                    holder.binding.followbtn.setEnabled(false);
                }else {
                    holder.binding.followbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Follow follow=new Follow();
                            follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                            follow.setFollowedAt(new Date().getTime());


                            FirebaseDatabase.getInstance().getReference()
                                    .child("Users")
                                    .child(user.getUserID())
                                    .child("followers")
                                    .child(FirebaseAuth.getInstance().getUid())
                                    .setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(user.getUserID())
                                            .child("followerCount")
                                            .setValue(user.getFollowerCount()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "you Followed"+user.getName(), Toast.LENGTH_SHORT).show();

                                            Notification notification=new Notification();
                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                            notification.setNotificationAt(new Date().getTime());
                                            notification.setType("follows");

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("notification")
                                                    .child(user.getUserID())
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
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });






    }

    @Override
    public int getItemCount() {
        return list1.size();

    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public TextView lastMsg;
        UserSampleBinding binding;
        public viewHolder(@NonNull  View itemView) {
            super(itemView);

            binding=UserSampleBinding.bind(itemView);
            lastMsg=itemView.findViewById(R.id.profession);
        }
    }

    private void LastMessage(String friendid, TextView lastmsg){
        thelastmsg="default";
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Chat");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);

                    if (firebaseUser!=null && chat!=null){

                        if (chat.getSender().equals(friendid) && chat.getReceiver().equals(firebaseUser.getUid()) ||
                                chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(friendid)){
                            thelastmsg=chat.getMessage();
                        }
                    }

                }

                switch (thelastmsg){
                    case "default":
                        lastmsg.setText("No Message");
                        break;
                    default:
                        lastmsg.setText(thelastmsg);
                }

                thelastmsg="default";
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
