package com.alamicompany.collect;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by AC04 on 25.04.17.
 */

public class DialogProjectFragment extends DialogFragment{

    EditText projectName;
    String name;
    String action;
    Context activity;
    String projectId;
    ArrayList<String> projectNames;
    Bundle bundle;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         bundle = getArguments();
        action = bundle.getString("action");
        projectId = bundle.getString("project_id");
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.fragment_dialog_project);
         projectName = (EditText) dialog.findViewById(R.id.dialogName);
        projectNames = bundle.getStringArrayList("projectNames");

        dialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                name = projectName.getText().toString();
                if (validateProjectName(name, projectNames)) {
                    if (action.equals("update")) {
                        ((ProjectActivity) activity).onProjectNameUpdated(projectId, name);
                        dismiss();
                    } else if (action.equals("add")) {
                            ((ProjectActivity) activity).onProjectAdd(name);
                            dismiss();

                    }
                }
            }

        });


        // Close

        dialog.findViewById(R.id.update_project_Cancel).setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                dismiss();

            }

        });
        return dialog;
    }
    public boolean validateProjectName(String name, ArrayList<String> projectNames) {

        if (name.isEmpty()|| name.length() < 3) {
            projectName.setError("at least 3 characters");
            return false;
        }

        for (String item: projectNames){
            if (item.equals(name)){
                projectName.setError("this name already exist");
                return false;
            }
        }
        return true;
    }

    public interface OnProjectNameUpdated{
         void onProjectNameUpdated(String id, String name);
        void onProjectAdd(String name);
    }


}
