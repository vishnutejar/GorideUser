package com.rider.ui.main.dialog.tollsdetail;

import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rider.R;
import com.rider.appBase.AppBaseDialogFragment;
import com.rider.model.TotalTollsModel;
import com.rider.ui.main.dialog.tollsdetail.adapter.TollsAdapter;

/**
 * Created by ubuntu on 10/4/18.
 */

public class TollsDetailDialog extends AppBaseDialogFragment {

    private static final String TAG = TollsDetailDialog.class.getSimpleName();
    private RecyclerView recycler_view;
    private TextView tv_ok;

    private TotalTollsModel totalTollsModel;
    private TollsAdapter taxiAdapter;

    private String title;

    public void setTitle(String title) {
        this.title = title;
    }
    public void setTotalTollsModel (TotalTollsModel totalTollsModel) {
        this.totalTollsModel = totalTollsModel;
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.dialog_tolls_detail;
    }

    @Override
    public void initializeComponent () {
        recycler_view = getView().findViewById(R.id.recycler_view);
        tv_ok = getView().findViewById(R.id.tv_ok);
        initializeRecyclerView();
        tv_ok.setOnClickListener(this);

    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                dismiss();
                break;

        }
    }

    private void initializeRecyclerView () {
        if (!isValidObject(totalTollsModel)) return;
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        taxiAdapter = new TollsAdapter(totalTollsModel.getTolls());
        recycler_view.setAdapter(taxiAdapter);
    }

    @Override
    public int getFragmentContainerResourceId () {
        return -1;
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
        wlmp.windowAnimations = R.style.BadeDialogStyle;
        wlmp.gravity = Gravity.CENTER;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }
}
