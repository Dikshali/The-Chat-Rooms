package com.app.thechatrooms.ui.messages;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;

public class MyTripInProgressViewHolder extends RecyclerView.ViewHolder {
    private TextView myTripProgressMessage, myTripRequestLikes, myTripProgressTime;
    private ImageButton likeButton, deleteButton, driverButton, infoButton;

    public TextView getMyTripProgressMessage() {
        return this.myTripProgressMessage;
    }

    public void setMyTripProgressMessage(TextView myTripProgressMessage) {
        this.myTripProgressMessage = myTripProgressMessage;
    }

    public TextView getMyTripRequestLikes() {
        return this.myTripRequestLikes;
    }

    public void setMyTripRequestLikes(TextView myTripRequestLikes) {
        this.myTripRequestLikes = myTripRequestLikes;
    }

    public TextView getMyTripProgressTime() {
        return myTripProgressTime;
    }

    public void setMyTripProgressTime(TextView myTripProgressTime) {
        this.myTripProgressTime = myTripProgressTime;
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

    public ImageButton getDriverButton() {
        return driverButton;
    }

    public void setDriverButton(ImageButton driverButton) {
        this.driverButton = driverButton;
    }

    public ImageButton getInfoButton() {
        return infoButton;
    }

    public void setInfoButton(ImageButton infoButton) {
        this.infoButton = infoButton;
    }

    public MyTripInProgressViewHolder(@NonNull View itemView) {
        super(itemView);
        myTripProgressMessage = itemView.findViewById(R.id.myTripProgress_messageTextView);
        myTripRequestLikes = itemView.findViewById(R.id.myTripProgress_likeCountTextView);
        myTripProgressTime = itemView.findViewById(R.id.myTripProgress_timeTextView);
        likeButton = itemView.findViewById(R.id.myTripProgress_likeImageButton);
        deleteButton = itemView.findViewById(R.id.myTripProgress_deleteImageButton);
        driverButton = itemView.findViewById(R.id.myTripProgress_drivers);
        infoButton = itemView.findViewById(R.id.myTripProgress_info);

    }
}
