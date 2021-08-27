package com.quickzetuser.ui.main.dialog.seatDialog;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseDialogFragment;
import com.quickzetuser.model.RentalFareModel;

public class SeatAvailableDialog extends AppBaseDialogFragment {

    private LinearLayout ll_one_seat, ll_second_seat;
    private TextView tv_one_seat_price, tv_second_seat_price, tv_message;
    private SeatAvailableListener seatAvailableListener;
    private RentalFareModel rentalFareModel;

    public void setFareModel(RentalFareModel rentalFareModel) {
        this.rentalFareModel = rentalFareModel;
    }

    public void setSeatAvailableListener(SeatAvailableListener seatAvailableListener) {
        this.seatAvailableListener = seatAvailableListener;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_seat_available;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.windowAnimations = R.style.SelectSeatDialogStyle;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        ll_one_seat = getView().findViewById(R.id.ll_one_seat);
        ll_second_seat = getView().findViewById(R.id.ll_second_seat);
        tv_one_seat_price = getView().findViewById(R.id.tv_one_seat_price);
        tv_second_seat_price = getView().findViewById(R.id.tv_second_seat_price);
        tv_message = getView().findViewById(R.id.tv_message);

        ll_one_seat.setOnClickListener(this);
        ll_second_seat.setOnClickListener(this);
        setData();
    }

    private void setData() {
        if (rentalFareModel != null) {
            String payble_fare = rentalFareModel.getPayble_fare();
            String secondSeatCharge = rentalFareModel.getSecondSeatCharge();
            tv_one_seat_price.setText(currency_symbol + payble_fare);
            tv_second_seat_price.setText(currency_symbol + secondSeatCharge);
        }
    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_one_seat:
                if (this.seatAvailableListener != null) {
                    this.seatAvailableListener.onSeatSelected(this, "1 seat", rentalFareModel != null?rentalFareModel.getPayble_fare():"");
                } else {
                    this.dismiss();
                }
                break;
            case R.id.ll_second_seat:
                if (this.seatAvailableListener != null) {
                    this.seatAvailableListener.onSeatSelected(this,  "2 seat", rentalFareModel != null?rentalFareModel.getSecondSeatCharge():"");
                } else {
                    this.dismiss();
                }
                break;
        }
    }

    public interface SeatAvailableListener {
        void onSeatSelected(SeatAvailableDialog seatAvailableDialog,String seat, String seatCharge);
    }


}
