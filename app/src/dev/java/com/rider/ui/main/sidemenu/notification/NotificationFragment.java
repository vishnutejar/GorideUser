package com.rider.ui.main.sidemenu.notification;

import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.medy.retrofitwrapper.WebRequest;
import com.rider.R;
import com.rider.appBase.AppBaseFragment;
import com.rider.model.NotificationModel;
import com.rider.model.web_response.NotificationResponseModel;
import com.rider.ui.main.dialog.FullScreenProfileDialog;
import com.rider.ui.main.sidemenu.notification.adapter.NotificationAdapter;
import com.utilities.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunil kumar
 * 13/3/18.
 */

public class NotificationFragment extends AppBaseFragment {

    SwipeRefreshLayout swipe_layout;
    RecyclerView recycler_view;
    NotificationAdapter adapter;
    CardView cv_no_record_found;
    List<NotificationModel> list = new ArrayList<>();
    boolean loadingNextData = false;
    private int totalPages = 1000;
    private int currentPage = 0;

    @Override
    public int getLayoutResourceId () {
        return R.layout.fragment_notification;
    }

    @Override
    public void initializeComponent () {
        swipe_layout = getView().findViewById(R.id.swipe_layout);
        recycler_view = getView().findViewById(R.id.recycler_view);
        cv_no_record_found = getView().findViewById(R.id.cv_no_record_found);
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh () {
                swipe_layout.setRefreshing(true);
                currentPage = 0;
                callNextNotificationApi();
            }
        });
        initializeRecyclerView();
        callNextNotificationApi();
    }

    private void initializeRecyclerView () {
        adapter = new NotificationAdapter(getActivity(), list);
        recycler_view.setLayoutManager(getVerticalLinearLayoutManager());
        recycler_view.setAdapter(adapter);


        ItemClickSupport.addTo(recycler_view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked (RecyclerView recyclerView, int position, View v) {
                switch (v.getId()) {
                    case R.id.iv_image:
                        NotificationModel notificationModel = list.get(position);
                        displayFullScreenImage(notificationModel.getLarge_image());
                        break;

                }
            }
        });

        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled (RecyclerView recyclerView,
                                    int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = ((LinearLayoutManager) recycler_view.getLayoutManager()).getItemCount();
                int lastVisibleItem = ((LinearLayoutManager) recycler_view.getLayoutManager()).findLastVisibleItemPosition();
                if (!loadingNextData
                        && totalItemCount <= (lastVisibleItem + 3)) {
                    callNextNotificationApi();
                }
            }
        });

    }

    private void displayFullScreenImage (String imgPath) {
        FullScreenProfileDialog dialog = new FullScreenProfileDialog();
        if (!isValidString(imgPath)) return;

        dialog.setImagePath(imgPath);
        dialog.show(getFragmentManager(), dialog.getClass().getSimpleName());

    }

    public void callNextNotificationApi () {
        if (this.currentPage == 0) {
            this.currentPage = 1;
            totalPages = 1000;
            getNotification();
            return;
        }
        if (this.totalPages > this.currentPage) {
            this.currentPage = this.currentPage + 1;
            getNotification();
        }
    }

    private void getNotification () {

        try {
            setLoadingNexData(true);
            getMainActivity().getWebRequestHelper().getNotification(currentPage, this);
        } catch (Exception ignore) {
            currentPage--;
            setLoadingNexData(true);
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
        super.onWebRequestResponse(webRequest);
        setLoadingNexData(false);
        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_GET_NOTIFICATION:
                    NotificationResponseModel responseModel = webRequest.getResponsePojo(NotificationResponseModel.class);
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
            }
        } else {
            currentPage--;
            String errorMessageFromResponse = webRequest.getErrorMessageFromResponse();
            if (isValidString(errorMessageFromResponse)) {
                showErrorMessage(errorMessageFromResponse);
            }
        }
    }

    private void updateData (List<NotificationModel> levelList) {

        this.list.clear();
        if (levelList != null) {
            this.list.addAll(levelList);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            updateNoDataView();
        }
    }

    private void addDataToList (List<NotificationModel> levelList) {

        if (levelList != null) {
            this.list.addAll(levelList);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            updateNoDataView();
        }

    }

    private void updateNoDataView () {
        if (this.list.size() == 0) {
            if (cv_no_record_found.getVisibility() != View.VISIBLE)
                cv_no_record_found.setVisibility(View.VISIBLE);
        } else {
            if (cv_no_record_found.getVisibility() != View.GONE)
                cv_no_record_found.setVisibility(View.GONE);
        }
    }
}
