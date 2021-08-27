package com.quickzetuser.appBase;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.addressfetching.AddressResultReceiver;
import com.base.BaseFragment;
import com.google.android.gms.maps.model.LatLng;
import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebRequestErrorDialog;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.quickzetuser.R;
import com.quickzetuser.rest.WebRequestConstants;
import com.quickzetuser.rest.WebRequestHelper;
import com.quickzetuser.ui.MyApplication;
import com.quickzetuser.ui.main.MainActivity;
import com.quickzetuser.ui.utilities.Validate;

/**
 * Created by ubuntu on 20/2/18.
 */

public abstract class AppBaseFragment extends BaseFragment
        implements WebServiceResponseListener, WebRequestConstants {


    public String currency_symbol = "";
    WebRequestErrorDialog errorMessageDialog;
    private Dialog alertDialogProgressBar;

    @Override
    public void initializeComponent() {
        currency_symbol = getResources().getString(R.string.rupee_symbol) + " ";
    }

    @Override
    public void reInitializeComponent() {
    }

    @Override
    public void onWebRequestCall(WebRequest webRequest) {
        if (getActivity() != null) {
            ((AppBaseActivity) getActivity()).onWebRequestCall(webRequest);
        }
    }

    @Override
    public void onWebRequestPreResponse(WebRequest webRequest) {
        if (getActivity() != null) {
            ((AppBaseActivity) getActivity()).onWebRequestPreResponse(webRequest);
        }
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        if (getActivity() != null) {
            ((AppBaseActivity) getActivity()).onWebRequestResponse(webRequest);
        }
    }

    public void dismissProgressBar() {
        if (alertDialogProgressBar != null) {
            alertDialogProgressBar.dismiss();
        }
    }

    public void displayProgressBar(boolean isCancellable) {
        displayProgressBar(isCancellable, "");
    }

    public void displayProgressBar(boolean isCancellable, String loaderMsg) {
        dismissProgressBar();
        if (getActivity() == null) return;
        alertDialogProgressBar = new Dialog(getActivity(),
                R.style.CustomAlertDialogStyle);
        alertDialogProgressBar.setCancelable(isCancellable);
        alertDialogProgressBar
                .requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialogProgressBar.setContentView(R.layout.progress_dialog);
        TextView tv_loader_msg = alertDialogProgressBar.findViewById(R.id.tv_loader_msg);
        if (loaderMsg != null && !loaderMsg.trim().isEmpty()) {
            tv_loader_msg.setText(loaderMsg);
        } else {
            tv_loader_msg.setVisibility(View.GONE);
        }

        alertDialogProgressBar.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        alertDialogProgressBar.show();
    }

    public void showErrorMessage(String msg) {
        if (isValidActivity() && isVisible()) {
            if (errorMessageDialog == null) {
                errorMessageDialog = new WebRequestErrorDialog(getActivity(), msg) {
                    @Override
                    public int getLayoutResourceId() {
                        return R.layout.errordialog;
                    }

                    @Override
                    public int getMessageTextViewId() {
                        return R.id.tv_message;
                    }

                    @Override
                    public int getDismissBtnTextViewId() {
                        return R.id.tv_ok;
                    }
                };
            } else if (errorMessageDialog.isShowing()) {
                errorMessageDialog.dismiss();
            }
            errorMessageDialog.setMsg(msg);
            errorMessageDialog.show();
        }

    }

    public WebRequestHelper getWebRequestHelper() {
        if (getActivity() == null) return null;
        return ((AppBaseActivity) getActivity()).getWebRequestHelper();
    }


    public void showCustomToast(String message) {
        if (getActivity() == null) return;
        ((AppBaseActivity) getActivity()).showCustomToast(message);
    }

    public MainActivity getMainActivity() throws IllegalAccessException {

        if (getActivity() == null)
            throw new IllegalAccessException("Activity is not found");
        return ((MainActivity) getActivity());
    }

    public MyApplication getMyApplication() throws IllegalAccessException {
        return ((MyApplication) getMainActivity().getApplication());
    }

    public void getCurrentAddress(AddressResultReceiver addressResultReceiver) throws IllegalAccessException {
        getMainActivity().getMapHandler().getGoogleApiClientHelper().
                fetchCurrentAddress(addressResultReceiver);
    }

    public Location getCurrentLocation() throws IllegalAccessException {
        return getMainActivity().getMapHandler().getRealCurrentLocation();
    }

    public LatLng getCurrentLatLng() throws IllegalAccessException {
        return getMainActivity().getMapHandler().getRealCurrentLatLng();
    }

    public void updateViewVisibility(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility)
            view.setVisibility(visibility);
    }

    @Override
    public void onDestroyView() {
        dismissProgressBar();
        super.onDestroyView();
    }

    public Validate getValidate() {
        Validate validate = null;
        try {
            validate = getMainActivity().getValidate();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return validate;
    }

}
