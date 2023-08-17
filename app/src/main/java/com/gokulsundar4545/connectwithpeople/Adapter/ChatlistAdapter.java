package com.gokulsundar4545.connectwithpeople.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.ChartActivity;
import com.gokulsundar4545.connectwithpeople.MainCameraActivity;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.viewHolder> {

    Context context;
    ArrayList<User> list1;
    FirebaseAuth firebaseAuth;
    boolean ischat;

    String thelastmsg;
    String time;


    public ChatlistAdapter(Context context, ArrayList<User> list, boolean ischat) {
        this.context = context;
        this.list1 = list;
        this.ischat=ischat;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.chatlist,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ChatlistAdapter.viewHolder holder, int position) {


        final String hisUid=list1.get(position).getUid();
        firebaseAuth=FirebaseAuth.getInstance();




        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        String myuid=user1.getUid();




        holder.binding.ChatLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
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
        if (user.getStatus().equals("online")){
            holder.binding.online.setVisibility(View.VISIBLE);

        }else {
            holder.binding.online.setVisibility(View.GONE);


        }

        if (ischat){
            if (user.getStatus().equals("online")){
                holder.binding.online.setVisibility(View.VISIBLE);

            }else {
                holder.binding.online.setVisibility(View.GONE);


            }
        }else {
            holder.binding.online.setVisibility(View.GONE);


        }



        if (ischat){
            LastMessage(user.getUid(),holder.lastMsg,holder.lasttime);
        }else {
            holder.lastMsg.setVisibility(View.GONE);
        }

        Picasso.get()
                .load(user.getProfile_photo())
                .placeholder(R.drawable.placeholder)
                .into(holder.binding.profileImage);

        holder.binding.name.setText(user.getName());





        holder.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(), MainCameraActivity.class));

            }
        });





    }

    @Override
    public int getItemCount() {
        return list1.size();

    }

    public class viewHolder extends RecyclerView.ViewHolder{

        public TextView lastMsg;
        TextView lasttime;
        UserSampleBinding binding;
        ImageView camera;
        public viewHolder(@NonNull  View itemView) {
            super(itemView);

            binding= UserSampleBinding.bind(itemView);
            lastMsg=itemView.findViewById(R.id.profession);
            lasttime=itemView.findViewById(R.id.lasttime);
            camera=itemView.findViewById(R.id.camera);
        }
    }

    private void LastMessage(String friendid, TextView lastmsg,TextView lasttime){
        thelastmsg="default";
        time="dafault";
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
                            time=chat.getTimestamp();


                        }
                    }

                }

                switch (thelastmsg){
                    case "default":
                        lastmsg.setText("No Message");
                        lasttime.setText("");
                        break;
                    default:
                        if (thelastmsg.contains("https://firebasestorage.googleapis.com/")) {
                            lastmsg.setText("send a image");
                        } else {
                            lastmsg.setText(thelastmsg);
                        }

                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(time));
                        String dateTime = DateFormat.format("hh:mm aa", cal).toString();
                        lasttime.setText(dateTime);

                }
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(friendid);
                userRef.child("lasttime").setValue(time); // Update lasttime
                thelastmsg="default";
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
