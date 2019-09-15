package com.app.thechatrooms.ui.messages;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.adapters.MessageAdapter;
import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.profile.ProfileFragment;
import com.app.thechatrooms.ui.trips.PickUpOffersFragment;
import com.app.thechatrooms.ui.trips.RequestTripFragment;
import com.app.thechatrooms.ui.trips.TripLiveLocationFragment;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements MessageAdapter.MessageInterface {

    private static final String TAG = "MessageFragment";
    static final int PICKUPOFFERS = 1, LIVELOCATION = 2, TRIPREQUEST=3;
    ArrayList<Messages> messagesArrayList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private User user;
    private DatabaseReference myRef, groupDbRef, tripRef,userRef;
    private FirebaseDatabase firebaseDatabase;
    LatLng latlng;
    LocationListener locationListener;
    private FirebaseAuth mAuth;
    Location location;
    private String groupId;

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
                        user.getFirstName() + " " + user.getLastName(), createdOn, Parameters.MESSAGE_TYPE_NORMAL);
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
                RequestTripFragment requestTripFragment = new RequestTripFragment();
                Bundle requestRideBundle = new Bundle();
                requestRideBundle.putSerializable(Parameters.USER_ID, user);
                requestRideBundle.putString(Parameters.GROUP_ID, groupId);
                requestTripFragment.setArguments(requestRideBundle);
                fragmentTransaction.replace(R.id.nav_host_fragment,requestTripFragment).addToBackStack(null);
                fragmentTransaction.commit();
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
        Intent intent = new Intent(getActivity(), PickUpOffersFragment.class);
        intent.putExtra(Parameters.GROUP_ID, groupId);
        intent.putExtra(Parameters.MESSAGE_ID, messageId);
        startActivityForResult(intent, PICKUPOFFERS);
    }

    @Override
    public void setDriversLocation(User user, String messageId) {
        tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId );

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latlng = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location!=null){
            latlng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100,10,locationListener);
        }
        PlaceLatitudeLongitude placeLatitudeLongitude = new PlaceLatitudeLongitude(latlng.latitude, latlng.longitude);
        Drivers drivers = new Drivers(user.getId(), user.getFirstName(), placeLatitudeLongitude);
        tripRef.child(Parameters.DRIVERS).child(drivers.getDriverId()).setValue(drivers);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toast.makeText(getContext(), "DESTROY", Toast.LENGTH_LONG).show();
        tripRef = firebaseDatabase.getReference("chatRooms/trips/" );
        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    PlaceLatitudeLongitude startPoint = null;
    Drivers drivers = null;

    @Override
    public void viewDriversProgress(String messageId) {
        tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId );

        Intent intent = new Intent(getActivity(), TripLiveLocationFragment.class);

        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startPoint = dataSnapshot.child(Parameters.START_POINT).getValue(PlaceLatitudeLongitude.class);
                drivers = dataSnapshot.child(Parameters.DRIVER_ACCEPTED).getValue(Drivers.class);
                Log.d("Drivers", dataSnapshot.child(Parameters.DRIVER_ACCEPTED).toString());
                intent.putExtra(Parameters.DRIVER_ACCEPTED ,drivers);
                intent.putExtra(Parameters.START_POINT, startPoint);
                startActivityForResult(intent, LIVELOCATION);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
}
