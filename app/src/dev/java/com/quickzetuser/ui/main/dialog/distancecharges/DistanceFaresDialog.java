package com.quickzetuser.ui.main.dialog.distancecharges;

import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseDialogFragment;
import com.quickzetuser.model.PerKmChargeModel;
import com.quickzetuser.ui.main.dialog.distancecharges.adapter.DistanceFaresAdapter;

import java.util.List;

/**
 * Created by ubuntu on 10/4/18.
 */

public class DistanceFaresDialog extends AppBaseDialogFragment {

    private RecyclerView recycler_view;
    private TextView tv_ok;
    private TextView tv_texi_brand_name;
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    private List<PerKmChargeModel> per_km_charge;

    private DistanceFaresAdapter taxiAdapter;

    public void setPer_km_charge(List<PerKmChargeModel> per_km_charge) {
        this.per_km_charge = per_km_charge;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_tolls_detail;
    }

    @Override
    public void initializeComponent() {
        recycler_view = getView().findViewById(R.id.recycler_view);
        tv_ok = getView().findViewById(R.id.tv_ok);
        tv_texi_brand_name = getView().findViewById(R.id.tv_texi_brand_name);
        tv_texi_brand_name.setText(isValidString(title) ? title : getString(R.string.app_name));
        initializeRecyclerView();
        tv_ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                dismiss();
                break;

        }
    }

    private void initializeRecyclerView() {
        if (!isValidObject(per_km_charge)) return;
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        taxiAdapter = new DistanceFaresAdapter(per_km_charge);
        recycler_view.setAdapter(taxiAdapter);

    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog_country view
        dialog.setContentView(layout);
        //setup dialog_country window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.windowAnimations = R.style.BadeDialogStyle;
        wlmp.gravity = Gravity.CENTER;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }
}
