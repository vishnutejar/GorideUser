package com.rider.appBase;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.base.BaseDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.rider.R;
import com.rider.rest.WebRequestConstants;
import com.rider.rest.WebRequestHelper;
import com.rider.ui.MyApplication;
import com.rider.ui.main.MainActivity;
import com.rider.ui.utilities.SmsListener;
import com.rider.ui.utilities.SmsReceiver;
import com.rider.ui.utilities.Validate;

/**
 * Created by ubuntu on 29/3/18.
 */

public abstract class AppBaseDialogFragment extends BaseDialogFragment
        implements WebServiceResponseListener, WebRequestConstants, SmsListener {

    private Dialog alertDialogProgressBar;
    private SmsReceiver smsReceiver;
    public String currency_symbol;

    @Override
    public void initializeComponent() {
        smsReceiver = new SmsReceiver(this);
        currency_symbol = getResources().getString(R.string.rupee_symbol) + " ";
    }

    public void displayProgressBar(boolean isCancellable) {
        dismissProgressBar();
        alertDialogProgressBar = new Dialog(getActivity(),
                R.style.CustomAlertDialogStyle);
        alertDialogProgressBar.setCancelable(false);
        alertDialogProgressBar
                .requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialogProgressBar.setContentView(R.layout.progress_dialog);

        alertDialogProgressBar.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));

        alertDialogProgressBar.show();
    }

    public void dismissProgressBar() {
        if (alertDialogProgressBar != null) {
            alertDialogProgressBar.dismiss();
        }
    }

    public void showCustomToast(String message) {
        if (getActivity() == null)return ;
        ((AppBaseActivity)getActivity()).showCustomToast(message);
    }


    public synchronized void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getActivity());
        }
        hideKeyboard(view);
    }

    public synchronized void hideKeyboard(View view) {
        if (view == null && getActivity() == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
            ((AppBaseActivity) getActivity()).onWebRequestCall(webRequest);
        }
    }

    public WebRequestHelper getWebRequestHelper() {
        if (getActivity() != null) {
            return ((AppBaseActivity) getActivity()).getWebRequestHelper();
        }
        return null;
    }

    public MainActivity getMainActivity() throws IllegalAccessException {

        if (getActivity() == null)
            throw new IllegalAccessException("Activity is not found");
        return ((MainActivity) getActivity());
    }

    public LatLng getCurrentLatLng() throws IllegalAccessException {
        return getMainActivity().getMapHandler().getRealCurrentLatLng();
    }

    public MyApplication getMyApplication() throws IllegalAccessException {
        return ((MyApplication) getMainActivity().getApplication());
    }

    public boolean isValidObject(Object object) {
        return object != null;
    }

    @Override
    public void otpMessageReceived(String messageText) {

    }

    public Validate getValidate() {
        if (getActivity() == null)return null;
        return ((AppBaseActivity)getActivity()).getValidate();
    }

    public SmsReceiver getSmsReceiver() {
        return smsReceiver;
    }

    @Override
    public void onDestroyView() {
        dismissProgressBar();
        super.onDestroyView();
    }
}
