package com.rider.ui.main.dialog.termCondition;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.rider.R;
import com.rider.appBase.AppBaseDialogFragment;
import com.rider.ui.MyApplication;


/**
 * Created by ubuntu on 10/4/18.
 */

public class TermConditionDialog extends AppBaseDialogFragment {

    private WebView web_view;
    private TextView tv_ok, tv_title;
    String message;
    String title;

    public static TermConditionDialog getNewInstance(String title, String message) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        bundle.putString("title", title);
        TermConditionDialog messageDialog = new TermConditionDialog();
        messageDialog.setArguments(bundle);
        return messageDialog;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_term_condition;
    }

    @Override
    public void initializeComponent() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.message = bundle.getString("message", "");
            this.title = bundle.getString("title", getContext().getString(R.string.term_condition));
        }
        tv_ok = getView().findViewById(R.id.tv_ok);
        tv_title = getView().findViewById(R.id.tv_title);

        tv_title.setText(title);
        tv_ok.setOnClickListener(this);
        setupWebView();
    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                MyApplication.getInstance().getAppPrefs().setTermsConditions(true);
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

        //set dialog_country view
        dialog.setContentView(layout);
        //setup dialog_country window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.windowAnimations = R.style.BadeDialogStyle;
        wlmp.gravity = Gravity.CENTER;
        wlmp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setTitle(null);
        setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void setupWebView() {
        String URL = URL_TERM_AND_CONDITION;
        WebView wv_terms_conditions = getView().findViewById(R.id.web_view);
        wv_terms_conditions.setBackgroundColor(0x00000000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            wv_terms_conditions.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            wv_terms_conditions.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        final WebSettings webSettings = wv_terms_conditions.getSettings();
        Resources res = getResources();
        float fontSize = res.getDimension(R.dimen.sp10);
        webSettings.setDefaultFontSize((int)fontSize);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);


        wv_terms_conditions.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        wv_terms_conditions.loadUrl(URL);
    }

}
