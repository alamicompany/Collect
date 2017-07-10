package com.alamicompany.collect;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by AC04 on 01.06.17.
 */

public class MainDisplayPhotoDialog extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    Bundle bundle = getArguments();
    String photo = bundle.getString("photo");
    Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.main_display_photo);
        ImageView image = (ImageView) dialog.getWindow().findViewById(R.id.main_display_photo);
        Glide.with(getActivity()).load(photo).into(image);

        return dialog;
    }
}
