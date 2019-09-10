package com.app.thechatrooms.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.thechatrooms.R;
import com.app.thechatrooms.models.GroupChatRoom;
import com.app.thechatrooms.models.User;
import com.app.thechatrooms.ui.messages.MessageFragment;
import com.app.thechatrooms.utilities.Parameters;

import java.util.ArrayList;

public class ChatFragmentAdapter extends RecyclerView.Adapter<ChatFragmentAdapter.ViewHolder> {

    Context context;
    User user;
    ArrayList<GroupChatRoom> groupList;
    ChatFragmentInterface chatFragmentInterface;

    public ChatFragmentAdapter(User user, ArrayList<GroupChatRoom> groupList, Activity a, Context context, ChatFragmentInterface chatFragmentInterface) {
        this.groupList = groupList;
        this.context = context;
        this.user = user;
        this.chatFragmentInterface = chatFragmentInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_groups_item, parent, false);
        ViewHolder viewHolder = new ChatFragmentAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final GroupChatRoom group = groupList.get(position);
        if (group.getCreatedById().equals(user.getId())) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setClickable(true);
        } else {
            holder.deleteButton.setVisibility(View.GONE);
            holder.deleteButton.setClickable(false);
        }
        holder.joinButton.setVisibility(View.GONE);
        holder.joinButton.setClickable(false);
        if (!group.getCreatedById().equals(user.getId())) {
            holder.leaveButton.setVisibility(View.VISIBLE);
            holder.leaveButton.setClickable(true);
        } else {
            holder.leaveButton.setVisibility(View.GONE);
            holder.leaveButton.setClickable(false);
        }
        holder.groupName.setText(group.getGroupName());
        holder.createdBy.setText(group.getCreatedByName());

        holder.deleteButton.setOnClickListener(view -> chatFragmentInterface.deleteGroup(group));

        holder.leaveButton.setOnClickListener(view -> chatFragmentInterface.leaveGroup(group));

        holder.linearLayout.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Parameters.USER_ID, user);
            bundle.putString("GroupID", groupList.get(position).getGroupId());
            MessageFragment messageFragment = new MessageFragment();
            messageFragment.setArguments(bundle);
            chatFragmentInterface.openMessageWindow(messageFragment);
        });

    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public interface ChatFragmentInterface {
        void deleteGroup(GroupChatRoom groupChatRoom);

        void leaveGroup(GroupChatRoom groupChatRoom);

        void openMessageWindow(MessageFragment messageFragment);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView createdBy;
        ImageButton joinButton;
        ImageButton deleteButton;
        ImageButton leaveButton;
        LinearLayout linearLayout;

        ViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.fragment_groups_item_texxtViewLinearLayout);
            groupName = itemView.findViewById(R.id.fragment_groups_item_groupName);
            createdBy = itemView.findViewById(R.id.fragment_groups_item_createdBy);
            joinButton = itemView.findViewById(R.id.fragment_groups_item_joinButton);
            deleteButton = itemView.findViewById(R.id.fragment_groups_item_deleteButton);
            leaveButton = itemView.findViewById(R.id.fragment_groups_item_leaveButton);
        }
    }
}
