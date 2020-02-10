package com.cs242.githubmobile_android.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cs242.githubmobile_android.Constant;
import com.cs242.githubmobile_android.GithubService;
import com.cs242.githubmobile_android.MainActivity;
import com.cs242.githubmobile_android.R;
import com.cs242.githubmobile_android.RepoAdapter;
import com.cs242.githubmobile_android.model.Repository;
import com.cs242.githubmobile_android.model.User;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * follower's page fragment
 */
public class FollowerFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Repository> followerList;
    GithubService githubService;
    SharedPreferences mPrefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.repo_list, container, false);
        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //initializing the repo list
        followerList = new ArrayList<>();

        githubService = MainActivity.build().create(GithubService.class);
        Call<List<User>> callAsync = githubService.getFollowers(Constant.CURR_USER);

        callAsync.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> users = response.body();

                for (User user : users){
                    followerList.add(new Repository(user.getName(), user, user.getUserName(), null));
                    storeFollower(followerList);
                }

                RepoAdapter adapter = new RepoAdapter(getContext(), followerList,getActivity());

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });

        return view;
    }

    public void storeFollower(List<Repository> repositoryList){

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(repositoryList);
        prefsEditor.putString(Constant.FOLLOWER_KEY, json);
        prefsEditor.apply();

    }

    public List getFollower(){
        Gson gson = new Gson();
        String json = mPrefs.getString(Constant.FOLLOWER_KEY, "");
        return gson.fromJson(json, List.class);
    }
}
