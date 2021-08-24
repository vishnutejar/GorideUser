package com.rider.ui.main.dialog.cancelBooking.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.rider.R;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 6/3/18.
 */

public class CancelBookingAdapter extends BaseRecycleAdapter {
    private Context context;
    private  List<String> list;
    private int isSelect = -1;

    public int getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(int isSelect) {
        this.isSelect = isSelect;
    }

    public CancelBookingAdapter(Context context, List<String> data) {
        isForDesign = false;
        this.context = context;
        this.list = data;
    }

    @Override
    public BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.item_cancel_booking));
    }

    @Override
    public int getDataCount() {
        return list==null?0:list.size();
    }

   
    private class ViewHolder extends BaseViewHolder {
        private TextView tv_text;
        private View v_selected;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
            v_selected = itemView.findViewById(R.id.v_selected);
        }

        @Override
        public void setData(int position) {
            String reason = list.get(position);
            if (reason==null && reason.isEmpty())return;
            if (isSelect==position){
                v_selected.setVisibility(View.VISIBLE);
            }else {
                v_selected.setVisibility(View.GONE);
            }
            tv_text.setText(reason);
        }

    }
}
