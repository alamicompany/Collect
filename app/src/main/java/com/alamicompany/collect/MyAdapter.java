package com.alamicompany.collect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by AC04 on 22.03.17.
 */
// Project Recycler view Adapter
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Project> mDataset;
    static Context context;
    public String id_project;
    ContextMenu.ContextMenuInfo info;


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(){

    }
    public MyAdapter(Context context, List myDataset) {

        this.context = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.projectitem, null);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(itemLayoutView);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).getName());
        id_project = mDataset.get(position).getId();
        holder.pos = id_project;
        holder.listAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProjectActivity)context).showDialogListAction(id_project);
            }
        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{
        // each data item is just a string in this case
        TextView mTextView;
        String pos;
        Button deleteProject;
        Button updateProject;
        TextView listAction;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mTextView = (TextView) itemLayoutView.findViewById(R.id.projectName);

            listAction = (TextView) itemLayoutView.findViewById(R.id.project_list_action);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.projectCard:
                    ((ProjectActivity) context).onClickCalled(pos);
                    break;
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            new MyAdapter().info = menuInfo;
            menu.setHeaderTitle("Select The Action");
            menu.add(0, R.id.menu_update, 0, "Update");//groupId, itemId, order, title
            menu.add(0, R.id.menu_delete, 0, "Delete");
            menu.add(0, R.id.menu_cancel, 0, "Cancel" );

        }

    }
}

