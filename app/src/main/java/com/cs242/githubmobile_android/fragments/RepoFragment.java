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

import com.cs242.githubmobile_android.Constant;
import com.cs242.githubmobile_android.GithubService;
import com.cs242.githubmobile_android.MainActivity;
import com.cs242.githubmobile_android.R;
import com.cs242.githubmobile_android.RepoAdapter;
import com.cs242.githubmobile_android.model.Repository;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * repo's page fragment
 */
public class RepoFragment extends Fragment {

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
        Call<List<Repository>> callAsync = githubService.getRepositories(Constant.CURR_USER);


        callAsync.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                List<Repository> repos = response.body();
                Log.e("length", String.valueOf(repos.size()));

                for (Repository repo : repos){
                    Log.e("url repo", repo.getName());

                    repoList.add(new Repository(repo.getName(),
                            repo.getUser(), repo.getDescription(), repo.getUrl()));
                    //storing repolist
                    storeRepo(repoList);
                }
                //added
                RepoAdapter adapter = new RepoAdapter(getContext(), repoList, getActivity());

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {

            }
        });

        return view;
    }

    public void storeRepo(List<Repository> repositoryList){

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(repositoryList);
        prefsEditor.putString(Constant.REPO_KEY, json);
        prefsEditor.commit();

    }

    public List getRepo(){
        Gson gson = new Gson();
        String json = mPrefs.getString(Constant.REPO_KEY, "");
        return gson.fromJson(json, List.class);
    }
}
