package com.app.thechatrooms.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.GroupOnlineUsers;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.utilities.Utility;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupOnlineMembersAdapter extends RecyclerView.Adapter<GroupOnlineMembersAdapter.ViewHolder> {
    Context context;
    FirebaseStorage storage;
    FirebaseDatabase dbRef;
    ArrayList<GroupOnlineUsers> onlineUsers;

    public GroupOnlineMembersAdapter(ArrayList<GroupOnlineUsers> onlineUsers, Activity a, Context context){
        this.context = context;
        this.onlineUsers = onlineUsers;
    }

    @NonNull
    @Override
    public GroupOnlineMembersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_messages_online_users, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        storage = FirebaseStorage.getInstance();
        dbRef = FirebaseDatabase.getInstance();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupOnlineMembersAdapter.ViewHolder holder, int position) {

        final GroupOnlineUsers user = onlineUsers.get(position);
        Log.d("HOLA","HOLA");
        User userDetails = new Utility().getUserDetails(user.getUserId());
        holder.userName.setText(user.getUserId());

    }

    @Override
    public int getItemCount() {
        return onlineUsers.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        ImageView userProfile;
        ViewHolder(View itemView){
            super(itemView);
            userName= itemView.findViewById(R.id.fragment_messages_online_users_username);
            userProfile = itemView.findViewById(R.id.fragment_messages_online_users_profileImage);
        }
    }
}
