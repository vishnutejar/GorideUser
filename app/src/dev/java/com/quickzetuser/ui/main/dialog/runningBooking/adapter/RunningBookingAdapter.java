package com.quickzetuser.ui.main.dialog.runningBooking.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.quickzetuser.R;
import com.quickzetuser.model.BookCabModel;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 6/3/18.
 */

public class RunningBookingAdapter extends BaseRecycleAdapter {
    private Context context;
    private List<BookCabModel> list;

    public RunningBookingAdapter(Context context, List<BookCabModel> data) {
        isForDesign = false;
        this.context = context;
        this.list = data;
    }

    @Override
    public BaseViewHolder getViewHolder () {
        return new ViewHolder(inflateLayout(R.layout.item_running_booking));
    }

    @Override
    public int getDataCount () {
        return list == null ? 0 : list.size();
    }

    private class ViewHolder extends BaseViewHolder {
        private TextView tv_booking_id;
        private TextView tv_start_day_time;
        private TextView tv_running_booking_time_title;
        private TextView tv_pickup_address;
        private TextView tv_drop_address;
        private ImageView iv_delete;

        public ViewHolder (View itemView) {
            super(itemView);
            tv_booking_id = itemView.findViewById(R.id.tv_booking_id);
            tv_start_day_time = itemView.findViewById(R.id.tv_start_day_time);
            tv_running_booking_time_title = itemView.findViewById(R.id.tv_running_booking_time_title);
            tv_pickup_address = itemView.findViewById(R.id.tv_pickup_address);
            tv_drop_address = itemView.findViewById(R.id.tv_drop_address);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }

        @Override
        public void setData (int position) {
            iv_delete.setTag(position);
            iv_delete.setOnClickListener(this);
            BookCabModel bookCabModel = list.get(position);
            if (bookCabModel == null) return;
            tv_booking_id.setText("Booking ID - " + bookCabModel.getBooking_id());
            if (bookCabModel.getStatus() == 0) {
                iv_delete.setVisibility(View.VISIBLE);
                if (bookCabModel.isScheduleBooking()) {
                    tv_running_booking_time_title.setText(getContext().getResources().getString(R.string.schedule_at));
                } else {
                    tv_running_booking_time_title.setText(getContext().getResources().getString(R.string.booked_at));
                }
                tv_start_day_time.setText(bookCabModel.getFormattedBookingTime(2));
            } else if (bookCabModel.getStatus() < 4) {
                iv_delete.setVisibility(View.GONE);
                if (bookCabModel.isScheduleBooking()) {
                    tv_running_booking_time_title.setText(getContext().getResources().getString(R.string.schedule_at));
                } else {
                    tv_running_booking_time_title.setText(getContext().getResources().getString(R.string.booked_at));
                }
                tv_start_day_time.setText(bookCabModel.getFormattedBookingTime(2));
            }  else {
                iv_delete.setVisibility(View.GONE);
                tv_running_booking_time_title.setText(getContext().getResources().getString(R.string.start_time));
                tv_start_day_time.setText(bookCabModel.getFormattedBookingStartTime(2));
            }
            tv_pickup_address.setText(bookCabModel.getPickup_address());
            tv_drop_address.setText(bookCabModel.getDrop_address());

        }

        @Override
        public void onClick (View v) {
            performItemClick((Integer) v.getTag(), v);
        }
    }
}
