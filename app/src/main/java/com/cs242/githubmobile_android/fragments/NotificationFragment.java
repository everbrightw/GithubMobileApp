package com.cs242.githubmobile_android.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cs242.githubmobile_android.Constant;
import com.cs242.githubmobile_android.GithubService;
import com.cs242.githubmobile_android.MainActivity;
import com.cs242.githubmobile_android.R;
import com.cs242.githubmobile_android.RepoAdapter;
import com.cs242.githubmobile_android.model.Notification;
import com.cs242.githubmobile_android.model.Repository;
import com.cs242.githubmobile_android.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Repository> repoList;
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
        repoList = new ArrayList<>();

        githubService = MainActivity.build().create(GithubService.class);

        Call<List<Notification>> callAsync = githubService.
                getNotifications("Bearer " + "a24711d5f098482cfae8f48734fd9aae74f51f28");


        callAsync.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                Toast.makeText(getContext(), "notification list fetched ", Toast.LENGTH_SHORT).show();

                Log.e("notification", String.valueOf(response.code()));
                List<Notification> notifications = response.body();
                Log.e("length", String.valueOf(notifications.size()));

                for (Notification notification : notifications){
                    Log.e("url repo", notification.getRepository().getName());

                    repoList.add(new Repository(notification.getRepository().getName(),
                            notification.getRepository().getUser(), notification.getReason() +
                            "=" + notification.getUnread(), notification.getRepository().getUrl()));

                }
                //added
                RepoAdapter adapter = new RepoAdapter(getContext(), repoList, getActivity());

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {

            }
        });

        return view;
    }

}
