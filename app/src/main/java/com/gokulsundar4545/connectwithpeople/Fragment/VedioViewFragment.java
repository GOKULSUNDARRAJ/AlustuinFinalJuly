package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gokulsundar4545.connectwithpeople.Adapter.VedioAdapter;
import com.gokulsundar4545.connectwithpeople.Model.VedioMode;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.firebase.database.FirebaseDatabase;


public class VedioViewFragment extends Fragment {

    public VedioViewFragment() {
        // Required empty public constructor
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_vedio_view, container, false);


        return view;
    }


}