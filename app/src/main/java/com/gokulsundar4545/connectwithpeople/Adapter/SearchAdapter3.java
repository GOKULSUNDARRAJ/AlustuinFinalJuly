package com.gokulsundar4545.connectwithpeople.Adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gokulsundar4545.AddnotesActivity;
import com.gokulsundar4545.connectwithpeople.BottomSheetFragmentmuiscnotes;
import com.gokulsundar4545.connectwithpeople.Model.SongModel;
import com.gokulsundar4545.connectwithpeople.R;

import java.io.IOException;
import java.util.List;

public class SearchAdapter3 extends RecyclerView.Adapter<SearchAdapter3.SongViewHolder> {

    private Context context;
    private List<SongModel> songList;
    private MediaPlayer mediaPlayer;
    private int playingPosition = -1; // Track which item is currently playing
    private BottomSheetFragmentmuiscnotes bottomSheetDialogFragment;
    AddnotesActivity addnotesActivity;
    public SearchAdapter3(Context context, List<SongModel> songList, BottomSheetFragmentmuiscnotes bottomSheetDialogFragment, AddnotesActivity addnotesActivity) {
        this.context = context;
        this.songList = songList;
        this.addnotesActivity=addnotesActivity;
        this.bottomSheetDialogFragment=bottomSheetDialogFragment;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allsong, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        SongModel song = songList.get(position);
        holder.titleTextView.setText(song.getTitle());
        holder.subtitleTextView.setText(song.getSubtitle());
        Glide.with(context)
                .load(song.getCoverUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.songImage);

        // Determine if this item is currently playing
        boolean isPlaying = position == playingPosition && mediaPlayer.isPlaying();

        // Update UI based on the playback state
        if (isPlaying) {
            holder.pause.setVisibility(View.VISIBLE);
            holder.play.setVisibility(View.GONE);
        } else {
            holder.pause.setVisibility(View.GONE);
            holder.play.setVisibility(View.VISIBLE);
        }

        holder.carproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongModel clickedSong = songList.get(holder.getAdapterPosition());
                String coverUrl = clickedSong.getCoverUrl();
                String title = clickedSong.getTitle();
                String subtitle = clickedSong.getSubtitle();
                String songurl = clickedSong.getUrl();
                Intent intent = new Intent(context, AddnotesActivity.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(addnotesActivity).toBundle();
                context.startActivity(intent,b);
                addnotesActivity.finish();
                saveUrlLocally(context, coverUrl, title, subtitle, songurl);

                if (bottomSheetDialogFragment != null) {
                    bottomSheetDialogFragment.dismiss();
                }
            }
        });


        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.pause.setVisibility(View.VISIBLE);
                holder.play.setVisibility(View.GONE);
                // Reset previously playing item
                if (playingPosition != -1 && playingPosition != position) {
                    notifyItemChanged(playingPosition);
                }

                // Update the current playing position
                playingPosition = position;

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(song.getUrl());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                            holder.pause.setVisibility(View.VISIBLE);
                            holder.play.setVisibility(View.GONE);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    holder.play.setVisibility(View.VISIBLE);
                    holder.pause.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView subtitleTextView;
        ImageView songImage;
        ConstraintLayout carproduct;
        ImageView play, pause;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            subtitleTextView = itemView.findViewById(R.id.subtitle1);
            songImage = itemView.findViewById(R.id.itemImage1);
            carproduct = itemView.findViewById(R.id.carproduct1);
            pause = itemView.findViewById(R.id.pause);
            play = itemView.findViewById(R.id.play);
        }
    }

    private void saveUrlLocally(Context context, String coverUrl, String title, String subtitle,String songUrl) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefsnotes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("coverUrl", coverUrl);
        editor.putString("title", title);
        editor.putString("subtitle", subtitle);
        editor.putString("songurl", songUrl);
        editor.apply();
    }

    // Method to release MediaPlayer resources
    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void filterList(List<SongModel> filteredList) {
        songList = filteredList;
        notifyDataSetChanged();
    }
}
