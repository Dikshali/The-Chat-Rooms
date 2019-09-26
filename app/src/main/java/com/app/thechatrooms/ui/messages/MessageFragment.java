package com.app.thechatrooms.ui.messages;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.MapsActivity;
import com.app.thechatrooms.R;
import com.app.thechatrooms.adapters.MessageAdapter;
import com.app.thechatrooms.adapters.OffersAdapter;
import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.models.TripStatus;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.profile.ProfileFragment;
import com.app.thechatrooms.ui.trips.RequestTripFragment;
import com.app.thechatrooms.ui.trips.DriverLiveLocationFragment;
import com.app.thechatrooms.ui.trips.RiderLiveLocationFragment;
import com.app.thechatrooms.ui.trips.ViewRideOffersFragment;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements MessageAdapter.MessageInterface{

    private static final String TAG = "MessageFragment";
    static final int PICKUPOFFERS = 1, LIVELOCATION = 2, TRIPREQUEST=3;
    ArrayList<Messages> messagesArrayList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private User user;
    private DatabaseReference myRef, groupDbRef, tripRef,userRef, addTripRef;
    private FirebaseDatabase firebaseDatabase;
    LatLng latlng;
    LocationListener locationListener;
    private Boolean aBoolean = false;
    private FirebaseAuth mAuth;
    Location location;
    private String groupId;
    private MenuItem requestTripMenuItem;

    ArrayList<String> tripIds = new ArrayList<>();

    private double longitude, latitude;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private String messageId="";
    private PlaceLatitudeLongitude driversCurrentLocation;
    private ValueEventListener val;

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
        groupDbRef = firebaseDatabase.getReference("chatRooms/groupChatRoom/" + groupId + "/membersListWithOnlineStatus");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesArrayList.clear();
                tripIds.clear();
                aBoolean = false;
                for (DataSnapshot val : dataSnapshot.getChildren()) {

                    Messages messages = val.getValue(Messages.class);
                    messagesArrayList.add(messages);
                    if (messages.getCreatedBy().equals(user.getId()) && !messages.getMessageType().equals(Parameters.MESSAGE_TYPE_NORMAL)){
                        tripIds.add(messages.getMessageId());
                        if (!messages.getMessageType().equals(Parameters.MESSAGE_TYPE_RIDE_END))
                            aBoolean = true;
                    }
                    RecyclerView recyclerView = view.findViewById(R.id.fragment_chats_recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    messageAdapter = new MessageAdapter(user, groupId, messagesArrayList, getActivity(), getContext(), MessageFragment.this);
                    recyclerView.setAdapter(messageAdapter);
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(messagesArrayList.size()-1);
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
                        user.getFirstName() + " " + user.getLastName(), createdOn, Parameters.MESSAGE_TYPE_NORMAL, true);
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
        FragmentManager manager = getFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.action_createGroup:
                return false;
            case R.id.action_showMembers:
                Log.i("item id ", item.getItemId() + "");
                DialogFragment fragment = new ShowMembersFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Parameters.SHOW_MEMBERS,"chatRooms/groupChatRoom/"+groupId+"/membersListWithOnlineStatus");
                fragment.setArguments(bundle);
                fragment.show(manager,"show_members");
                return true;
            case R.id.action_requestTrip:
                if (!aBoolean){
                    RequestTripFragment requestTripFragment = new RequestTripFragment();
                    Bundle requestRideBundle = new Bundle();
                    requestRideBundle.putSerializable(Parameters.USER_ID, user);
                    requestRideBundle.putString(Parameters.GROUP_ID, groupId);
                    requestTripFragment.setArguments(requestRideBundle);
                    fragmentTransaction.replace(R.id.nav_host_fragment,requestTripFragment).addToBackStack(null);
                    fragmentTransaction.commit();
                } else
                    Toast.makeText(getContext(),"TRIP NOT COMPLETED", Toast.LENGTH_LONG).show();
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

    @Override
    public void viewPickUpOffers(String messageId) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        ViewRideOffersFragment viewRideOffersFragment = new ViewRideOffersFragment();
        Bundle rideOffersBundle = new Bundle();
        rideOffersBundle.putString(Parameters.GROUP_ID, groupId);
        rideOffersBundle.putString(Parameters.MESSAGE_ID, messageId);
        rideOffersBundle.putSerializable(Parameters.USER_ID, user);
        viewRideOffersFragment.setArguments(rideOffersBundle);
        fragmentTransaction.replace(R.id.nav_host_fragment,viewRideOffersFragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void setDriversLocation(User user, String messageId) {
        this.messageId = messageId;
        tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId );
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        locationRequest.setFastestInterval(20 * 1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        PlaceLatitudeLongitude placeLatitueLongitude = new PlaceLatitudeLongitude(latitude, longitude);
                        Drivers drivers = new Drivers(user.getId(), user.getFirstName(), placeLatitueLongitude);
                        tripRef.child(Parameters.DRIVERS).child(drivers.getDriverId()).setValue(drivers);
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }else{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        PlaceLatitudeLongitude placeLatitueLongitude = new PlaceLatitudeLongitude(latitude, longitude);
                        Drivers drivers = new Drivers(user.getId(), user.getFirstName(), placeLatitueLongitude);
                        tripRef.child(Parameters.DRIVERS).child(drivers.getDriverId()).setValue(drivers);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKUPOFFERS){
            Toast.makeText(getContext(), "Driver Selected", Toast.LENGTH_LONG).show();
        }
        else if (requestCode == LIVELOCATION){
            Toast.makeText(getContext(), "DRIVER WILL BE HERE SOON", Toast.LENGTH_LONG).show();
        }
        else if (requestCode == TRIPREQUEST){
            Toast.makeText(getContext(), "TRIP REQUEST", Toast.LENGTH_LONG).show();
        }

    }

    PlaceLatitudeLongitude startPoint = null, endPoint = null;
    Drivers drivers = null;



    @Override
    public void viewDriversProgress(String messageId, String groupId ) {

        tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId );
        val = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Parameters.DRIVER_ACCEPTED).exists()) {
                    drivers = dataSnapshot.child(Parameters.DRIVER_ACCEPTED).getValue(Drivers.class);
                    tripRef.removeEventListener(val);
                    Intent intent;
                    if(drivers.getDriverId().equals(user.getId())){
                        intent = new Intent(getActivity(), DriverLiveLocationFragment.class);
                    }else{
                        intent = new Intent(getActivity(), RiderLiveLocationFragment.class);
                    }
                    intent.putExtra(Parameters.MESSAGE_ID, messageId);
                    intent.putExtra(Parameters.GROUP_ID, groupId);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        tripRef.addValueEventListener(val);
    }

    @Override
    public void theirTripRequestInfo(String messageId) {

        tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId );
        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String riderId = (String) dataSnapshot.child("riderId").getValue();
                userRef = firebaseDatabase.getReference("chatRooms/userProfiles");
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User riderData = dataSnapshot.child(riderId).getValue(User.class);
                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = manager.beginTransaction();
                        ProfileFragment profileFragment = new ProfileFragment();
                        Bundle profileBundle = new Bundle();
                        profileBundle.putSerializable(Parameters.USER_ID, riderData);
                        profileFragment.setArguments(profileBundle);
                        fragmentTransaction.replace(R.id.nav_host_fragment,profileFragment).addToBackStack(null);
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void openMap(PlaceLatitudeLongitude startPoint, PlaceLatitudeLongitude endPoint) {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        intent.putExtra(Parameters.START_POINT, startPoint);
        intent.putExtra(Parameters.END_POINT, endPoint);
        startActivity(intent);
    }

    @Override
    public void showNotification(Messages messages, String tripType, String content) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "The_Chat_Rooms";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext(), NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("The Chat Rooms")
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(tripType)
                .setContentText(content)
                .setContentInfo("Information");
        notificationManager.notify(1, notificationBuilder.build());
        myRef = firebaseDatabase.getReference("chatRooms/messages/" + groupId);
        myRef.child(messages.getMessageId()).child("notification").setValue(false);

    }
}
