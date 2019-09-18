package com.app.thechatrooms.ui.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.adapters.ContactsRecyclerView;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.profile.ProfileFragment;
import com.app.thechatrooms.utilities.Parameters;
import com.app.thechatrooms.utilities.RecyclerItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    private static final String TAG = "ContactsFragment";
    OnContactsSelectedListener callback;
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<User> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsRecyclerView contactsRecyclerView;

    public void setOnContactsSelectedListener(OnContactsSelectedListener callback) {
        this.callback = callback;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.menu_contacts);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contacts, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/userProfiles");
        recyclerView = root.findViewById(R.id.fragment_contacts_recyclerView);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("CHILD", child.toString());
                    User user = child.getValue(User.class);
                    userList.add(user);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    contactsRecyclerView = new ContactsRecyclerView(userList, getActivity(), getContext());
                    recyclerView.setAdapter(contactsRecyclerView);
                    contactsRecyclerView.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                User recyclerUser = userList.get(position);
                Log.v(TAG, recyclerUser.toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable(Parameters.USER_ID, recyclerUser);
                ProfileFragment fragment = new ProfileFragment();
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment, "Profile Fragment").addToBackStack(null);
                callback.updateProfileToolbar(Parameters.PROFILE);
                fragmentTransaction.commit();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return root;
    }

    public interface OnContactsSelectedListener {
        public void updateProfileToolbar(String title);
    }
}