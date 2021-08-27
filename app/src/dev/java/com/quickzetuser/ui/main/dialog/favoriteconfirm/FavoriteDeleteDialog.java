package com.quickzetuser.ui.main.dialog.favoriteconfirm;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.medy.retrofitwrapper.WebRequest;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseDialogFragment;
import com.quickzetuser.model.FavouriteModel;
import com.quickzetuser.model.web_response.FavouriteResponseModel;


/**
 * Created by Sunil kumar yadav on 15/3/18.
 */

public class  FavoriteDeleteDialog extends AppBaseDialogFragment {


    ConfirmationDialogListener confirmationDialogListener;

    private TextView tv_message;
    private TextView tv_no;
    private TextView tv_yes;
    private FavouriteModel locationModel;

    public static FavoriteDeleteDialog getNewInstance() {
        Bundle bundle = new Bundle();
        FavoriteDeleteDialog confirmationDialog = new FavoriteDeleteDialog();
        confirmationDialog.setArguments(bundle);
        return confirmationDialog;
    }

    public void setConfirmationDialogListener(ConfirmationDialogListener confirmationDialogListener) {
        this.confirmationDialogListener = confirmationDialogListener;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_favorite_delete;
    }

    @Override
    public void initializeComponent() {

        tv_message = getView().findViewById(R.id.tv_message);
        tv_no = getView().findViewById(R.id.tv_no);
        tv_yes = getView().findViewById(R.id.tv_yes);

        tv_no.setOnClickListener(this);
        tv_yes.setOnClickListener(this);
    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_yes:
                deleteFavourite();
                break;
            case R.id.tv_no:
                this.dismiss();
                break;
        }
    }

    private void deleteFavourite() {
        displayProgressBar(false);
        getWebRequestHelper().deleteFavouriteAddress(locationModel, this);
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

    public void setLocationModel(FavouriteModel locationModel) {
        this.locationModel = locationModel;
    }


    @Override
    public void onWebRequestCall(WebRequest webRequest) {
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
                case ID_DELETE_FAVOURITE:
                    handleFavouriteResponse(webRequest);
                    break;
            }
        } else {
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                showErrorMessage(msg);
            }

        }

    }

    private void handleFavouriteResponse(WebRequest webRequest) {
        FavouriteResponseModel responseModel =
                webRequest.getResponsePojo(FavouriteResponseModel.class);
        if (responseModel == null) return;
        String message = responseModel.getMessage();
        if (isValidString(message)){
            showCustomToast(message);
        }
        if (this.confirmationDialogListener != null) {
            this.confirmationDialogListener.onClickConfirm(this);
        } else {
            this.dismiss();
        }

    }

    public interface ConfirmationDialogListener {
        void onClickConfirm(DialogFragment dialogFragment);
    }

}
