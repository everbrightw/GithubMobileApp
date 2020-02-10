package com.cs242.githubmobile_android.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs242.githubmobile_android.Constant;
import com.cs242.githubmobile_android.GithubService;
import com.cs242.githubmobile_android.MainActivity;
import com.cs242.githubmobile_android.R;
import com.cs242.githubmobile_android.model.User;
import com.squareup.picasso.Picasso;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * profile's page fragment
 */
public class ProfileFragment extends Fragment {

    FButton repoButton, followingButton, followerButton;
    TextView userName, name, repoCount,
            followerCount, followingCount,
            website, bio, date;
    ImageView avatar;

    FButton follow, unfollow;
    GithubService githubService;

    public SharedPreferences mPrefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_page, container, false);

        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        //initialization
        initTextViews(view);
        initButtons(view);

        githubService = MainActivity.build().create(GithubService.class);
        Call<User> callAsync = githubService.getUser(Constant.CURR_USER);

        if(!mPrefs.getBoolean(Constant.HAVE_NOT_LOG_IN, true)){
            //user have logged in
            callAsync.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    Log.e("code ", String.valueOf(response.code()));
                    userName.setText(response.body().getUserName());
                    name.setText(response.body().getName());
                    repoCount.setText(String.valueOf(response.body().getReposNum()));
                    followerCount.setText(String.valueOf(response.body().getFollowers()));
                    followingCount.setText(String.valueOf(response.body().getFollowing()));
                    website.setText(response.body().getUrl());
                    bio.setText("Bio: " + response.body().getBio());
                    date.setText("Profile Created: " + response.body().getDate());

                    Picasso.get().load(response.body().getAvatarUrl()).into(avatar);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    System.out.println(t.getMessage());
                }
            });
        }
        return view;
    }

    /**
     * initializing profile page text views
     * @param view
     */
    private void initTextViews(View view) {

        userName = view.findViewById(R.id.profile_user_name);
        name = view.findViewById(R.id.profile_name);
        repoCount = view.findViewById(R.id.repo_count);
        followerCount = view.findViewById(R.id.follower_count);
        followingCount = view.findViewById(R.id.following_count);
        website = view.findViewById(R.id.website);
        avatar = view.findViewById(R.id.avatar);
        bio = view.findViewById(R.id.bio);
        date = view.findViewById(R.id.profile_date);

        follow = view.findViewById(R.id.follow_btn);
        unfollow = view.findViewById(R.id.unfollow_btn);
    }

    /**
     * initializing button views, and onclick listeners,
     * TODO: probably better ways for the onclick?
     * @param view
     */
    public void initButtons(View view){

        repoButton = view.findViewById(R.id.repo_id);
        followerButton = view.findViewById(R.id.follower_btn);
        followingButton = view.findViewById(R.id.following_btn);

        //onclick listeners
        repoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right,
                            R.animator.enter_from_right, R.animator.exit_to_left)
                        .replace(R.id.fragment_container, new RepoFragment())
                        .commit();
            }
        });

        followerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right,
                        R.animator.enter_from_right, R.animator.exit_to_left).replace(R.id.fragment_container,
                        new FollowerFragment()).commit();
            }
        });

        followingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right,
                        R.animator.enter_from_right, R.animator.exit_to_left).replace(R.id.fragment_container,
                        new FollowingFragment()).commit();
            }
        });

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userToFollow = userName.getText().toString();
                Call<User> callAsync2 = githubService.followUser("Bearer " + mPrefs.getString(Constant.TOKEN, ""),
                        userToFollow);
                callAsync2.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(getContext(), "user followed! ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
            }
        });

        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userToFollow = userName.getText().toString();
                Call<User> callAsync2 = githubService.unFollowUser("Bearer " + mPrefs.getString(Constant.TOKEN, ""),
                        userToFollow);
                callAsync2.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(getContext(), "user unfollowed! ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
            }
        });
    }

}
