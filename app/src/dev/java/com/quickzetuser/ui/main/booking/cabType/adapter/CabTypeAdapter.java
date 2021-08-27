package com.quickzetuser.ui.main.booking.cabType.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.models.DeviceScreenModel;
import com.quickzetuser.R;
import com.quickzetuser.model.VehicleTypeModel;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Sunil kumar yadav on 6/3/18.
 */

public class CabTypeAdapter extends BaseRecycleAdapter {

    VehicleTypeModel selectedVehicleModel;
    private Context context;
    private List<VehicleTypeModel> list;

    public CabTypeAdapter(Context context, List<VehicleTypeModel> list) {
        isForDesign = false;
        this.context = context;
        this.list = list;
    }

    public VehicleTypeModel getSelectedVehicleModel() {
        return selectedVehicleModel;
    }

    public void setSelectedVehicleModel(int position) {
        if (position == -1) {
            this.selectedVehicleModel = null;
        } else {
            this.selectedVehicleModel = list.get(position);
        }
    }

    @Override
    public BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.item_cab_type));
    }

    @Override
    public int getDataCount() {
        return list == null ? 0 : list.size();
    }


    private class ViewHolder extends BaseViewHolder {
        private LinearLayout ll_layout;
        private ImageView iv_vehicle_type;
        private TextView tv_vehicle_name;
        private ImageView ivSelect;
        private TextView tv_available_time;

        public ViewHolder(View itemView) {
            super(itemView);
            ll_layout = itemView.findViewById(R.id.ll_layout);
            iv_vehicle_type = itemView.findViewById(R.id.iv_vehicle_type);
            tv_available_time = itemView.findViewById(R.id.tv_available_time);
            tv_vehicle_name = itemView.findViewById(R.id.tv_vehicle_name);
            ivSelect = itemView.findViewById(R.id.iv_select);

            int width = DeviceScreenModel.getInstance().getWidth(0.33f);
            ll_layout.setLayoutParams(DeviceScreenModel.getInstance()
                    .getLinearLayoutParams(ll_layout, width));
        }

        @Override
        public void setData(int position) {
            VehicleTypeModel vehicleTypeModel = list.get(position);
            if (vehicleTypeModel == null) return;
            if (vehicleTypeModel.isValidString(vehicleTypeModel.getAvailableDriverTime())) {
                tv_available_time.setText(vehicleTypeModel.getAvailableDriverTime());
                if (tv_available_time.getVisibility() != View.VISIBLE)
                    tv_available_time.setVisibility(View.VISIBLE);
            } else {
                if (tv_available_time.getVisibility() != View.GONE)
                    tv_available_time.setVisibility(View.GONE);
            }
            if (selectedVehicleModel != null) {
                if (selectedVehicleModel.equals(vehicleTypeModel)) {
                    ivSelect.setVisibility(View.VISIBLE);
                    Picasso.get().load(vehicleTypeModel.getImage_selected())
                            .placeholder(R.mipmap.ic_riksya)
                            .into(iv_vehicle_type);
                } else {
                    ivSelect.setVisibility(View.INVISIBLE);
                    Picasso.get().load(vehicleTypeModel.getImage_unselected())
                            .placeholder(R.mipmap.ic_riksya)
                            .into(iv_vehicle_type);
                }
            } else {
                ivSelect.setVisibility(View.INVISIBLE);
                Picasso.get().load(vehicleTypeModel.getImage_unselected())
                        .placeholder(R.mipmap.ic_riksya)
                        .into(iv_vehicle_type);
            }

            tv_vehicle_name.setText(vehicleTypeModel.getName());
        }
    }
}
