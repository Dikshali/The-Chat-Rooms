package com.app.thechatrooms.ui.trips;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.PlaceLatitueLongitude;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PickUpOffersFragment extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private User user;
    private String messageId, groupId;


    private DatabaseReference myRef, messageRef, tripRef;
    PlaceLatitueLongitude riderLocation;
    private GoogleMap mMap;
    private FirebaseDatabase firebaseDatabase;
    private HashMap<String, Drivers> driversArrayList =new HashMap<>();
    SupportMapFragment mapFragment;
    public PickUpOffersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pick_up_offers);
        Intent i = getIntent();
        groupId = i.getStringExtra(Parameters.GROUP_ID);
        messageId = i.getStringExtra(Parameters.MESSAGE_ID);
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_pick_up_offers, container, false);
//        user = (User) getArguments().getSerializable(Parameters.USER_ID);
//        messageId = getArguments().getString(Parameters.MESSAGE_ID);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_request_trip_map);
        myRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Parameters.DRIVERS).exists()){
                    for (DataSnapshot val: dataSnapshot.child(Parameters.DRIVERS).getChildren()){
//                        Drivers drivers = val.
//                        PlaceLatitueLongitude placeLatitueLongitude = (PlaceLatitueLongitude) val.child("driverLocation").getValue();

                        Drivers drivers = val.getValue(Drivers.class);
                        driversArrayList.put(drivers.getDriverId(), drivers);
//                        PlaceLatitueLongitude placeLatitueLongitude = val.child("driverLocation").getValue(PlaceLatitueLongitude.class);
                        Log.d("Location", drivers.getDriverName());


                    }
                    Toast.makeText(PickUpOffersFragment.this,"DRIVERS HERE", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(PickUpOffersFragment.this,"NO OFFERS", Toast.LENGTH_LONG).show();
                mapFragment.getMapAsync(PickUpOffersFragment.this::onMapReady);
                riderLocation = dataSnapshot.child("startPoint").getValue(PlaceLatitueLongitude.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        mapFragment.getMapAsync(this);

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
//        boundsBuilder.include(myLocation);
        int routePadding = 200;

//        LatLngBounds latLngBounds = boundsBuilder.build();
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));

        for (HashMap.Entry<String, Drivers> entry: driversArrayList.entrySet()){

            LatLng latLng = new LatLng(entry.getValue().getDriverLocation().getLatitude(), entry.getValue().getDriverLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(entry.getValue().getDriverName())).setTag(entry.getValue().getDriverId());
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            boundsBuilder.include(latLng);

        }
        LatLng latLng = new LatLng(riderLocation.getLatitude(), riderLocation.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        Marker locationMarker = mMap.addMarker(marker);
        locationMarker.showInfoWindow();
        boundsBuilder.include(latLng);
//        mMap.addMarker(new MarkerOptions().position(riderLocation.getLatitude(), riderLocation.getLongitude()));
        LatLngBounds latLngBounds = boundsBuilder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));

        mMap.setOnMarkerClickListener(this::onMarkerClick);
//        LatLng sydney = new LatLng(-34, 151);



    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("MARKER", (String) marker.getTag());

        messageRef = firebaseDatabase.getReference("chatRooms/messages/"+groupId);
        messageRef.child(messageId).child(Parameters.MESSAGE_TYPE).setValue(Parameters.MESSAGE_TYPE_RIDE_IN_PROGRESS);
        messageRef.child(messageId).child(Parameters.MESSAGE).setValue(Parameters.TRIP_PROGRESS);
        tripRef = firebaseDatabase.getReference("chatRooms/trips/"+messageId);
        tripRef.child(Parameters.TRIP_STATUS).setValue(Parameters.TRIP_STATUS_PROGRESS);
        tripRef.child(Parameters.DRIVERS).setValue(null);

        tripRef.child(Parameters.DRIVER_ACCEPTED).setValue(marker.getTag());
        return false;
    }
}
