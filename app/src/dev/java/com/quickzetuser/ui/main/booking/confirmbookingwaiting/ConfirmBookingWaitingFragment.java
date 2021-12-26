package com.quickzetuser.ui.main.booking.confirmbookingwaiting;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.fcm.NotificationModal;
import com.fcm.PushNotificationHelper;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.database.tables.BookingTable;
import com.quickzetuser.fcm.AppNotificationModel;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.ui.main.dialog.cancelBooking.CancelBookingDialog;
import com.quickzetuser.ui.utilities.BookingStatusHelper;

import io.supercharge.shimmerlayout.ShimmerLayout;


/**
 * Created by Sunil kumar yadav on 7/5/18.
 */
public class ConfirmBookingWaitingFragment extends AppBaseFragment implements
        BookingStatusHelper.BookingStatusHelperListener,
        PushNotificationHelper.PushNotificationHelperListener {

    BookingStatusHelper bookingStatusHelper;
    PushNotificationHelper pushNotificationHelper;
    CancelBookingDialog cancelBookingDialog;
    private ShimmerLayout shimmerLayout;
    private LinearLayout ll_cancel_ride;
    private TextView tv_fare_estimate;
    private BookCabModel bookCabModel;

    public BookCabModel getBookCabModel () {
        return bookCabModel;
    }

    public void setBookCabModel (BookCabModel bookCabModel) {
        this.bookCabModel = bookCabModel;
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.fragment_confirm_booking_waiting;
    }

    @Override
    public void initializeComponent () {
        try {
            if (getMyApplication() != null) {
                pushNotificationHelper = getMyApplication().getPushNotificationHelper();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        shimmerLayout = getView().findViewById(R.id.shimmer_view_container);
        shimmerLayout.startShimmerAnimation();
        ll_cancel_ride = getView().findViewById(R.id.ll_cancel_ride);
        tv_fare_estimate = getView().findViewById(R.id.tv_fare_estimate);
        ll_cancel_ride.setOnClickListener(this);

        setupLayoutObserver();
    }

    @Override
    public void onResume () {
        super.onResume();
        if (pushNotificationHelper != null) {
            pushNotificationHelper.addNotificationHelperListener(this);
        }

        try {
            bookingStatusHelper = BookingStatusHelper.getNewInstances(getMainActivity(), bookCabModel);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (bookingStatusHelper != null) {
            bookingStatusHelper.setBookingStatusHelperListener(this);
            bookingStatusHelper.startBookingUpdater();
        }
    }

    @Override
    public void onPause () {
        super.onPause();
        if (pushNotificationHelper != null) {
            pushNotificationHelper.removeNotificationHelperListener(this);
        }
        if (bookingStatusHelper != null) {
            bookingStatusHelper.stopBookingUpdater(true);
        }
    }

    @Override
    public void onDestroyView () {
        if (cancelBookingDialog != null && cancelBookingDialog.isVisible()) {
            cancelBookingDialog.dismiss();
        }
        super.onDestroyView();
    }

    private void setupLayoutObserver () {
        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout () {
                getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setupMapPadding();
            }
        });
    }

    private void setupMapPadding () {
        try {
            getMainActivity().getMapHandler().setMapPadding(0,
                    getMainActivity().getMapHandler().getTopMapHeight(), 0, getView().getHeight());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.ll_cancel_ride:
                addCancelBookingDialog();
                break;
        }
    }

    private void addCancelBookingDialog () {
        cancelBookingDialog = CancelBookingDialog.getNewInstance("");
        cancelBookingDialog.setBookCabModel(bookCabModel);
        cancelBookingDialog.setConfirmationDialogListener(new CancelBookingDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm (DialogFragment dialogFragment, BookCabModel data) {
                bookCabModel = data;
                handleBookingStatusUpdate();
            }
        });
        cancelBookingDialog.show(getActivity().getSupportFragmentManager(), cancelBookingDialog.getClass().getSimpleName());
    }

    @Override
    public void onBookingUpdate (BookCabModel bookCabModel) {
        if (bookCabModel != null && this.bookCabModel != null) {
            if (this.bookCabModel.getStatus() != bookCabModel.getStatus()) {
                this.bookCabModel = bookCabModel;
                handleBookingStatusUpdate();
            }
        }

    }

    private void handleBookingStatusUpdate () {

        if (this.bookCabModel != null) {
            if (this.bookCabModel.getStatus() == -1) {
                revertToFareEstimateScreen();
            } else if (this.bookCabModel.getStatus() == 6) {
                revertToFareEstimateScreen();
            } else if (this.bookCabModel.getStatus() == 7) {
                revertToFareEstimateScreen();
            } else {
                try {
                    BookingTable.getInstance().addOrUpdateBooking(bookCabModel);
                    getMyApplication().setRunningBookingModel(bookCabModel);
                    getMainActivity().getMapHandler().showConfirmBookingFragment(bookCabModel);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void revertToFareEstimateScreen () {
        BookingTable.getInstance().deleteBooking(bookCabModel.getBooking_id());
        try {
            BookCabModel data = getMainActivity().getMapHandler().fareEstimateModel;
            if (data != null) {
                if (bookCabModel.getStatus() == -1 || bookCabModel.getStatus() == 7) {
                    String msg = "Sorry! All of our drivers are busy. Please try again.";
                    if (bookCabModel.getStatus() == 7) {
                        msg = "Your booking is cancelled by driver.";
                    }
                    getMainActivity().showMessageDialog(getString(R.string.app_name),
                            msg);
                }
                if (data.isRentalBooking()) {
                    getMainActivity().getMapHandler().showFareEstimateRentalFragment(1, data);
                }else if (data.isSharingBooking()) {
                    getMainActivity().getMapHandler().showFareEstimateSharingFragment(1, data);
                } else {
                    getMainActivity().getMapHandler().showFareEstimateFragment(1, data);
                }

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPushNotificationReceived (NotificationModal notificationModal) {
        AppNotificationModel appNotificationModel = (AppNotificationModel) notificationModal;
        if (appNotificationModel.getBookingId() == null ||
                (appNotificationModel.getBookingId() != this.bookCabModel.getBooking_id()))
            return;

        if (appNotificationModel.isAutoCancelBookingNotification()) {
            if (appNotificationModel.getStatus() == null) return;
            if (this.bookCabModel.getStatus() != appNotificationModel.getStatus()) {
                this.bookCabModel.setStatus(appNotificationModel.getStatus());
                handleBookingStatusUpdate();
            }
        } else if (appNotificationModel.isDriverAcceptBookingNotification()) {
            if (appNotificationModel.getStatus() == null) return;
            if (this.bookCabModel.getStatus() != appNotificationModel.getStatus()) {
                if (bookingStatusHelper != null) {
                    bookingStatusHelper.startBookingUpdater();
                }
            }

        } else if (appNotificationModel.isDriversDeclineBookingNotification()) {
            if (appNotificationModel.getStatus() == null) return;
            if (this.bookCabModel.getStatus() != appNotificationModel.getStatus()) {
                this.bookCabModel.setStatus(appNotificationModel.getStatus());
                handleBookingStatusUpdate();
            }
        }
    }

    @Override
    public boolean handleOnBackPress () {
        super.handleOnBackPress();
        if (this.bookCabModel != null) {
            if (this.bookCabModel.getStatus() == -1) {
                revertToFareEstimateScreen();
            } else if (this.bookCabModel.getStatus() == 6) {
                revertToFareEstimateScreen();
            } else if (this.bookCabModel.getStatus() == 0) {
                ll_cancel_ride.performClick();
            }
        } else {
            revertToFareEstimateScreen();
        }

        return true;
    }
}
