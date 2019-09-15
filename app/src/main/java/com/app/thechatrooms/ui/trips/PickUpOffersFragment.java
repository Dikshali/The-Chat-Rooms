package com.app.thechatrooms.ui.trips;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.models.TripStatus;
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

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class PickUpOffersFragment extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    PlaceLatitudeLongitude riderLocation;
    SupportMapFragment mapFragment;
    Drivers drivers;
    private User user;
    private String messageId, groupId;
    private DatabaseReference myRef, messageRef, tripRef, addTrip;
    private GoogleMap mMap;
    private FirebaseDatabase firebaseDatabase;
    private HashMap<String, Drivers> driversArrayList = new HashMap<>();


    public PickUpOffersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pick_up_offers);
        Intent i = getIntent();
        groupId = i.getStringExtra(Parameters.GROUP_ID);
        messageId = i.getStringExtra(Parameters.MESSAGE_ID);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_request_trip_map);
        myRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Parameters.DRIVERS).exists()) {
                    for (DataSnapshot val : dataSnapshot.child(Parameters.DRIVERS).getChildren()) {
                        Drivers drivers = val.getValue(Drivers.class);
                        driversArrayList.put(drivers.getDriverId(), drivers);
                        Log.d("Location", drivers.getDriverName());
                    }
                    Toast.makeText(PickUpOffersFragment.this, "DRIVERS HERE", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(PickUpOffersFragment.this, "NO OFFERS", Toast.LENGTH_LONG).show();
                mapFragment.getMapAsync(PickUpOffersFragment.this::onMapReady);
                riderLocation = dataSnapshot.child("startPoint").getValue(PlaceLatitudeLongitude.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        int routePadding = 200;
        for (HashMap.Entry<String, Drivers> entry : driversArrayList.entrySet()) {
            LatLng latLng = new LatLng(entry.getValue().getDriverLocation().getLatitude(), entry.getValue().getDriverLocation().getLongitude());
            MarkerOptions mkOptions = new MarkerOptions().position(latLng).title(entry.getValue().getDriverName());
            Marker locationMarker = mMap.addMarker(mkOptions);
            locationMarker.setTag(entry.getValue().getDriverId());
            locationMarker.showInfoWindow();
            boundsBuilder.include(latLng);
        }
        LatLng latLng = new LatLng(riderLocation.getLatitude(),riderLocation.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(marker);
        boundsBuilder.include(latLng);
        LatLngBounds latLngBounds = boundsBuilder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        mMap.setOnMarkerClickListener(this::onMarkerClick);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null) {
            Log.d("MARKER", (String) marker.getTag());
            messageRef = firebaseDatabase.getReference("chatRooms/messages/" + groupId);
            messageRef.child(messageId).child(Parameters.MESSAGE_TYPE).setValue(Parameters.MESSAGE_TYPE_RIDE_IN_PROGRESS);
            messageRef.child(messageId).child(Parameters.MESSAGE).setValue(Parameters.TRIP_PROGRESS);
            tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId);
            tripRef.child(Parameters.TRIP_STATUS).setValue(TripStatus.IN_PROGESS);
            drivers = new Drivers();
            tripRef.child(Parameters.DRIVERS).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Drivers drivers1 = dataSnapshot.child((String) marker.getTag()).getValue(Drivers.class);
                    addDriverAccepted(drivers1, messageId);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return false;
    }

    public void addDriverAccepted(Drivers drivers, String messageId) {
        tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId);
        tripRef.child(Parameters.DRIVERS).setValue(null);
        tripRef.child(Parameters.DRIVER_ACCEPTED).setValue(drivers);
        addTrip = firebaseDatabase.getReference("chatRooms");
        String key = addTrip.child(Parameters.ADD_TRIPS).child(Parameters.DRIVERS).child(drivers.getDriverId()).push().getKey();
        addTrip.child(Parameters.ADD_TRIPS).child(Parameters.DRIVERS).child(drivers.getDriverId()).child(key).setValue(messageId);
        finish();
    }
}
