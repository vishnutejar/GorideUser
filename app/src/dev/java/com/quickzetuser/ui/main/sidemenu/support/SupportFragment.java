package com.quickzetuser.ui.main.sidemenu.support;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.model.UserModel;
import com.quickzetuser.ui.MyApplication;

/**
 * Created by Sunil kumar yadav on 14/3/18.
 */

public class SupportFragment extends AppBaseFragment {

    private LinearLayout ll_support;
    private LinearLayout ll_contact;
    private LinearLayout ll_web;

    private TextView tv_support_mail;
    private TextView tv_support_contact;
    private TextView tv_support_web;

    @Override
    public int getLayoutResourceId () {
        return R.layout.fragment_support;
    }

    @Override
    public void initializeComponent () {
        ll_support = getView().findViewById(R.id.ll_support);
        ll_contact = getView().findViewById(R.id.ll_contact);
        ll_web = getView().findViewById(R.id.ll_web);

        tv_support_mail = getView().findViewById(R.id.tv_support_mail);
        tv_support_contact = getView().findViewById(R.id.tv_support_contact);
        tv_support_web = getView().findViewById(R.id.tv_support_web);

        UserModel userModel = MyApplication.getInstance().getUserModel();
        if(userModel!=null) {
            tv_support_mail.setText(userModel.getSupport_email());
            tv_support_contact.setText(userModel.getSupport_phone());
            tv_support_web.setText(userModel.getSupport_web());
        }

        ll_support.setOnClickListener(this);
        ll_contact.setOnClickListener(this);
        ll_web.setOnClickListener(this);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.ll_support:
                sendMail(tv_support_mail);
                break;

            case R.id.ll_contact:
                makeCall();
                break;

            case R.id.ll_web:
                browseWeb();
                break;
        }
    }

    private void makeCall () {
        String phone = tv_support_contact.getText().toString().trim();
        if (!isValidString(phone)) {
            return;
        }

        try {
            getMainActivity().makeCall(phone);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void sendMail (TextView textView) {
        String email = textView.getText().toString().trim();
        if (!isValidString(email)) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, getMainActivity().getString(R.string.app_name));
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an email client:"));
        } catch (ActivityNotFoundException e) {
            showErrorMessage(getString(R.string.no_apps_found_to_open_msg));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void browseWeb () {
        String web = tv_support_web.getText().toString().trim();
        if (!isValidString(web)) {
            return;
        }
        String domain1 = "http://";
        String domain2 = "https://";
        if (!web.startsWith(domain1) || !web.startsWith(domain2)) {
            web = domain1 + web;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(web));
            startActivity(Intent.createChooser(intent, "Choose Browser:"));
        } catch (ActivityNotFoundException e) {
            showErrorMessage(getString(R.string.no_apps_found_to_open_msg));
        }

    }
}
