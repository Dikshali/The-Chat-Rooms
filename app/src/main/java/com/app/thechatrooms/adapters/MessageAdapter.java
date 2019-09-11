package com.app.thechatrooms.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Drivers;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.PlaceLatitueLongitude;
import com.app.thechatrooms.models.Trips;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.messages.MyMessageViewHolder;
import com.app.thechatrooms.ui.messages.MyTripInProgressViewHolder;
import com.app.thechatrooms.ui.messages.MyTripRequestViewHolder;
import com.app.thechatrooms.ui.messages.TheirMessageViewHolder;
import com.app.thechatrooms.ui.messages.TheirTripInProgressViewHolder;
import com.app.thechatrooms.ui.messages.TheirTripRequestViewHolder;
import com.app.thechatrooms.utilities.Parameters;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int MY_TEXT_MESSAGE = 0, THEIR_TEXT_MESSAGE = 1, MY_TRIP_REQUEST = 2, THEIR_TRIP_REQUEST = 3, THEIR_TRIP_PROGRESS = 5, MY_TRIP_PROGRESS = 4;
    Context context;
    ArrayList<Messages> messagesArrayList;
    User user;
    String groupId;
    MessageInterface messageInterface;
    PrettyTime pt = new PrettyTime();
    private DatabaseReference myRef, tripRef;
    private FirebaseDatabase firebaseDatabase;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

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
    LocationManager lm;
    Location location;

    public MessageAdapter(User user, String groupId, ArrayList<Messages> messagesArrayList, Activity a, Context context, MessageInterface messageInterface) {
        this.messagesArrayList = messagesArrayList;
        this.groupId = groupId;
        this.user = user;
        this.context = context;
        this.messageInterface = messageInterface;
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/messages/" + this.groupId);

        lm = (LocationManager) a.getSystemService(Context.LOCATION_SERVICE);
        if (a.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && a.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = messagesArrayList.get(position);
        if (message.getCreatedBy().equals(user.getId())) {
            if (message.getMessageType().equals(Parameters.MESSAGE_TYPE_NORMAL))
                return MY_TEXT_MESSAGE;
            else if (message.getMessageType().equals(Parameters.MESSAGE_TYPE_RIDE_REQUEST))
                return MY_TRIP_REQUEST;
            else
                return MY_TRIP_PROGRESS;
        } else if (message.getMessageType().equals(Parameters.MESSAGE_TYPE_NORMAL))
            return THEIR_TEXT_MESSAGE;
        else if (message.getMessageType().equals(Parameters.MESSAGE_TYPE_RIDE_REQUEST))
            return THEIR_TRIP_REQUEST;
        else
            return THEIR_TRIP_PROGRESS;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == MY_TEXT_MESSAGE) {
            View v1 = inflater.inflate(R.layout.fragment_message_items_my_text_message, parent, false);
            viewHolder = new MyMessageViewHolder(v1);
        } else if (viewType == MY_TRIP_REQUEST) {
            View v1 = inflater.inflate(R.layout.fragment_message_items_my_trip_request, parent, false);
            viewHolder = new MyTripRequestViewHolder(v1);
        } else if (viewType == THEIR_TEXT_MESSAGE) {
            View v1 = inflater.inflate(R.layout.fragment_message_items_their_text_message, parent, false);
            viewHolder = new TheirMessageViewHolder(v1);
        } else if (viewType == THEIR_TRIP_REQUEST) {
            View v1 = inflater.inflate(R.layout.fragment_message_items_their_trip_request, parent, false);
            viewHolder = new TheirTripRequestViewHolder(v1);
        } else if (viewType == MY_TRIP_PROGRESS) {
            View v1 = inflater.inflate(R.layout.fragment_message_items_my_trip_progress, parent, false);
            viewHolder = new MyTripInProgressViewHolder(v1);
        } else {
            View v1 = inflater.inflate(R.layout.fragment_message_items_my_trip_progress, parent, false);
            viewHolder = new TheirTripInProgressViewHolder(v1);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder.getItemViewType() == MY_TEXT_MESSAGE) {
                MyMessageViewHolder vh1 = (MyMessageViewHolder) holder;
                configureMyTextMessageViewHolder(vh1, position);
            } else if (holder.getItemViewType() == THEIR_TEXT_MESSAGE) {
                TheirMessageViewHolder vh1 = (TheirMessageViewHolder) holder;
                configureTheirTextMessageViewHolder(vh1, position);
            } else if (holder.getItemViewType() == MY_TRIP_REQUEST) {
                MyTripRequestViewHolder vh1 = (MyTripRequestViewHolder) holder;
                configureMyTripRequestViewHolder(vh1, position);
            } else if (holder.getItemViewType() == THEIR_TRIP_REQUEST) {
                TheirTripRequestViewHolder vh1 = (TheirTripRequestViewHolder) holder;
                configureTheirTripRequestViewHolder(vh1, position);

            } else if (holder.getItemViewType() == MY_TRIP_PROGRESS) {
                MyTripInProgressViewHolder vh1 = (MyTripInProgressViewHolder) holder;
                configureMyTripProgressViewHolder(vh1, position);
            } else {
                TheirTripInProgressViewHolder vh1 = (TheirTripInProgressViewHolder) holder;
                configureTheirTripProgressViewHolder(vh1, position);
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    private void configureTheirTextMessageViewHolder(TheirMessageViewHolder viewHolder, int position) throws ParseException {
        Messages messages = messagesArrayList.get(position);
        viewHolder.getSenderNameTextView().setText(messages.getCreatedByName());
        viewHolder.getMessageTextView().setText(messages.getMessage());
        if (messages.getLikesUserId() != null) {
            viewHolder.getLikeCountTextView().setText(Integer.toString(messages.getLikesUserId().size()));
            if (messages.checkLikeId(user.getId())) {
                viewHolder.getLikeButton().setImageResource(R.drawable.ic_thumb_up_dark_blue);
                viewHolder.getLikeButton().setEnabled(false);
            }
        } else
            viewHolder.getLikeCountTextView().setText(Integer.toString(0));
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getTimeTextView().setText(pt.format(date));
        viewHolder.getLikeButton().setOnClickListener(view -> {
            messages.addLikes(user.getId());
            myRef.child(messages.getMessageId()).setValue(messages);
        });
    }

    private void configureMyTripRequestViewHolder(MyTripRequestViewHolder viewHolder, int position) throws ParseException {
        Messages messages = messagesArrayList.get(position);
        viewHolder.getMyTripRequestMessage().setText(messages.getMessage());
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getMyTripRequestTime().setText(pt.format(date));
        viewHolder.getInfoButton().setOnClickListener(view -> {
            messageInterface.viewPickUpOffers(messages.getMessageId());
        });
    }

    private void configureMyTripProgressViewHolder(MyTripInProgressViewHolder viewHolder, int position) throws ParseException {

        Messages messages = messagesArrayList.get(position);
        viewHolder.getMyTripProgressMessage().setText(messages.getMessage());
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getMyTripProgressTime().setText(pt.format(date));
        viewHolder.getInfoButton().setOnClickListener(view -> {

        });
    }

    private void configureTheirTripProgressViewHolder(TheirTripInProgressViewHolder viewHolder, int position) throws ParseException {

        Messages messages = messagesArrayList.get(position);
        viewHolder.getSenderName().setText(messages.getCreatedByName());
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getTheirTripProgressTime().setText(pt.format(date));
    }

    private void configureTheirTripRequestViewHolder(TheirTripRequestViewHolder viewHolder, int position) throws ParseException {
        Messages messages = messagesArrayList.get(position);

        viewHolder.getTheirRequestMessage().setText(messages.getMessage());
        viewHolder.getSenderName().setText(messages.getCreatedByName());
        tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messages.getMessageId());
        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("drivers").exists()) {
                    if (dataSnapshot.child("drivers").child(user.getId()).exists()) {
                        viewHolder.getAccept().setVisibility(View.GONE);
                        viewHolder.getAccept().setClickable(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        viewHolder.getAccept().setOnClickListener(view -> {

//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
//                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//                return;
//            }
//
//            if (location != null) {
//                latitude = location.getLatitude();
//                longitude = location.getLongitude();
//                Log.d("Map Activity not null", "Location: latitude: " + location.getLatitude() + " longitude: " + location.getLongitude());
//            } else {
//                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                    return;
//                }
//                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, locationListener);
//            }
            //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, locationListener);
//            PlaceLatitueLongitude placeLatitueLongitude = new PlaceLatitueLongitude(location.getLatitude(), location.getLongitude());
//            Drivers drivers = new Drivers(user.getId(), user.getFirstName(), placeLatitueLongitude);
//            tripRef.child(messages.getMessageId()).child("drivers").child(drivers.getDriverId()).setValue(drivers);
            //PlaceLatitueLongitude placeLatitueLongitude = new PlaceLatitueLongitude(0.0,0.0);
//            Drivers drivers = new Drivers(user.getId(), user.getFirstName(), placeLatitueLongitude);
            messageInterface.setDriversLocation(user, messages.getMessageId());
//            tripRef.child(Parameters.DRIVERS).child(drivers.getDriverId()).setValue(drivers);
        });
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getTheirTripRequestTime().setText(pt.format(date));
    }

    private void configureMyTextMessageViewHolder(MyMessageViewHolder viewHolder, int position) throws ParseException {
        Messages messages = messagesArrayList.get(position);
        viewHolder.getMessageTextView().setText(messages.getMessage());
        if (messages.getLikesUserId() != null) {
            viewHolder.getLikeCountTextView().setText(Integer.toString(messages.getLikesUserId().size()));
            if (messages.checkLikeId(user.getId())) {
                viewHolder.getLikeButton().setImageResource(R.drawable.ic_thumb_up_dark_blue);
                viewHolder.getLikeButton().setEnabled(false);
            }
        } else
            viewHolder.getLikeCountTextView().setText(Integer.toString(0));
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getTimeTextView().setText(pt.format(date));
        viewHolder.getLikeButton().setOnClickListener(view -> {
            messages.addLikes(user.getId());
            myRef.child(messages.getMessageId()).setValue(messages);
        });
        viewHolder.getDeleteButton().setOnClickListener(view -> {
            myRef.child(messages.getMessageId()).removeValue();
        });
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    public interface MessageInterface {
        void viewPickUpOffers(String messageId);
        void setDriversLocation(User drivers, String messageId);
    }

}
