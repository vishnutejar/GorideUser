package com.rider.ui.main.helpers;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.BaseFragment;
import com.rider.R;
import com.rider.ui.main.MainActivity;
import com.rider.ui.main.booking.addresschooser.AddressChooserFragment;
import com.rider.ui.main.booking.cabType.CabTypeFragment;
import com.rider.ui.main.booking.confirmBooking.ConfirmBookingFragment;
import com.rider.ui.main.booking.confirmbookingwaiting.ConfirmBookingWaitingFragment;
import com.rider.ui.main.booking.fareEstimate.FareEstimateFragment;
import com.rider.ui.main.booking.fareEstimateOutstation.FareEstimateOutstationFragment;
import com.rider.ui.main.booking.fareEstimateSharing.FareEstimateSharingFragment;
import com.rider.ui.main.booking.fareEstimaterentel.FareEstimateRentalFragment;
import com.rider.ui.main.booking.rideDetail.RideDetailFragment;
import com.rider.ui.main.booking.selectaddress.SelectedAddressFragment;
import com.rider.ui.main.sidemenu.invites.InvitesFragment;
import com.rider.ui.main.sidemenu.myRide.MyRideFragment;
import com.rider.ui.main.sidemenu.myRide.trip_details.TripDetailsFragment;
import com.rider.ui.main.sidemenu.notification.NotificationFragment;
import com.rider.ui.main.sidemenu.payment.PaymentFragment;
import com.rider.ui.main.sidemenu.payment.cash.CashFragment;
import com.rider.ui.main.sidemenu.payment.remainingPayment.PaymentRemainingFragment;
import com.rider.ui.main.sidemenu.payment.wallet.WalletFragment;
import com.rider.ui.main.sidemenu.profile.EditProfileFragment;
import com.rider.ui.main.sidemenu.profile.ProfileFragment;
import com.rider.ui.main.sidemenu.support.SupportFragment;

/**
 * Created by Sunil kumar yadav on 27/2/18.
 */

public class ToolBarHelper implements View.OnClickListener {

    MainActivity mainActivity;
    RelativeLayout rl_tool_bar;
    ImageView iv_menu_left;
    ImageView iv_back;
    ImageView iv_bagghi_icon;
    TextView tv_title;
    ImageView iv_right_menu;

    public ToolBarHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        initializeComponent();
    }

    private void initializeComponent() {
        rl_tool_bar = mainActivity.findViewById(R.id.rl_tool_bar);
        iv_menu_left = mainActivity.findViewById(R.id.iv_menu_left);
        iv_back = mainActivity.findViewById(R.id.iv_back);
        iv_bagghi_icon = mainActivity.findViewById(R.id.iv_bagghi_icon);
        tv_title = mainActivity.findViewById(R.id.tv_title);
        updateViewVisibility(tv_title, View.GONE);
        iv_right_menu = mainActivity.findViewById(R.id.iv_right_menu);


        iv_back.setVisibility(View.GONE);


        iv_menu_left.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_right_menu.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu_left:
                mainActivity.getSideMenuHelper().handleDrawer();
                break;
            case R.id.iv_back:
                mainActivity.onBackPressed();
                break;

            case R.id.iv_right_menu:
                if (mainActivity.getLatestFragment() != null)
                    mainActivity.getLatestFragment().onClick(v);
                break;
        }
    }

    private Resources getResources() {
        return mainActivity.getResources();
    }

    public void updateViewVisibility(View view, int visibility) {
        if (view != null && view.getVisibility() != visibility)
            view.setVisibility(visibility);
    }

    public void onCreateViewFragment(BaseFragment baseFragment) {
        mainActivity.hideKeyboard();
        SideMenuHelper sideMenuHelper = mainActivity.getSideMenuHelper();

        if (baseFragment == null) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_book_ride);
            return;
        }
        if (baseFragment instanceof SelectedAddressFragment) {
            updateViewVisibility(iv_menu_left, View.VISIBLE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.GONE);
            updateViewVisibility(iv_bagghi_icon, View.VISIBLE);
            updateViewVisibility(tv_title, View.GONE);
            tv_title.setText(getResources().getString(R.string.app_name));

        } else if (baseFragment instanceof FareEstimateFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_book_ride);

            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.VISIBLE);
            updateViewVisibility(tv_title, View.GONE);
            tv_title.setText(getResources().getString(R.string.app_name));

        } else if (baseFragment instanceof FareEstimateRentalFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_book_ride);

            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.VISIBLE);
            updateViewVisibility(tv_title, View.GONE);
            tv_title.setText(getResources().getString(R.string.app_name));

        } else if (baseFragment instanceof FareEstimateOutstationFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_book_ride);
            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(((FareEstimateOutstationFragment) baseFragment).getTitle());

        }else if (baseFragment instanceof FareEstimateSharingFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_book_ride);

            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.VISIBLE);
            updateViewVisibility(tv_title, View.GONE);
            tv_title.setText(getResources().getString(R.string.app_name));

        } else if (baseFragment instanceof ConfirmBookingWaitingFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_book_ride);
            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            ConfirmBookingWaitingFragment confirmBookingWaitingFragment = (ConfirmBookingWaitingFragment) baseFragment;
            String title = getResources().getString(R.string.app_name);
            if (confirmBookingWaitingFragment.getBookCabModel() != null) {
                title = "Booking Id - " + confirmBookingWaitingFragment.getBookCabModel().getBooking_id();
            }
            tv_title.setText(title);

        } else if (baseFragment instanceof ConfirmBookingFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_book_ride);

            updateViewVisibility(iv_menu_left, View.VISIBLE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.GONE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(((ConfirmBookingFragment) baseFragment).getTitle());

        } else if (baseFragment instanceof MyRideFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_my_ride);
            updateViewVisibility(iv_menu_left, View.VISIBLE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.GONE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.text_side_menu_my_rides));

        } else if (baseFragment instanceof TripDetailsFragment) {
            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.text_side_menu_my_rides_history));

        } else if (baseFragment instanceof NotificationFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_notifications);
            updateViewVisibility(iv_menu_left, View.VISIBLE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.GONE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.text_side_menu_notification));

        } else if (baseFragment instanceof SupportFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_support);

            updateViewVisibility(iv_menu_left, View.VISIBLE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.GONE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.text_side_menu_support));

        } else if (baseFragment instanceof ProfileFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_profile);

            updateViewVisibility(iv_menu_left, View.VISIBLE);
            updateViewVisibility(iv_right_menu, View.VISIBLE);
            updateViewVisibility(iv_back, View.GONE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.text_side_menu_profile));
            iv_right_menu.setImageResource(R.mipmap.ic_edit);

        } else if (baseFragment instanceof RideDetailFragment) {
            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.text_title_ride_details));

        } else if (baseFragment instanceof PaymentFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_payments);


            updateViewVisibility(iv_menu_left, View.VISIBLE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.GONE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.text_side_menu_payments));

        } else if (baseFragment instanceof CashFragment) {
            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.text_cash));

        } else if (baseFragment instanceof WalletFragment) {
            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.text_add_money));

        } else if (baseFragment instanceof EditProfileFragment) {
            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.VISIBLE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.text_side_menu_profile));
            iv_right_menu.setImageResource(R.drawable.ic_save);

        } else if (baseFragment instanceof CabTypeFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_book_ride);

            updateViewVisibility(iv_menu_left, View.VISIBLE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.GONE);
            updateViewVisibility(iv_bagghi_icon, View.VISIBLE);
            updateViewVisibility(tv_title, View.GONE);
            tv_title.setText(getResources().getString(R.string.app_name));

        } else if (baseFragment instanceof InvitesFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_invites);

            updateViewVisibility(iv_menu_left, View.VISIBLE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.GONE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.invites));

        } else if (baseFragment instanceof AddressChooserFragment) {
            sideMenuHelper.setSelectedTextView(sideMenuHelper.tv_book_ride);
            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.select_address));

        } else if (baseFragment instanceof PaymentRemainingFragment) {
            updateViewVisibility(iv_menu_left, View.GONE);
            updateViewVisibility(iv_right_menu, View.GONE);
            updateViewVisibility(iv_back, View.VISIBLE);
            updateViewVisibility(iv_bagghi_icon, View.GONE);
            updateViewVisibility(tv_title, View.VISIBLE);
            tv_title.setText(getResources().getString(R.string.remining_payment));

        }
    }

    public void onDestroyViewFragment(BaseFragment baseFragment) {

    }
}
