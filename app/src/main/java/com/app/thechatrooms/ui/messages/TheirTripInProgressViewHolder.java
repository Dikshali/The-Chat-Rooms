package com.app.thechatrooms.ui.messages;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;

public class TheirTripInProgressViewHolder extends RecyclerView.ViewHolder {

    private TextView theirTripProgressMessage, theirTripProgressLikes, theirTripProgressTime, senderName;
    private ImageButton likeButton;
    private ImageButton infoButton;

    public TextView getTheirTripProgressMessage() {
        return theirTripProgressMessage;
    }

    public void setTheirTripProgressMessage(TextView theirTripProgressMessage) {
        this.theirTripProgressMessage = theirTripProgressMessage;
    }

    public TextView getTheirTripProgressLikes() {
        return theirTripProgressLikes;
    }

    public void setTheirTripProgressLikes(TextView theirTripProgressLikes) {
        this.theirTripProgressLikes = theirTripProgressLikes;
    }

    public TextView getTheirTripProgressTime() {
        return theirTripProgressTime;
    }

    public void setTheirTripProgressTime(TextView theirTripProgressTime) {
        this.theirTripProgressTime = theirTripProgressTime;
    }

    public TextView getSenderName() {
        return this.senderName;
    }

    public void setSenderName(TextView senderName) {
        this.senderName = senderName;
    }

    public ImageButton getLikeButton() {
        return likeButton;
    }

    public void setLikeButton(ImageButton likeButton) {
        this.likeButton = likeButton;
    }

    public ImageButton getInfoButton() {
        return infoButton;
    }

    public void setInfoButton(ImageButton infoButton) {
        this.infoButton = infoButton;
    }

    public TheirTripInProgressViewHolder(@NonNull View itemView) {
        super(itemView);
        theirTripProgressMessage = itemView.findViewById(R.id.theirTripProgress_messageTextView);
        theirTripProgressLikes = itemView.findViewById(R.id.theirTripProgress_likeCountTextView);
        theirTripProgressTime = itemView.findViewById(R.id.theirTripProgress_timeTextView);
        likeButton = itemView.findViewById(R.id.theirTripProgress_likeImageButton);
        senderName = itemView.findViewById(R.id.theirTripProgress_senderNameTextView);
        infoButton = itemView.findViewById(R.id.theirTripProgress_info);

    }
}
