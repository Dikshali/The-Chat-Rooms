package com.app.thechatrooms.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.TripStatus;
import com.app.thechatrooms.models.Trips;
import com.app.thechatrooms.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RiderFragmentAdapter extends RecyclerView.Adapter<RiderFragmentAdapter.ViewHolder> {
    Context context;
    User user;
    ArrayList<Trips> tripsArrayList;
    FirebaseDatabase dbRef;
    private DatabaseReference myRef;

    public RiderFragmentAdapter(Context context, User user, ArrayList<Trips> tripsArrayList) {
        this.context = context;
        this.user = user;
        this.tripsArrayList = tripsArrayList;
    }

    @NonNull
    @Override
    public RiderFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_history_item, parent, false);
        RiderFragmentAdapter.ViewHolder viewHolder = new RiderFragmentAdapter.ViewHolder(view);
        dbRef = FirebaseDatabase.getInstance();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RiderFragmentAdapter.ViewHolder holder, int position) {
        final Trips trip = tripsArrayList.get(position);
        holder.pickUpTextView.setText("Pickup: "+trip.getStartPoint().getName());
        holder.dropOffTextView.setText("Drop off: "+trip.getEndPoint().getName());
        holder.rideStatus.setText("Ride Status: "+trip.getTripStatus());
        if (trip.getTripStatus()!= TripStatus.CREATED){
            String driverId = trip.getDriverAccepted().getDriverId();
            myRef = dbRef.getReference("chatRooms/userProfiles/"+driverId);
            final User[] rider = new User[1];
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    rider[0] = dataSnapshot.getValue(User.class);
                    holder.personTextView.setText("Driver: "+ rider[0].getFirstName()+" "+ rider[0].getLastName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            holder.personTextView.setText("Driver: No Driver Accepted");
        }
    }

    @Override
    public int getItemCount() {
        return tripsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pickUpTextView;
        TextView dropOffTextView;
        TextView personTextView;
        ImageButton infoImageView;
        TextView openMapsTextView;
        TextView rideStatus;

        ViewHolder(View itemView) {
            super(itemView);
            pickUpTextView = itemView.findViewById(R.id.tripHistory_pickUpTextView);
            dropOffTextView = itemView.findViewById(R.id.tripHistory_dropOffTextView);
            personTextView = itemView.findViewById(R.id.tripHistory_personTextView);
            rideStatus = itemView.findViewById(R.id.tripHistory_rideStatus);
        }
    }
}
