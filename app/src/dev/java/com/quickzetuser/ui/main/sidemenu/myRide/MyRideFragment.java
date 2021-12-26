package com.quickzetuser.ui.main.sidemenu.myRide;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.medy.retrofitwrapper.WebRequest;
import com.goride.user.R;
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
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class MyRideFragment extends AppBaseFragment
        implements TripDetailsFragment.PayPaymentListener {

    SwipeRefreshLayout swipe_layout;
    RecyclerView recycler_view;
    MyRideAdapter adapter;
    List<BookCabModel> list = new ArrayList<>();
    boolean loadingNextData = false;
    private TextView tv_noRideFound;
    private int totalPages = 1000;
    private int currentPage = 0;

    @Override
    public int getLayoutResourceId () {
        return R.layout.fragment_my_ride;
    }

    @Override
    public void initializeComponent () {
        swipe_layout = getView().findViewById(R.id.swipe_layout);
        recycler_view = getView().findViewById(R.id.recycler_view);
        tv_noRideFound = getView().findViewById(R.id.tv_noRideFound);
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh () {
                swipe_layout.setRefreshing(true);
                currentPage = 0;
                getBookingHistory();
            }
        });


        initializeRecyclerView();
        getBookingHistory();
    }

    private void initializeRecyclerView () {
        adapter = new MyRideAdapter(getActivity(), list);
        recycler_view.setLayoutManager(getVerticalLinearLayoutManager());
        recycler_view.setAdapter(adapter);

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled (RecyclerView recyclerView,
                                    int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = ((LinearLayoutManager) recycler_view.getLayoutManager()).getItemCount();
                int lastVisibleItem = ((LinearLayoutManager) recycler_view.getLayoutManager()).findLastVisibleItemPosition();
                if (!loadingNextData
                        && totalItemCount <= (lastVisibleItem + 3)) {
                    getBookingHistory();
                }
            }
        });
        ItemClickSupport.addTo(recycler_view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked (RecyclerView recyclerView, int position, View v) {
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

    private void payRemainingPayment (BookCabModel bookCabModel) {
        displayProgressBar(false);
        RemainingPaymentRequestModel requestModel = new RemainingPaymentRequestModel();
        requestModel.booking_id = bookCabModel.getBooking_id();
        getWebRequestHelper().pay_remaining_payment(requestModel, bookCabModel, this);
    }

    private void addTripDetailsFragment (BookCabModel bookCabModel) {
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


    public void getBookingHistory () {
        if (this.currentPage == 0) {
            this.currentPage = 1;
            totalPages=1000;
            getHistory();
            return;
        }
        if (this.totalPages > this.currentPage) {
            this.currentPage = this.currentPage + 1;
            getHistory();
        }
    }

    private void getHistory () {

        try {
            setLoadingNexData(true);
            getWebRequestHelper().getBookingHistory(currentPage, this);
        } catch (Exception ignore) {
            currentPage--;
            setLoadingNexData(false);
        }
    }

    public void setLoadingNexData (boolean isLoading) {
        this.loadingNextData = isLoading;
        if (swipe_layout.isRefreshing()) {
            swipe_layout.setRefreshing(isLoading);
        } else {
            adapter.setLoadMore(loadingNextData);
        }
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
        setLoadingNexData(false);
        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_BOOKING_HISTORY:
                    BookingHistoryResponseModel responseModel = webRequest.getResponsePojo(BookingHistoryResponseModel.class);
                    if (responseModel == null || responseModel.getData() == null) {
                        currentPage--;
                        return;
                    }
                    if (responseModel.getData().size() == 0)
                        totalPages = currentPage;

                    if (currentPage == 1) {
                        updateData(responseModel.getData());
                    } else {
                        addDataToList(responseModel.getData());
                    }
                    break;

                case ID_PAY_REMAINING_PAYMENT:
                    handlePayRemainingPayment(webRequest);
                    break;
            }
        } else {
            currentPage--;
            String error = webRequest.getErrorMessageFromResponse();
            if (isValidString(error)) {
                showErrorMessage(error);
            }
        }
    }

    private void handlePayRemainingPayment (WebRequest webRequest) {
        BookCabResponseModel responsePojo = webRequest.getResponsePojo(BookCabResponseModel.class);
        if (responsePojo == null) return;
        BookCabModel data = responsePojo.getData();
        if (data == null) return;
        int index = list.indexOf(data);
        if (index != -1) {
            list.set(index, data);
            adapter.notifyItemChanged(index);
            showCustomToast(responsePojo.getMessage());
        }
    }

    private void updateData (List<BookCabModel> levelList) {

        this.list.clear();
        if (levelList != null) {
            this.list.addAll(levelList);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            updateNoDataView();
        }
    }

    private void addDataToList (List<BookCabModel> levelList) {

        int listPreviousSize = this.list.size();
        if (levelList != null) {
            this.list.addAll(levelList);
        }
        int listCurrentSize = this.list.size();
        if (adapter != null && (listPreviousSize < listCurrentSize)) {
            adapter.notifyItemRangeInserted(listPreviousSize, listCurrentSize);
            updateNoDataView();
        }

    }

    private void updateNoDataView () {
        if (this.list.size() == 0) {
            if (tv_noRideFound.getVisibility() != View.VISIBLE)
                tv_noRideFound.setVisibility(View.VISIBLE);
        } else {
            if (tv_noRideFound.getVisibility() != View.GONE)
                tv_noRideFound.setVisibility(View.GONE);
        }
    }


    @Override
    public void onPaymentSuccess (BookCabModel data) {
        if (data == null) return;
        int index = list.indexOf(data);
        if (index != -1) {
            list.set(index, data);
            if (adapter != null)
                adapter.notifyItemChanged(index);
        }
    }
}
