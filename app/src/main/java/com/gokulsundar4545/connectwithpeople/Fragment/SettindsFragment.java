package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.gokulsundar4545.connectwithpeople.databinding.FragmentSettindsBinding;


public class SettindsFragment extends Fragment {

    FragmentSettindsBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentSettindsBinding.inflate(inflater, container, false);

        binding.fingerprintswithch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener( ) {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    Toast.makeText(getContext(), "Finger Print", Toast.LENGTH_SHORT).show( );
                }
            }
        });

        return binding.getRoot();
    }
}