package com.alamicompany.collect;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Load_Post_Fragment extends Fragment {
    private List<Post> posts;
    private LinearLayoutManager lLayout;
    private PostRecyclerViewAdapter rcAdapter;
    Activity postActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.load_post_fragment, container, false);
        //posts = new ArrayList<Post>();
        //posts = getArguments().getParcelableArrayList("posts");
        posts = ((PostActivity) postActivity).getPosts();
        lLayout = new GridLayoutManager(getActivity(),3);
        RecyclerView rView = (RecyclerView)fragmentView.findViewById(R.id.postRecycle);
        //rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        rcAdapter = new PostRecyclerViewAdapter(posts,getActivity());
        rView.setAdapter(rcAdapter);
        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.postActivity = activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        rcAdapter.notifyDataSetChanged();
    }

    public void refreshPosts(){
        rcAdapter.notifyDataSetChanged();
    }
}
