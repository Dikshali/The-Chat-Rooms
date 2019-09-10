package com.app.thechatrooms.models;

import java.util.ArrayList;

public class GroupMessage {
    private String groupId;
    private ArrayList<Messages> messageList;

    public GroupMessage() {
    }

    public GroupMessage(String groupId, ArrayList<Messages> messageList) {
        this.groupId = groupId;
        this.messageList = messageList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ArrayList<Messages> getMessageList() {
        return messageList;
    }

    public void setMessageList(ArrayList<Messages> messageList) {
        this.messageList = messageList;
    }
}
