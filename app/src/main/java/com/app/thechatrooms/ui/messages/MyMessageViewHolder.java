package com.app.thechatrooms.ui.messages;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;

public class MyMessageViewHolder extends RecyclerView.ViewHolder {
    private TextView messageTextView, timeTextView, likeCountTextView;
    private ImageButton likeButton, deleteButton;

    public MyMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.myChat_messageTextView);
        timeTextView = itemView.findViewById(R.id.myChat_timeTextView);
        likeCountTextView = itemView.findViewById(R.id.myChat_likeCountTextView);
        likeButton = itemView.findViewById(R.id.myChat_likeImageButton);
        deleteButton = itemView.findViewById(R.id.myChat_deleteImageButton);
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

    public ImageButton getDeleteButton() {
        return deleteButton;
    }

    public void setDeleteButton(ImageButton deleteButton) {
        this.deleteButton = deleteButton;
    }
}
