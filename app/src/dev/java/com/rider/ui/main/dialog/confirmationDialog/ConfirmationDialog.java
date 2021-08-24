package com.rider.ui.main.dialog.confirmationDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.base.BaseDialogFragment;
import com.rider.R;


/**
 * Created by Sunil kumar yadav on 15/3/18.
 */

public class ConfirmationDialog extends BaseDialogFragment {

    private TextView tv_mobile;
    private TextView tv_cancel;
    private TextView tv_call_now;
    String message;
    String negativeBtn;
    String positiveBtn;

    ConfirmationDialogListener confirmationDialogListener;

    public void setConfirmationDialogListener (ConfirmationDialogListener confirmationDialogListener) {
        this.confirmationDialogListener = confirmationDialogListener;
    }

    public static ConfirmationDialog getNewInstance (String message, String negativeBtn, String positiveBtn) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putString("negativeBtn", negativeBtn);
        bundle.putString("positiveBtn", positiveBtn);
        ConfirmationDialog confirmationDialog = new ConfirmationDialog();
        confirmationDialog.setArguments(bundle);
        return confirmationDialog;
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.dialog_call_confirm;
    }

    @Override
    public void initializeComponent () {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.message = bundle.getString("message", "");
            this.negativeBtn = bundle.getString("negativeBtn", null);
            this.positiveBtn = bundle.getString("positiveBtn", null);
        }

        tv_mobile = getView().findViewById(R.id.tv_mobile);
        tv_cancel = getView().findViewById(R.id.tv_cancel);
        tv_call_now = getView().findViewById(R.id.tv_call_now);

        tv_mobile.setText(message == null ? "" : message);
        if (negativeBtn != null) {
            tv_cancel.setText(negativeBtn);
        }
        if (positiveBtn != null) {
            tv_call_now.setText(positiveBtn);
        }

        tv_cancel.setOnClickListener(this);
        tv_call_now.setOnClickListener(this);

    }

    @Override
    public int getFragmentContainerResourceId () {
        return -1;
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.tv_call_now:
                if (this.confirmationDialogListener != null) {
                    this.confirmationDialogListener.onClickConfirm(this);
                } else {
                    this.dismiss();
                }
                break;
            case R.id.tv_cancel:
                this.dismiss();
                break;
        }
    }

    @Override
    public void setupDialog (Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog view
        dialog.setContentView(layout);
        //setup dialog window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.windowAnimations = R.style.BadeDialogStyle;
        wlmp.gravity = Gravity.CENTER;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }


    public interface ConfirmationDialogListener {
        void onClickConfirm (DialogFragment dialogFragment);
    }

}
