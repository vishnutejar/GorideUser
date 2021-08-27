package com.quickzetuser.rest;

import android.content.Context;

import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.FavouriteModel;
import com.quickzetuser.model.request_model.BookCabRequestModel;
import com.quickzetuser.model.request_model.CancelTripRequestModel;
import com.quickzetuser.model.request_model.ChangeDropRequestModel;
import com.quickzetuser.model.request_model.CheckFareRequestModel;
import com.quickzetuser.model.request_model.DriverOnMapRequestModel;
import com.quickzetuser.model.request_model.FavouriteRequestModel;
import com.quickzetuser.model.request_model.ForgotPasswordRequestModel;
import com.quickzetuser.model.request_model.LogOutRequestModel;
import com.quickzetuser.model.request_model.LoginRequestModel;
import com.quickzetuser.model.request_model.NewUserRequestModel;
import com.quickzetuser.model.request_model.RatingRequestModel;
import com.quickzetuser.model.request_model.RemainingPaymentRequestModel;
import com.quickzetuser.model.request_model.SendBillRequestModel;
import com.quickzetuser.model.request_model.SosRequestModel;
import com.quickzetuser.model.request_model.UpdateNotificationTokenRequestModel;
import com.quickzetuser.model.request_model.UpdateProfileRequestModel;
import com.quickzetuser.model.request_model.VerifyOtpRequestModel;
import com.quickzetuser.ui.main.dialog.confirmBooking.ConfirmBookingDialog;
import com.quickzetuser.ui.main.dialog.promocode.PromocodeDialog;

/**
 * Created by ubuntu on 27/3/18.
 */

public class WebRequestHelper implements WebRequestConstants {

    private static final String TAG = WebRequestHelper.class.getSimpleName();
    private Context context;

    public WebRequestHelper (Context context) {
        this.context = context;
    }

    public boolean isValidString (String data) {
        return data != null && !data.trim().isEmpty();
    }

    public void countries (WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_COUNTRIES, URL_COUNTRIES, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void signUp (NewUserRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_NEW_USER, URL_NEW_USER, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.addExtra(KEY_NEW_USER, requestModel);
        webRequest.send(context, webServiceResponseListener);
    }

    public void verifyotp (VerifyOtpRequestModel verifyOtp, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_VERIFY_OTP, URL_VERIFY_OTP, WebRequest.POST_REQ);
        webRequest.setRequestModel(verifyOtp);
        webRequest.send(context, webServiceResponseListener);
    }

    public void login (LoginRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_LOGIN, URL_LOGIN, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
    }

    public void forgotPassword (ForgotPasswordRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_FORGOT_PASSWORD, URL_FORGOT_PASSWORD, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.addExtra(KEY_FORGOT_PASSWORD, requestModel);
        webRequest.send(context, webServiceResponseListener);
    }

    public void logOut (LogOutRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_LOGOUT, URL_LOGOUT, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
    }

    public void updateprofile (UpdateProfileRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_UPDATE_PROFILE, URL_UPDATE_PROFILE, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
    }

    public void getCities (long state_id, WebServiceResponseListener webServiceResponseListener) {
        String url = String.format(URL_CITIES, state_id);
        WebRequest webRequest = new WebRequest(ID_CITIES, url, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void get_vehicle_types (long city_id, WebServiceResponseListener webServiceResponseListener) {
        String url = String.format(URL_VEHICLE_TYPE, city_id);
        WebRequest webRequest = new WebRequest(ID_VEHICLE_TYPE, url, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void check_fare (CheckFareRequestModel requestModel, PromocodeDialog promocodeDialog, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_CHECK_FARE, URL_CHECK_FARE, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.addExtra(KEY_PROMOCODE_DIALOG, promocodeDialog);
        webRequest.send(context, webServiceResponseListener);
    }

    public void check_package(CheckFareRequestModel requestModel, PromocodeDialog promocodeDialog, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_CHECK_PACKAGE, URL_CHECK_PACKAGE, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.addExtra(KEY_PROMOCODE_DIALOG, promocodeDialog);
        webRequest.send(context, webServiceResponseListener);
    }

    public void check_package_promocode(CheckFareRequestModel requestModel, PromocodeDialog promocodeDialog, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_CHECK_PACKAGE_PROMOCODE, URL_CHECK_PACKAGE_PROMOCODE, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.addExtra(KEY_PROMOCODE_DIALOG, promocodeDialog);
        webRequest.send(context, webServiceResponseListener);
    }


    public void check_outstation_package(CheckFareRequestModel requestModel, PromocodeDialog promocodeDialog, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_CHECK_OUTSTATION_PACKAGE, URL_CHECK_OUTSTATION_PACKAGE, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.addExtra(KEY_PROMOCODE_DIALOG, promocodeDialog);
        webRequest.send(context, webServiceResponseListener);
    }

    public void check_sharing_package(CheckFareRequestModel requestModel, PromocodeDialog promocodeDialog, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_CHECK_SHARING_PACKAGE, URL_CHECK_SHARING_PACKAGE, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.addExtra(KEY_PROMOCODE_DIALOG, promocodeDialog);
        webRequest.send(context, webServiceResponseListener);
    }

    public void fetchRoute(String url, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_GET_ROUTE, url, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public WebRequest viewDriversOnMap (DriverOnMapRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_VIEW_DRIVER_ON_MAP, URL_VIEW_DRIVER_ON_MAP, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
        return webRequest;
    }

    public WebRequest viewDriversOnMapRental(DriverOnMapRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_VIEW_DRIVER_ON_MAP_RENTAL,
                URL_VIEW_DRIVER_ON_MAP_RENTAL, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
        return webRequest;
    }

    public WebRequest bookCab(ConfirmBookingDialog dialogFragment, BookCabRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_BOOK_CAB, URL_BOOK_CAB, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.addExtra(ConfirmBookingDialog.class.getSimpleName(), dialogFragment);
        webRequest.send(context, webServiceResponseListener);
        return webRequest;
    }

    public WebRequest getBookingsDetail (String bookingIds,
                                         WebServiceResponseListener webServiceResponseListener) {
        final String URL = String.format(URL_BOOKING_DETAIL, bookingIds);
        WebRequest webRequest = new WebRequest(ID_BOOKING_DETAIL, URL, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
        return webRequest;
    }

    public WebRequest getCurrentBookings (WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_CURRENT_BOOKING, URL_CURRENT_BOOKING, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
        return webRequest;
    }

    public WebRequest cancelTrip (CancelTripRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_CANCEL_TRIP, URL_CANCEL_TRIP, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
        return webRequest;
    }

    public void getBookingHistory (int currentPage, WebServiceResponseListener webServiceResponseListener) {
        String URL = String.format(URL_BOOKING_HISTORY, "5,6,7", currentPage);
        WebRequest webRequest = new WebRequest(ID_BOOKING_HISTORY, URL, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void getWalletDetail (WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_GET_WALLET_DETAIL, URL_GET_WALLET_DETAIL, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void getNotification (int currentPage, WebServiceResponseListener webServiceResponseListener) {
        String URL = String.format(URL_GET_NOTIFICATION, currentPage);
        WebRequest webRequest = new WebRequest(ID_GET_NOTIFICATION, URL, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void give_rating (RatingRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_GIVE_RATING, URL_GIVE_RATING, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
    }

    public void pay_remaining_payment (RemainingPaymentRequestModel requestModel,
                                       BookCabModel bookCabModel,
                                       WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_PAY_REMAINING_PAYMENT, URL_PAY_REMAINING_PAYMENT, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.addExtra(KEY_PAY_REMAINING_PAYMENT, bookCabModel);
        webRequest.send(context, webServiceResponseListener);
    }

    public void get_remaining_payment (WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_GET_REMAINING_PAYMENT, URL_GET_REMAINING_PAYMENT, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void updateToken (UpdateNotificationTokenRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_UPDATE_TOKEN, URL_UPDATE_TOKEN, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
    }

    public void addFavouriteAddress (FavouriteRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_ADD_FAVOURITE, URL_ADD_FAVOURITE, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
    }

    public void getFavouriteAddress (WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_GET_FAVOURITE, URL_GET_FAVOURITE, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void deleteFavouriteAddress (FavouriteModel locationModel, WebServiceResponseListener webServiceResponseListener) {
        String URL = String.format(URL_DELETE_FAVOURITE, locationModel.getId());
        WebRequest webRequest = new WebRequest(ID_DELETE_FAVOURITE, URL, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void bookingRouteImageSave (long bookingId, WebServiceResponseListener webServiceResponseListener) {
        String url = String.format(URL_BOOKING_ROUTE_IMAGE_SAVE, bookingId);
        WebRequest webRequest = new WebRequest(ID_BOOKING_ROUTE_IMAGE_SAVE, url, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void sos (SosRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_SOS, URL_SOS, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
    }

    public void sendBill (SendBillRequestModel sendBillRequestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_SEND_BILL, URL_SEND_BILL, WebRequest.POST_REQ);
        webRequest.setRequestModel(sendBillRequestModel);
        webRequest.send(context, webServiceResponseListener);
    }


    public void getRiderDetail(WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_GET_RIDER_DETAIL, URL_GET_RIDER_DETAIL, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void getTermAndCondition(WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_TERM_AND_CONDITION, URL_TERM_AND_CONDITION, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    public void changeBookingDropLocation(ChangeDropRequestModel requestModel, WebServiceResponseListener webServiceResponseListener) {
        WebRequest webRequest = new WebRequest(ID_CHANGE_BOOKING_DROP_LOCATION, URL_CHECK_BOOKING_DROP_LOCATION, WebRequest.POST_REQ);
        webRequest.setRequestModel(requestModel);
        webRequest.send(context, webServiceResponseListener);
    }
}
