package com.gokulsundar4545.connectwithpeople.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Model.ModelChat;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatAdapter  extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;
    Context context;
    List<ModelChat> chatList;
    String imageUri;

    FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<ModelChat> chatList, String imageUri) {
        this.context = context;
        this.chatList = chatList;
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view;
        if (viewType==MSG_TYPE_RIGHT){
            view = LayoutInflater.from(context).inflate(R.layout.left, parent, false);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.right, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ChatAdapter.ViewHolder holder, int position) {

        try {
            String message=chatList.get(position).getMessage();
            String timeStamp=chatList.get(position).getTimestamp();
            String type=chatList.get(position).getType();

            Calendar cal=Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(timeStamp));
            String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

            if (type.equals("text")){
                holder.messageTv.setVisibility(View.VISIBLE);
                holder.messageIv.setVisibility(View.GONE);

                holder.messageTv.setText(message);

            }else {
                holder.messageTv.setVisibility(View.GONE);
                holder.messageIv.setVisibility(View.VISIBLE);

                Picasso.get()
                        .load(message)
                        .into(holder.messageIv);

            }
            if (message != null){
                holder.messageTv.setText(message);
            }

            if (dateTime!=null){
                holder.timeTv.setText(dateTime);
            }

            try {

                Picasso.get()
                        .load(imageUri)
                        .into(holder.profileIv);
            }catch (Exception e){

            }

            holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure do yo want to delete message?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            DeleteMaessage(position);
                        }
                    });

                    builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.create().show();
                    return true;
                }
            });





            if (position==chatList.size()-1){
                if (chatList.get(position).isIsseen()){
                    holder.deliveredtv.setText("Seen");
                }else {
                    holder.deliveredtv.setText("Delivered");
                }
            }else {
                holder.deliveredtv.setVisibility(View.GONE);
            }




        }catch(Exception e){
            Log.e("tag" ,"Exception: "+ e.getMessage());
        }

    }

    private void DeleteMaessage(int position) {
        String myUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        String msgTimeStamp=chatList.get(position).getTimestamp();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Chat");
        Query query=databaseReference.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    if (ds.child("sender").getValue().equals(myUid)){
                        //ds.getRef().removeValue();
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("message","This message was deleted...");
                        ds.getRef().updateChildren(map);

                        Toast.makeText(context, "Message deleted....", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "You can delete only yor message", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profileIv,messageIv;
        TextView messageTv,timeTv,deliveredtv;

        RelativeLayout messageLayout;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);

            profileIv=itemView.findViewById(R.id.profile_image);
            messageTv=itemView.findViewById(R.id.messagetv);
            timeTv=itemView.findViewById(R.id.timetv);
            deliveredtv=itemView.findViewById(R.id.isseentv);
            messageLayout=itemView.findViewById(R.id.messageLayout);
            messageIv=itemView.findViewById(R.id.messageImage);
        }
    }
}
