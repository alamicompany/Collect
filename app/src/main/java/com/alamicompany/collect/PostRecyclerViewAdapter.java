package com.alamicompany.collect;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.URI;
import java.util.List;

/**
 * Created by AC04 on 23.03.17.
 */

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.RecyclerViewHolders> {
    private List<Post> mDataset;
    Context context;

    public PostRecyclerViewAdapter(List<Post> mDataset ,Context context) {

        this.mDataset = mDataset;
        this.context = context ;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, null);
        // set the view's size, margins, paddings and layout parameters

        RecyclerViewHolders  vh = new RecyclerViewHolders(itemLayoutView);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, final int position) {
        final Post post = mDataset.get(position);
        holder.postName.setText(mDataset.get(position).getName());
        holder.postDescription.setText(post.getNote());
        Glide.with(context).load(post.getPhoto()).into(holder.postPhoto);
        /*.placeholder(R.drawable.placeholder)
                .error(R.drawable.imagenotfound)
                .override(200, 200);
        .centerCrop();*/
        holder.postActionList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PostActivity)context).showDialogListAction(post);
            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PostActivity)context).showPostDialog(post);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public class RecyclerViewHolders  extends RecyclerView.ViewHolder {

        public TextView postName;
        public ImageView postPhoto;
        public TextView postDescription;



        public TextView postActionList;
        public RecyclerViewHolders(View itemView) {
            super(itemView);
            postName = (TextView)itemView.findViewById(R.id.post_name);
            postActionList = (TextView) itemView.findViewById(R.id.post_list_action);
            postPhoto = (ImageView) itemView.findViewById(R.id.post_photo);
            postDescription = (TextView)itemView.findViewById(R.id.post_description);

        }

    }

}