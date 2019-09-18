package com.app.thechatrooms.ui.chats;

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
import com.app.thechatrooms.adapters.ChatFragmentAdapter;
import com.app.thechatrooms.models.GroupChatRoom;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.TripStatus;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.messages.MessageFragment;
import com.app.thechatrooms.utilities.Parameters;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatsFragment extends Fragment implements ChatFragmentAdapter.ChatFragmentInterface {

    private static final String TAG = "ChatsFragment";
    ArrayList<GroupChatRoom> groupList = new ArrayList<>();
    private View view;
    private User user;
    private ChatFragmentAdapter chatFragmentAdapter;
    private StorageReference mStorageRef;
    private DatabaseReference myRef, groupRef, messageRef, addTripRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private String userId;
    ArrayList<String> tripIds = new ArrayList<>();
    private FirebaseDatabase database;

    @Override
    public void onResume() {
        super.onResume();
        //noChats.setVisibility(View.GONE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.menu_chats);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chats, container, false);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        user = (User) getArguments().getSerializable(Parameters.USER_ID);

        if (user == null) {
            myRef = database.getReference("chatRooms/userProfiles/" + userId);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            });
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/groupChatRoom");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    GroupChatRoom group = new GroupChatRoom();
                    group.setCreatedByName(child.child("createdByName").getValue().toString());
                    group.setCreatedById(child.child("createdById").getValue().toString());
                    group.setCreatedOn(child.child("createdOn").getValue().toString());
                    group.setGroupId(child.child("groupId").getValue().toString());
                    group.setGroupName(child.child("groupName").getValue().toString());
                    if (group.getCreatedById().equals(userId)) {
                        groupList.add(group);
                    } else {
                        if (child.child("membersListWithOnlineStatus").hasChild(userId)) {
                            groupList.add(group);
                        }
                    }
                }

                RecyclerView recyclerView = view.findViewById(R.id.fragment_chats_recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                chatFragmentAdapter = new ChatFragmentAdapter(user, groupList, getActivity(), getContext(), ChatsFragment.this);
                recyclerView.setAdapter(chatFragmentAdapter);
                chatFragmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void deleteGroup(GroupChatRoom groupChatRoom) {
        groupRef = database.getReference("chatRooms/groupChatRoom/" + groupChatRoom.getGroupId());

        groupRef.setValue(null);

        messageRef = database.getReference("chatRooms/messages/"+ groupChatRoom.getGroupId());
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tripIds.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    Messages messages = child.getValue(Messages.class);
                    if (messages.getCreatedBy().equals(user.getId()) && !messages.getMessageType().equals(Parameters.MESSAGE_TYPE_NORMAL)){
                        tripIds.add(child.getKey());
                    }
//
                }

                completeTrips(tripIds, groupChatRoom.getGroupId());
                messageRef.setValue(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        chatFragmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void leaveGroup(GroupChatRoom groupChatRoom) {
        myRef.child(groupChatRoom.getGroupId()).child("membersListWithOnlineStatus").child(userId).setValue(null);
        messageRef = database.getReference("chatRooms/messages/"+ groupChatRoom.getGroupId());
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tripIds.clear();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    Messages messages = child.getValue(Messages.class);
                    if (messages.getCreatedBy().equals(user.getId()) && !messages.getMessageType().equals(Parameters.MESSAGE_TYPE_NORMAL)){
                        tripIds.add(child.getKey());
                    }
//
                }

                completeTrips(tripIds, groupChatRoom.getGroupId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        chatFragmentAdapter.notifyDataSetChanged();
    }

    private void completeTrips(ArrayList<String> tripIds, String groupId){
        messageRef = database.getReference("chatRooms/messages/"+ groupId);
        addTripRef = database.getReference("chatRooms/trips");
        for (String s: tripIds){
            messageRef.child(s).child(Parameters.MESSAGE_TYPE).setValue(Parameters.TRIP_STATUS_END);
            messageRef.child(s).child(Parameters.MESSAGE).setValue(Parameters.TRIP_ENDED);
            addTripRef.child(s).child(Parameters.TRIP_STATUS).setValue(TripStatus.COMPLETED);
        }
 }

    @Override
    public void openMessageWindow(MessageFragment messageFragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, messageFragment,"Message Fragment").addToBackStack(null);
        fragmentTransaction.commit();
    }

}