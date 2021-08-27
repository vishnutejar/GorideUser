package com.quickzetuser.ui.main.helpers;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.navigation.NavigationView;
import com.models.DeviceScreenModel;
import com.quickzetuser.R;
import com.quickzetuser.model.UserModel;
import com.quickzetuser.ui.login.LoginActivity;
import com.quickzetuser.ui.main.MainActivity;
import com.quickzetuser.ui.main.dialog.logout.LogOutConfirmDialog;
import com.quickzetuser.ui.main.sidemenu.invites.InvitesFragment;
import com.quickzetuser.ui.main.sidemenu.myRide.MyRideFragment;
import com.quickzetuser.ui.main.sidemenu.notification.NotificationFragment;
import com.quickzetuser.ui.main.sidemenu.payment.PaymentFragment;
import com.quickzetuser.ui.main.sidemenu.payment.wallet.WalletFragment;
import com.quickzetuser.ui.main.sidemenu.profile.ProfileFragment;
import com.quickzetuser.ui.main.sidemenu.support.SupportFragment;
import com.squareup.picasso.Picasso;



/**
 * Created by Sunil kumar yadav on 27/2/18.
 */

public class SideMenuHelper implements View.OnClickListener {

    MainActivity mainActivity;
    DrawerLayout drawer_layout;
    NavigationView navigation_view_left;

    ImageView iv_user_image;
    TextView tv_user_name;
    TextView tv_user_email;
    TextView tv_wallet;
    LinearLayout ll_book_ride;
    LinearLayout ll_my_ride;
    LinearLayout ll_payment;
    LinearLayout ll_profile;
    LinearLayout ll_support;
    public LinearLayout ll_notification;
    LinearLayout ll_invite;
    LinearLayout ll_logout;
    LinearLayout ll_walletbal_layout;
    RatingBar rider_RatingBar;

    public TextView tv_book_ride, tv_my_ride, tv_payments, tv_profile, tv_support, tv_notifications, tv_invites, tv_logout;

    private TextView sel_text_view;


    public SideMenuHelper (MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initializeComponent();
//        adjustNavigationViewWidth();
    }

    private void initializeComponent () {
        initView();
        initSetUserData();
        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide (View view, float v) {

            }

            @Override
            public void onDrawerOpened (View view) {

            }

            @Override
            public void onDrawerClosed (View view) {

            }

            @Override
            public void onDrawerStateChanged (int i) {

            }
        });


    }


    private void initView () {
        drawer_layout = mainActivity.findViewById(R.id.drawer_layout);
        navigation_view_left = mainActivity.findViewById(R.id.navigation_view_left);

        iv_user_image = mainActivity.findViewById(R.id.iv_user_image);
        tv_user_name = mainActivity.findViewById(R.id.tv_user_name);
        tv_user_email = mainActivity.findViewById(R.id.tv_user_email);
        tv_wallet = mainActivity.findViewById(R.id.tv_wallet);
        ll_book_ride = mainActivity.findViewById(R.id.ll_book_ride);
        ll_my_ride = mainActivity.findViewById(R.id.ll_my_ride);
        ll_payment = mainActivity.findViewById(R.id.ll_payment);
        ll_profile = mainActivity.findViewById(R.id.ll_profile);
        ll_support = mainActivity.findViewById(R.id.ll_support);
        ll_notification = mainActivity.findViewById(R.id.ll_notification);
        ll_invite = mainActivity.findViewById(R.id.ll_invite);
        ll_logout = mainActivity.findViewById(R.id.ll_logout);
        ll_walletbal_layout = mainActivity.findViewById(R.id.ll_walletbal_layout);

        tv_book_ride = mainActivity.findViewById(R.id.tv_book_ride);
        tv_my_ride = mainActivity.findViewById(R.id.tv_my_ride);
        tv_payments = mainActivity.findViewById(R.id.tv_payments);
        tv_profile = mainActivity.findViewById(R.id.tv_profile);
        tv_support = mainActivity.findViewById(R.id.tv_support);
        tv_notifications = mainActivity.findViewById(R.id.tv_notifications);
        tv_invites = mainActivity.findViewById(R.id.tv_invites);
        tv_logout = mainActivity.findViewById(R.id.tv_logout);

        rider_RatingBar = mainActivity.findViewById(R.id.rider_RatingBar);


        ll_book_ride.setOnClickListener(this);
        ll_my_ride.setOnClickListener(this);
        ll_payment.setOnClickListener(this);
        ll_profile.setOnClickListener(this);
        ll_support.setOnClickListener(this);
        ll_notification.setOnClickListener(this);
        ll_invite.setOnClickListener(this);
        ll_logout.setOnClickListener(this);
        ll_walletbal_layout.setOnClickListener(this);
    }

    public void initSetUserData () {
        UserModel userModel = mainActivity.getUserModel();
        if (userModel == null) return;
        if (!userModel.getImage().isEmpty()) {
            Picasso.get()
                    .load(userModel.getImage())
                    .placeholder(R.drawable.noimage)
                    .error(R.drawable.noimage)
                    .into(iv_user_image);
        } else {
            iv_user_image.setImageResource(R.drawable.noimage);
        }
        tv_user_name.setText(userModel.getFullName());
        tv_user_email.setText(userModel.getFullMobile());
        rider_RatingBar.setRating(userModel.getAvg_rating());

        if (userModel.getWallet() != null) {
            tv_wallet.setText(String.valueOf(mainActivity.currency_symbol + userModel.getWallet().getWallet_amountText()));
        } else {
            tv_wallet.setText(String.valueOf(mainActivity.currency_symbol + "0.00"));
        }
    }

    private void adjustNavigationViewWidth () {
        int navigationViewWidth = DeviceScreenModel.getInstance().getNavigationViewWidth();
        DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) navigation_view_left.getLayoutParams();
        layoutParams.width = navigationViewWidth;
        layoutParams.height = DrawerLayout.LayoutParams.MATCH_PARENT;
        navigation_view_left.setLayoutParams(layoutParams);
    }

    public void handleDrawer () {
        if (!closeDrawer()) {
            drawer_layout.openDrawer(navigation_view_left);
        }
    }

    public boolean closeDrawer () {
        if (drawer_layout.isDrawerOpen(navigation_view_left)) {
            drawer_layout.closeDrawers();
            return true;
        }
        return false;
    }

    public void drawerLock (boolean value) {
        int drawerMode = value ? DrawerLayout.LOCK_MODE_UNLOCKED
                : DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer_layout.setDrawerLockMode(drawerMode);
    }


    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.ll_book_ride:
                mainActivity.getMapHandler().showLayout();
                break;

            case R.id.ll_my_ride:
                addMyRideFragment();
                break;
            case R.id.ll_payment:
                addPaymentFragment();
                break;

            case R.id.ll_profile:
                addProfileFragment();
                break;

            case R.id.ll_support:
                addSupportFragment();
                break;

            case R.id.ll_notification:
                addNotificationFragment();
                break;

            case R.id.ll_invite:
                addInvitesFragment();
                break;

            case R.id.ll_logout:
                logout();
                break;
            case R.id.ll_walletbal_layout:
                addWalletFragment();
                break;
        }
        closeDrawer();
    }

    public void setSelectedTextView (TextView textView) {
        if (sel_text_view != null) {
            sel_text_view.setSelected(false);
        }
        sel_text_view = textView;
        sel_text_view.setSelected(true);
    }


    public void addMyRideFragment () {
        MyRideFragment fragment = new MyRideFragment();
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.left_out;
        mainActivity.changeFragment(fragment, true, true, 0,
                enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
    }

    private void addPaymentFragment () {
        PaymentFragment fragment = new PaymentFragment();
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.left_out;
        mainActivity.changeFragment(fragment, true, true, 0,
                enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
    }

    private void addProfileFragment () {
        ProfileFragment fragment = new ProfileFragment();
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.left_out;
        mainActivity.changeFragment(fragment, true, true, 0,
                enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
    }

    private void addSupportFragment () {
        SupportFragment fragment = new SupportFragment();
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.left_out;
        mainActivity.changeFragment(fragment, true, true, 0,
                enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
    }

    private void addNotificationFragment () {
        NotificationFragment fragment = new NotificationFragment();
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.left_out;
        mainActivity.changeFragment(fragment, true, true, 0,
                enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
    }

    private void addInvitesFragment () {
        InvitesFragment fragment = new InvitesFragment();
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.left_out;
        mainActivity.changeFragment(fragment, true, true, 0,
                enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
    }


    private void logout () {
        LogOutConfirmDialog confirmationDialog = LogOutConfirmDialog.
                getNewInstance("Are you sure to logout?");
        confirmationDialog.setConfirmationDialogListener(new LogOutConfirmDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm (DialogFragment dialogFragment) {
                dialogFragment.dismiss();
                mainActivity.sendActivityFinish(mainActivity, LoginActivity.class);
            }
        });
        confirmationDialog.show(mainActivity.getFm(), confirmationDialog.getClass().getSimpleName());
    }

    private void addWalletFragment () {
        WalletFragment fragment = new WalletFragment();
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.right_out;

        mainActivity.changeFragment(fragment, true, true, 0,
                enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);

    }

}
