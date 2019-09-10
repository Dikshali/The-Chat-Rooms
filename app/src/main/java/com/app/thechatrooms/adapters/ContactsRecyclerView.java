package com.app.thechatrooms.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.utilities.CircleTransform;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContactsRecyclerView extends RecyclerView.Adapter<ContactsRecyclerView.ViewHolder> {
    Context context;
    FirebaseStorage storage;
    FirebaseDatabase dbRef;
    ArrayList<User> userList;

    public ContactsRecyclerView(ArrayList<User> userList, Activity a, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contacts_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        storage = FirebaseStorage.getInstance();
        dbRef = FirebaseDatabase.getInstance();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = userList.get(position);
        holder.contactName.setText(user.getFirstName() + " " + user.getLastName());
        Picasso.get()
                .load(user.getUserProfileImageUrl())
                .transform(new CircleTransform()).centerCrop().fit()
                .into(holder.profileImage);
        if (!user.isOnline())
            holder.isOnline.setVisibility(View.GONE);
        else
            holder.isOnline.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactName;
        ImageView isOnline;
        ImageView profileImage;

        ViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.fragment_contacts_item_userName);
            profileImage = itemView.findViewById(R.id.fragment_contacts_item_userImage);
            isOnline = itemView.findViewById(R.id.fragment_contacts_item_isOnlineButton);
        }
    }
}
