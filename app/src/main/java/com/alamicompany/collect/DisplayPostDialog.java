package com.alamicompany.collect;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by AC04 on 27.04.17.
 */

public class DisplayPostDialog extends DialogFragment {

    private Button favoritButton;
    private Button infoButton;
    public Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final Post post = bundle.getParcelable("post");
        final Boolean favorit = post.getFavorit();
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.display_post_fragment);
        ImageView image = (ImageView) dialog.getWindow().findViewById(R.id.displayPostPic);
        Glide.with(getActivity())
        .load(post.getPhoto())
                .placeholder(R.drawable.profile)
                .dontAnimate()
                .error(R.drawable.profile)
                .into(image);
        favoritButton = (Button) dialog.findViewById(R.id.display_post_favorit);
        infoButton = (Button) dialog.findViewById(R.id.displayInfo);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_info_)
                        .setTitle(post.getName())
                        .setMessage(post.getNote()+"\n  \n "+post.getDescription())
                        .setPositiveButton("OK", null).show();            }
        });
        if (favorit) {
            favoritButton.setBackgroundResource(R.drawable.ic_favorit_post_black);
        }else
            favoritButton.setBackgroundResource(R.drawable.ic_favorit_post_white);

        favoritButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorit) {
                    ((PostActivity) context).addPostToFavorit(post, false);
                    favoritButton.setBackgroundResource(R.drawable.ic_favorit_post_white);
                    dismiss();
                }else
                    ((PostActivity)context).addPostToFavorit(post,true);
                favoritButton.setBackgroundResource(R.drawable.ic_favorit_post_black);
                dismiss();
            }
        });
        return dialog;

    }
    public void AlertDialogCreate(){

        new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Alert Dialog Box Title")
                .setMessage("Description:")
                .setPositiveButton("OK", null).show();
    }
    public interface displayPostAction{
        public void addPostToFavorit(Post post, boolean favorit);
    }
}
