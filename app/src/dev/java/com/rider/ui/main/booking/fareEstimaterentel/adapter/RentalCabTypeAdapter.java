package com.rider.ui.main.booking.fareEstimaterentel.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.rider.R;
import com.rider.model.RentalFareModel;
import com.rider.model.VehicleTypeModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RentalCabTypeAdapter extends BaseRecycleAdapter {

    private List<RentalFareModel> list;

    public RentalCabTypeAdapter (List<RentalFareModel> list) {
        isForDesign = false;
        this.list = list;
    }

    @Override
    public BaseViewHolder getViewHolder () {

        return new ViewHolder(inflateLayout(R.layout.item_rental_cab_type));
    }

    @Override
    public int getDataCount () {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends BaseViewHolder {

        private LinearLayout ll_view;
        private TextView tv_name;
        private TextView tv_time;
        private ImageView iv_texi_image;

        public ViewHolder (View itemView) {
            super(itemView);
            ll_view = itemView.findViewById(R.id.ll_view);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_texi_image = itemView.findViewById(R.id.iv_texi_image);
            tv_time = itemView.findViewById(R.id.tv_time);
        }

        @Override
        public void setData (int position) {
            VehicleTypeModel vehicleTypeModel = list.get(position).getVehicle();

            tv_name.setText(vehicleTypeModel.getName());
            if (vehicleTypeModel.isValidString(
                    vehicleTypeModel.getAvailableDriverTime())) {
                tv_time.setText(vehicleTypeModel.getAvailableDriverTime());
            } else {
                tv_time.setText("N/A");
            }
            Picasso.get()
                    .load(vehicleTypeModel.getImage_selected())
                    .placeholder(R.mipmap.ic_book_ride)
                    .error(R.drawable.noimage)
                    .into(iv_texi_image);
            ll_view.setTag(position);
            ll_view.setOnClickListener(this);
        }

        @Override
        public void onClick (View v) {
            performItemClick((Integer) v.getTag(), v);
        }
    }
}
