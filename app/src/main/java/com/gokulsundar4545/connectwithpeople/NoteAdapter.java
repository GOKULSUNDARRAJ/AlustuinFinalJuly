package com.gokulsundar4545.connectwithpeople;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<NoteData> noteDataList;
    FirebaseDatabase database;
    FirebaseAuth Auth;
    Context context;

    public NoteAdapter(List<NoteData> noteDataList, Context context) {
        this.noteDataList = noteDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteData noteData = noteDataList.get(position);

        // Bind data to views

        holder.notesTextView.setText(noteData.getNotes());
        holder.title.setText(noteData.getTitle());
        holder.title.setSelected(true);
        database = FirebaseDatabase.getInstance();
        Auth = FirebaseAuth.getInstance();
        database.getReference().child("Users").child(noteData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    User user = snapshot.getValue(User.class);

                    Picasso.get().load(user.getProfile_photo()).into(holder.coverImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.procon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragmentplaynotes bottomSheetFragment = new BottomSheetFragmentplaynotes(context,noteData.getUid());
                bottomSheetFragment.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());

            }
        });




        // Optionally, handle song URL or other features
    }

    @Override
    public int getItemCount() {
        return noteDataList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView notesTextView,title;
        CircleImageView coverImageView;
        ConstraintLayout procon;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notesTextView = itemView.findViewById(R.id.notetxt);
            coverImageView = itemView.findViewById(R.id.pro);
            title=itemView.findViewById(R.id.title);
            procon = itemView.findViewById(R.id.pro1);

        }
    }
}
