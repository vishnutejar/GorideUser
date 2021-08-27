package com.quickzetuser.ui.main.sidemenu.payment.remainingPayment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.medy.retrofitwrapper.WebRequest;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.request_model.RemainingPaymentRequestModel;
import com.quickzetuser.model.web_response.BookCabResponseModel;
import com.quickzetuser.model.web_response.BookingHistoryResponseModel;
import com.quickzetuser.ui.main.sidemenu.myRide.adapter.MyRideAdapter;
import com.quickzetuser.ui.main.sidemenu.myRide.trip_details.TripDetailsFragment;
import com.utilities.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sunil kumar Yadav
 * @Since 26/6/18
 */
public class PaymentRemainingFragment extends AppBaseFragment
        implements TripDetailsFragment.PayPaymentListener {

    RecyclerView recycler_view;
    MyRideAdapter adapter;
    List<BookCabModel> list = new ArrayList<>();
    private TextView tv_noRideFound;

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_payment_remaining;
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();

        recycler_view = getView().findViewById(R.id.recycler_view);
        tv_noRideFound = getView().findViewById(R.id.tv_noRideFound);

        initializeRecyclerView();
        get_remaining_payment();
    }

    private void initializeRecyclerView() {
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
                    addTripDetailsFragment(bookCabModel);
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

    private void get_remaining_payment() {
        displayProgressBar(false);
        getWebRequestHelper().get_remaining_payment(this);
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
    public void onWebRequestCall(WebRequest webRequest) {
        try {
            getMainActivity().onWebRequestCall(webRequest);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);
        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_GET_REMAINING_PAYMENT:
                    handleGetRemainingPayment(webRequest);
                    break;

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

    private void handleGetRemainingPayment(WebRequest webRequest) {
        BookingHistoryResponseModel responseModel = webRequest.getResponsePojo(BookingHistoryResponseModel.class);
        if (responseModel == null) return;
        List<BookCabModel> data = responseModel.getData();
        if (data != null && data.size() > 0) {
            list.clear();
            list.addAll(data);
            adapter.notifyDataSetChanged();
        } else {
            updateNoDataView();
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
            adapter.notifyItemRangeChanged(index, list.size()-1);
            showCustomToast(responsePojo.getMessage());
        }
    }

    private void updateNoDataView() {
        if (this.list.size() == 0) {
            if (tv_noRideFound.getVisibility() != View.VISIBLE)
                tv_noRideFound.setVisibility(View.VISIBLE);
        } else {
            if (tv_noRideFound.getVisibility() != View.GONE)
                tv_noRideFound.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPaymentSuccess(BookCabModel data) {
        if (data == null) return;
        int index = list.indexOf(data);
        if (index != -1) {
            list.remove(data);
            if (adapter != null) {
                adapter.notifyItemChanged(index);
                adapter.notifyItemRangeChanged(index, list.size() - 1);
            }
        }
    }
}
