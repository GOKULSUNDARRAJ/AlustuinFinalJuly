package com.gokulsundar4545.connectwithpeople.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.animation.content.Content;
import com.bumptech.glide.Glide;
import com.gokulsundar4545.connectwithpeople.Model.Storynew;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StorynewAdapter extends RecyclerView.Adapter<StorynewAdapter.ViewHolder> {


    private Content content;
    private List<Storynew> mStory;

    public StorynewAdapter(Content content, List<Storynew> mStory) {
        this.content = content;
        this.mStory = mStory;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType==0){
            View view= LayoutInflater.from((Context) content).inflate(R.layout.add_story_item,parent,false);
            return new StorynewAdapter.ViewHolder(view);
        }else {
            View view= LayoutInflater.from((Context) content).inflate(R.layout.story_item,parent,false);
            return new StorynewAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        Storynew storynew=mStory.get(position);
        userInfo(holder,storynew.getUserid(),position);

        if (holder.getAdapterPosition()!=0){
            sennStory(holder,storynew.getUserid());
        }

        if (holder.getAdapterPosition()==0){
            myStory(holder.addstory_text,holder.story_plus,false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition()==0){
                    myStory(holder.addstory_text,holder.story_plus,true);
                }else {

                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView story_photo,story_plus,story_photo_seen;
        public TextView story_username,addstory_text;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            story_photo=itemView.findViewById(R.id.stroy_photo);
            story_plus=itemView.findViewById(R.id.story_plus);
            story_photo_seen=itemView.findViewById(R.id.stroy_photo_seen);
            story_username=itemView.findViewById(R.id.story_username);
            addstory_text=itemView.findViewById(R.id.addstory_text);

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return 0;
        }
        return 1;
    }


    private  void  userInfo(ViewHolder viewHolder,String userid,int pos){
        DatabaseReference reference= FirebaseDatabase.getInstance( ).getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                Glide.with((Context) content).load(user.getProfile_photo()).into(viewHolder.story_photo);
                if (pos!=0){
                    Glide.with((Context) content).load(user.getProfile_photo()).into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void myStory(TextView textView,ImageView imageView,boolean click){
       DatabaseReference reference=FirebaseDatabase.getInstance( ).getReference( "Story")
               .child(FirebaseAuth.getInstance( ).getCurrentUser().getUid());

       reference.addListenerForSingleValueEvent(new ValueEventListener( ) {
           @Override
           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               int count=0;
               long timecurrent=System.currentTimeMillis();
               for (DataSnapshot snapshot1:snapshot.getChildren()){
                   Storynew storynew=snapshot1.getValue( Storynew.class );
                   if (timecurrent>storynew.getTimestart() && timecurrent<storynew.getTimeEnd()){
                       count++;
                   }
               }


               if (click){

               }else{
                   if (count>0){
                       textView.setText("My Story");
                       imageView.setVisibility(View.GONE);
                   }else {
                       textView.setText("Add Story");
                       imageView.setVisibility(View.VISIBLE);
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull @NotNull DatabaseError error) {

           }
       });
    }

    private void sennStory(ViewHolder viewHolder,String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance( ).getReference( "Story")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener( ) {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int i=0;
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    if (!snapshot1.child("views")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .exists() && System.currentTimeMillis() < snapshot1.getValue( Storynew.class ).getTimeEnd()){
                        i++;

                    }
                }

                if(i>0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
