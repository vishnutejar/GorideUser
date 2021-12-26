package com.appupdater;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.goride.user.R;

import java.util.Locale;

import static com.appupdater.AppUpdateUtils.getAppPackageName;


/**
 * Created by ubuntu on 29/5/17.
 */


public class DialogAppUpdater extends Dialog implements View.OnClickListener

{
    static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=%s&hl=%s";

    Context context;
    TextView tv_update, tv_later;
    TextView tv_message_data;
    String msg;

    public DialogAppUpdater (Context context, String msg) {
        super(context);
        this.context = context;
        this.msg = msg;

    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflate.inflate(R.layout.dialog_app_updater, null);
        setContentView(layout);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;

        setTitle(null);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setOnCancelListener(null);
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow (DialogInterface dialog) {
                if (msg != null && !msg.trim().isEmpty()) {
                    tv_message_data.setText(Html.fromHtml(msg));
                }
            }
        });


        tv_update = (TextView) findViewById(R.id.tv_update);
        tv_later = (TextView) findViewById(R.id.tv_later);
        tv_message_data = (TextView) findViewById(R.id.tv_message_data);
        tv_update.setOnClickListener(this);
        tv_later.setOnClickListener(this);
    }

    @Override
    public void onClick (View view) {
        if (view.getId() == R.id.tv_later) {
            this.dismiss();
        } else if (view.getId() == R.id.tv_update) {
            this.dismiss();
            goToPlayStore();
        }

    }

    static String getPlayStoreUrl (Context context) {

        return String.format(PLAY_STORE_URL,
                getAppPackageName(context), Locale.getDefault().getLanguage());
    }

    private void goToPlayStore () {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + getAppPackageName(context)));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            String url = getPlayStoreUrl(context);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }
}
