package com.alamicompany.collect;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by AC04 on 09.05.17.
 */

public class DialogTakeAction extends DialogFragment {
    Context context;
    String idProject;
    String target;
    Post post;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle bundle = getArguments();
            target = bundle.getString("target");
        if (target.equals("post")){
            post = bundle.getParcelable("post");
        }else
            idProject = bundle.getString("id");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.list_actions, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position of the selected item
                    if(target.equals("project")){
                        switch (which){
                            case 0:
                                ((ProjectActivity)context).onProjectActionUpdate(idProject);
                                break;
                            case 1:
                                ((ProjectActivity)context).onProjectActionDelete(idProject);
                                break;
                            case 2:
                                dialog.dismiss();
                                break;
                        }
                    }else
                        if(target.equals("post")) {
                            switch (which) {
                                case 0:
                                    ((PostActivity) context).onPostActionUpdate(post);
                                    break;
                                case 1:
                                    ((PostActivity) context).onPostActionDelete(post);
                                    break;
                                case 2:
                                    dialog.dismiss();
                                    break;
                            }
                        }



                }
            });
            return builder.create();
        }
        public interface OnProjectActionTake{
            void onProjectActionDelete(String idProject);
             void onProjectActionUpdate(String idProject);
        }
        public interface  OnPostActionTake{
            void onPostActionDelete(Post post);
            void onPostActionUpdate(Post post);
        }

    }


