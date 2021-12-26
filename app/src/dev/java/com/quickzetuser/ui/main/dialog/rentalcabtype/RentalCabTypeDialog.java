package com.quickzetuser.ui.main.dialog.rentalcabtype;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.distancematrix.DistanceMatrixHandler;
import com.distancematrix.DistanceMatrixModel;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseDialogFragment;
import com.quickzetuser.model.RentalFareModel;
import com.quickzetuser.ui.main.booking.fareEstimaterentel.adapter.RentalCabTypeAdapter;
import com.utilities.ItemClickSupport;

import java.util.List;


public class RentalCabTypeDialog extends AppBaseDialogFragment implements DistanceMatrixHandler.DistanceMatrixListener {

    RentalCabTypeAdapter adapter;
    RecyclerView recycler_view;
    TextView tv_title;
    private String title;
    List<RentalFareModel> dataList;
    OnItemSelectedListener onItemSelectedListener;

    public void setOnItemSelectedListener (OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public void setDataList (List<RentalFareModel> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.dialog_rental_cab_type;
    }

    @Override
    public int getFragmentContainerResourceId () {
        return -1;
    }

    @Override
    public void initializeComponent () {
        recycler_view = getView().findViewById(R.id.recycler_view);
        tv_title = getView().findViewById(R.id.tv_title);
        if (title != null)
            tv_title.setText(title);
        if (dataList != null && dataList.size() > 0)
            initializeRecyclerView();
    }


    private void initializeRecyclerView () {
        adapter = new RentalCabTypeAdapter(dataList);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view.setAdapter(adapter);
        ItemClickSupport.addTo(recycler_view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked (RecyclerView recyclerView, int position, View v) {
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelectedListener(position);
                    dismiss();
                }
            }
        });
    }

    @Override
    public void onDistanceMatrixRequestStart () {

    }

    @Override
    public void onDistanceMatrixRequestEnd (List<DistanceMatrixModel> rows) {
        if (isValidActivity() && isVisible() && adapter != null)
            adapter.notifyDataSetChanged();
    }

    public interface OnItemSelectedListener {
        void onItemSelectedListener(int position);
    }

    @Override
    public void setupDialog (Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog_country view
        dialog.setContentView(layout);
        //setup dialog_country window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        // wlmp.gravity = Gravity.LEFT | Gravity.TOP;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }
}
