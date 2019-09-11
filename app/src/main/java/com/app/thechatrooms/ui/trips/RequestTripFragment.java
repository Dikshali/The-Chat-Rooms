package com.app.thechatrooms.ui.trips;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

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
public class RequestTripFragment extends Fragment implements OnMapReadyCallback {

    String TAG = "RequestTrip";
    User user;
    String groupId;
    private DatabaseReference myRef, tripRef;
    private FirebaseDatabase firebaseDatabase;
    private GoogleMap mMap;

    public RequestTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_trip, container, false);
        Button request = view.findViewById(R.id.framgnet_request_trip_RequestButton);
        groupId = getArguments().getString(Parameters.GROUP_ID);
        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        PlaceLatitueLongitude startPointLocation = new PlaceLatitueLongitude(), endPointLocation = new PlaceLatitueLongitude();
        Places.initialize(getContext(), "AIzaSyCjQlEN9SKDCtC30zy7grp-lyhPjEv792Q");
        AutocompleteSupportFragment startPoint = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.fragment_request_trip_start_point);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/messages/" + groupId);

        // Specify the types of place data to return.

        startPoint.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        AutocompleteSupportFragment endPoint = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.fragment_request_trip_end_point);
        endPoint.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
// Set up a PlaceSelectionListener to handle the response.
        startPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                LatLng latLng = place.getLatLng();
                startPointLocation.setLatitude(latLng.latitude);
                startPointLocation.setLongitude(latLng.longitude);
                Log.d("StartPoint", place.getLatLng().latitude + " -- " + place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
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

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startPointLocation.isEmpty() || endPointLocation.isEmpty()) {
                    Log.d("EMPTY", String.valueOf(startPointLocation.isEmpty()) + " --- " + String.valueOf(endPointLocation.isEmpty()));
                    Toast.makeText(getContext(), "Please select start and end points", Toast.LENGTH_LONG).show();


                } else {
                    Toast.makeText(getContext(), "GOT START AND END POINTS", Toast.LENGTH_LONG).show();
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    myRef = firebaseDatabase.getReference("chatRooms/messages/" + groupId);
                    String messageId = myRef.push().getKey();
                    String createdOn = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
                    Messages messages = new Messages(messageId, Parameters.TRIP_REQUEST, user.getId(),
                            user.getFirstName() + " " + user.getLastName(), createdOn, Parameters.MESSAGE_TYPE_RIDE_REQUEST);
                    myRef.child(messageId).setValue(messages);
                    tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId);

                    Trips trips = new Trips(Parameters.TRIP_STATUS_START, user.getId(), null, startPointLocation, endPointLocation, null);
                    tripRef.setValue(trips);
                }
            }
        });


        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
