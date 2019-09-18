package com.app.thechatrooms.ui.trips;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.models.TripStatus;
import com.app.thechatrooms.models.Trips;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.utilities.Parameters;
import com.google.android.gms.common.api.Status;
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
public class RequestTripFragment extends Fragment {

    private static final String TAG = "RequestTripFragment";
    private DatabaseReference myRef, tripRef, addTrip;
    private User user;
    private PlaceLatitudeLongitude pickUpPlace, dropOffPlace;
    private FirebaseDatabase firebaseDatabase;
    private String groupId;

    public RequestTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_trip, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        Places.initialize(getContext(), "AIzaSyCjQlEN9SKDCtC30zy7grp-lyhPjEv792Q");
        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        groupId = getArguments().getString(Parameters.GROUP_ID);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment pickUpAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.requestRide_pickUpAutocompleteFragment);

        // Specify the types of place data to return.
        pickUpAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        pickUpAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + " , "
                        + place.getAddress() + " ," + place.getLatLng().toString());
                pickUpPlace = new PlaceLatitudeLongitude(place.getId(),place.getName(),place.getAddress(),place.getLatLng().latitude,place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment dropOffAutocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.requestRide_dropOffAutocompleteFragment);

        // Specify the types of place data to return.
        dropOffAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        dropOffAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + " , "
                        + place.getAddress() + " ," + place.getLatLng().toString());
                dropOffPlace = new PlaceLatitudeLongitude(place.getId(),place.getName(),place.getAddress(),place.getLatLng().latitude,place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        view.findViewById(R.id.requestRide_requestTripButton).setOnClickListener(view1 -> {
            if (pickUpPlace == null) {
                Toast.makeText(getContext(), "Select a Pick Up Place", Toast.LENGTH_LONG).show();
            } else if (dropOffPlace == null) {
                Toast.makeText(getContext(), "Select a Drop Off Place", Toast.LENGTH_LONG).show();
            } else {
                myRef = firebaseDatabase.getReference("chatRooms/messages/" + groupId);
                String messageId = myRef.push().getKey();
                String createdOn = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
                String textMessage = "Trip Requested\nFrom: " + pickUpPlace.getAddress() +"\nTo:" + pickUpPlace.getAddress();
                Log.d("StartPoint", pickUpPlace.getAddress());
                Messages messages = new Messages(messageId, textMessage, user.getId(),
                        user.getFirstName() + " " + user.getLastName(), createdOn, Parameters.MESSAGE_TYPE_RIDE_REQUEST, true);
                myRef.child(messageId).setValue(messages);
                tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messageId);
                addTrip = firebaseDatabase.getReference("chatRooms");
                String key = addTrip.child(Parameters.ADD_TRIPS).child(Parameters.RIDERS).child(user.getId()).push().getKey();
                addTrip.child(Parameters.ADD_TRIPS).child(Parameters.RIDERS).child(user.getId()).child(key).setValue(messageId);
                Trips trips = new Trips(TripStatus.CREATED, user.getId(), pickUpPlace, dropOffPlace);
                tripRef.setValue(trips);
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
