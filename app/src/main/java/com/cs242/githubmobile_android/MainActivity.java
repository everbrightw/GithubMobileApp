package com.cs242.githubmobile_android;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs242.githubmobile_android.fragments.FollowerFragment;
import com.cs242.githubmobile_android.fragments.FollowingFragment;
import com.cs242.githubmobile_android.fragments.LoginFragment;
import com.cs242.githubmobile_android.fragments.NotificationFragment;
import com.cs242.githubmobile_android.fragments.ProfileFragment;
import com.cs242.githubmobile_android.fragments.RepoFragment;
import com.cs242.githubmobile_android.fragments.RepoSearchResultFragment;
import com.cs242.githubmobile_android.fragments.RepoViewFragment;
import com.cs242.githubmobile_android.fragments.UserSearchResultFragment;
import com.cs242.githubmobile_android.model.AccessToken;
import com.cs242.githubmobile_android.model.RepoSearchResult;
import com.cs242.githubmobile_android.model.Repository;
import com.cs242.githubmobile_android.model.User;
import com.cs242.githubmobile_android.model.UserSearchResult;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private DrawerLayout drawerLayout;// menu layout
    private GithubService githubService; // service for fetching github data
    private GithubService githubLogInService; //service for logging in github

    // drawer menu items
    private ImageView menuAvatar;
    private TextView menuName;
    private TextView menuEmail;

    public String clientId = "454b2fe8189809448317";
    public String clientSecret = "e40c25b32f995454e5210bddaf3cb8ac839020db";
    public String redirectUri = "githubmobile://callback";

    public SharedPreferences mPrefs;//local storage


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fetching data from git hub
        githubService = MainActivity.build().create(GithubService.class);
        githubLogInService = MainActivity.buildLogIn().create(GithubService.class);

        //init local storage
        mPrefs = getPreferences(MODE_PRIVATE);

        //setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setting drawer listener
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //preparing setting menu header view
        initMenuItem(navigationView);

        // set first page, preventing reload
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }

        if(mPrefs.getBoolean(Constant.HAVE_NOT_LOG_IN, true)){
            //redirect the user to log in page
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LoginFragment()).commit();
        }
        else{

            // if the users have logged in their github account before
            User user = getStoredUser();

            menuName.setText(user.getName());
            menuEmail.setText(user.getUserName());
            Picasso.get().load(user.getAvatarUrl()).into(menuAvatar);

            if(Constant.CURR_USER.equals("")){
                Constant.CURR_USER = user.getUserName();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();

        }
    }

    /**
     * call back url resumed my app
     */
    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();

        if(!mPrefs.getBoolean(Constant.HAVE_NOT_LOG_IN, true)){
            //redirect the user to log in page
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
            return;
        }

        //requesting access token
        if(uri != null && uri.toString().startsWith(redirectUri)){

            String code = uri.getQueryParameter("code");
            Call<AccessToken> callAsync = githubLogInService.getAccessToken(clientId, clientSecret, code);
            callAsync.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    Toast.makeText(MainActivity.this, "you have been logged in! ",
                            Toast.LENGTH_SHORT).show();

                    //setting the menu side bar to the current user

                    mPrefs.edit().putString(Constant.TOKEN, response.body().getAccessToken()).apply();

                    initLoggedInUser("Bearer " + response.body().getAccessToken());

                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "log in failed! ", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     * init menu items
     * @param navigationView
     */
    private void initMenuItem(NavigationView navigationView) {

        menuName = navigationView.getHeaderView(0).findViewById(R.id.menu_name);
        menuEmail = navigationView.getHeaderView(0).findViewById(R.id.menu_email);
        menuAvatar = navigationView.getHeaderView(0).findViewById(R.id.menu_avatar);
        menuAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPrefs.getBoolean(Constant.HAVE_NOT_LOG_IN, true)){
                    //get current user name
                    Constant.CURR_USER = getStoredUser().getUserName();
                    MainActivity.this.finish();
                    startActivity(MainActivity.this.getIntent());
                }
                else{
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new LoginFragment()).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);

            }
        });
    }

    /**
     * build a api caller
     * @return
     */
    public static Retrofit build() {

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(Constant.GITHUB_BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());

        return builder.build();
    }

    /**
     * build a api caller
     * @return
     */
    public static Retrofit buildLogIn() {

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(Constant.GITHUB_LOG_BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());

        return builder.build();
    }




    /**
     * handling back button pressed
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * navigation drawer listener
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_profile:
                //replace fragment with profile view
                getSupportFragmentManager().beginTransaction().
                        setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right,
                                R.animator.enter_from_right, R.animator.exit_to_left).
                        replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_repo:
                //replace fragment with repository view
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RepoFragment()).commit();
                break;
            case R.id.nav_following:
                //replace fragment with following view
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FollowingFragment()).commit();
                break;
            case R.id.nav_follower:
                //replace fragment with follower view
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FollowerFragment()).commit();
                break;
            case R.id.nav_search_user:
                //finding another user
               // showSearchUserDialog();
                showSearchUserDialog();
                break;
            case R.id.nav_notifications:
                //replace fragment with notification view
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NotificationFragment()).commit();
                break;
            case R.id.nav_search_repository:
                showSearchRepoDialog();
                break;
                

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSearchRepoDialog() {
        final String prev = Constant.CURR_USER;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Repo Name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<RepoSearchResult> callAsycn = githubService.getSearchRepos(input.getText().toString());
                callAsycn.enqueue(new Callback<RepoSearchResult>() {
                    @Override
                    public void onResponse(Call<RepoSearchResult> call, Response<RepoSearchResult> response) {
                        Log.e("search result", String.valueOf(response.body().getItems().size()));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new RepoSearchResultFragment(response.body().getItems())).commit();
                    }

                    @Override
                    public void onFailure(Call<RepoSearchResult> call, Throwable t) {

                    }
                });
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * user searching dialog
     */
    private void showSearchUserDialog(){
        final String prev = Constant.CURR_USER;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Name");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Call<UserSearchResult> callAsycn = githubService.getSearchUsers(input.getText().toString());
                callAsycn.enqueue(new Callback<UserSearchResult>() {
                    @Override
                    public void onResponse(Call<UserSearchResult> call, Response<UserSearchResult> response) {
                        Log.e("search result", String.valueOf(response.body().getItems().size()));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new UserSearchResultFragment(response.body().getItems())).commit();
                    }

                    @Override
                    public void onFailure(Call<UserSearchResult> call, Throwable t) {

                    }
                });
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void initLoggedInUser(String token){

        Call<User> callAsync = githubService.getLoggedInUser(token);

        callAsync.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                //store user in local storage
                Log.e("userStored", response.body().getUserName());
                Constant.CURR_USER = response.body().getUserName();
                storeUser(response.body());
                menuName.setText(response.body().getName());
                menuEmail.setText(response.body().getUserName());
                Picasso.get().load(response.body().getAvatarUrl()).into(menuAvatar);

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

        mPrefs.edit().putBoolean(Constant.HAVE_NOT_LOG_IN, false).apply();

    }

    /**
     * store current user
     * @param user
     */
    public void storeUser(User user){

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString(Constant.USER_KEY, json);
        prefsEditor.apply();
    }

    public User getStoredUser(){

        Gson gson = new Gson();
        String json = mPrefs.getString(Constant.USER_KEY, "");
        return gson.fromJson(json, User.class);
    }



}
