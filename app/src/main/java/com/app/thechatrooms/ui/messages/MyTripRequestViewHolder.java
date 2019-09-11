package com.app.thechatrooms.ui.messages;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;

public class MyTripRequestViewHolder extends RecyclerView.ViewHolder {
    private TextView myTripRequestMessage, myTripRequestLikes, myTripRequestTime;
    private ImageButton likeButton, deleteButton, driversButton, infoButton;

    public ImageButton getInfoButton() {
        return infoButton;
    }

    public void setInfoButton(ImageButton infoButton) {
        this.infoButton = infoButton;
    }

    public MyTripRequestViewHolder(@NonNull View itemView) {
        super(itemView);
        myTripRequestMessage = itemView.findViewById(R.id.myTripRequest_messageTextView);
        myTripRequestLikes = itemView.findViewById(R.id.myTripRequest_likeCountTextView);
        myTripRequestTime = itemView.findViewById(R.id.myTripRequest_timeTextView);
        likeButton = itemView.findViewById(R.id.myTripRequest_likeImageButton);
        deleteButton = itemView.findViewById(R.id.myTripRequest_deleteImageButton);
        driversButton = itemView.findViewById(R.id.myTripRequest_drivers);
        infoButton = itemView.findViewById(R.id.myTripRequest_info);
    }

    public TextView getMyTripRequestMessage() {
        return myTripRequestMessage;
    }

    public void setMyTripRequestMessage(TextView myTripRequestMessage) {
        this.myTripRequestMessage = myTripRequestMessage;
    }

    public TextView getMyTripRequestLikes() {
        return myTripRequestLikes;
    }

    public void setMyTripRequestLikes(TextView myTripRequestLikes) {
        this.myTripRequestLikes = myTripRequestLikes;
    }

    public TextView getMyTripRequestTime() {
        return myTripRequestTime;
    }

    public void setMyTripRequestTime(TextView myTripRequestTime) {
        this.myTripRequestTime = myTripRequestTime;
    }

    public ImageButton getLikeButton() {
        return likeButton;
    }

    public void setLikeButton(ImageButton likeButton) {
        this.likeButton = likeButton;
    }

    public ImageButton getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(ImageButton deleteButton) {
        this.deleteButton = deleteButton;
    }

    public ImageButton getDriversButton() {
        return driversButton;
    }

    public void setDriversButton(ImageButton driversButton) {
        this.driversButton = driversButton;
    }
}
