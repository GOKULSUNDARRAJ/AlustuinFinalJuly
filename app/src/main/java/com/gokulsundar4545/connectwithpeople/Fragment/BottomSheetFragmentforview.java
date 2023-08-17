package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gokulsundar4545.connectwithpeople.Adapter.CurrentUserUidsAdapter;
import com.gokulsundar4545.connectwithpeople.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragmentforview extends BottomSheetDialogFragment {

    private List<String> currentUserUids = new ArrayList<>();

    public BottomSheetFragmentforview() {
        // Required empty public constructor
    }

    // Static method to create a new instance of BottomSheetFragmentforview and pass arguments
    public static BottomSheetFragmentforview newInstance(List<String> currentUserUids) {
        BottomSheetFragmentforview fragment = new BottomSheetFragmentforview();
        Bundle args = new Bundle();
        args.putStringArrayList("currentUserUids", new ArrayList<>(currentUserUids));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_layoutforview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCurrentUserUids);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Retrieve currentUserUids from arguments
        Bundle args = getArguments();
        if (args != null) {
            currentUserUids = args.getStringArrayList("currentUserUids");
        }

        // Set adapter
        CurrentUserUidsAdapter adapter = new CurrentUserUidsAdapter(currentUserUids);
        recyclerView.setAdapter(adapter);
    }
}
