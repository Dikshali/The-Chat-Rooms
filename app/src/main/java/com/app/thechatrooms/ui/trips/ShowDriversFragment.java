package com.app.thechatrooms.ui.trips;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.thechatrooms.R;
import com.app.thechatrooms.adapters.DriversAdapter;
import com.app.thechatrooms.models.Drivers;
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
public class ShowDriversFragment extends Fragment {

    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerView;
    private ArrayList<Drivers> drivers;
    DriversAdapter driversAdapter;


    private String messageId;

    public ShowDriversFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_drivers, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        messageId = getArguments().getString(Parameters.MESSAGE_ID);

        myRef = firebaseDatabase.getReference("chatRooms/trips/"+messageId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Parameters.DRIVERS).exists()){
                    for (DataSnapshot child: dataSnapshot.getChildren()){
                        Drivers driver = child.getValue(Drivers.class);
                        drivers.add(driver);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        driversAdapter = new DriversAdapter();
                        recyclerView.setAdapter(driversAdapter);
                        driversAdapter.notifyDataSetChanged();
                    }
                }else {
                    Toast.makeText(getContext(), "NO OFFERS YET", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

}
