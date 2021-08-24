package com.rider.ui.main.dialog.paymentmode;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.base.BaseDialogFragment;
import com.rider.R;


/**
 * Created by Sunil kumar yadav on 15/3/18.
 */

public class PaymentModeDialog extends BaseDialogFragment {

    private TextView tv_title;
    private LinearLayout ll_cash;
    private LinearLayout ll_wallet;
    String message;

    ConfirmationDialogListener confirmationDialogListener;
    private int bottomMargin;

    public void setConfirmationDialogListener(ConfirmationDialogListener confirmationDialogListener) {
        this.confirmationDialogListener = confirmationDialogListener;
    }

    public static PaymentModeDialog getNewInstance (String message) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        PaymentModeDialog confirmationDialog = new PaymentModeDialog();
        confirmationDialog.setArguments(bundle);
        return confirmationDialog;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_payment_mode;
    }

    @Override
    public void initializeComponent() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.message = bundle.getString("message", "");
        }

        tv_title =  getView().findViewById(R.id.tv_title);
        ll_cash =  getView().findViewById(R.id.ll_cash);
        ll_wallet =  getView().findViewById(R.id.ll_wallet);

//        tv_title.setText(message==null?"":message);

        ll_cash.setOnClickListener(this);
        ll_wallet.setOnClickListener(this);

    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_cash:
                if (this.confirmationDialogListener != null) {
                    this.confirmationDialogListener.onClickConfirm(this, "Cash");
                } else {
                    this.dismiss();
                }
                break;
            case R.id.ll_wallet:
                if (this.confirmationDialogListener != null) {
                    this.confirmationDialogListener.onClickConfirm(this, "Wallet");
                } else {
                    this.dismiss();
                }
                break;
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog view
        dialog.setContentView(layout);
        //setup dialog window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.windowAnimations = R.style.PaymentDialogStyle;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.gravity = Gravity.BOTTOM|Gravity.RIGHT;
        wlmp.y = bottomMargin;
        wlmp.dimAmount =0.0f;
        dialog.getWindow().setAttributes(wlmp);
        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }


    public interface ConfirmationDialogListener {
        void onClickConfirm(DialogFragment dialogFragment, String paymentMode);
    }

}
