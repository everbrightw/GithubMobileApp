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

import com.cs242.githubmobile_android.R;
import com.cs242.githubmobile_android.RepoAdapter;
import com.cs242.githubmobile_android.model.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.hoang8f.widget.FButton;

@SuppressLint("ValidFragment")
public class RepoSearchResultFragment extends Fragment {

    private RecyclerView recyclerView;
    SharedPreferences mPrefs;

    List<Repository> items;

    FButton sortByWatchers, sortByStars;

    public RepoSearchResultFragment(List<Repository> items) {
        this.items = items;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.repo_list, container, false);
        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        sortByWatchers = view.findViewById(R.id.sort);
        sortByStars = view.findViewById(R.id.sort2);
        sortByStars.setText("Sort By Star");
        sortByWatchers.setText("Sort By Watch");

        //sort list by stars order
        sortStarsOrder(view);

        //sort list by watchers order
        sortWatchersOrder(view);


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //initializing the repo list
        RepoAdapter adapter = new RepoAdapter(getContext(), items, getActivity());

        recyclerView.setAdapter(adapter);


        return view;
    }

    private void sortWatchersOrder(final View view) {
        sortByWatchers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(items, new Comparator<Repository>() {
                    @Override
                    public int compare(Repository o1, Repository o2) {
                        return o2.getWatchers()- o1.getWatchers();
                    }
                });
                RepoAdapter adapter = new RepoAdapter(getContext(), items, getActivity());
                recyclerView.setAdapter(adapter);
                view.invalidate();
            }
        });

        sortByWatchers.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Collections.sort(items, new Comparator<Repository>() {
                    @Override
                    public int compare(Repository o1, Repository o2) {
                        Log.e("watchers", String.valueOf(o1.getWatchers()));
                        return o1.getWatchers()- o2.getWatchers();
                    }
                });
                RepoAdapter adapter = new RepoAdapter(getContext(), items, getActivity());
                recyclerView.setAdapter(adapter);
                view.invalidate();
                return false;
            }
        });
    }

    private void sortStarsOrder(final View view) {
        sortByStars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("sort", "sort button clicked");
                //TODO sort repo list
                Collections.sort(items, new Comparator<Repository>() {
                    @Override
                    public int compare(Repository o1, Repository o2) {
                        return o2.getStarCount()- o1.getStarCount();
                    }
                });
                RepoAdapter adapter = new RepoAdapter(getContext(), items, getActivity());
                for(Repository repo: items){
                    Log.e("sort", String.valueOf(repo.getStarCount()));
                }
                recyclerView.setAdapter(adapter);
                view.invalidate();
            }
        });

        //descending order
        sortByStars.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Collections.sort(items, new Comparator<Repository>() {
                    @Override
                    public int compare(Repository o1, Repository o2) {
                        return o1.getStarCount()- o2.getStarCount();
                    }
                });
                RepoAdapter adapter = new RepoAdapter(getContext(), items, getActivity());

                recyclerView.setAdapter(adapter);
                view.invalidate();
                return false;
            }
        });
    }
}
