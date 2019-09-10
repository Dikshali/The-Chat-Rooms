package com.app.thechatrooms.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.Messages;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.messages.MyMessageViewHolder;
import com.app.thechatrooms.ui.messages.TheirMessageViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int MY = 0, THEIR = 1;
    Context context;
    ArrayList<Messages> messagesArrayList;
    User user;
    String groupId;
    MessageInterface messageInterface;
    PrettyTime pt = new PrettyTime();
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;

    public MessageAdapter(User user, String groupId, ArrayList<Messages> messagesArrayList, Activity a, Context context, MessageInterface messageInterface) {
        this.messagesArrayList = messagesArrayList;
        this.groupId = groupId;
        this.user = user;
        this.context = context;
        this.messageInterface = messageInterface;
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("chatRooms/messages/" + this.groupId);
    }

    @Override
    public int getItemViewType(int position) {
        if (messagesArrayList.get(position).getCreatedBy().equals(user.getId()))
            return MY;
        else
            return THEIR;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == MY) {
            View v1 = inflater.inflate(R.layout.fragment_message_items_mychat, parent, false);
            viewHolder = new MyMessageViewHolder(v1);
        } else {
            View v1 = inflater.inflate(R.layout.fragment_message_items_theirchat, parent, false);
            viewHolder = new TheirMessageViewHolder(v1);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder.getItemViewType() == MY) {
                MyMessageViewHolder vh1 = (MyMessageViewHolder) holder;
                configureMyMessageViewHolder(vh1, position);
            } else {
                TheirMessageViewHolder vh1 = (TheirMessageViewHolder) holder;
                configureTheirMessageViewHolder(vh1, position);
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    private void configureTheirMessageViewHolder(TheirMessageViewHolder viewHolder, int position) throws ParseException {
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

    private void configureMyMessageViewHolder(MyMessageViewHolder viewHolder, int position) throws ParseException {
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

    }

}
