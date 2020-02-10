package com.cs242.githubmobile_android.fragments;

import android.annotation.SuppressLint;
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

import com.cs242.githubmobile_android.GithubService;
import com.cs242.githubmobile_android.MainActivity;
import com.cs242.githubmobile_android.R;
import com.cs242.githubmobile_android.RepoAdapter;
import com.cs242.githubmobile_android.model.Repository;
import com.cs242.githubmobile_android.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.hoang8f.widget.FButton;

/**
 * repo's page fragment
 */
@SuppressLint("ValidFragment")
public class UserSearchResultFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<Repository> repoList;
    GithubService githubService;

    SharedPreferences mPrefs;

    List<User> items;
    List<User> users;


    FButton sortByAlphabet, sortByFollowers;



    public UserSearchResultFragment(List<User> items){
        this.items = items;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.repo_list, container, false);
        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        users = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        githubService = MainActivity.build().create(GithubService.class);

        sortByAlphabet = view.findViewById(R.id.sort);
        sortByFollowers = view.findViewById(R.id.sort2);
        sortByFollowers.setText("Sort By Followers");
        sortByAlphabet.setText("Sort By Alphabet");


        Log.e("sort user", String.valueOf(users.size()));
        //initializing the repo list
        repoList = new ArrayList<>();
        for(User user:items){
            Log.e("added?", "??");

            repoList.add(new Repository(user.getName(), user, user.getUserName(), null));


        }

        sortByAlphabet(view);

        RepoAdapter adapter = new RepoAdapter(getContext(), repoList, getActivity());

        recyclerView.setAdapter(adapter);

        return view;
    }

    /**
     * sort lisrt order by alphabet
     * @param view
     */
    private void sortByAlphabet(final View view) {
        sortByAlphabet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(repoList, new Comparator<Repository>() {
                    @Override
                    public int compare(Repository o1, Repository o2) {
                        return Integer.valueOf(o1.getUser().getUserName().charAt(0))- Integer.valueOf(o2.getUser().getUserName().charAt(0));
                    }
                });

                RepoAdapter adapter = new RepoAdapter(getContext(), repoList, getActivity());
                recyclerView.setAdapter(adapter);
                view.invalidate();
            }
        });

        sortByAlphabet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Collections.sort(repoList, new Comparator<Repository>() {
                    @Override
                    public int compare(Repository o1, Repository o2) {
                        return Integer.valueOf(o2.getUser().getUserName().charAt(0))- Integer.valueOf(o1.getUser().getUserName().charAt(0));
                    }
                });

                RepoAdapter adapter = new RepoAdapter(getContext(), repoList, getActivity());
                recyclerView.setAdapter(adapter);
                view.invalidate();
                return false;
            }
        });
    }


}
