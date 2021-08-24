package com.rider.ui.main.dialog.walletrecharge;

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

public class WalletRechargeDialog extends BaseDialogFragment {

    private TextView tv_message_data;
    private TextView tv_later;
    private TextView tv_update;
    String message;

    ConfirmationDialogListener confirmationDialogListener;

    public void setConfirmationDialogListener (ConfirmationDialogListener confirmationDialogListener) {
        this.confirmationDialogListener = confirmationDialogListener;
    }

    public static WalletRechargeDialog getNewInstance (String message) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        WalletRechargeDialog confirmationDialog = new WalletRechargeDialog();
        confirmationDialog.setArguments(bundle);
        return confirmationDialog;
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.dialog_recharge_wallet;
    }

    @Override
    public void initializeComponent () {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.message = bundle.getString("message", "");
        }

        tv_message_data = getView().findViewById(R.id.tv_message_data);
        tv_later = getView().findViewById(R.id.tv_later);
        tv_update = getView().findViewById(R.id.tv_update);

        tv_message_data.setText(message == null ? "" : message);

        tv_later.setOnClickListener(this);
        tv_update.setOnClickListener(this);

    }

    @Override
    public int getFragmentContainerResourceId () {
        return -1;
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.tv_update:
                if (this.confirmationDialogListener != null) {
                    this.confirmationDialogListener.onClickConfirm(this);
                } else {
                    this.dismiss();
                }
                break;
            case R.id.tv_later:
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
