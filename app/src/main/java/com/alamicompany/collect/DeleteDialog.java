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

/**
 * Created by AC04 on 17.05.17.
 */

public class DeleteDialog extends DialogFragment {
    Bundle bundle;
    String action;
    String projectId;
    String postId;
    Post post;
    Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        bundle = getArguments();
        action = bundle.getString("action");


        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_delete_project);
        dialog.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (action.equals("project")) {
                    projectId = bundle.getString("id");
                    ((ProjectActivity) context).onDeleteProjectConfirm(projectId);
                    dismiss();
                }else
                    if (action.equals("post")){
                        postId = bundle.getString("id");
                        post = bundle.getParcelable("post");
                        ((PostActivity)context).onDeletePostConfirm(post);
                        dismiss();

                    }
            }
        });
        dialog.findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return dialog;
    }

    public interface OnDeleteProjectConfirmation{
        void onDeleteProjectConfirm(String idProject);
    }
    public interface OnDeletePostConfirmation{
        void onDeletePostConfirm(Post post);
    }
}
