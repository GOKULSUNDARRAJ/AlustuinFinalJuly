package com.gokulsundar4545.connectwithpeople.Fragment;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SeekBar;

import com.gokulsundar4545.connectwithpeople.Adapter.UserAdapter;
import com.gokulsundar4545.connectwithpeople.LoginActivity;
import com.gokulsundar4545.connectwithpeople.MainActivity;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    FragmentSearchBinding binding;
    ArrayList<User> list=new ArrayList<>();
    FirebaseAuth auth;
    FirebaseDatabase database;
    private SearchView searchView;
    UserAdapter adapter;

    public SearchFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentSearchBinding.inflate(inflater, container, false);

        binding.userRv.showShimmerAdapter();

        adapter=new UserAdapter(getContext(),list,false);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        binding.userRv.setLayoutManager(layoutManager);


        getAllUser();


        return binding.getRoot();
    }


    private void getAllUser(){
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()){
                    User user=datasnapshot.getValue(User.class);
                    user.setUserID(datasnapshot.getKey());

                    if (!datasnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
                        list.add(user);
                    }



                }
                binding.userRv.hideShimmerAdapter();
                binding.userRv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }



    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_item, menu);

        MenuItem item = menu.findItem(R.id.search);

        androidx.appcompat.widget.SearchView searchView
                = (androidx.appcompat.widget.SearchView) item.getActionView();
        searchView.setQueryHint(getResources().getString(
                R.string.search_title));

        SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(getActivity().SEARCH_SERVICE);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!TextUtils.isEmpty(query.trim())){
                    SearchUsers(query);
                }else {
                    getAllUser();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));

        View closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeButton.setOnClickListener(v -> {

            searchView.setQuery(null, false);
            searchView.clearFocus();

        });


    }

    private void SearchUsers(String query) {

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()){
                    User user=datasnapshot.getValue(User.class);
                    user.setUserID(datasnapshot.getKey());

                    if (!datasnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
                        if (user.getName().toLowerCase().contains(query.toLowerCase()) ||
                         user.getDepartment().toLowerCase().contains(query.toLowerCase())){
                            list.add(user);
                        }

                    }



                }
                binding.userRv.hideShimmerAdapter();
                adapter.notifyDataSetChanged();
                binding.userRv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }


}