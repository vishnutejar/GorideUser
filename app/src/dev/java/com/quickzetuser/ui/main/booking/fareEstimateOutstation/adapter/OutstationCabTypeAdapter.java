package com.quickzetuser.ui.main.booking.fareEstimateOutstation.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.quickzetuser.R;
import com.quickzetuser.model.OutstationFareModel;
import com.quickzetuser.model.VehicleTypeModel;
import com.squareup.picasso.Picasso;

import java.util.List;


public class OutstationCabTypeAdapter extends BaseRecycleAdapter {

    private List<OutstationFareModel> list;

    public OutstationCabTypeAdapter(List<OutstationFareModel> list) {
        isForDesign = false;
        this.list = list;
    }

    public void updateData(List<OutstationFareModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder getViewHolder() {

        return new ViewHolder(inflateLayout(R.layout.item_outstation_cab_type));
    }

    @Override
    public int getDataCount() {
        return list == null ? 0 : list.size();
    }

    public boolean isOnewaySelected() {
        return true;
    }

    class ViewHolder extends BaseViewHolder {

        private LinearLayout ll_view;
        private TextView tv_name;
        private TextView tv_fare;
        private ImageView iv_texi_image;

        public ViewHolder(View itemView) {
            super(itemView);
            ll_view = itemView.findViewById(R.id.ll_view);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_texi_image = itemView.findViewById(R.id.iv_texi_image);
            tv_fare = itemView.findViewById(R.id.tv_fare);
        }

        @Override
        public void setData(int position) {
            ll_view.setTag(position);
            ll_view.setOnClickListener(this);
            OutstationFareModel outstationFareModel = list.get(position);
            VehicleTypeModel vehicleTypeModel = outstationFareModel.getVehicle();

            tv_name.setText(vehicleTypeModel.getName());

            Picasso.get()
                    .load(vehicleTypeModel.getImage_selected())
                    .placeholder(R.mipmap.ic_riksya)
                    .error(R.mipmap.ic_riksya)
                    .into(iv_texi_image);
            if (isOnewaySelected()) {
                OutstationFareModel.FareData onewayfare = outstationFareModel.getOnewayfare();
                if (onewayfare != null) {
                    tv_fare.setText(getContext().getResources().getString(R.string.rupee_symbol) + onewayfare.getPayble_fare());
                } else {
                    tv_fare.setText("");
                }
            } else {
                OutstationFareModel.FareData roundtripfare = outstationFareModel.getRoundtripfare();
                if (roundtripfare != null) {
                    tv_fare.setText(getContext().getResources().getString(R.string.rupee_symbol) + roundtripfare.getPayble_fare());
                    /*    tv_fare.setText(getContext().getResources().getString(R.string.rupee_symbol) + roundtripfare.getPer_km_charge()
                            .get(0).getAmountText())*/
                } else {
                    tv_fare.setText("");
                }
            }

        }

        @Override
        public void onClick(View v) {
            performItemClick((Integer) v.getTag(), v);
        }
    }
}
