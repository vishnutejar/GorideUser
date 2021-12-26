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
import com.goride.user.R;
import com.quickzetuser.model.FavouriteModel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Sunil kumar Yadav
 * @Since 2/7/18
 */
public class RecyclerViewFavoriteAdapter extends BaseRecycleAdapter
        implements Filterable, TextWatcher {

    private EditText editText;
    private List<FavouriteModel> mResultList;
    private List<FavouriteModel> mSearchResultList = new ArrayList<>();
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
            List<FavouriteModel> data =
                    (List<FavouriteModel>) results.values;
            mSearchResultList.clear();
            if (data != null) {
                mSearchResultList.addAll(data);
            }
            RecyclerViewFavoriteAdapter.this.notifyDataSetChanged();
        }
    };

    public RecyclerViewFavoriteAdapter(List<FavouriteModel> mResultList, EditText et_address) {
        isForDesign = false;
        this.mResultList = mResultList;
        this.editText = et_address;
        this.editText.addTextChangedListener(this);
        mSearchResultList.addAll(mResultList);
    }

    @Override
    public BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.item_favorite_address));
    }

    @Override
    public int getDataCount() {
        return mSearchResultList == null ? 0 : mSearchResultList.size();
    }

    public FavouriteModel getItem(int position) {
        return (mSearchResultList == null || (mSearchResultList.size() - 1 < position)) ?
                null : mSearchResultList.get(position);
    }

    public void deleteFavourite(FavouriteModel favouriteModel) {
        int index = mResultList.indexOf(favouriteModel);
        if (index != -1) ;
        mResultList.remove(index);
        index = mSearchResultList.indexOf(favouriteModel);
        if (index != -1) ;
        mSearchResultList.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, mSearchResultList.size());
    }

    public void addFavourite(FavouriteModel favouriteModel) {
        int addedIndex = 0;
        if (favouriteModel.isHomeType()) {
            mResultList.add(addedIndex, favouriteModel);
            mSearchResultList.add(addedIndex, favouriteModel);
        } else if (favouriteModel.isWorkType()) {
            if (mResultList.indexOf(new FavouriteModel("Home")) != -1) {
                addedIndex = 1;
            }
            mResultList.add(addedIndex, favouriteModel);
            addedIndex = 0;
            if (mSearchResultList.indexOf(new FavouriteModel("Home")) != -1) {
                addedIndex = 1;
            }
            mSearchResultList.add(addedIndex, favouriteModel);
        } else {
            mResultList.add(favouriteModel);
            mSearchResultList.add(favouriteModel);
            addedIndex = mSearchResultList.size() - 1;
        }
        notifyItemInserted(addedIndex);
        notifyItemRangeChanged(addedIndex, mSearchResultList.size());
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private List getPredictions(CharSequence constraint) {
        String charString = constraint.toString();
        if (charString.isEmpty()) {
            return mResultList;
        } else {
            List<FavouriteModel> modelList = new ArrayList<>();
            for (FavouriteModel row : mResultList) {
                String description = row.getDetail().getDescription();
                if (description.toLowerCase().contains(charString.toLowerCase())) {
                    modelList.add(row);
                }
            }
            return modelList;
        }

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

    private class ViewHolder extends BaseViewHolder {
        private ImageView iv_address_icon;
        private TextView tv_header;
        private TextView tv_sub_header;
        private ImageView iv_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_address_icon = itemView.findViewById(R.id.iv_address_icon);
            tv_header = itemView.findViewById(R.id.tv_header);
            tv_sub_header = itemView.findViewById(R.id.tv_sub_header);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }

        @Override
        public void setData(int position) {
            if (mSearchResultList == null) return;
            FavouriteModel favouriteModel = getItem(position);
            if (favouriteModel == null) return;
            LocationModelFull.LocationModel locationModel = favouriteModel.getDetail();
            if (locationModel == null) return;
            iv_delete.setTag(position);
            iv_delete.setOnClickListener(this);
            if (favouriteModel.getType().equalsIgnoreCase("Home")) {
                iv_address_icon.setImageResource(R.mipmap.ic_home);
            } else if (favouriteModel.getType().equalsIgnoreCase("Work")) {
                iv_address_icon.setImageResource(R.mipmap.ic_work);
            } else {
                iv_address_icon.setImageResource(R.mipmap.ic_favorite);
            }

            tv_header.setText(favouriteModel.getType());
            tv_sub_header.setText(locationModel.getDescription());
        }

        @Override
        public void onClick(View v) {
            performItemClick((Integer) v.getTag(), v);
        }
    }
}
