package com.app.thechatrooms.models;

public class GroupOnlineUsers {
    private String userId, displayName, profileLink;
    private boolean online; //1 - online, 0 - offline

    public GroupOnlineUsers(String userId, String displayName, String profileLink, boolean online) {
        this.userId = userId;
        this.displayName = displayName;
        this.profileLink = profileLink;
        this.online = online;
    }

    public GroupOnlineUsers() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "GroupOnlineUsers{" +
                "userId='" + userId + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profileLink='" + profileLink + '\'' +
                ", online=" + online +
                '}';
    }
}
