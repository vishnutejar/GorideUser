package com.quickzetuser.ui.main.dialog.tollsdetail.adapter;

import android.view.View;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.quickzetuser.R;
import com.quickzetuser.model.TollModel;

import java.util.List;


/**
 * Created by ubuntu on 10/4/18.
 */

public class TollsAdapter extends BaseRecycleAdapter {

    public String currency_symbol = "";

    private List<TollModel> tollsList;

    public TollsAdapter (List<TollModel> tollsList) {
        isForDesign = false;
        this.tollsList = tollsList;
    }

    @Override
    public BaseViewHolder getViewHolder () {
        return new ViewHolder(inflateLayout(R.layout.item_tolls));
    }

    @Override
    public int getDataCount () {
        if (tollsList != null) return tollsList.size();
        return 0;
    }

    private class ViewHolder extends BaseViewHolder {

        private TextView tv_toll_name;
        private TextView tv_toll_price;

        public ViewHolder (View itemView) {
            super(itemView);

            tv_toll_name = itemView.findViewById(R.id.tv_toll_name);
            tv_toll_price = itemView.findViewById(R.id.tv_toll_price);
        }

        @Override
        public void setData (int position) {
            currency_symbol = getContext().getResources().getString(R.string.rupee_symbol) + " ";

            TollModel tollModel = tollsList.get(position);
            if (tollModel != null) {
                tv_toll_name.setText(tollModel.getToll_name());
                tv_toll_price.setText(currency_symbol + tollModel.getValue());
            }
        }
    }

}
