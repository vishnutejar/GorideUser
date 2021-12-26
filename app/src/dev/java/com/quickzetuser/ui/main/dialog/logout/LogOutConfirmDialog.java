package com.quickzetuser.ui.main.dialog.logout;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.medy.retrofitwrapper.WebRequest;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseDialogFragment;
import com.quickzetuser.model.request_model.LogOutRequestModel;
import com.quickzetuser.model.web_response.EmptyResponseModel;
import com.quickzetuser.model.web_response.LogOutResponseModel;
import com.utilities.DeviceUtils;


/**
 * Created by Sunil kumar yadav on 26/2/18.
 */


public class LogOutConfirmDialog extends AppBaseDialogFragment {

    String message;
    ConfirmationDialogListener confirmationDialogListener;
    private ImageView iv_close;
    private TextView tv_message;
    private TextView tv_ok;
    private TextView tv_cancel;

    public static LogOutConfirmDialog getNewInstance(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        LogOutConfirmDialog confirmationDialog = new LogOutConfirmDialog();
        confirmationDialog.setArguments(bundle);
        return confirmationDialog;
    }

    public void setConfirmationDialogListener(ConfirmationDialogListener confirmationDialogListener) {
        this.confirmationDialogListener = confirmationDialogListener;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_log_out_confirm;
    }

    @Override
    public void initializeComponent() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.message = bundle.getString("message", "");
        }
        iv_close = (ImageView) getView().findViewById(R.id.iv_close);
        iv_close.setVisibility(View.GONE);
        tv_message = (TextView) getView().findViewById(R.id.tv_message);
        tv_ok = (TextView) getView().findViewById(R.id.tv_ok);
        tv_cancel = (TextView) getView().findViewById(R.id.tv_cancel);

        tv_ok.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        tv_message.setText(this.message == null ? "" : this.message);
    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                onSubmit();
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

    private void onSubmit() {
        LogOutRequestModel requestModel = new LogOutRequestModel();
        requestModel.device_id = DeviceUtils.getUniqueDeviceId();

        displayProgressBar(false);
        getWebRequestHelper().logOut(requestModel,
                this);
    }

    public interface ConfirmationDialogListener {
        void onClickConfirm(DialogFragment dialogFragment);
    }

    @Override
    public void onWebRequestCall(WebRequest webRequest) {
        super.onWebRequestCall(webRequest);
        try {
            getMainActivity().onWebRequestCall(webRequest);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);

        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_LOGOUT:
                    handleLogOutResponse(webRequest);
                    break;

            }
        } else {
            EmptyResponseModel responseModel = webRequest.getResponsePojo(EmptyResponseModel.class);
            if (responseModel != null) {
                if (responseModel.getCode() == 35) {
                    handleLogOutResponse(webRequest);
                    return;
                }
            }
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                showErrorMessage(msg);
                this.dismiss();
            }
        }
    }

    private void handleLogOutResponse(WebRequest webRequest) {
        LogOutResponseModel responseModel = webRequest.getResponsePojo(LogOutResponseModel.class);
        if (responseModel == null) return;
        try {
            showCustomToast("You have logged out successfully.");
            getMainActivity().clearLoggedInUser();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (this.confirmationDialogListener != null) {
            this.confirmationDialogListener.onClickConfirm(this);
        } else {
            this.dismiss();
        }
    }
}
