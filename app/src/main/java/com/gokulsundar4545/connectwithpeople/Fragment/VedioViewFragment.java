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

    ViewPager2 viewPager2;

    VedioAdapter vedioAdapter;

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

        viewPager2=view.findViewById(R.id.vpager);

        FirebaseRecyclerOptions<VedioMode> options =
                new FirebaseRecyclerOptions.Builder<VedioMode>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("vedio"), VedioMode.class)
                        .build();

        vedioAdapter=new VedioAdapter(options);
        viewPager2.setAdapter(vedioAdapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        vedioAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        vedioAdapter.startListening();
    }


    @Override
    public void onResume() {
        super.onResume( );
        vedioAdapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause( );
        vedioAdapter.startListening();
    }
}