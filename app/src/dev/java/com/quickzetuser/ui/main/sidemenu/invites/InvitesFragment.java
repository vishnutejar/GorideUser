package com.quickzetuser.ui.main.sidemenu.invites;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goride.user.BuildConfig;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.model.UserModel;
import com.quickzetuser.preferences.UserPrefs;
import com.quickzetuser.ui.main.MainActivity;


/**
 * Created by Sunil kumar yadav on 23/3/18.
 */

public class InvitesFragment extends AppBaseFragment {
    private ImageView iv_image;
    private TextView tv_dummy_text;
    private TextView tv_invite;
    private TextView tv_invite_code;
    private TextView tv_submit;
    private String rideAmount = "₹25";

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_invites;
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();

        iv_image =  getView().findViewById(R.id.iv_image);
        tv_dummy_text =  getView().findViewById(R.id.tv_dummy_text);
        tv_invite =  getView().findViewById(R.id.tv_invite);
        tv_invite_code =  getView().findViewById(R.id.tv_invite_code);
        tv_submit =  getView().findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(this);

        try {
            UserPrefs userPrefs = getMyApplication().getUserPrefs();
            if (userPrefs!=null){
                UserModel userModel = userPrefs.getLoggedInUser();
                String referral_code = userModel.getReferral_code();
                if (referral_code!=null)
                    tv_invite_code.setText(userModel.getReferral_code());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_submit:
                shareApp();
                break;
        }
    }

    public void shareApp() {
        String appName = getString(R.string.app_name);
        String shareBodyText =
                "https://play.google.com/store/apps/details?id="+
                        BuildConfig.APPLICATION_ID+"&hl=en\n"
                +"\nInvite your friends and family," +
//                " Get a free ride worth up to "+rideAmount+
                " when you refer a friend to try "+ appName +
                " Referral code "+tv_invite_code.getText().toString().trim();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
        try {
            startActivity(Intent.createChooser(shareIntent, "Share"));
        } catch (ActivityNotFoundException e) {
            ((MainActivity)getActivity()).showToast("No Apps found please install app from PlayStore.");
        }
    }

}
