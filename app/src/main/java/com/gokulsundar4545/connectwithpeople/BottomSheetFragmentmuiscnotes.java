package com.gokulsundar4545.connectwithpeople;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.AddnotesActivity;
import com.gokulsundar4545.connectwithpeople.Adapter.SearchAdapter2;
import com.gokulsundar4545.connectwithpeople.Adapter.SearchAdapter3;
import com.gokulsundar4545.connectwithpeople.Model.SongModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragmentmuiscnotes extends BottomSheetDialogFragment {
    private RecyclerView recyclerView;
    private SearchAdapter3 adapter;
    private List<SongModel> songList;
    private EditText editText;

    ImageView back;
    TextView textView;
    AddnotesActivity addnotesActivity;

    public BottomSheetFragmentmuiscnotes(AddnotesActivity addnotesActivity) {
        this.addnotesActivity = addnotesActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout_music, container, false);

        textView = view.findViewById(R.id.textView);
        editText = view.findViewById(R.id.search);
        back = view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement back button functionality if needed
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        songList = new ArrayList<>();
        adapter = new SearchAdapter3(getContext(), songList,this,addnotesActivity);
        recyclerView.setAdapter(adapter);

        // Add a TextWatcher to the EditText for searching
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the songList based on the user input
                filterSongs(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        fetchData();
        return view;
    }

    private void fetchData() {
        FirebaseFirestore.getInstance().collection("songs")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        songList.clear(); // Clear existing data
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                String songTitle = documentSnapshot.getString("title");
                                String subtitle = documentSnapshot.getString("subtitle");
                                String coverUrl = documentSnapshot.getString("coverUrl");
                                String Url = documentSnapshot.getString("url");
                                String id = documentSnapshot.getString("id");
                                String lyrics = documentSnapshot.getString("lyrics");
                                String artist = documentSnapshot.getString("artist");
                                String name = documentSnapshot.getString("name");
                                Long count = documentSnapshot.getLong("count");
                                String key = documentSnapshot.getId();
                                SongModel song = new SongModel(key, id, songTitle, subtitle, Url, coverUrl, lyrics, artist, name, count);

                                songList.add(song);
                            } else {
                                Log.d("SearchActivity", "No such document");
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter after data change
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SearchActivit", "Error fetching songs", e);
                        // Displaying Toast message on failure
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Error fetching songs"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to filter the song list based on search text
    private void filterSongs(String searchText) {
        List<SongModel> filteredList = new ArrayList<>();
        for (SongModel song : songList) {
            if (song.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(song);
            }
        }

        adapter.filterList(filteredList);
    }
}
