package com.app.thechatrooms.ui.messages;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;

public class ShowMembersViewHolder extends RecyclerView.ViewHolder {

    TextView displayNameTextView;
    ImageView userProfileImageView, onlineIconImageView;

    public ShowMembersViewHolder(@NonNull View itemView) {
        super(itemView);
        displayNameTextView = itemView.findViewById(R.id.fragment_contacts_item_userName);
        userProfileImageView = itemView.findViewById(R.id.fragment_contacts_item_userImage);
        onlineIconImageView = itemView.findViewById(R.id.fragment_contacts_item_isOnlineButton);
    }

    public TextView getDisplayNameTextView() {
        return displayNameTextView;
    }

    public void setDisplayNameTextView(TextView displayNameTextView) {
        this.displayNameTextView = displayNameTextView;
    }

    public ImageView getUserProfileImageView() {
        return userProfileImageView;
    }

    public void setUserProfileImageView(ImageView userProfileImageView) {
        this.userProfileImageView = userProfileImageView;
    }

    public ImageView getOnlineIconImageView() {
        return onlineIconImageView;
    }

    public void setOnlineIconImageView(ImageView onlineIconImageView) {
        this.onlineIconImageView = onlineIconImageView;
    }
}
