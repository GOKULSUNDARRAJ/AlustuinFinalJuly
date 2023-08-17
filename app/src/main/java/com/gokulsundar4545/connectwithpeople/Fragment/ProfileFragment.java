package com.gokulsundar4545.connectwithpeople.Fragment;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.gokulsundar4545.AddHighlitescover;
import com.gokulsundar4545.AddnotesActivity;
import com.gokulsundar4545.connectwithpeople.Adapter.MypostAdapter;
import com.gokulsundar4545.connectwithpeople.BottomSheetFragmentforbio;
import com.gokulsundar4545.connectwithpeople.BottomSheetFragmentplaynotes;
import com.gokulsundar4545.connectwithpeople.BottomSheetFragmentplaynotes2;
import com.gokulsundar4545.connectwithpeople.Contact;
import com.gokulsundar4545.connectwithpeople.ContactsAdapter;
import com.gokulsundar4545.connectwithpeople.EditUserProfile;
import com.gokulsundar4545.connectwithpeople.Model.Follow;
import com.gokulsundar4545.connectwithpeople.Model.Post;
import com.gokulsundar4545.connectwithpeople.Model.User;
import com.gokulsundar4545.connectwithpeople.QrActivity;
import com.gokulsundar4545.connectwithpeople.R;
import com.gokulsundar4545.connectwithpeople.StartActivityFirst;
import com.gokulsundar4545.connectwithpeople.StoryHighlightsAdapter;
import com.gokulsundar4545.connectwithpeople.TransitionHandler;
import com.gokulsundar4545.connectwithpeople.databinding.FragmentProfileBinding;
import com.gokulsundar4545.connectwithpeople.youFollowersFragment;
import com.gokulsundar4545.connectwithpeople.youFollowingFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ProfileFragment extends Fragment {

    ArrayList<Follow> list;
    FragmentProfileBinding binding;

    FirebaseAuth Auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    DatabaseReference PostRef;

    private RecyclerView contactsRecyclerView;
    private ContactsAdapter contactsAdapter;
    ShimmerRecyclerView dashboardRv;

    ArrayList<Post> dashboardlist;
    String firebaseUser;

    private int count = 0;

    public ProfileFragment() {

    }
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        Auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        firebaseUser = Auth.getCurrentUser().getUid();

        PostRef = FirebaseDatabase.getInstance().getReference("post");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.contactsRecyclerView.setLayoutManager(layoutManager);

      readData();


        retrieveUserPhoneNumbers();


        dashboardlist = new ArrayList<>();


        binding.recyclerview12.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<String> keysList = new ArrayList<>(); // Replace with your actual data list

        // Corrected usage with parameters swapped
        StoryHighlightsAdapter adapter = new StoryHighlightsAdapter(keysList,getActivity(), FirebaseAuth.getInstance().getCurrentUser().getUid());


        binding.recyclerview12.setAdapter(adapter);


        DatabaseReference highlightsRef = FirebaseDatabase.getInstance().getReference()
                .child("storieshighlites") // Updated name here
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("userhighlites");

        highlightsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> keysList = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    keysList.add(key);  // Add each key to the list

                }

                // Now, you can pass keysList to your RecyclerView adapter
                StoryHighlightsAdapter adapter = new StoryHighlightsAdapter(keysList,getActivity(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                binding.recyclerview12.setAdapter(adapter); // Update setting adapter

                // Show Toast with item count

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
                Log.e("FirebaseError", "Error getting child keys: " + databaseError.getMessage());
            }
        });





        MypostAdapter dashboardAdapter = new MypostAdapter(dashboardlist, getContext());
        // LinearLayoutManager layoutManager11 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);

        //layoutManager11.setReverseLayout(true);
        //layoutManager11.setStackFromEnd(true);
        //binding.dashboardRv.setLayoutManager(layoutManager11);
        binding.dashboardRv.setNestedScrollingEnabled(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.dashboardRv.setLayoutManager(gridLayoutManager);




        PostRef = FirebaseDatabase.getInstance().getReference().child("posts");
        PostRef.orderByChild("postedBy").equalTo(firebaseUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    count = (int) snapshot.getChildrenCount();

                    binding.textView18.setText(Integer.toString(count) + "");
                } else {
                    binding.textView18.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        binding.textView21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditUserProfile.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(intent, b);
                if (getActivity() instanceof TransitionHandler) {
                    ((TransitionHandler) getActivity()).performTransition();
                }
            }
        });


        binding.menuicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.options);

                LinearLayout shareoption = dialog.findViewById(R.id.Settings);
                shareoption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettindsFragment pf = new SettindsFragment();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fram_layout, pf).commit();

                        dialog.dismiss();

                    }
                });

                LinearLayout about = dialog.findViewById(R.id.about);
                about.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        About pf = new About();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fram_layout, pf).commit();

                        dialog.dismiss();

                    }
                });


                LinearLayout updat = dialog.findViewById(R.id.editprofile);
                updat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), EditUserProfile.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });

                LinearLayout logout = dialog.findViewById(R.id.logout);
                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Logout();
                    }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });
        binding.followes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                youFollowersFragment pf = new youFollowersFragment();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pf).commit();

            }
        });


        binding.following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youFollowingFragment pf = new youFollowingFragment();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fram_layout, pf).commit();

            }
        });


        binding.textView25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), QrActivity.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(intent, b);
                if (getActivity() instanceof TransitionHandler) {
                    ((TransitionHandler) getActivity()).performTransition();
                }
            }
        });

        binding.notesstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddnotesActivity.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(intent, b);
                if (getActivity() instanceof TransitionHandler) {
                    ((TransitionHandler) getActivity()).performTransition();
                }

            }
        });

        binding.notesstart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                BottomSheetFragmentplaynotes2 bottomSheetFragment = new BottomSheetFragmentplaynotes2(view.getContext());
                bottomSheetFragment.show(((AppCompatActivity) view.getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
                return false;
            }
        });


        binding.addhighlites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddHighlitescover.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(intent, b);
                if (getActivity() instanceof TransitionHandler) {
                    ((TransitionHandler) getActivity()).performTransition();
                }
            }
        });
        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Camera", Toast.LENGTH_SHORT).show();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fram_layout, new AddPostFragment()).addToBackStack(null).commit();

            }
        });


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dashboardlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {


                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    if (firebaseUser.getUid().equals(post.getPostedBy())&& "image".equals(post.getPostType())) {
                        dashboardlist.add(post);
                    }

                }
                binding.dashboardRv.setAdapter(dashboardAdapter);
                binding.dashboardRv.hideShimmerAdapter();
                dashboardAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Retrieve user data from Firebase Realtime Database
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get user data from snapshot

                    String bio = dataSnapshot.child("bio").getValue(String.class);
                    String links1 = dataSnapshot.child("links").getValue(String.class);

                    binding.profession.setText(bio);

                    binding.textView24.setText(links1);

                    try {
                        if (links1.isEmpty()){
                            binding.linkslayout.setVisibility(View.GONE);

                        }else {
                            binding.linkslayout.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){

                    }




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        binding.biolayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragmentforbio bottomSheetFragment = new BottomSheetFragmentforbio();
                bottomSheetFragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });





        database.getReference().child("Users").child(Auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    User user = snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getCover_photo())
                            .into(binding.coverphoto);
                    Picasso.get()
                            .load(user.getProfile_photo())
                            .placeholder(R.drawable.profile)
                            .into(binding.pro);
                    binding.textView3.setText(user.getName());
                    binding.textView5.setText(user.getProfission());
                    binding.username.setText(user.getName());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(userId).child("youfollowing");

            // Get the count of "youfollowing" node children
            userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long count = snapshot.getChildrenCount(); // Get the count
                    binding.textView17.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to get follow count");
                }
            });


        }


        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userFavoritesRef = databaseReference.child("Users").child(userId).child("yourfollowers");

            // Get the count of "youfollowing" node children
            userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long count = snapshot.getChildrenCount(); // Get the count
                    binding.textView16.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showToast("Failed to get follow count");
                }
            });


        }


        binding.changecoverphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });

        binding.verifiedAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 22);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 11) {
            if (data.getData() != null) {
                Uri uri = data.getData();
                binding.coverphoto.setImageURI(uri);


                final StorageReference reference = storage.getReference().child("Cover_photo").child(FirebaseAuth.getInstance().getUid());
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Cover photo Saved", Toast.LENGTH_SHORT).show();

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                database.getReference().child("Users").child(Auth.getUid()).child("Cover_photo").setValue(uri.toString());
                            }
                        });
                    }
                });
            }
        } else {
            try {
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    binding.pro.setImageURI(uri);

                    final StorageReference reference = storage.getReference().child("Profile_photo").child(FirebaseAuth.getInstance().getUid());
                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Profile photo Saved", Toast.LENGTH_SHORT).show();

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    database.getReference().child("Users").child(Auth.getUid()).child("Profile_photo").setValue(uri.toString());
                                }
                            });
                        }
                    });
                }
            } catch (Exception e) {

            }

        }

    }


    private void Logout() {
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();

        Dialog dialog = new Dialog(getContext());
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView login = dialog.findViewById(R.id.logout);
        TextView cancel = dialog.findViewById(R.id.cancel);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getContext(), StartActivityFirst.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void readData() {
        DatabaseReference databaseReference;
        databaseReference = FirebaseDatabase.getInstance().getReference("notesData");

        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve data
                    String coverUrl = dataSnapshot.child("coverUrl").getValue(String.class);
                    String notes = dataSnapshot.child("notes").getValue(String.class);
                    String songUrl = dataSnapshot.child("songurl").getValue(String.class);
                    String subtitle = dataSnapshot.child("subtitle").getValue(String.class);
                    String title = dataSnapshot.child("title").getValue(String.class);

                  binding.notetxt.setText(notes);
                  binding.title.setText(title);
                  binding.title.setSelected(true);
                } else {
                    Log.d("Firebase", "No data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Log.e("Firebase", "Failed to read data", databaseError.toException());
            }
        });
    }

    private void retrieveUserPhoneNumbers() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> phoneToUidMap = new HashMap<>();
                Set<String> firebasePhoneNumbers = new HashSet<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String uid = userSnapshot.getKey();
                    String phoneNumber = userSnapshot.child("profission").getValue(String.class);
                    if (phoneNumber != null) {
                        phoneNumber = formatPhoneNumber(phoneNumber);
                        firebasePhoneNumbers.add(phoneNumber);
                        phoneToUidMap.put(phoneNumber, uid);
                    }
                }

                // Display Firebase phone numbers in a Toast
                showFirebasePhoneNumbersInToast(firebasePhoneNumbers);

                // Retrieve device contacts after fetching Firebase phone numbers
                retrieveDeviceContacts(firebasePhoneNumbers, phoneToUidMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void showFirebasePhoneNumbersInToast(Set<String> firebasePhoneNumbers) {
        if (firebasePhoneNumbers.isEmpty()) {
            Toast.makeText(requireContext(), "No phone numbers found in Firebase.", Toast.LENGTH_SHORT).show();
        } else {
            StringBuilder sb = new StringBuilder("Firebase phone numbers:\n");
            for (String phoneNumber : firebasePhoneNumbers) {
                sb.append(phoneNumber).append("\n");
            }

        }
    }

    private void retrieveDeviceContacts(Set<String> firebasePhoneNumbers, Map<String, String> phoneToUidMap) {
        // Check for permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            return;
        }

        ContentResolver contentResolver = requireContext().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        Set<String> deviceContacts = new HashSet<>();

        while (cursor != null && cursor.moveToNext()) {
            String contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            // Format phone number for comparison
            contactNumber = formatPhoneNumber(contactNumber);
            deviceContacts.add(contactNumber);
        }

        if (cursor != null) {
            cursor.close();
        }

        // Find common contacts
        findCommonContacts(firebasePhoneNumbers, deviceContacts, phoneToUidMap);
    }

    private void findCommonContacts(Set<String> firebasePhoneNumbers, Set<String> deviceContacts, Map<String, String> phoneToUidMap) {
        Set<String> commonContactsSet = new HashSet<>(firebasePhoneNumbers);
        commonContactsSet.retainAll(deviceContacts);

        List<Contact> commonContacts = new ArrayList<>();
        for (String contact : commonContactsSet) {
            String uid = phoneToUidMap.get(contact);
            if (uid != null) {
                commonContacts.add(new Contact(contact, uid));
            }
        }

        // Display common contacts in RecyclerView
        displayCommonContacts(commonContacts);
    }

    private void showCommonContactsInToast(Set<String> commonContacts, List<String> commonContactUids) {
        if (commonContacts.isEmpty()) {
            Toast.makeText(requireContext(), "No common contacts found.", Toast.LENGTH_SHORT).show();
        } else {
            StringBuilder sb = new StringBuilder("Common contacts:\n");
            int index = 0;
            for (String contact : commonContacts) {
                sb.append(contact).append(" (UID: ").append(commonContactUids.get(index)).append(")\n");
                index++;
            }
            Toast.makeText(requireContext(), sb.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Normalize phone number: remove non-numeric characters
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");

        // Assuming phone number is in the format without country code
        // Format phone number with country code (assuming +91 for India)
        return "+91" + phoneNumber;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                retrieveUserPhoneNumbers(); // Retry fetching user phone numbers if permission granted
            } else {
                Toast.makeText(requireContext(), "Permission denied. Cannot access contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void displayCommonContacts(List<Contact> commonContacts) {
        contactsAdapter = new ContactsAdapter(commonContacts,getApplicationContext());
        binding.contactsRecyclerView.setAdapter(contactsAdapter);
    }



}