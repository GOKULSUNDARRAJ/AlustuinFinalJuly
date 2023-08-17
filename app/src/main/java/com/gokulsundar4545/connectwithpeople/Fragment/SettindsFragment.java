package com.gokulsundar4545.connectwithpeople.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentSettindsBinding;

import android.content.Context;
import android.content.SharedPreferences;
// other imports...
public class SettindsFragment extends Fragment {

    private FragmentSettindsBinding binding;
    private DatabaseReference userDatabaseReference;
    private String userId;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences2;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String ACCOUNT_TYPE_KEY = "accountType";
    private static final String PREF_NAME = "switchPreferences";
    private static final String SWITCH_KEY = "switchState";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettindsBinding.inflate(inflater, container, false);

        // Initialize Firebase Database reference
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
            userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        }

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences2 = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Load saved switch states
        boolean switchState = sharedPreferences2.getBoolean(SWITCH_KEY, false);
        binding.realsscrool.setChecked(switchState);

        // Set a listener to save the switch state when changed
        binding.realsscrool.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences2.edit();
            editor.putBoolean(SWITCH_KEY, isChecked);
            editor.apply();
        });

        // Retrieve account type from SharedPreferences or Firebase
        String savedAccountType = sharedPreferences.getString(ACCOUNT_TYPE_KEY, null);
        if (savedAccountType != null) {
            binding.fingerprintswithch.setChecked("private".equals(savedAccountType));
        } else {
            userDatabaseReference.child("accountType").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String accountType = snapshot.getValue(String.class);
                    if (accountType != null) {
                        sharedPreferences.edit().putString(ACCOUNT_TYPE_KEY, accountType).apply();
                        binding.fingerprintswithch.setChecked("private".equals(accountType));
                    } else {
                        binding.fingerprintswithch.setChecked(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Failed to load account type", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set OnCheckedChangeListener on the Switch
        binding.fingerprintswithch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String accountType = isChecked ? "private" : "public";
            userDatabaseReference.child("accountType").setValue(accountType)
                    .addOnSuccessListener(aVoid -> {
                        sharedPreferences.edit().putString(ACCOUNT_TYPE_KEY, accountType).apply();
                        Toast.makeText(getActivity(), "Account type updated to " + accountType, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Failed to update account type", Toast.LENGTH_SHORT).show();
                    });
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
