package com.quickzetuser.appBase;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.base.BaseActivity;
import com.base.BaseFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebRequestErrorDialog;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.quickzetuser.R;
import com.quickzetuser.rest.WebRequestConstants;
import com.quickzetuser.rest.WebRequestHelper;
import com.quickzetuser.ui.MyApplication;
import com.quickzetuser.ui.utilities.SmsListener;
import com.quickzetuser.ui.utilities.SmsReceiver;
import com.quickzetuser.ui.utilities.Validate;
import com.utilities.GoogleApiClientHelper;
import com.utilities.Utils;



/**
 * Created by ubuntu on 20/2/18.
 */

public abstract class AppBaseActivity extends BaseActivity
        implements WebServiceResponseListener, WebRequestConstants, SmsListener {

    public String currency_symbol = "";
    private Window mWindow;
    private WebRequestErrorDialog errorMessageDialog;
    private Dialog alertDialogProgressBar;
    private WebRequestHelper webRequestHelper;
    private Validate validate;
    private SmsReceiver smsReceiver;

    protected AppBaseLocationService locationService;
    protected boolean isServiceBound;
    protected ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            onLocationServiceDisconnected();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isServiceBound = true;
            AppBaseLocationService.LocationBinder locationBinder = (AppBaseLocationService.LocationBinder) service;
            locationService = locationBinder.getLocationService();
            onLocationServiceConnected();
        }
    };

    Handler gpsCheckHandler = new Handler();
    Runnable gpsRunnable = new Runnable() {
        @Override
        public void run() {
            if (locationService != null) {
                Task<LocationSettingsResponse> locationSettingsResponseTask = locationService.checkLocationSetting();
                if (locationSettingsResponseTask != null)
                    locationSettingsResponseTask.addOnCompleteListener(onCompleteListener);
            }
        }
    };

    OnCompleteListener<LocationSettingsResponse> onCompleteListener = new OnCompleteListener<LocationSettingsResponse>() {
        @Override
        public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

            try {
                LocationSettingsResponse response =
                        task.getResult(ApiException.class);

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        ResolvableApiException resolvableApiException = (ResolvableApiException) exception;
                        try {
                            if (!Utils.isGpsProviderEnabled(AppBaseActivity.this)) {
                                resolvableApiException.startResolutionForResult(AppBaseActivity.this, REQUEST_CODE_CHECK_SETTINGS);
                            }
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;

                }
            }

        }
    };

    public void checkLocationSetting() {
        gpsCheckHandler.removeCallbacks(gpsRunnable);
        gpsCheckHandler.postDelayed(gpsRunnable, 100);
    }


    public void onLocationServiceDisconnected() {

    }

    public void onLocationServiceConnected() {

    }

    public void unBindLocationService() {
        if (isServiceBound) {
            getApplication().unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    public void bindLocationService() {
        ((MyApplication) getApplication()).startLocationService(serviceConnection);
    }


    public GoogleApiClientHelper getGoogleApiClientHelper() {
        if (locationService != null) {
            return locationService.getGoogleApiClientHelper();
        }
        return null;
    }

    @Override
    public void initializeComponent () {
        currency_symbol = getResources().getString(R.string.rupee_symbol) + " ";
        webRequestHelper = new WebRequestHelper(this);
        validate = new Validate(this);
        smsReceiver = new SmsReceiver(this);
    }

    public void sendActivity (Context context, Class<?> className) {
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void sendActivity (Context context, Class<?> className, Bundle args) {
        Intent intent = new Intent(context, className);
        intent.putExtras(args);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void sendActivityFinish (Context context, Class<?> className) {
        Intent intent = new Intent(context, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    public void sendActivityFinish (Context context, Class<?> className, Bundle args) {
        Intent intent = new Intent(context, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(args);
        context.startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
        finish();
    }

    public void transparentStatusBar () {
        mWindow = getWindow();
        mWindow.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


    @Override
    public BaseFragment getLatestFragment () {
        if (getFragmentCount() > 0) {
            return super.getLatestFragment();
        } else {
            return null;
        }
    }

    @Override
    public void onWebRequestCall (WebRequest webRequest) {

    }

    @Override
    public void onWebRequestPreResponse(WebRequest webRequest) {

    }

    @Override
    public void onWebRequestResponse (WebRequest webRequest) {

    }

    @Override
    public void otpMessageReceived (String messageText) {

    }

    public void dismissProgressBar () {
        if (alertDialogProgressBar != null) {
            alertDialogProgressBar.dismiss();
        }
    }

    public void displayProgressBar (boolean isCancellable) {
        displayProgressBar(isCancellable, "");
    }

    public void displayProgressBar (boolean isCancellable, String loaderMsg) {
        dismissProgressBar();
        if (isFinishing()) return;
        alertDialogProgressBar = new Dialog(this,
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

    public void showErrorMessage (String msg) {
        if (!isFinishing()) {
            if (errorMessageDialog == null) {
                errorMessageDialog = new WebRequestErrorDialog(this, msg) {
                    @Override
                    public int getLayoutResourceId () {
                        return R.layout.errordialog;
                    }

                    @Override
                    public int getMessageTextViewId () {
                        return R.id.tv_message;
                    }

                    @Override
                    public int getDismissBtnTextViewId () {
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

    public void updateViewVisibility (View view, int visibility) {
        if (view.getVisibility() != visibility)
            view.setVisibility(visibility);
    }

    public WebRequestHelper getWebRequestHelper () {
        return webRequestHelper;
    }

    public Validate getValidate () {
        return validate;
    }

    public SmsReceiver getSmsReceiver () {
        return smsReceiver;
    }

    @Override
    protected void onDestroy () {
        dismissProgressBar();
        super.onDestroy();
    }

}
