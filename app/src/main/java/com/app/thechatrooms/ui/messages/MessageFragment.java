package com.app.thechatrooms.ui.messages;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements MessageAdapter.MessageInterface {

    private static final String TAG = "MessageFragment";
    private static final int PICK_IMAGE_REQUEST = 234;
    ArrayList<Messages> messagesArrayList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private User user;
    private DatabaseReference myRef, groupDbRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private String groupId;
    Uri filePath;
    byte[] imageData;
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
        ImageButton attachButton = view.findViewById(R.id.fragment_chats_attach_button);

        mAuth = FirebaseAuth.getInstance();
        groupId = getArguments().getString("GroupID");
        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        setHasOptionsMenu(true);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/messages/" + groupId);
        groupDbRef = firebaseDatabase.getReference("chatRooms/groupChatRoom/"+groupId+"/membersListWithOnlineStatus");

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
                String createdOn = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
                Messages messages = new Messages(messageId, editText.getText().toString(), user.getId(),
                        user.getFirstName() + " " + user.getLastName(), createdOn);
                myRef.child(messageId).setValue(messages);
                editText.setText("");
                hideKeyboard(getContext(), view1);
            }
        });
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.Animation);
        popupWindow.setAttachedInDecor(false);
        //Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
        ImageButton imageButton = popupView.findViewById(R.id.image_button);
        ImageButton mapButton = popupView.findViewById(R.id.map_button);
        ImageButton carButton = popupView.findViewById(R.id.car_button);

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }else {
                    popupWindow.showAtLocation(popupView,Gravity.BOTTOM|Gravity.RIGHT,0,150);
                }

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send current location

                checkCoarseLocationPermissionGranted();
                checkFineLocationPermissionGranted();
                LocationManager service = (LocationManager)getContext().getSystemService(LOCATION_SERVICE);
                boolean isGpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkLocationEnabled = service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGpsEnabled) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                    startActivity(intent);
                }

            }
        });
        return view;
    }

    public void checkFineLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
        }
    }

    public void checkCoarseLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            filePath = data.getData();
            Log.d("demo", "2 " + filePath.getPath().toString());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                imageData = baos.toByteArray();
                //attachImage.setImageBitmap(bitmap);
                //imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
