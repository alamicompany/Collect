package com.alamicompany.collect;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by AC04 on 26.04.17.
 */

public class UpdatePostFragment extends DialogFragment{
    private EditText editPostName;
    private EditText postTags;
    private EditText editPostNote;
    private String postName;
    private String postDescription;
    private String postNote;
    public  Context context;
    Post post;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        post = bundle.getParcelable("post");
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.update_post_fragment);
        editPostName = (EditText) dialog.findViewById(R.id.updatePostName);
        editPostNote = (EditText) dialog.findViewById(R.id.update_post_note);
        postTags = (EditText) dialog.findViewById(R.id.updatePostDescription);
        editPostName.setText(post.getName().toString());
        editPostNote.setText(post.getNote().toString());
        postTags.setText(post.getDescription());

        dialog.findViewById(R.id.updatePostOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postName = editPostName.getText().toString();
                postDescription = postTags.getText().toString();
                postNote = editPostNote.getText().toString();
                if (validePost(postName, postDescription, postNote)){
                ((PostActivity) context).onPostNameUpdated(post,postName,postDescription, postNote);
                 dismiss();
                }
            }
        });

        dialog.findViewById(R.id.update_button_Cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return dialog;

    }

    public interface OnPostNameUpdated{
        void onPostNameUpdated(Post post, String name, String description, String note);
    }
    public boolean validePost(String name, String tags, String note){
        boolean valid = true;
        if (name.isEmpty()|| name.length() < 3) {
            editPostName.setError("at least 3 characters");
            valid= false;
        }else
            editPostName.setError(null);

        if (tags.isEmpty()|| !tags.contains("#")) {
            postTags.setError("at least add a # ");
            valid = false;
        }else
            postTags.setError(null);

        return valid;
    }

}
