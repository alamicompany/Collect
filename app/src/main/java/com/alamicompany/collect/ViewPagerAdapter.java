package com.alamicompany.collect;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by AC04 on 30.05.17.
 */

public class ViewPagerAdapter extends PagerAdapter {

    private ArrayList<Post> images;
    private LayoutInflater inflater;
    private Context context;

    public ViewPagerAdapter(Context context, ArrayList<Post> images) {
        this.context = context;
        this.images=images;
        inflater = LayoutInflater.from(context);
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }



    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View myImageLayout = inflater.inflate(R.layout.image_slider, view, false);
        final Post post = images.get(position);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.mainCardPhoto);
        TextView mainCardName = (TextView) myImageLayout.findViewById(R.id.mainCardName);
        TextView mainCardTags = (TextView) myImageLayout.findViewById(R.id.main_card_tags);
        mainCardName.setText(post.getName());
        mainCardTags.setText(post.getDescription());
        Glide.with(context).load(post.getPhoto()).into(myImage);
        //myImage.setImageResource(images.get(position));
        view.addView(myImageLayout, 0);
        myImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).showMainDisplay(post.getPhoto());
            }
        });
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public float getPageWidth(int position) {
        return(0.4f);
    }


}
