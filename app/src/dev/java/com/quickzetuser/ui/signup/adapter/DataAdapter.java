package com.quickzetuser.ui.signup.adapter;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.BaseRecycleAdapter;
import com.quickzetuser.R;

import java.util.List;


/**
 * Created by ubuntu on 27/3/18.
 */

public class DataAdapter<T>  extends BaseRecycleAdapter {

    private static final String TAG = DataAdapter.class.getSimpleName();
    private List<T> dataList;

    public DataAdapter(List<T> data) {
        isForDesign = false;
        this.dataList = data;
    }

    @Override
    public BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.item_text_view));
    }

    @Override
    public int getDataCount() {
        return dataList==null?0:dataList.size();
    }


    private class ViewHolder extends BaseViewHolder {
        private TextView tv_name;
        private RelativeLayout ll_view;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            ll_view = itemView.findViewById(R.id.rl_view);

        }

        @Override
        public void setData(int position) {
            if (dataList==null)return;
            String s = dataList.get(position).toString();
            if (s!=null)
            tv_name.setText(s);
            ll_view.setTag(position);
            ll_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            performItemClick((Integer) ll_view.getTag(), v);
        }
    }
}