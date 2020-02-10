package com.cs242.githubmobile_android;

import com.cs242.githubmobile_android.model.AccessToken;
import com.cs242.githubmobile_android.model.Notification;
import com.cs242.githubmobile_android.model.RepoSearchResult;
import com.cs242.githubmobile_android.model.Repository;
import com.cs242.githubmobile_android.model.UserSearchResult;
import com.cs242.githubmobile_android.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface GithubService {


    @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("users/{username}/repos")
    Call<List<Repository>> getRepositories(@Path("username") String username);

    @GET("users/{username}/followers")
    Call<List<User>> getFollowers(@Path("username") String username);

    @GET("users/{username}/following")
    Call<List<User>> getFollowings(@Path("username") String username);

    @Headers("Accept:application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    Call<AccessToken> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String code
    );

    //follow a user
    @PUT("/user/following/{username}")
    Call<User> followUser(@Header("Authorization") String token,  @Path("username") String username);

    @GET("/user")
    Call<User> getLoggedInUser(@Header("Authorization") String token);

    @DELETE("/user/following/{username}")
    Call<User> unFollowUser(@Header("Authorization") String token,  @Path("username") String username);

    // star a repo
    @PUT("/user/starred/{username}/{repoName}")
    Call<User> starRepo(@Header("Authorization") String token,  @Path("username") String username,
                        @Path("repoName") String repoName);

    @DELETE("/user/starred/{username}/{repoName}")
    Call<User> unStarRepo(@Header("Authorization") String token,  @Path("username") String username,
                          @Path("repoName") String repoName);


    //    https://api.github.com/notifications?all=true
    // get user's read and unread notifications
    @GET("/notifications?all=true")
    Call<List<Notification>> getNotifications(@Header("Authorization") String token);

    // search results
    @GET("/search/users")
    Call<UserSearchResult> getSearchUsers(@Query("q") String query);

    @GET("/search/repositories")
    Call<RepoSearchResult> getSearchRepos(@Query("q") String query);

    @GET("/users/{login}/followers")
    Call<List<User>> getUserFollowers(@Path("login") String login);
}