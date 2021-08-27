package com.quickzetuser.ui.main.dialog.paytmpayment;

import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.medy.retrofitwrapper.WebRequest;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseDialogFragment;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.model.UserModel;
import com.utilities.Utils;

public class  PaytmPaymentDialog extends AppBaseDialogFragment {

    private static final String TAG = PaytmPaymentDialog.class.getSimpleName();
    private WebView web_view;
    private String walletRechargeAmount = "0";
    private String RETURN_URL = "";

    public void setWalletRechargeAmount(String walletRechargeAmount) {
        this.walletRechargeAmount = walletRechargeAmount;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_paytm_payment;
    }

    @Override
    public void initializeComponent() {
        web_view = getView().findViewById(R.id.web_view);
        setupWebViewsettings();
        startPayment();
    }

    private void setupWebViewsettings() {
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setSupportMultipleWindows(true);
        web_view.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        web_view.getSettings().setDomStorageEnabled(true);

        web_view.addJavascriptInterface(new ResponseHandleInterface(), ResponseHandleInterface.NAME);

        web_view.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (errorCode == -2) {
                    showErrorMessage("Internet not available");
                    dismiss();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Utils.printLog(getContext(), "Payment: ", "onPageFinished : url: "+ url);
                if(url.equals(getWalletRechargeUrl())){
                    view.loadUrl ("javascript:window." + ResponseHandleInterface.NAME +
                            ".handleResponse2(document.getElementById('return_url') .value);");
                }else if (url.contains(RETURN_URL)) {
                    view.loadUrl("javascript:window." + ResponseHandleInterface.NAME +
                            ".handleResponse(document.getElementsByTagName('body')[0].innerText);");

                }
            }
        });
    }

    private String getWalletRechargeUrl(){
        UserModel userModel = null;
        try {
            userModel = getMyApplication().getUserModel();
            if (!isValidObject(userModel)) return "";
            return String.format(URL_WALLET_RECHARGE_PAYTM, walletRechargeAmount, userModel.getId());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";

    }

    private void startPayment() {

        web_view.loadUrl(getWalletRechargeUrl());

    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
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
        wlmp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    class ResponseHandleInterface {

        public static final String NAME = "RESPONSE_HANDLER";

        public ResponseHandleInterface() {
        }

        @SuppressWarnings("unused")
        @JavascriptInterface
        public void handleResponse(String response) {
            if (isValidString(response)) {
                handleWalletRechargeResponse(response);
            } else {
                dismiss();
            }
        }
        @SuppressWarnings("unused")
        @JavascriptInterface
        public void handleResponse2(String response) {
            RETURN_URL = response;
            Utils.printLog(getContext(), "Payment: ", "RETURN url: "+ RETURN_URL);
        }
    }

    public void handleWalletRechargeResponse(String response) {
        final WebRequest webRequest = new WebRequest(ID_WALLET_RECHARGE_PAYTM, URL_WALLET_RECHARGE_PAYTM);
        webRequest.isDebug = true;
        webRequest.onRequestCompleted(null, 200, response, null);
        webRequest.printResponseLog();
        webRequest.addExtra(PaytmPaymentDialog.class.getSimpleName(), this);

        if (getTargetFragment() != null && getTargetFragment() instanceof AppBaseFragment) {

            final AppBaseFragment appBaseFragment = (AppBaseFragment) getTargetFragment();
            if (appBaseFragment.isValidActivity()) {
                appBaseFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appBaseFragment.onWebRequestResponse(webRequest);

                    }
                });

            }
        }
    }
}
