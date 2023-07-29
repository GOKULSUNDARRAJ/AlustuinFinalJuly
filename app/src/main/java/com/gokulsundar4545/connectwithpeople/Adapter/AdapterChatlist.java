package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.ChartActivity;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder>{

    Context context;
    List<User> userList;
    private HashMap<String,String> lastMessageMap;

    public AdapterChatlist(Context context, List<User> userList) {
        this.context = context;
        this.userList= userList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_chatlist,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterChatlist.MyHolder holder, int position) {

        String hisUid=userList.get(position).getUid();
        String userImage=userList.get(position).getProfile_photo();
        String userName=userList.get(position).getName();
        String lastMessage=lastMessageMap.get(hisUid);

        holder.nameTv.setText(userName);

        if (lastMessage==null || lastMessage.equals("default")){
            holder.lastMessagetv.setVisibility(View.GONE);
        }else {
            holder.lastMessagetv.setVisibility(View.VISIBLE);
            holder.lastMessagetv.setText(lastMessage);
        }

        try {
            Picasso.get()
                    .load(userImage)
                    .into(holder.profile);

        }catch (Exception e){
            Picasso.get()
                    .load(R.drawable.profile)
                    .into(holder.profile);

        }

        if (userList.get(position).getOnlineStatus().equals("online")){

           holder.onlinestatus.setImageResource(R.drawable.online);
        }else {

            holder.onlinestatus.setImageResource(R.drawable.offline);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ChartActivity.class);
                intent.putExtra("hisUid",hisUid);
                intent.putExtra("hisToken",userList.get(position).getToken());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public void setLastMessageMap(String userId,String lastMessage){
        lastMessageMap.put(userId,lastMessage);
    }
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView profile,onlinestatus;
        TextView nameTv,lastMessagetv;
        public MyHolder(@NonNull  View itemView) {
            super(itemView);

            profile=itemView.findViewById(R.id.profile_image);
            onlinestatus=itemView.findViewById(R.id.onlinestatus);
            nameTv=itemView.findViewById(R.id.name);
            lastMessagetv=itemView.findViewById(R.id.lastMessage);
        }
    }
}
