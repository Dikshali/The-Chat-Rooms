package com.app.thechatrooms.ui.messages;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;

public class TheirTripRequestViewHolder extends RecyclerView.ViewHolder {
    private TextView theirRequestMessage, theirTripReuestLikes, theirTripRequestTime, senderName, openInMaps;
    private ImageButton likeButton,accept, reject, infoButton;

    public ImageButton getAccept() {
        return accept;
    }

    public void setAccept(ImageButton accept) {
        this.accept = accept;
    }


    public TextView getTheirRequestMessage() {
        return theirRequestMessage;
    }

    public void setTheirRequestMessage(TextView theirRequestMessage) {
        this.theirRequestMessage = theirRequestMessage;
    }

    public TextView getTheirTripReuestLikes() {
        return theirTripReuestLikes;
    }

    public void setTheirTripReuestLikes(TextView theirTripReuestLikes) {
        this.theirTripReuestLikes = theirTripReuestLikes;
    }

    public TextView getTheirTripRequestTime() {
        return theirTripRequestTime;
    }

    public void setTheirTripRequestTime(TextView theirTripRequestTime) {
        this.theirTripRequestTime = theirTripRequestTime;
    }

    public ImageButton getLikeButton() {
        return likeButton;
    }

    public void setLikeButton(ImageButton likeButton) {
        this.likeButton = likeButton;
    }

    public TextView getSenderName() {
        return senderName;
    }

    public void setSenderName(TextView senderName) {
        this.senderName = senderName;
    }

    public ImageButton getInfoButton() {
        return infoButton;
    }

    public void setInfoButton(ImageButton infoButton) {
        this.infoButton = infoButton;
    }

    public TextView getOpenInMaps() {
        return openInMaps;
    }

    public void setOpenInMaps(TextView openInMaps) {
        this.openInMaps = openInMaps;
    }

    public ImageButton getReject() {
        return reject;
    }

    public void setReject(ImageButton reject) {
        this.reject = reject;
    }

    public TheirTripRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        theirRequestMessage = itemView.findViewById(R.id.theirTripRequest_messageTextView);
        theirTripReuestLikes = itemView.findViewById(R.id.theirTripRequest_likeCountTextView);
        theirTripRequestTime = itemView.findViewById(R.id.theirTripRequest_timeTextView);
        likeButton = itemView.findViewById(R.id.theirTripRequest_likeImageButton);
        senderName = itemView.findViewById(R.id.theirTripRequest_senderNameTextView);
        accept = itemView.findViewById(R.id.fragment_message_items_their_trip_request_accept);
        reject = itemView.findViewById(R.id.fragment_message_items_their_trip_request_reject);
        infoButton = itemView.findViewById(R.id.fragment_message_items_their_trip_request_info);
        openInMaps = itemView.findViewById(R.id.fragment_message_items_their_trip_request_openInMaps);

    }
}
