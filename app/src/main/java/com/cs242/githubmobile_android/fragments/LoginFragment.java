package com.cs242.githubmobile_android.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cs242.githubmobile_android.Constant;
import com.cs242.githubmobile_android.GithubService;
import com.cs242.githubmobile_android.MainActivity;
import com.cs242.githubmobile_android.R;
import com.cs242.githubmobile_android.RepoAdapter;
import com.cs242.githubmobile_android.model.Repository;
import com.cs242.githubmobile_android.model.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login's page fragment
 */
public class LoginFragment extends Fragment {

    public String clientId = "454b2fe8189809448317";
    public String clientSecret = "e40c25b32f995454e5210bddaf3cb8ac839020db";
    public String redirectUri = "githubmobile://callback";
    private TextView loginButton;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_page, container, false);

        initLoginButton(view);

        return view;
    }

    /**
     * initializing login button
     * @param view
     */
    public void initLoginButton(View view){
        loginButton = view.findViewById(R.id.textView);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://github.com/login/oauth/authorize"+"?client_id=" +
                                clientId+"&scope=user "+"repo&redirect_uri=" + redirectUri));
                startActivity(intent) ;
            }
        });

    }
}
