package com.quickzetuser.ui.main.sidemenu.myRide.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.goride.user.R;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.DriverModel;
import com.quickzetuser.model.FareModel;
import com.quickzetuser.model.TaxiModel;
import com.quickzetuser.ui.main.MainActivity;

import java.util.List;


/**
 * Created by Sunil kumar yadav on 6/3/18.
 */

public class MyRideAdapter extends BaseRecycleAdapter {
    private Context context;
    private List<BookCabModel> list;
    private boolean loadMore;

    public MyRideAdapter(Context context, List<BookCabModel> list) {
        isForDesign = false;
        this.context = context;
        this.list = list;
    }

    public void setLoadMore(boolean loadMore) {
        this.loadMore = loadMore;
        if (list == null) return;
        if (loadMore) {
            notifyItemInserted(list.size());
        } else {
            notifyItemRemoved(list.size());
        }
    }


    @Override
    public BaseViewHolder getViewHolder() {
        return null;
    }

    @Override
    public int getViewType(int position) {
        if (list == null) {
            return VIEW_TYPE_DATA;
        } else if (loadMore && position == list.size()) {
            return VIEW_TYPE_LOAD_MORE;
        }
        return VIEW_TYPE_DATA;

    }

    @Override
    public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        if (VIEW_TYPE_DATA == viewType) {
            return new ViewHolder(inflateLayout(R.layout.item_my_ride));
        }
        return new LoadMoreViewHolder(inflateLayout(R.layout.item_load_more));
    }


    @Override
    public int getDataCount() {
        return list == null ? 0 : (loadMore ? list.size() + 1 : list.size());
    }

    private class ViewHolder extends BaseViewHolder {
        private TextView tv_booking_id;
        private TextView tv_rider_name;
        private TextView tv_license_plate_no;
        private TextView tv_fare_price;
        private TextView tv_payment_method;

        private TextView tv_pickup_address;
        private TextView tv_drop_address;
        private LinearLayout ll_start_time;
        private TextView tv_start_time;
        private LinearLayout ll_booking_time;
        private TextView tv_booking_time;
        private LinearLayout ll_end_time;
        private TextView tv_end_time;
        private LinearLayout ll_cancel_time;
        private TextView tv_cancel_time;
        private LinearLayout ll_payment_remaining;
        private TextView tv_payment_remaining;
        private TextView tv_payment_pay;
        private ImageView iv_status_img;


        public ViewHolder(View itemView) {
            super(itemView);

            tv_booking_id = itemView.findViewById(R.id.tv_booking_id);
            tv_rider_name = itemView.findViewById(R.id.tv_rider_name);
            tv_license_plate_no = itemView.findViewById(R.id.tv_license_plate_no);
            tv_fare_price = itemView.findViewById(R.id.tv_fare_price);
            tv_payment_method = itemView.findViewById(R.id.tv_payment_method);
            ll_start_time = itemView.findViewById(R.id.ll_start_time);
            tv_start_time = itemView.findViewById(R.id.tv_start_time);
            ll_booking_time = itemView.findViewById(R.id.ll_booking_time);
            tv_booking_time = itemView.findViewById(R.id.tv_booking_time);
            tv_pickup_address = itemView.findViewById(R.id.tv_pickup_address);
            tv_drop_address = itemView.findViewById(R.id.tv_drop_address);
            ll_end_time = itemView.findViewById(R.id.ll_end_time);
            tv_end_time = itemView.findViewById(R.id.tv_end_time);
            ll_cancel_time = itemView.findViewById(R.id.ll_cancel_time);
            tv_cancel_time = itemView.findViewById(R.id.tv_cancel_time);
            ll_payment_remaining = itemView.findViewById(R.id.ll_payment_remaining);
            tv_payment_remaining = itemView.findViewById(R.id.tv_payment_remaining);
            tv_payment_pay = itemView.findViewById(R.id.tv_payment_pay);
            iv_status_img = itemView.findViewById(R.id.iv_status_img);
        }

        @Override
        public void setData(int position) {
            BookCabModel bookCabModel = list.get(position);
            if (bookCabModel == null) return;
            tv_booking_id.setText("Booking ID - " + bookCabModel.getBooking_id());
            tv_payment_method.setText(bookCabModel.getPaymentmethod());

            if (bookCabModel.getStatus() == 5) {

                ((MainActivity) context).updateViewVisibility(ll_start_time, View.VISIBLE);
                ((MainActivity) context).updateViewVisibility(ll_end_time, View.VISIBLE);
                ((MainActivity) context).updateViewVisibility(ll_booking_time, View.GONE);
                ((MainActivity) context).updateViewVisibility(ll_cancel_time, View.GONE);
                ((MainActivity) context).updateViewVisibility(tv_license_plate_no, View.VISIBLE);
                tv_start_time.setText(bookCabModel.getFormattedBookingStartTime(2));
                tv_end_time.setText(bookCabModel.getFormattedBookingEndTime(2));
                tv_pickup_address.setText(bookCabModel.getStart_address());
                tv_drop_address.setText(bookCabModel.getEnd_address());
            } else {
                if (bookCabModel.getDriver() == null)
                    ((MainActivity) context).updateViewVisibility(tv_license_plate_no, View.GONE);
                ((MainActivity) context).updateViewVisibility(ll_start_time, View.GONE);
                ((MainActivity) context).updateViewVisibility(ll_end_time, View.GONE);
                ((MainActivity) context).updateViewVisibility(ll_booking_time, View.VISIBLE);
                ((MainActivity) context).updateViewVisibility(ll_cancel_time, View.VISIBLE);
                tv_booking_time.setText(bookCabModel.getFormattedBookingTime(2));
                tv_cancel_time.setText(bookCabModel.getFormattedBookingCancelTime(2));
                tv_pickup_address.setText(bookCabModel.getPickup_address());
                tv_drop_address.setText(bookCabModel.getDrop_address());
            }


            if (bookCabModel.getStatus() == 5) {
                DriverModel driver = bookCabModel.getDriver();
                if (driver != null) {
                    tv_rider_name.setText(driver.getFullName());
                }
                iv_status_img.setImageResource(R.mipmap.stamp_completed);
                iv_status_img.setColorFilter(context.getResources().getColor(R.color.color_green));
            } else if (bookCabModel.getStatus() == 6) {
                tv_rider_name.setText(context.getString(R.string.cancel_by_you));
                iv_status_img.setImageResource(R.mipmap.stamp_cancelled);
                iv_status_img.setColorFilter(context.getResources().getColor(R.color.red));
            } else {
                tv_rider_name.setText(context.getString(R.string.cancel_by_driver));
                iv_status_img.setImageResource(R.mipmap.stamp_cancelled);
                iv_status_img.setColorFilter(context.getResources().getColor(R.color.red));
            }

            TaxiModel taxi = bookCabModel.getTaxi();
            if (taxi != null) {
                tv_license_plate_no.setText(taxi.getLicense_plate_no());
            }

            FareModel fare = bookCabModel.getFare();
            if (fare != null) {
                tv_fare_price.setText(context.getString(R.string.rupee_symbol) + " " +
                        fare.getPayble_fare());
            }

            if (fare.getRemaining_payble_fare() > 0) {
                ((MainActivity) context).updateViewVisibility(ll_payment_remaining, View.VISIBLE);
                tv_payment_remaining.setText(context.getString(R.string.rupee_symbol) + " " +
                        fare.getRemaining_payble_fareText());
                tv_payment_pay.setTag(position);
                tv_payment_pay.setOnClickListener(this);
            } else {
                ((MainActivity) context).updateViewVisibility(ll_payment_remaining, View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            performItemClick((Integer) v.getTag(), v);
        }
    }
}
