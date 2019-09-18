package com.app.thechatrooms.ui.myTrips;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.thechatrooms.R;
import com.app.thechatrooms.adapters.DriverFragmentAdapter;
import com.app.thechatrooms.adapters.RiderFragmentAdapter;
import com.app.thechatrooms.models.Trips;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.utilities.Parameters;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RiderTripsFragment extends Fragment {

    private User user;
    private DatabaseReference myRef, tripRef;
    private FirebaseDatabase firebaseDatabase;
    ArrayList<Trips> trips = new ArrayList<>();
    RecyclerView recyclerView;
    private RiderFragmentAdapter riderFragmentAdapter;
    public RiderTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rider_trips, container, false);
        user = (User) getArguments().getSerializable(Parameters.USER_ID);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/addTrip/riders/"+user.getId());
        recyclerView = view.findViewById(R.id.riderTrips_recyclerView);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trips.clear();
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                for (DataSnapshot val: dataSnapshot.getChildren()){
                    String tripId = (String) val.getValue();
                    tripRef = firebaseDatabase.getReference("chatRooms/trips/"+tripId);
                    tripRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Trips trip = dataSnapshot.getValue(Trips.class);
                            Log.v("Trip","Data");
                            trips.add(trip);
                            riderFragmentAdapter = new RiderFragmentAdapter(getContext(),user,trips);
                            recyclerView.setAdapter(riderFragmentAdapter);
                            riderFragmentAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

}
