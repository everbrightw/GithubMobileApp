package com.cs242.githubmobile_android.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserSearchResult {

    @SerializedName("items")
    List<User> items;

    public List<User> getItems() {
        return items;
    }

    public void setItems(List<User> items) {
        this.items = items;
    }
}
