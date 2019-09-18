package com.app.thechatrooms.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.PlaceLatitudeLongitude;
import com.app.thechatrooms.models.Trips;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.messages.MyMessageViewHolder;
import com.app.thechatrooms.ui.messages.MyTripEndViewHolder;
import com.app.thechatrooms.ui.messages.MyTripInProgressViewHolder;
import com.app.thechatrooms.ui.messages.MyTripRequestViewHolder;
import com.app.thechatrooms.ui.messages.TheirMessageViewHolder;
import com.app.thechatrooms.ui.messages.TheirTripEndViewHolder;
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
    private final int MY_TEXT_MESSAGE = 0, THEIR_TEXT_MESSAGE = 1, MY_TRIP_REQUEST = 2, THEIR_TRIP_REQUEST = 3, THEIR_TRIP_PROGRESS = 5, MY_TRIP_PROGRESS = 4, MY_TRIP_END =7,THEIR_TRIP_END = 8;
    private Context context;
    private ArrayList<Messages> messagesArrayList;
    private User user;
    private String groupId;
    private MessageInterface messageInterface;
    private PrettyTime pt = new PrettyTime();
    private DatabaseReference myRef, tripRef;
    private FirebaseDatabase firebaseDatabase;
    private Activity activity;

    public MessageAdapter(User user, String groupId, ArrayList<Messages> messagesArrayList, Activity a, Context context, MessageInterface messageInterface) {
        this.messagesArrayList = messagesArrayList;
        this.groupId = groupId;
        this.user = user;
        this.context = context;
        this.activity = a;
        this.messageInterface = messageInterface;
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/messages/" + this.groupId);
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = messagesArrayList.get(position);
        if (message.getCreatedBy().equals(user.getId())) {
            if (message.getMessageType().equals(Parameters.MESSAGE_TYPE_NORMAL))
                return MY_TEXT_MESSAGE;
            else if (message.getMessageType().equals(Parameters.MESSAGE_TYPE_RIDE_REQUEST))
                return MY_TRIP_REQUEST;
            else if (message.getMessageType().equals(Parameters.MESSAGE_TYPE_RIDE_IN_PROGRESS))
                return MY_TRIP_PROGRESS;
            else
                return MY_TRIP_END;
        } else if (message.getMessageType().equals(Parameters.MESSAGE_TYPE_NORMAL))
            return THEIR_TEXT_MESSAGE;
        else if (message.getMessageType().equals(Parameters.MESSAGE_TYPE_RIDE_REQUEST))
            return THEIR_TRIP_REQUEST;
        else if (message.getMessageType().equals(Parameters.MESSAGE_TYPE_RIDE_IN_PROGRESS))
            return THEIR_TRIP_PROGRESS;
        else
            return THEIR_TRIP_END;

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
        } else if (viewType == THEIR_TRIP_PROGRESS){
            View v1 = inflater.inflate(R.layout.fragment_message_items_their_trip_progress, parent, false);
            viewHolder = new TheirTripInProgressViewHolder(v1);
        } else if (viewType == MY_TRIP_END){
            View v1 = inflater.inflate(R.layout.fragment_message_my_trip_end, parent, false);
            viewHolder = new MyTripEndViewHolder(v1);
        } else {
            View v1 = inflater.inflate(R.layout.fragment_message_items_their_trip_end, parent, false);
            viewHolder = new TheirTripEndViewHolder(v1);
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
            } else if (holder.getItemViewType() == THEIR_TRIP_PROGRESS){
                TheirTripInProgressViewHolder vh1 = (TheirTripInProgressViewHolder) holder;
                configureTheirTripProgressViewHolder(vh1, position);
            } else if (holder.getItemViewType() == MY_TRIP_END){
                MyTripEndViewHolder vh1 = (MyTripEndViewHolder) holder;
                configureMyTripEndViewHolder(vh1, position);
            } else if (holder.getItemViewType() == THEIR_TRIP_END){
                TheirTripEndViewHolder vh1 = (TheirTripEndViewHolder) holder;
                configureTheirTripEndViewHolder(vh1, position);
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    public void likeButtonOnClickListener(Messages messages){
        if (messages.checkLikeId(user.getId())){
            messages.getLikesUserId().remove(user.getId());
        }else{
            messages.addLikes(user.getId());
        }
        myRef.child(messages.getMessageId()).setValue(messages);
    }

    private void configureTheirTextMessageViewHolder(TheirMessageViewHolder viewHolder, int position) throws ParseException {
        Messages messages = messagesArrayList.get(position);
        viewHolder.getSenderNameTextView().setText(messages.getCreatedByName());
        viewHolder.getMessageTextView().setText(messages.getMessage());
        if (messages.getLikesUserId() != null) {
            viewHolder.getLikeCountTextView().setText(Integer.toString(messages.getLikesUserId().size()));
            if (messages.checkLikeId(user.getId())) {
                viewHolder.getLikeButton().setImageResource(R.drawable.ic_thumb_up_dark_blue);
            }
        } else
            viewHolder.getLikeCountTextView().setText(Integer.toString(0));
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getTimeTextView().setText(pt.format(date));
        viewHolder.getLikeButton().setOnClickListener(view -> {
            likeButtonOnClickListener(messages);
        });
    }

    private void configureTheirTripEndViewHolder(TheirTripEndViewHolder viewHolder, int position) throws ParseException{
        Messages messages = messagesArrayList.get(position);
        viewHolder.getSenderNameTextView().setText(messages.getCreatedByName());
        viewHolder.getMessageTextView().setText(messages.getMessage());
        if (messages.getLikesUserId() != null) {
            viewHolder.getLikeCountTextView().setText(Integer.toString(messages.getLikesUserId().size()));
            if (messages.checkLikeId(user.getId())) {
                viewHolder.getLikeButton().setImageResource(R.drawable.ic_thumb_up_dark_blue);
            }
        } else
            viewHolder.getLikeCountTextView().setText(Integer.toString(0));
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getTimeTextView().setText(pt.format(date));
        viewHolder.getLikeButton().setOnClickListener(view -> {
            likeButtonOnClickListener(messages);
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
        viewHolder.getDeleteButton().setOnClickListener(view -> {
            deleteTrip(messages.getMessageId());
            deleteMessage(messages.getMessageId());

        });

        viewHolder.getLikeButton().setOnClickListener(view -> {
            likeButtonOnClickListener(messages);
        });
    }

    private void configureMyTripProgressViewHolder(MyTripInProgressViewHolder viewHolder, int position) throws ParseException {

        Messages messages = messagesArrayList.get(position);
        viewHolder.getMyTripProgressMessage().setText(messages.getMessage());
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getMyTripProgressTime().setText(pt.format(date));
        viewHolder.getInfoButton().setOnClickListener(view -> {
            messageInterface.viewDriversProgress(messages.getMessageId(),groupId);
        });
        viewHolder.getDeleteButton().setOnClickListener(view -> {
            deleteTrip(messages.getMessageId());
            deleteMessage(messages.getMessageId());
        });

        viewHolder.getLikeButton().setOnClickListener(view -> {
            likeButtonOnClickListener(messages);
        });
    }

    private void configureTheirTripProgressViewHolder(TheirTripInProgressViewHolder viewHolder, int position) throws ParseException {

        Messages messages = messagesArrayList.get(position);
        Log.d("Message", messages.getCreatedByName());
        viewHolder.getSenderName().setText(messages.getCreatedByName());
        viewHolder.getTheirTripProgressMessage().setText(messages.getMessage());
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getTheirTripProgressTime().setText(pt.format(date));
        viewHolder.getInfoButton().setOnClickListener(view -> messageInterface.viewDriversProgress(messages.getMessageId(), groupId));
        if (messages.getNotification()){
            tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messages.getMessageId());
            tripRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (messages.getNotification())
                        messageInterface.showNotification(messages, Parameters.TRIP_PROGRESS, "Accepted Driver: " + (String) dataSnapshot.child(Parameters.DRIVER_ACCEPTED).child("driverName").getValue());

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        viewHolder.getLikeButton().setOnClickListener(view -> likeButtonOnClickListener(messages));

    }

    private void configureTheirTripRequestViewHolder(TheirTripRequestViewHolder viewHolder, int position) throws ParseException {
        Messages messages = messagesArrayList.get(position);
        viewHolder.getTheirRequestMessage().setText(messages.getMessage());
        viewHolder.getSenderName().setText(messages.getCreatedByName());
        viewHolder.getTheirRequestMessage().setText(messages.getMessage());
        tripRef = firebaseDatabase.getReference("chatRooms/trips/" + messages.getMessageId());

        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Trips trips = dataSnapshot.getValue(Trips.class);
                    viewHolder.getOpenInMaps().setOnClickListener(view -> {
                        messageInterface.openMap(trips.getStartPoint(), trips.getEndPoint());
                    });
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
            messageInterface.setDriversLocation(user, messages.getMessageId());

        });
        viewHolder.getInfoButton().setOnClickListener(view -> {
            messageInterface.theirTripRequestInfo(messages.getMessageId());
        });

        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getTheirTripRequestTime().setText(pt.format(date));
        if (messages.getNotification())
            messageInterface.showNotification(messages, Parameters.TRIP_REQUEST, "By " + messages.getCreatedByName());

        viewHolder.getLikeButton().setOnClickListener(view -> likeButtonOnClickListener(messages));
    }


    private void configureMyTextMessageViewHolder(MyMessageViewHolder viewHolder, int position) throws ParseException {
        Messages messages = messagesArrayList.get(position);
        viewHolder.getMessageTextView().setText(messages.getMessage());
        if (messages.getLikesUserId() != null) {
            viewHolder.getLikeCountTextView().setText(Integer.toString(messages.getLikesUserId().size()));
            if (messages.checkLikeId(user.getId())) {
                viewHolder.getLikeButton().setImageResource(R.drawable.ic_thumb_up_dark_blue);
            }
        } else
            viewHolder.getLikeCountTextView().setText(Integer.toString(0));
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getTimeTextView().setText(pt.format(date));
        viewHolder.getLikeButton().setOnClickListener(view -> {
            likeButtonOnClickListener(messages);
        });
        viewHolder.getDeleteButton().setOnClickListener(view -> {
            deleteMessage(messages.getMessageId());
        });
    }

    private void configureMyTripEndViewHolder(MyTripEndViewHolder viewHolder, int position) throws ParseException{
        Messages messages = messagesArrayList.get(position);
        viewHolder.getMessageTextView().setText(messages.getMessage());

        if (messages.getLikesUserId() != null) {
            viewHolder.getLikeCountTextView().setText(Integer.toString(messages.getLikesUserId().size()));
            if (messages.checkLikeId(user.getId())) {
                viewHolder.getLikeButton().setImageResource(R.drawable.ic_thumb_up_dark_blue);
            }
        } else
            viewHolder.getLikeCountTextView().setText(Integer.toString(0));
        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(messages.getCreatedOn());
        viewHolder.getTimeTextView().setText(pt.format(date));
        viewHolder.getLikeButton().setOnClickListener(view -> {
            likeButtonOnClickListener(messages);
        });
        viewHolder.getDeleteButton().setOnClickListener(view -> {
            deleteMessage(messages.getMessageId());
        });
        if (messages.getNotification())
            messageInterface.showNotification(messages, Parameters.TRIP_STATUS_END, "By " + messages.getCreatedByName());
    }

    private void deleteTrip(String messageId) {
        tripRef = firebaseDatabase.getReference("chatRooms/trips/");
        tripRef.child(messageId).removeValue();
    }

    private void deleteMessage(String messageId) {
        myRef.child(messageId).removeValue();

    }


    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    public interface MessageInterface {
        void viewPickUpOffers(String messageId);

        void setDriversLocation(User drivers, String messageId);

        void viewDriversProgress(String messageId, String groupId);

        void theirTripRequestInfo(String messageId);

        void openMap(PlaceLatitudeLongitude startPoint, PlaceLatitudeLongitude endPoint);

        void showNotification(Messages messages, String tripType, String content);
    }

}
