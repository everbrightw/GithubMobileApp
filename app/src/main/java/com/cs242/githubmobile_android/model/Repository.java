package com.cs242.githubmobile_android.model;

import com.google.gson.annotations.SerializedName;

//Repository name
//        Owner’s GitHub username
//        Repository description

public class Repository {

    @SerializedName("name")
    private String name;

    @SerializedName("owner")
    private User user;

    @SerializedName("description")
    private String description;

    @SerializedName("html_url")
    private String url;
    @SerializedName("stargazers_count")
    private int starCount;

    @SerializedName("watchers")
    private int watchers;

    public Repository(String name, User user, String description, String url){
        this.name = name;
        this.user = user;
        this.description = description;
        this.url = url;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }
}
