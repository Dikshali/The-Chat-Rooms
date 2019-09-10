package com.app.thechatrooms.models;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatRoom {
    private String groupId;
    private String groupName;
    private HashMap<String,GroupOnlineUsers> membersListWithOnlineStatus = new HashMap<>();
    private String createdByName;
    private String createdOn;
    private String createdById;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public HashMap<String, GroupOnlineUsers> getMembersListWithOnlineStatus() {
        return membersListWithOnlineStatus;
    }

    public void setMembersListWithOnlineStatus(HashMap<String, GroupOnlineUsers> membersListWithOnlineStatus) {
        this.membersListWithOnlineStatus = membersListWithOnlineStatus;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }


}
