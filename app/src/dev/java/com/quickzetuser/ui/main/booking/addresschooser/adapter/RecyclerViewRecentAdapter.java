package com.quickzetuser.ui.main.booking.addresschooser.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.addressfetching.LocationModelFull;
import com.base.BaseRecycleAdapter;
import com.quickzetuser.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sunil kumar Yadav
 * @Since 2/7/18
 */
public class RecyclerViewRecentAdapter extends BaseRecycleAdapter
        implements Filterable, TextWatcher {

    private List<LocationModelFull.LocationModel> mResultList;
    private EditText editText;
    private List<LocationModelFull.LocationModel> mSearchResultList = new ArrayList<>();
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null) {
                List mResultList = getPredictions(constraint);
                if (mResultList != null) {
                    results.values = mResultList;
                    results.count = mResultList.size();
                }
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<LocationModelFull.LocationModel> data =
                    (List<LocationModelFull.LocationModel>) results.values;
            mSearchResultList.clear();
            if (data != null) {
                mSearchResultList.addAll(data);
            }
            RecyclerViewRecentAdapter.this.notifyDataSetChanged();
        }
    };

    public RecyclerViewRecentAdapter(List<LocationModelFull.LocationModel> mResultList, EditText et_address) {
        isForDesign = false;
        this.mResultList = mResultList;
        this.editText = et_address;
        this.editText.addTextChangedListener(this);
        mSearchResultList.addAll(mResultList);
    }


    private List getPredictions(CharSequence constraint) {
        String charString = constraint.toString();
        if (charString.isEmpty()) {
            return mResultList;
        } else {
            List<LocationModelFull.LocationModel> filteredList = new ArrayList<>();
            for (LocationModelFull.LocationModel row : mResultList) {
                String description = row.getDescription();
                if (description.toLowerCase().contains(charString.toLowerCase())) {
                    filteredList.add(row);
                }
            }
            return filteredList;
        }
    }

    @Override
    public BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.item_recent_address));
    }

    @Override
    public int getDataCount() {
        return mSearchResultList == null ? 0 : mSearchResultList.size();
    }

    public LocationModelFull.LocationModel getItem(int position) {
        return (mSearchResultList == null || (mSearchResultList.size() - 1 < position)) ?
                null : mSearchResultList.get(position);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        getFilter().filter(s);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    private class ViewHolder extends BaseViewHolder {
        private ImageView iv_recent;
        private TextView tv_header;
        private TextView tv_sub_header;
        private ImageView iv_favorite;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_recent = itemView.findViewById(R.id.iv_recent);
            tv_header = itemView.findViewById(R.id.tv_header);
            tv_sub_header = itemView.findViewById(R.id.tv_sub_header);
            iv_favorite = itemView.findViewById(R.id.iv_favorite);
        }

        @Override
        public void setData(int position) {
            if (mSearchResultList == null) return;
            LocationModelFull.LocationModel locationModel = getItem(position);
            if (locationModel == null) return;
            iv_favorite.setTag(position);
            iv_favorite.setOnClickListener(this);
            tv_header.setText(locationModel.getPrimaryText());
            tv_sub_header.setText(locationModel.getSecondaryText());
        }

        @Override
        public void onClick(View v) {
            performItemClick((Integer) v.getTag(), v);
        }
    }
}
