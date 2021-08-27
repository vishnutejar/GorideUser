package com.quickzetuser.ui.main.dialog.cancelBooking;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.medy.retrofitwrapper.WebRequest;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseDialogFragment;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.request_model.CancelTripRequestModel;
import com.quickzetuser.model.web_response.BookCabResponseModel;
import com.quickzetuser.ui.main.dialog.cancelBooking.adapter.CancelBookingAdapter;
import com.utilities.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sunil kumar yadav on 15/3/18.
 */

public class CancelBookingDialog extends AppBaseDialogFragment {

    String message;
    String reason = "";
    ConfirmationDialogListener confirmationDialogListener;
    private RecyclerView recycler_view;
    private CancelBookingAdapter adapter;
    private TextView tv_do_not_cancel;
    private TextView tv_cancel_ride;
    private BookCabModel bookCabModel;

    public static CancelBookingDialog getNewInstance (String message) {
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        CancelBookingDialog confirmationDialog = new CancelBookingDialog();
        confirmationDialog.setArguments(bundle);
        return confirmationDialog;
    }

    public void setConfirmationDialogListener (ConfirmationDialogListener confirmationDialogListener) {
        this.confirmationDialogListener = confirmationDialogListener;
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.dialog_cancel_booking;
    }

    @Override
    public void initializeComponent () {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            this.message = bundle.getString("message", "");
        }

        recycler_view = getView().findViewById(R.id.recycler_view);
        tv_do_not_cancel = getView().findViewById(R.id.tv_do_not_cancel);
        tv_cancel_ride = getView().findViewById(R.id.tv_cancel_ride);


        tv_do_not_cancel.setOnClickListener(this);
        tv_cancel_ride.setOnClickListener(this);
        initializeRecyclerView();
    }

    @Override
    public int getFragmentContainerResourceId () {
        return -1;
    }


    private void initializeRecyclerView () {
        final List<String> data = getData();
        adapter = new CancelBookingAdapter(getActivity(), data);
        recycler_view.setLayoutManager(getVerticalLinearLayoutManager());
        recycler_view.setAdapter(adapter);

        ItemClickSupport.addTo(recycler_view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked (RecyclerView recyclerView, int position, View v) {
                reason = data.get(position);
                adapter.setIsSelect(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_ride:
                cancelTrip();
                break;
            case R.id.tv_do_not_cancel:
                this.dismiss();
                break;
        }
    }

    public List<String> getData () {
        List<String> list = new ArrayList<>();
        if (bookCabModel.getStatus() > 0) {
            list.add("Unable to contact driver");
            list.add("Driver denied duty");
        }
        list.add("I used the wrong payment method");
        list.add("Changed my mind");
        list.add("My reason is not listed");
        return list;
    }

    private void cancelTrip () {
        if (!TextUtils.isEmpty(reason)) {
            try {
                CancelTripRequestModel cancelTripRequestModel = new CancelTripRequestModel();
                cancelTripRequestModel.booking_id = bookCabModel.getBooking_id();
                cancelTripRequestModel.rider_lat = getCurrentLatLng().latitude;
                cancelTripRequestModel.rider_lng = getCurrentLatLng().longitude;
                cancelTripRequestModel.reason = reason;
                displayProgressBar(false);
                getWebRequestHelper().cancelTrip(cancelTripRequestModel, this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            showErrorMessage("Please select valid reason");
        }

    }

    @Override
    public void setupDialog (Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog view
        dialog.setContentView(layout);
        //setup dialog window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        wlmp.windowAnimations = R.style.BadeDialogStyle;
        wlmp.gravity = Gravity.CENTER;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setTitle(null);
        setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    public void setBookCabModel (BookCabModel bookCabModel) {
        this.bookCabModel = bookCabModel;
    }

    @Override
    public void onWebRequestCall (WebRequest webRequest) {
        try {
            getMainActivity().onWebRequestCall(webRequest);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebRequestResponse (WebRequest webRequest) {

        dismissProgressBar();

        super.onWebRequestResponse(webRequest);

        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_CANCEL_TRIP:
                    handleCancelTripResponse(webRequest);
                    break;
            }
        } else {
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                webRequest.showInvalidResponse(msg);
            }
        }
    }

    private void handleCancelTripResponse (WebRequest webRequest) {
        BookCabResponseModel responseModel = webRequest.getResponsePojo(BookCabResponseModel.class);
        if (responseModel == null) return;
        BookCabModel data = responseModel.getData();
        if (data != null) {
            this.confirmationDialogListener.onClickConfirm(this, data);
        }
    }

    public interface ConfirmationDialogListener {
        void onClickConfirm (DialogFragment dialogFragment, BookCabModel data);
    }
}
