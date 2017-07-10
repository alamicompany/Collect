package com.alamicompany.collect;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AC04 on 31.05.17.
 */

public class HelpRecyclerAdaper extends RecyclerView.Adapter<HelpRecyclerAdaper.ViewHolder>{

    List<HelpItem> list = new ArrayList<>();

    public HelpRecyclerAdaper(List<HelpItem> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public HelpItem getItem(int i) {
        return list.get(i);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.help_card, parent, false);


        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.helpItem = getItem(position);
        holder.cardtitle.setText(list.get(position).name);
    }





    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cardimage;
        TextView cardtitle;
        HelpItem helpItem;

        public ViewHolder(View itemView) {
            super(itemView);
            cardtitle = (TextView) itemView.findViewById(R.id.cardtitle);
        }
    }
}
