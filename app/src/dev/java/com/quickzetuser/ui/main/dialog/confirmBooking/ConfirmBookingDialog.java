package com.quickzetuser.ui.main.dialog.confirmBooking;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.BaseDialogFragment;
import com.distancematrix.DistanceMatrixHandler;
import com.distancematrix.DistanceMatrixModel;
import com.models.BaseModel;
import com.quickzetuser.R;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.FareModel;
import com.quickzetuser.model.PromoCodeModel;
import com.quickzetuser.model.RentalPackageModel;
import com.quickzetuser.model.VehicleTypeModel;
import com.squareup.picasso.Picasso;
import com.utilities.ViewAnimationUtils;

import java.util.List;


/**
 * Created by Sunil kumar yadav on 26/2/18.
 */

public class  ConfirmBookingDialog extends BaseDialogFragment
        implements DistanceMatrixHandler.DistanceMatrixListener {

    private final int TIME_INTERVAL = 1000;
    String message;
    ConfirmationDialogListener confirmationDialogListener;
    private RelativeLayout rl_pickup_request;
    private LinearLayout ll_pickup_status;
    private LinearLayout ll_more_detail;
    private ImageView iv_more_less;
    private TextView tv_package_name;
    private TextView tv_approx_fare_msg;
    private TextView tv_pickup_address;
    private TextView tv_drop_address;
    private TextView tv_vehicle_reached_time;
    private TextView tv_vehicle_name;
    private TextView tv_approx;
    private TextView tv_approx_travel_time;
    private TextView tv_approx_travel_distance;
    private TextView tv_approx_fare_price;
    private TextView tv_confirm;
    private ImageView iv_cancel;
    private TextView tv_schedule_time;
    private TextView tv_promocode;
    private Handler handler;
    private Runnable runnable;
    private BookCabModel bookCabModel;
    private ImageView iv_vehicle_type;

    public static ConfirmBookingDialog getNewInstance (String message) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        ConfirmBookingDialog confirmationDialog = new ConfirmBookingDialog();
        confirmationDialog.setArguments(bundle);
        return confirmationDialog;
    }

    public void setConfirmationDialogListener (ConfirmationDialogListener confirmationDialogListener) {
        this.confirmationDialogListener = confirmationDialogListener;
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.dialog_ride_now_confirm;
    }

    @Override
    public void initializeComponent () {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.message = bundle.getString("message", "");
        }
        tv_package_name = getView().findViewById(R.id.tv_package_name);
        tv_approx_fare_msg = getView().findViewById(R.id.tv_approx_fare_msg);
        rl_pickup_request = getView().findViewById(R.id.rl_pickup_request);
        ll_pickup_status = getView().findViewById(R.id.ll_pickup_status);
        ll_more_detail = getView().findViewById(R.id.ll_more_detail);
        iv_more_less = getView().findViewById(R.id.iv_more_less);
        tv_vehicle_reached_time = getView().findViewById(R.id.tv_vehicle_reached_time);
        tv_vehicle_name = getView().findViewById(R.id.tv_vehicle_name);
        tv_approx = getView().findViewById(R.id.tv_approx);
        tv_approx_travel_time = getView().findViewById(R.id.tv_approx_travel_time);
        tv_approx_travel_distance = getView().findViewById(R.id.tv_approx_travel_distance);
        tv_approx_fare_price = getView().findViewById(R.id.tv_approx_fare_price);
        tv_pickup_address = getView().findViewById(R.id.tv_pickup_address);
        tv_drop_address = getView().findViewById(R.id.tv_drop_address);
        tv_confirm = getView().findViewById(R.id.tv_confirm);
        iv_cancel = getView().findViewById(R.id.iv_cancel);
        tv_schedule_time = getView().findViewById(R.id.tv_schedule_time);
        tv_promocode = getView().findViewById(R.id.tv_promocode);
        iv_vehicle_type = getView().findViewById(R.id.iv_vehicle_type);
        tv_approx.setText(getString(R.string.approx));

        updateViewVisibility(tv_package_name, View.GONE);
        updateViewVisibility(tv_schedule_time, View.GONE);
        animateMoreLess(0, true);
        if (bookCabModel != null) {
            updatePromocodeUi();
            updateScheduledUi();
            updateAvailableTime();
            tv_pickup_address.setText(bookCabModel.getPickup_address());
            tv_drop_address.setText(bookCabModel.getDrop_address());
            VehicleTypeModel vehicle = confirmationDialogListener.getVehicle();
            if (vehicle != null) {
                tv_vehicle_name.setText(vehicle.getName());
                if (isValidString(vehicle.getImage_selected()))
                    Picasso.get().load(vehicle.getImage_selected()).placeholder(R.mipmap.ic_riksya).into(iv_vehicle_type);
                else
                    iv_vehicle_type.setImageResource(R.mipmap.ic_riksya);
            }
            if (bookCabModel.hasDropAddress()) {
                tv_approx_travel_time.setText(" : " + bookCabModel.getTimeText());
                tv_approx_travel_distance.setText(" : " + bookCabModel.getTotal_distanceText());
                if (tv_approx_fare_msg.getVisibility() != View.VISIBLE)
                    tv_approx_fare_msg.setVisibility(View.VISIBLE);
                FareModel fare = confirmationDialogListener.getFare();
                if (fare.getSaetCount()==2){
                    tv_approx_fare_price.setText(getContext().getString(R.string.rupee_symbol) + " " +
                            confirmationDialogListener.getFare().getSecondSeatCharge());
                }else {
                    tv_approx_fare_price.setText(getContext().getString(R.string.rupee_symbol) + " " +
                            confirmationDialogListener.getFare().getPayble_fare());
                }

            } else {
                if (bookCabModel.isRentalBooking()) {
                    tv_approx.setText(getString(R.string._package));
                    RentalPackageModel selectedRentalPackage = confirmationDialogListener.getSelectedRentalPackage();

                    tv_package_name.setText("(" + selectedRentalPackage.getPackage_name() + ")");
                    tv_approx_travel_time.setText(" : " + selectedRentalPackage.getMax_durationText());
                    tv_approx_travel_distance.setText(" : " + selectedRentalPackage.getMax_distanceText());
                    if (tv_package_name.getVisibility() != View.VISIBLE)
                        tv_package_name.setVisibility(View.VISIBLE);
                    if (tv_approx_fare_msg.getVisibility() != View.VISIBLE)
                        tv_approx_fare_msg.setVisibility(View.VISIBLE);
                    tv_approx_fare_price.setText(getContext().getString(R.string.rupee_symbol) + " " +
                            confirmationDialogListener.getFare().getPayble_fare());
                } else {
                    tv_approx_travel_time.setText(" : N/A");
                    tv_approx_travel_distance.setText(" : N/A");
                    tv_approx_fare_price.setText("N/A");
                    if (tv_approx_fare_msg.getVisibility() != View.GONE)
                        tv_approx_fare_msg.setVisibility(View.GONE);
                }


            }

        }

        iv_more_less.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
        iv_cancel.setOnClickListener(this);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run () {

                try {
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

    }

    private void updateScheduledUi () {
        if (bookCabModel == null) return;
        if (bookCabModel.isScheduleBooking()) {
            String msg = "Schedule at : ";
            tv_schedule_time.setText(msg + bookCabModel.getFormattedCalendar(BaseModel.DATE_TIME_TWO,
                    (bookCabModel.getBooking_date())));
            updateViewVisibility(tv_schedule_time, View.VISIBLE);
        } else {
            updateViewVisibility(tv_schedule_time, View.GONE);
        }
    }

    private void updateAvailableTime () {
        if (bookCabModel != null && confirmationDialogListener != null) {
            tv_vehicle_reached_time.setText(confirmationDialogListener.getAvailableTime());
        } else {
            tv_vehicle_reached_time.setText("N/A");
        }
    }


    private void updatePromocodeUi () {
        if (bookCabModel != null && confirmationDialogListener != null) {
            PromoCodeModel promocode = confirmationDialogListener.getPromoCode();
            if (promocode == null) {
                updateViewVisibility(tv_promocode, View.GONE);
                tv_promocode.setText("");
            } else {
                updateViewVisibility(tv_promocode, View.VISIBLE);
                tv_promocode.setText("Promocode : " + promocode.getPromocode());
            }
        } else {
            tv_vehicle_reached_time.setText("N/A");
        }

    }

    @Override
    public int getFragmentContainerResourceId () {
        return -1;
    }


    @Override
    public void setupDialog (Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog view
        dialog.setContentView(layout);
        //setup dialog window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.windowAnimations = R.style.BadeDialogStyle;
        wlmp.gravity = Gravity.CENTER;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (confirmationDialogListener == null) return;
                confirmationDialogListener.onClickConfirm(ConfirmBookingDialog.this);
                break;

            case R.id.iv_cancel:
                dismiss();
                break;
            case R.id.iv_more_less:
                float rotation = iv_more_less.getRotation();
                if (rotation == 0) {
                    animateMoreLess(180, true);
                } else if (rotation == 180) {
                    animateMoreLess(0, true);
                }
                break;
        }
    }

    public void updateSuccessUI () {
        ll_pickup_status.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, TIME_INTERVAL);
    }


    public void setBookCabModel (BookCabModel bookCabModel) {
        this.bookCabModel = bookCabModel;
    }

    @Override
    public void onDistanceMatrixRequestStart () {

    }

    @Override
    public void onDistanceMatrixRequestEnd (List<DistanceMatrixModel> rows) {
        if (isValidActivity() && isVisible())
            updateAvailableTime();
    }

    public interface ConfirmationDialogListener {
        void onClickConfirm (ConfirmBookingDialog dialogFragment);

        String getAvailableTime ();

        PromoCodeModel getPromoCode ();

        VehicleTypeModel getVehicle ();

        RentalPackageModel getSelectedRentalPackage ();

        FareModel getFare ();
    }

    private void animateMoreLess (int rotation, boolean isSilent) {
        if (isSilent) {
            updateViewVisibility(ll_more_detail, rotation == 0 ? View.GONE : View.VISIBLE);
            iv_more_less.setRotation(rotation);
        } else {
            if (rotation == 0) {
                iv_more_less.setRotation(0);
                ViewAnimationUtils.collapse(ll_more_detail, new ViewAnimationUtils.InterPolatedTimeListener() {
                    @Override
                    public void applyTransformation (float interpolatedTime) {
                        if (interpolatedTime == 1) {
                            iv_more_less.setRotation(0);
                        } else {
                            iv_more_less.setRotation(180.0f - (180.0f * interpolatedTime));
                        }

                    }
                });
            } else {
                iv_more_less.setRotation(180);
                ViewAnimationUtils.expand(ll_more_detail, new ViewAnimationUtils.InterPolatedTimeListener() {
                    @Override
                    public void applyTransformation (float interpolatedTime) {
                        if (interpolatedTime == 1) {
                            iv_more_less.setRotation(180);
                        } else {
                            iv_more_less.setRotation((180.0f * interpolatedTime));
                        }
                    }
                });
            }
        }
    }

}
