package com.app.thechatrooms.ui.messages;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;

public class TheirMessageViewHolder extends RecyclerView.ViewHolder {
    private TextView senderNameTextView, messageTextView, timeTextView, likeCountTextView;
    private ImageButton likeButton;

    public TheirMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        senderNameTextView = itemView.findViewById(R.id.theirChat_senderNameTextView);
        messageTextView = itemView.findViewById(R.id.theirChat_messageTextView);
        timeTextView = itemView.findViewById(R.id.theirChat_timeTextView);
        likeCountTextView = itemView.findViewById(R.id.theirChat_likeCountTextView);
        likeButton = itemView.findViewById(R.id.theirChat_likeImageButton);
    }

    public TextView getSenderNameTextView() {
        return senderNameTextView;
    }

    public void setSenderNameTextView(TextView senderNameTextView) {
        this.senderNameTextView = senderNameTextView;
    }

    public TextView getMessageTextView() {
        return messageTextView;
    }

    public void setMessageTextView(TextView messageTextView) {
        this.messageTextView = messageTextView;
    }

    public TextView getTimeTextView() {
        return timeTextView;
    }

    public void setTimeTextView(TextView timeTextView) {
        this.timeTextView = timeTextView;
    }

    public TextView getLikeCountTextView() {
        return likeCountTextView;
    }

    public void setLikeCountTextView(TextView likeCountTextView) {
        this.likeCountTextView = likeCountTextView;
    }

    public ImageButton getLikeButton() {
        return likeButton;
    }

    public void setLikeButton(ImageButton likeButton) {
        this.likeButton = likeButton;
    }
}


