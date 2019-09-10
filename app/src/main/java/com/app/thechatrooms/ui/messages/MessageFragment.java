package com.app.thechatrooms.ui.messages;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.adapters.GroupOnlineMembersAdapter;
import com.app.thechatrooms.adapters.MessageAdapter;
import com.app.thechatrooms.models.GroupOnlineUsers;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.utilities.Parameters;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements MessageAdapter.MessageInterface {

    private static final String TAG = "MessageFragment";
    ArrayList<Messages> messagesArrayList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private User user;
    private DatabaseReference myRef, groupDbRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private String groupId;
    private LinkedHashMap<String, GroupOnlineUsers> hashMap = new LinkedHashMap<>();
    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        EditText editText = view.findViewById(R.id.fragment_chats_message_EditText);
        ImageButton sendButton = view.findViewById(R.id.fragment_chats_send_button);

        mAuth = FirebaseAuth.getInstance();
        groupId = getArguments().getString("GroupID");
        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        setHasOptionsMenu(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/messages/" + groupId);
        groupDbRef = firebaseDatabase.getReference("chatRooms/groupChatRoom/"+groupId+"/membersListWithOnlineStatus");
        /*groupDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashMap.clear();
                for (DataSnapshot val: dataSnapshot.getChildren()){
                    GroupOnlineUsers onlineUser = val.getValue(GroupOnlineUsers.class);
                    hashMap.put(val.getKey(),onlineUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesArrayList.clear();
                for (DataSnapshot val : dataSnapshot.getChildren()) {
                    Messages messages = val.getValue(Messages.class);
                    messagesArrayList.add(messages);
                    RecyclerView recyclerView = view.findViewById(R.id.fragment_chats_recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    messageAdapter = new MessageAdapter(user, groupId, messagesArrayList, getActivity(), getContext(), MessageFragment.this);
                    recyclerView.setAdapter(messageAdapter);
                    messageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendButton.setOnClickListener(view1 -> {
            if (!editText.getText().toString().isEmpty()) {
                String messageId = myRef.push().getKey();
                String createdOn = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(new Date());
                Messages messages = new Messages(messageId, editText.getText().toString(), user.getId(),
                        user.getFirstName() + " " + user.getLastName(), createdOn);
                myRef.child(messageId).setValue(messages);
                editText.setText("");
                hideKeyboard(getContext(), view1);
            }
        });
        return view;
    }

    public void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.message, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_createGroup:
                return false;
            case R.id.action_showMembers:
                Log.i("item id ", item.getItemId() + "");
                FragmentManager manager = getFragmentManager();
                DialogFragment fragment = new ShowMembersFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Parameters.SHOW_MEMBERS,"chatRooms/groupChatRoom/"+groupId+"/membersListWithOnlineStatus");
                fragment.setArguments(bundle);
                fragment.show(manager,"show_members");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        groupDbRef.child(user.getId()).child("online").setValue(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        groupDbRef.child(user.getId()).child("online").setValue(false);
    }
}
