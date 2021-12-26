package com.quickzetuser.ui.main.dialog;

import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.models.DeviceScreenModel;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseDialogFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * Created by ubuntu on 25/4/18.
 */

public class FullScreenProfileDialog extends AppBaseDialogFragment {

    private ImageView tiv_profile, iv_back;
    private String imagePath;
    private ProgressBar pb_image;

    public String getImagePath () {
        return imagePath;
    }

    public void setImagePath (String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.dialog_profile;
    }

    @Override
    public void initializeComponent () {
        tiv_profile = getView().findViewById(R.id.tiv_profile);
        pb_image = getView().findViewById(R.id.pb_image);

        iv_back = getView().findViewById(R.id.iv_back);

        iv_back.setOnClickListener(this);
        updateViewVisibility(pb_image, View.VISIBLE);
        if (imagePath != null && !imagePath.isEmpty()) {
            Picasso.get()
                    .load(imagePath)
                    .placeholder(R.mipmap.ic_riksya)
                    .error(R.mipmap.ic_riksya)
                    .into(tiv_profile, new Callback() {
                        @Override
                        public void onSuccess () {
                            updateViewVisibility(pb_image, View.GONE);
                        }

                        @Override
                        public void onError (Exception e) {
                            updateViewVisibility(pb_image, View.GONE);
                        }
                    });

        } else {
            updateViewVisibility(pb_image, View.GONE);
        }

    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                dismiss();
                break;
        }
    }

    @Override
    public int getFragmentContainerResourceId () {
        return -1;
    }

    @Override
    public void setupDialog (Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        // wlmp.gravity = Gravity.LEFT | Gravity.TOP;
        wlmp.height = DeviceScreenModel.getInstance().getHeight(0.80f);
        //  wlmp.width =  DeviceScreenModel.getInstance().getWidth(0.80f);

    }
}
