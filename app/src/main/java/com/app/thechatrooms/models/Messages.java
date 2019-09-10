package com.app.thechatrooms.models;

import java.util.ArrayList;

public class Messages {
    private String messageId;
    private String message;
    private ArrayList<String> likesUserId;
    private String createdBy;
    private String createdByName;
    private String createdOn;

    public Messages(String messageId, String message, String createdBy, String createdByName, String createdOn) {
        this.messageId = messageId;
        this.message = message;
        this.likesUserId = new ArrayList<>();
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.createdOn = createdOn;
    }

    public Messages() {

    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<String> getLikesUserId() {
        return likesUserId;
    }

    public void setLikesUserId(ArrayList<String> likesUserId) {
        this.likesUserId = likesUserId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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

    public void addLikes(String id) {
        if (likesUserId == null)
            likesUserId = new ArrayList<>();
        if (!likesUserId.contains(id))
            likesUserId.add(id);
    }

    @Override
    public String toString() {
        return "Messages{" +
                "messageId='" + messageId + '\'' +
                ", message='" + message + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdByName='" + createdByName + '\'' +
                ", createdOn='" + createdOn + '\'' +
                '}';
    }

    public boolean checkLikeId(String id) {
        if (likesUserId.contains(id))
            return true;
        else
            return false;
    }
}
