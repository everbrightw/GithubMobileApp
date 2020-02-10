package com.cs242.githubmobile_android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Storing user json information
 */

public class User {

    @SerializedName("login")
    private String userName;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("followers")
    private int followers;

    @SerializedName("following")
    private int following;

    @SerializedName("public_repos")
    private int reposNum;

    @SerializedName("html_url")
    private String url;

    @SerializedName("name")
    private String name;

    @SerializedName("created_at")
    private String date;

    @SerializedName("bio")
    private String bio;


    public User(){

    }


    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    public int getReposNum() {
        return reposNum;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        // parsing date
        String parseDate = date.substring(0,10);
        return parseDate;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public void setFollowing(int following) {
        this.following = following;
    }


    public void setReposNum(int reposNum) {
        this.reposNum = reposNum;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
