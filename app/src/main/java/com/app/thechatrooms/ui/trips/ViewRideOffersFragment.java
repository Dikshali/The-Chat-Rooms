package com.app.thechatrooms.ui.trips;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.thechatrooms.R;
import com.app.thechatrooms.adapters.OffersAdapter;
import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.OfferDrivers;
import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.models.TripStatus;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewRideOffersFragment extends Fragment implements OffersAdapter.OffersInterface {

    private String messageId, groupId;


    private DatabaseReference myRef, messageRef, tripRef, addTrip;
    private User user;
    PlaceLatitudeLongitude riderLocation;
    private GoogleMap mMap;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<OfferDrivers> driversArrayList =new ArrayList<>();
    private RecyclerView recyclerView;
    private Boolean isUserDriver = false;
    private OffersAdapter offersAdapter;


    public ViewRideOffersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_ride_offers, container, false);
        groupId = getArguments().getString(Parameters.GROUP_ID);
        messageId = getArguments().getString(Parameters.MESSAGE_ID);
        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/");
        recyclerView = view.findViewById(R.id.fragment_view_ride_offers_recyclerView);

//        offersAdapter = new OffersAdapter(getContext(), drive)

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("trips").child(messageId).child(Parameters.DRIVERS).exists()){
                    driversArrayList.clear();

                    isUserDriver = false;
                    for (DataSnapshot val: dataSnapshot.child("trips").child(messageId).child(Parameters.DRIVERS).getChildren()){

//                        dataSnapshot.child("userProfile").child()
                        Drivers drivers = val.getValue(Drivers.class);
                        String img = (String) dataSnapshot.child("userProfiles/").child(drivers.getDriverId()).child("userProfileImageUrl").getValue();
                        Location startPoint = new Location("startPoint");
                        startPoint.setLatitude((Double) dataSnapshot.child("trips").child(messageId).child(Parameters.START_POINT).child(Parameters.LATITUDE).getValue());
                        startPoint.setLongitude((Double) dataSnapshot.child("trips").child(messageId).child(Parameters.START_POINT).child(Parameters.LONGITUDE).getValue());
                        OfferDrivers offerDrivers = new OfferDrivers(drivers, img, startPoint);

                        driversArrayList.add(offerDrivers);
                        if (drivers.getDriverId().equals(user.getId()))
                            isUserDriver = true;
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        offersAdapter = new OffersAdapter(getContext(), driversArrayList, driverId -> ViewRideOffersFragment.this.driverSelected(driverId));
                        recyclerView.setAdapter(offersAdapter);
                        offersAdapter.notifyDataSetChanged();
                        Log.d("Location", drivers.getDriverName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void driverSelected(Drivers driverId) {
        messageRef = firebaseDatabase.getReference("chatRooms/messages/"+groupId);
        messageRef.child(messageId).child(Parameters.MESSAGE_TYPE).setValue(Parameters.MESSAGE_TYPE_RIDE_IN_PROGRESS);
        messageRef.child(messageId).child(Parameters.MESSAGE).setValue(Parameters.TRIP_PROGRESS);
        messageRef.child(messageId).child("notification").setValue(true);

        tripRef = firebaseDatabase.getReference("chatRooms/trips/"+messageId);
        tripRef.child(Parameters.TRIP_STATUS).setValue(TripStatus.IN_PROGESS);

        tripRef.child(Parameters.DRIVERS).setValue(null);
        tripRef.child(Parameters.DRIVER_ACCEPTED).setValue(driverId);
        addTrip = firebaseDatabase.getReference("chatRooms");
        String key = addTrip.child(Parameters.ADD_TRIPS).child(Parameters.DRIVERS).child(driverId.getDriverId()).push().getKey();
        addTrip.child(Parameters.ADD_TRIPS).child(Parameters.DRIVERS).child(driverId.getDriverId()).child(key).setValue(messageId);

        getFragmentManager().popBackStack();
    }
}
