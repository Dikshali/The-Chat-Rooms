package com.app.thechatrooms.ui.messages;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.adapters.ShowMembersAdapter;
import com.app.thechatrooms.models.GroupOnlineUsers;
import com.app.thechatrooms.utilities.Parameters;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ShowMembersFragment extends DialogFragment {

    RecyclerView recyclerView;
    ShowMembersAdapter membersAdapter;
    LinkedHashMap<String,GroupOnlineUsers> hashMap = new LinkedHashMap<>();
    String dbReferencePoint;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference groupDbRef;

    public ShowMembersFragment(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_show_members, new LinearLayout(getActivity()),false);
        if (getArguments()!=null)
            dbReferencePoint = getArguments().getString(Parameters.SHOW_MEMBERS);
        recyclerView = view.findViewById(R.id.show_members_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseDatabase = FirebaseDatabase.getInstance();
        groupDbRef = firebaseDatabase.getReference(dbReferencePoint);
        groupDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashMap.clear();
                for (DataSnapshot val: dataSnapshot.getChildren()){
                    GroupOnlineUsers onlineUser = val.getValue(GroupOnlineUsers.class);
                    hashMap.put(val.getKey(),onlineUser);
                }
                membersAdapter = new ShowMembersAdapter(getContext(),hashMap);
                recyclerView.setAdapter(membersAdapter);
                membersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Dialog builder = new Dialog(getActivity());
        builder.setTitle("Show Members");
        builder.setContentView(view);

        Window window = builder.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return builder;
    }
}
