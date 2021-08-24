package com.rider.ui.main.dialog.distancecharges.adapter;

import android.view.View;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.rider.R;
import com.rider.model.PerKmChargeModel;

import java.util.List;


/**
 * Created by ubuntu on 10/4/18.
 */

public class DistanceFaresAdapter extends BaseRecycleAdapter {

    public String currency_symbol = "";

    private List<PerKmChargeModel> per_km_charge;

    public DistanceFaresAdapter(List<PerKmChargeModel> per_km_charge) {
        isForDesign=false;
        this.per_km_charge = per_km_charge;

    }

    @Override
    public BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.item_tolls));
    }

    @Override
    public int getDataCount() {
        if (per_km_charge != null) return per_km_charge.size();
        return 0;
    }

    private class ViewHolder extends BaseViewHolder {

        private TextView tv_toll_name;
        private TextView tv_toll_price;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_toll_name = itemView.findViewById(R.id.tv_toll_name);
            tv_toll_price = itemView.findViewById(R.id.tv_toll_price);
        }

        @Override
        public void setData(int position) {
            currency_symbol = getContext().getResources().getString(R.string.rupee_symbol) + " ";

            PerKmChargeModel chargeModel = per_km_charge.get(position);
            if (chargeModel != null) {
                tv_toll_name.setText(chargeModel.getDistanceText());
                tv_toll_price.setText(currency_symbol + chargeModel.getAmountText());
            }
        }
    }

}
