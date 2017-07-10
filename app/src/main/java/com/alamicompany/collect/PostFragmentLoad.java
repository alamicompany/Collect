package com.alamicompany.collect;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by AC04 on 06.04.17.
 */

public class PostFragmentLoad extends AsyncTask<String, Void, String>{

    static  PostFragmentLoad postFragmentLoadInstance;
    private ArrayList<Post> postsByProject;
    PostActivity postActivity;



    public PostFragmentLoad(PostActivity postActivity) {
        postFragmentLoadInstance = this;
        //postsByProject = new ArrayList<Post>();
        this.postActivity = postActivity;
    }




    public static PostFragmentLoad getInstance(){

        return postFragmentLoadInstance;
    }
    public ArrayList<Post> getPosts(){
        return this.postsByProject;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            postActivity.loadPosts();
            Thread.sleep(0);
            //Load all the posts array

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //show progressBar
        postActivity.showProgressBar();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //LoadFragment
        postActivity.displayFragment();
        postActivity.hideProgressBar();

       // postActivity.onResult();
        //hide progress bar
    }
}
