package com.cs242.githubmobile_android.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RepoSearchResult {
    @SerializedName("items")
    List<Repository> items;

    public List<Repository> getItems() {
        return items;
    }

    public void setItems(List<Repository> items) {
        this.items = items;
    }
}
