package com.rider.ui.main.dialog.reminingPayment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.RecyclerView;

import com.medy.retrofitwrapper.WebRequest;
import com.rider.R;
import com.rider.appBase.AppBaseDialogFragment;
import com.rider.model.BookCabModel;
import com.rider.model.request_model.RemainingPaymentRequestModel;
import com.rider.model.web_response.BookCabResponseModel;
import com.rider.ui.main.MainActivity;
import com.rider.ui.main.sidemenu.myRide.adapter.MyRideAdapter;
import com.rider.ui.main.sidemenu.myRide.trip_details.TripDetailsFragment;
import com.utilities.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sunil kumar yadav on 15/3/18.
 */

@SuppressLint("ValidFragment")
public class RemainingPaymentDialog extends AppBaseDialogFragment
        implements TripDetailsFragment.PayPaymentListener {

    RecyclerView recycler_view;
    MyRideAdapter adapter;
    List<BookCabModel> list = new ArrayList<>();
    MainActivity _activity;
    private BookCabModel bookCabModel;


    public RemainingPaymentDialog(MainActivity mainActivity) {
        this._activity = mainActivity;
    }


    @Override
    public int getLayoutResourceId() {
        return R.layout.dialog_remaining_payment;
    }

    @Override
    public void initializeComponent() {
        recycler_view = getView().findViewById(R.id.recycler_view);
        initializeRecyclerView();

    }

    @Override
    public int getFragmentContainerResourceId() {
        return -1;
    }


    private void initializeRecyclerView() {
        list.clear();
        list.add(bookCabModel);
        adapter = new MyRideAdapter(getActivity(), list);
        recycler_view.setLayoutManager(getVerticalLinearLayoutManager());
        recycler_view.setAdapter(adapter);

        ItemClickSupport.addTo(recycler_view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                BookCabModel bookCabModel = list.get(position);
                if (bookCabModel == null) return;
                if (v.getId() == R.id.tv_payment_pay) {
                    payRemainingPayment(bookCabModel);
                } else {
//                    addTripDetailsFragment(bookCabModel);
                }
            }
        });
    }

    private void payRemainingPayment(BookCabModel bookCabModel) {
        displayProgressBar(false);
        RemainingPaymentRequestModel requestModel = new RemainingPaymentRequestModel();
        requestModel.booking_id = bookCabModel.getBooking_id();
        getWebRequestHelper().pay_remaining_payment(requestModel, bookCabModel, this);
    }

    private void addTripDetailsFragment(BookCabModel bookCabModel) {
        TripDetailsFragment fragment = new TripDetailsFragment();
        fragment.setBookCabModel(bookCabModel);
        fragment.setPaymentListener(this);
        int enterAnimation = R.anim.right_in;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.right_out;
        try {
            getMainActivity().changeFragment(fragment, true, false, 0,
                    enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
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
        wlmp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
    }

    public void setBookCabModel(BookCabModel bookCabModel) {
        this.bookCabModel = bookCabModel;
    }

    @Override
    public void onWebRequestCall(WebRequest webRequest) {
        _activity.onWebRequestCall(webRequest);
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);
        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_PAY_REMAINING_PAYMENT:
                    handlePayRemainingPayment(webRequest);
                    break;
            }
        } else {
            String error = webRequest.getErrorMessageFromResponse();
            if (isValidString(error)) {
                showErrorMessage(error);
            }
        }
    }

    private void handlePayRemainingPayment(WebRequest webRequest) {
        BookCabResponseModel responsePojo = webRequest.getResponsePojo(BookCabResponseModel.class);
        if (responsePojo == null) return;
        BookCabModel data = responsePojo.getData();
        if (data == null) return;
        int index = list.indexOf(data);
        if (index != -1) {
            list.remove(data);
            adapter.notifyItemRemoved(index);
            adapter.notifyItemRangeChanged(index, list.size() - 1);
            dismiss();
            showCustomToast(responsePojo.getMessage());
        }
    }


    @Override
    public void onPaymentSuccess(BookCabModel data) {
        if (data == null) return;
        int index = list.indexOf(data);
        if (index != -1) {
            list.remove(data);
            if (list.size() == 0)
                dismiss();
        }
    }
}
