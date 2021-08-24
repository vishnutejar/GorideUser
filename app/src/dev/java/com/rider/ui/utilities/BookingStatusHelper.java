package com.rider.ui.utilities;

import android.os.Handler;
import android.os.Message;

import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.rider.model.BookCabModel;
import com.rider.model.web_response.BookingDetailResponseModel;
import com.rider.rest.WebRequestHelper;
import com.rider.ui.main.MainActivity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Sunil kumar yadav on 8/5/18.
 */
public class BookingStatusHelper {

    public static final int BOOKING_UPDATE_INTERVAL = 10000;
    static final Map<Long, BookingStatusHelper> bookingHelperMap = new ConcurrentHashMap<>();
    private boolean isUpdaterStart = false;
    private MainActivity context;
    private BookCabModel bookCabModel;
    private WebRequestHelper webRequestHelper;
    private WebRequest previousRequest;
    private BookingStatusHelperListener bookingStatusHelperListener;
    private WebServiceResponseListener webServiceResponseListener = new WebServiceResponseListener() {
        @Override
        public void onWebRequestCall(WebRequest webRequest) {
            context.onWebRequestCall(webRequest);
        }

        @Override
        public void onWebRequestResponse(WebRequest webRequest) {
            if (webRequest.getResponseCode() == 401 ||
                    webRequest.getResponseCode() == 412) {
                stopBookingUpdater(false);
                if (context != null && !context.isFinishing())
                    context.onWebRequestResponse(webRequest);
                return;
            }
            if (isUpdaterStart)
                handler.sendEmptyMessageDelayed(1, BOOKING_UPDATE_INTERVAL);
            if (webRequest.checkSuccess()) {
                handleBookingDetailResponse(webRequest);
                if (BookingStatusHelper.this.bookCabModel != null &&
                        BookingStatusHelper.this.bookCabModel.getStatus() == -1) {
                    stopBookingUpdater(false);
                }
            }

        }

        @Override
        public void onWebRequestPreResponse(WebRequest webRequest) {

        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getBookingDetail();
        }
    };

    private BookingStatusHelper(MainActivity context) {
        this.context = context;
        webRequestHelper = new WebRequestHelper(context);
    }

    public static BookingStatusHelper getNewInstances(MainActivity context, BookCabModel
            bookCabModel) {
        if (bookCabModel == null) return null;
        BookingStatusHelper bookingStatusHelper = null;
        if (bookingHelperMap.containsKey(bookCabModel.getBooking_id())) {
            bookingStatusHelper = bookingHelperMap.get(bookCabModel.getBooking_id());
            bookingStatusHelper.bookCabModel = bookCabModel;
        } else {
            bookingStatusHelper = new BookingStatusHelper(context);
            bookingStatusHelper.bookCabModel = bookCabModel;
            bookingHelperMap.put(bookCabModel.getBooking_id(), bookingStatusHelper);
        }
        return bookingStatusHelper;
    }

    public void setBookingStatusHelperListener(BookingStatusHelperListener bookingStatusHelperListener) {
        this.bookingStatusHelperListener = bookingStatusHelperListener;
    }

    public void startBookingUpdater() {
        stopBookingUpdater(false);
        if (bookCabModel != null) {
            isUpdaterStart = true;
            getBookingDetail();
        }

    }

    public void stopBookingUpdater(boolean needClean) {
        handler.removeMessages(1);
        isUpdaterStart = false;
        if (previousRequest != null) {
            previousRequest.cancel();
        }
        if (needClean && bookCabModel != null) {
            bookingHelperMap.remove(bookCabModel.getBooking_id());
        }
    }

    private void getBookingDetail() {
        if (bookCabModel != null)
            previousRequest = webRequestHelper.
                    getBookingsDetail(String.valueOf(bookCabModel.getBooking_id()),
                            webServiceResponseListener);

    }

    private void handleBookingDetailResponse(WebRequest webRequest) {
        BookingDetailResponseModel responseModel =
                webRequest.getResponsePojo(BookingDetailResponseModel.class);
        if (responseModel == null) {
            return;
        }
        List<BookCabModel> data = responseModel.getData();
        if (data == null || data.size() == 0) {
            return;
        }
        this.bookCabModel = data.get(0);
        triggerListener(this.bookCabModel);
    }

    private void triggerListener(BookCabModel bookCabModel) {
        if (bookingStatusHelperListener != null) {
            bookingStatusHelperListener.onBookingUpdate(bookCabModel);
        }
    }

    public interface BookingStatusHelperListener {
        void onBookingUpdate(BookCabModel bookCabModel);
    }
}
