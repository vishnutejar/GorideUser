package com.quickzetuser.ui.main.sidemenu.profile;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.kyleduo.switchbutton.SwitchButton;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.model.UserModel;
import com.quickzetuser.ui.main.MainActivity;
import com.squareup.picasso.Picasso;


/**
 * Created by Sunil kumar yadav on 14/3/18.
 */

public class ProfileFragment extends AppBaseFragment {

    private ImageView iv_user_image;
    private TextView tv_user_name;
    private TextView tv_user_email;
    private TextView tv_email;
    private TextView tv_firstname;
    private TextView tv_lastname;
    private TextView tv_password;
    private TextView tv_mobile_code;
    private TextView tv_mobile;
    EditProfileFragment.ProfileUpdate profileUpdate = new EditProfileFragment.ProfileUpdate() {
        @Override
        public void success() {
            setUserData();
        }
    };
    private CardView cv_layout;
    private SwitchButton sw_notification;

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_profile;
    }

    @Override
    public void initializeComponent() {
        iv_user_image = getView().findViewById(R.id.iv_user_image);
        tv_user_name = getView().findViewById(R.id.tv_user_name);
        tv_user_email = getView().findViewById(R.id.tv_user_email);
        tv_email = getView().findViewById(R.id.tv_email);
        tv_firstname = getView().findViewById(R.id.tv_firstname);
        tv_lastname = getView().findViewById(R.id.tv_lastname);
        tv_password = getView().findViewById(R.id.tv_password);
        tv_mobile_code = getView().findViewById(R.id.tv_mobile_code);
        tv_mobile = getView().findViewById(R.id.tv_mobile);
        cv_layout = getView().findViewById(R.id.cv_layout);
        sw_notification = getView().findViewById(R.id.sw_notification);

        setUserData();
    }

    private void setUserData() {
        UserModel userModel = ((MainActivity) getActivity()).getUserModel();
        if (userModel == null) return;

        Picasso.get()
                .load(userModel.getImage())
                .placeholder(R.drawable.noimage)
                .error(R.drawable.noimage)
                .into(iv_user_image);

        tv_user_name.setText(userModel.getFullName());
        tv_user_email.setText(userModel.getEmail());
        tv_email.setText(userModel.getEmail());
        tv_firstname.setText(userModel.getFirstname());
        tv_lastname.setText(userModel.getLastname());
        tv_password.setText(userModel.getPassword());
        tv_mobile.setText(userModel.getFullMobile());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_right_menu:
                addEditProfileFragment();
                break;
        }
    }

    public void addEditProfileFragment() {
        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setProfileUpdate(profileUpdate);
        int enterAnimation = R.anim.fadein;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.fadeout;
        ((MainActivity) getActivity()).changeFragment(fragment, true, false, 0,
                enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
    }
}
