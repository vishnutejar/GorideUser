package com.rider.ui.main.dialog.favoriteconfirm;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.addressfetching.LocationModelFull;
import com.medy.retrofitwrapper.WebRequest;
import com.models.DeviceScreenModel;
import com.rider.R;
import com.rider.appBase.AppBaseDialogFragment;
import com.rider.database.tables.FavoriteTable;
import com.rider.model.FavouriteModel;
import com.rider.model.request_model.FavouriteRequestModel;
import com.rider.model.web_response.FavouriteResponseModel;

import java.util.List;


/**
 * Created by Sunil kumar yadav on 15/3/18.
 */

public class FavoriteConfirmDialog extends AppBaseDialogFragment {

    String addressType = "Home";
    ConfirmationDialogListener confirmationDialogListener;
    private RadioGroup rb_address_type;
    private RadioButton radioButton;
    private ImageView iv_icon;
    private LinearLayout ll_address_type;
    private EditText et_address_type;
    private TextView tv_address;
    private TextView tv_cancel;
    private TextView tv_save;
    private LocationModelFull.LocationModel locationModel;

    public static FavoriteConfirmDialog getNewInstance() {
        Bundle bundle = new Bundle();
        FavoriteConfirmDialog confirmationDialog = new FavoriteConfirmDialog();
        confirmationDialog.setArguments(bundle);
        return confirmationDialog;
    }

    public void setConfirmationDialogListener(ConfirmationDialogListener confirmationDialogListener) {
        this.confirmationDialogListener = confirmationDialogListener;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_favorite_confirm;
    }

    @Override
    public void initializeComponent() {
        rb_address_type = getView().findViewById(R.id.rb_address_type);
        iv_icon = getView().findViewById(R.id.iv_icon);
        ll_address_type = getView().findViewById(R.id.ll_address_type);
        et_address_type = getView().findViewById(R.id.et_address_type);
        tv_address = getView().findViewById(R.id.tv_address);
        tv_cancel = getView().findViewById(R.id.tv_cancel);
        tv_save = getView().findViewById(R.id.tv_save);

        tv_cancel.setOnClickListener(this);
        tv_save.setOnClickListener(this);

        rb_address_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = getView().findViewById(checkedId);
                if (radioButton.getText().toString().equalsIgnoreCase("Home")) {
                    addressType = radioButton.getText().toString();
                    updateViewVisibility(ll_address_type, View.GONE);
                    iv_icon.setImageResource(R.mipmap.ic_home);
                } else if (radioButton.getText().toString().equalsIgnoreCase("Work")) {
                    addressType = radioButton.getText().toString();
                    updateViewVisibility(ll_address_type, View.GONE);
                    iv_icon.setImageResource(R.mipmap.ic_work);
                } else {
                    addressType = radioButton.getText().toString();
                    updateViewVisibility(ll_address_type, View.VISIBLE);
                    iv_icon.setImageResource(R.mipmap.ic_favorite);
                }
            }
        });

    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save:
                int selectedId = rb_address_type.getCheckedRadioButtonId();
                radioButton = getView().findViewById(selectedId);
                if (radioButton.getText().toString().equalsIgnoreCase("Other")) {
                    addressType = et_address_type.getText().toString().trim();
                }
                if (!FavoriteTable.getInstance().checkFavoriteExits(addressType)) {
                    if (!TextUtils.isEmpty(addressType)) {
                        addressInServer();
                    } else {
                        showErrorMessage("Please enter favourite name.");
                    }
                } else {
                    showErrorMessage("Favourite name already exist.");
                }

                break;
            case R.id.tv_cancel:
                this.dismiss();
                break;
        }
    }

    private void addressInServer() {
        FavouriteRequestModel requestModel = new FavouriteRequestModel();
        requestModel.type = addressType;
        requestModel.detail = locationModel;
        displayProgressBar(false);
        getWebRequestHelper().addFavouriteAddress(requestModel, this);
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
        int toolbarHeight = DeviceScreenModel.getInstance().convertDpToPixel(50);
        wlmp.y = (toolbarHeight);
        wlmp.windowAnimations = R.style.FavoriteDialogStyle;
        wlmp.gravity = Gravity.TOP;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    public void setLocationModel(LocationModelFull.LocationModel locationModel) {
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
                case ID_ADD_FAVOURITE:
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
        String msg = webRequest.getErrorMessageFromResponse();
        if (isValidString(msg)) {
            showCustomToast(msg);
        }
        List<FavouriteModel> data = responseModel.getData();
        if (this.confirmationDialogListener != null) {
            this.confirmationDialogListener.onClickConfirm(data, this);
        } else {
            this.dismiss();
        }

    }

    public interface ConfirmationDialogListener {
        void onClickConfirm(List<FavouriteModel> addressType, DialogFragment dialogFragment);
    }
}
