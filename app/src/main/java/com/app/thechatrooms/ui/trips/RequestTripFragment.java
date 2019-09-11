package com.app.thechatrooms.ui.trips;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.PlaceLatitueLongitude;
import com.app.thechatrooms.models.Trips;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestTripFragment extends FragmentActivity {

    String TAG = "RequestTrip";
    static final int TRIPREQUEST=3;
    User user;
    String groupId;
    private DatabaseReference myRef, tripRef, addTrip;
    private FirebaseDatabase firebaseDatabase;
    private GoogleMap mMap;
    PlaceLatitueLongitude startPointLocation = new PlaceLatitueLongitude(), endPointLocation = new PlaceLatitueLongitude();

    public RequestTripFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_request_trip);
        Intent intent = getIntent();
        Button request = findViewById(R.id.framgnet_request_trip_RequestButton);
        Places.initialize(getApplicationContext(), "AIzaSyCjQlEN9SKDCtC30zy7grp-lyhPjEv792Q");
        groupId = intent.getStringExtra(Parameters.GROUP_ID);
        user = (User) intent.getSerializableExtra(Parameters.USER_ID);
        AutocompleteSupportFragment startPoint = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_request_trip_start_point);
        AutocompleteSupportFragment endPoint = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_request_trip_end_point);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/messages/"+groupId);
        startPoint.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        endPoint.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        startPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                LatLng latLng = place.getLatLng();
                startPointLocation.setLatitude(latLng.latitude);
                startPointLocation.setLongitude(latLng.longitude);
                Log.d("StartPoint", place.getLatLng().latitude + " -- " + place.getLatLng().longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
                startPointLocation.setLatitude(null);
                startPointLocation.setLongitude(null);

            }
        });

        endPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                LatLng latLng = place.getLatLng();
                endPointLocation.setLongitude(latLng.longitude);
                endPointLocation.setLatitude(latLng.latitude);
                Log.d("EndPoint", latLng.latitude + " -- " + latLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {
                endPointLocation.setLongitude(null);
                endPointLocation.setLongitude(null);

            }
        });
        request.setOnClickListener(view -> {
            if (startPointLocation.isEmpty() || endPointLocation.isEmpty()) {
                Log.d("EMPTY", String.valueOf(startPointLocation.isEmpty()) + " --- " + String.valueOf(endPointLocation.isEmpty()));
                Toast.makeText(getApplicationContext(), "Please select start and end points", Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(getApplicationContext(), "GOT START AND END POINTS", Toast.LENGTH_LONG).show();
                firebaseDatabase = FirebaseDatabase.getInstance();
                myRef = firebaseDatabase.getReference("chatRooms/messages/" + groupId);
                String messageId = myRef.push().getKey();
                String createdOn = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
                Messages messages = new Messages(messageId, Parameters.TRIP_REQUEST, user.getId(),
                        user.getFirstName() + " " + user.getLastName(), createdOn, Parameters.MESSAGE_TYPE_RIDE_REQUEST);
                myRef.child(messageId).setValue(messages);
                tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId);
                addTrip = firebaseDatabase.getReference("chatRooms");
                String key = addTrip.child(Parameters.ADD_TRIPS).child(Parameters.RIDERS).child(user.getId()).push().getKey();
                addTrip.child(Parameters.ADD_TRIPS).child(Parameters.RIDERS).child(user.getId()).child(key).setValue(messageId);
                Trips trips = new Trips(Parameters.TRIP_STATUS_START, user.getId(), null, startPointLocation, endPointLocation, null);
                tripRef.setValue(trips);

                super.onBackPressed();
            }
        });



    }
}
