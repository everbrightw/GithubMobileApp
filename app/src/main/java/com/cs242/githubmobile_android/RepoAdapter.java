package com.cs242.githubmobile_android;
//referenced on stackoverflow.com
//modified by me.
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs242.githubmobile_android.model.Repository;
import com.cs242.githubmobile_android.model.User;
import com.squareup.picasso.Picasso; // image loader

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * an view adapter class for setting card view content
 * recycling view
 */
public class RepoAdapter extends RecyclerView.Adapter<RepoAdapter.RepoViewHolder>{


    //use to inflate the layout
    private Context mCtx;
    //use for reloading current activity
    private Activity activity;

    //we are storing all the repos in a list
    private List<Repository> repositoryList;

    //getting the context and product list with constructor
    public RepoAdapter(Context mCtx, List<Repository> repositoryList, Activity activity) {
        this.mCtx = mCtx;
        this.repositoryList = repositoryList;
        this.activity = activity;
    }

    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        final View view = inflater.inflate(R.layout.repo_card, null);


        return new RepoViewHolder(view);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RepoViewHolder holder, int i) {
        //getting the repo of the specified position
        Repository repo = repositoryList.get(i);
        holder.currentRepo = repo;
        //binding the data with the viewholder views
        holder.textViewName.setText(repo.getName());

        holder.textViewDescription.setText(repo.getUser().getUserName());
        //for notification read and unread
        if(repo.getDescription()!=null && repo.getDescription().contains("=false")){
            //read
            holder.textViewUserName.setText(repo.getDescription().replaceAll("=false", ""));
            holder.imageView.setImageDrawable(mCtx.getDrawable(R.drawable.ic_check_box_black_24dp));
        }else if(repo.getDescription()!=null && repo.getDescription().contains("=true")){
            //unread
            holder.textViewUserName.setText(repo.getDescription().replaceAll("=true", ""));
            holder.imageView.setImageDrawable(mCtx.getDrawable(R.drawable.ic_check_box_outline_blank_black_24dp));
        }else{
            //loading image from url
            holder.textViewUserName.setText(repo.getDescription());
            Picasso.get().load(repo.getUser().getAvatarUrl()).into(holder.imageView);
        }



    }


    @Override
    public int getItemCount() {
        return repositoryList.size();
    }


    class RepoViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewUserName, textViewDescription;
        ImageView imageView;

        public View view;
        public Repository currentRepo;
        public SharedPreferences mPrefs;

        public RepoViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            textViewName = itemView.findViewById(R.id.name);
            textViewUserName = itemView.findViewById(R.id.username);
            textViewDescription = itemView.findViewById(R.id.description);
            imageView = itemView.findViewById(R.id.imageView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    //check whether it is a user page or repository page
                    if(currentRepo.getUrl() == null){
                        //this is a user page, navigate to other user's profile page when item was clicked
                        Constant.CURR_USER = currentRepo.getUser().getUserName();
                        activity.finish();
                        mCtx.startActivity(activity.getIntent());
                    }
                    else{
                        // this is a repo page, navigate to repo's web url
                        showRepoOptions(currentRepo);
                    }
                }

            });
        }

        public void showRepoOptions(final Repository repository){

            mPrefs = activity.getPreferences(Context.MODE_PRIVATE);

            AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
            builder.setTitle(repository.getName());
            builder.setIcon(R.drawable.github_image_logo);
            builder.setMessage("Choose options below");
            builder.setPositiveButton("Unstar",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            GithubService githubService = MainActivity.build().create(GithubService.class);
                            Call<User> callAsync = githubService.unStarRepo("Bearer " + mPrefs.getString(Constant.TOKEN, ""),
                                    repository.getUser().getUserName(), repository.getName());

                            callAsync.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    Toast.makeText(activity, "un starred", Toast.LENGTH_SHORT);
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Toast.makeText(activity, "un starred failed", Toast.LENGTH_SHORT);
                                }
                            });

                            dialog.cancel();
                        }
                    });

            builder.setNeutralButton("Webpage",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(currentRepo.getUrl()));
                            mCtx.startActivity(browserIntent);
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton("Star",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            GithubService githubService = MainActivity.build().create(GithubService.class);
                            Call<User> callAsync = githubService.starRepo("Bearer " + mPrefs.getString(Constant.TOKEN, ""),
                                    repository.getUser().getUserName(), repository.getName());
                            Log.e("debugStar", mPrefs.getString(Constant.TOKEN, ""));
                            callAsync.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    Toast.makeText(activity, "starred", Toast.LENGTH_SHORT);
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Toast.makeText(activity, "starred failed", Toast.LENGTH_SHORT);

                                }
                            });
                            dialog.cancel();
                        }
                    });
            builder.create().show();
        }
    }

}