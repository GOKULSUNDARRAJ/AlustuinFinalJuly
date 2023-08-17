package com.gokulsundar4545.connectwithpeople;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private List<String> itemList;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Set LinearLayoutManager to horizontal orientation
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Initialize the data
        itemList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            itemList.add("Item " + i);
        }

        // Initialize the adapter and set it to the RecyclerView
        adapter = new MyAdapter(itemList);
        recyclerView.setAdapter(adapter);

        Fragment myFragment = new MyFragment();

        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add the fragment to the container
        fragmentTransaction.add(R.id.fragment_container, myFragment);
        fragmentTransaction.commit();
    }


    public void gotorecentlink(View view) {
        Fragment myFragment = new MyFragment();

        // Get the FragmentManager and start a transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Add the fragment to the container
        fragmentTransaction.add(R.id.fragment_container, myFragment);
        fragmentTransaction.commit();
    }

    public void gototoplink(View view) {
    }
}