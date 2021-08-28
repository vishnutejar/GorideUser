package com.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.utilities.ItemClickSupport;


/**
 * @author Manish Kumar
 * @since 18/8/17
 */


public abstract class  BaseRecycleAdapter extends RecyclerView.Adapter<BaseRecycleAdapter.BaseViewHolder> {


    public static final int VIEW_TYPE_LOAD_MORE = 404;
    public static final int VIEW_TYPE_DATA = 1;
    protected boolean isForDesign = true;
    LayoutInflater layoutInflater;
    Context context;
    RecyclerView recyclerView;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public Context getContext() {
        return context;
    }

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public View inflateLayout(int layoutId) {
        return getLayoutInflater().inflate(layoutId, null);
    }

    @Override
    public int getItemViewType(int position) {
        return getViewType(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        recyclerView = (RecyclerView) parent;
        context = parent.getContext();
        layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            return getViewHolder();
        }
        return getViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return isForDesign ? 6 : getDataCount();
    }

    public abstract BaseViewHolder getViewHolder();

    public BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public int getViewType(int position) {
        return super.getItemViewType(position);
    }

    public abstract int getDataCount();

    public boolean isValidString(String data) {
        return data != null && !data.trim().isEmpty();
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void setData(int position);

        public void performItemClick(int position, View view) {
            ItemClickSupport itemClickSupport = ItemClickSupport.getFrom(itemView);
            if (itemClickSupport != null) {
                itemClickSupport.getmOnItemClickListener()
                        .onItemClicked(itemClickSupport.getmRecyclerView(), position, view);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class LoadMoreViewHolder extends BaseViewHolder {

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void setData(int position) {


        }

    }
}
