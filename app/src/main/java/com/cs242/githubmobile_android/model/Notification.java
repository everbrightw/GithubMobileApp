package com.cs242.githubmobile_android.model;

import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("unread")
    boolean unread;
    @SerializedName("repository")
    Repository repository;
    @SerializedName("reason")
    String reason;

    public String getReason() {
        return reason;
    }

    public Repository getRepository() {
        return repository;
    }

    public boolean getUnread(){
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
