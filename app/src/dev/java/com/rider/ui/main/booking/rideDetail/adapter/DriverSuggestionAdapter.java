package com.rider.ui.main.booking.rideDetail.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.rider.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sunil kumar on 10/5/18.
 */
public class DriverSuggestionAdapter extends BaseRecycleAdapter {

    private Context context;
    private List<String> list;
    private Map<Integer, String>map;

    public Map<Integer, String> getMap() {
        return map;
    }

    public DriverSuggestionAdapter(Context context, List<String> data) {
        isForDesign = false;
        this.context = context;
        this.list = data;
        map = new HashMap<>();
    }

    @Override
    public BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.item_driver_suggestion));
    }

    @Override
    public int getDataCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends BaseViewHolder {
        private CheckBox check_box;
        private TextView tv_text;

        public ViewHolder(View itemView) {
            super(itemView);
            check_box = itemView.findViewById(R.id.check_box);
            tv_text = itemView.findViewById(R.id.tv_text);
        }

        @Override
        public void setData(int position) {
            if (list == null) return;
            String s = list.get(position);
            tv_text.setText(s);
            if (map.containsKey(position)){
                check_box.setChecked(true);
            }else {
                check_box.setChecked(false);
            }
        }
    }
}
