package com.rider.ui.main.dialog.message;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.rider.R;
import com.rider.appBase.AppBaseDialogFragment;
import com.rider.fcm.AppNotificationType;


/**
 * Created by ubuntu on 10/4/18.
 */

public class MessageDialog extends AppBaseDialogFragment {

    private TextView tv_message;
    private TextView tv_ok, tv_title, tv_cancel;
    String message;
    String title;
    String notType;

    public static MessageDialog getNewInstance(String title,String message) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putString("title", title);
        MessageDialog messageDialog = new MessageDialog();
        messageDialog.setArguments(bundle);
        return messageDialog;
    }

    public static MessageDialog getNewInstance(String title,String message, String notType) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putString("title", title);
        bundle.putString("not_type", notType);
        MessageDialog messageDialog = new MessageDialog();
        messageDialog.setArguments(bundle);
        return messageDialog;
    }


    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_message;
    }

    @Override
    public void initializeComponent() {

        tv_message = (TextView) getView().findViewById(R.id.tv_message);
        tv_ok = (TextView) getView().findViewById(R.id.tv_ok);
        tv_title = (TextView) getView().findViewById(R.id.tv_title);
        tv_cancel = (TextView) getView().findViewById(R.id.tv_cancel);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.message = bundle.getString("message", "");
            this.title = bundle.getString("title", getContext().getString(R.string.app_name));

            if(bundle.getString("not_type")!=null) {
                tv_cancel.setVisibility(View.VISIBLE);
                this.notType = bundle.getString("not_type");
            }else {
                tv_cancel.setVisibility(View.GONE);
            }
        }

        tv_message.setText(message == null ? "" : Html.fromHtml(message));
        tv_title.setText(title);
        tv_ok.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if(notType!=null) {
                    try {
                        if(notType.equalsIgnoreCase(AppNotificationType.TYPE_ADMIN_ALERT)) {
                            getMainActivity().getSideMenuHelper().ll_notification.performClick();
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    this.dismiss();
                }else {
                    this.dismiss();
                }
                break;

            case R.id.tv_cancel:
                this.dismiss();
                break;
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog_country view
        dialog.setContentView(layout);
        //setup dialog_country window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.windowAnimations = R.style.BadeDialogStyle;
        wlmp.gravity = Gravity.CENTER;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setTitle(null);
        setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

}
