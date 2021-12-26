package com.quickzetuser.ui.main.booking.fareEstimate;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.addressfetching.LocationModelFull;
import com.distancematrix.DistanceMatrixHandler;
import com.distancematrix.DistanceMatrixModel;
import com.medy.retrofitwrapper.WebRequest;
import com.models.BaseModel;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.database.tables.BookingTable;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.CityModel;
import com.quickzetuser.model.FareModel;
import com.quickzetuser.model.PromoCodeModel;
import com.quickzetuser.model.RentalPackageModel;
import com.quickzetuser.model.VehicleTypeModel;
import com.quickzetuser.model.request_model.BookCabRequestModel;
import com.quickzetuser.model.request_model.CheckFareRequestModel;
import com.quickzetuser.model.web_response.BaseWebServiceModelResponse;
import com.quickzetuser.model.web_response.BookCabResponseModel;
import com.quickzetuser.ui.main.dialog.confirmBooking.ConfirmBookingDialog;
import com.quickzetuser.ui.main.dialog.paymentmode.PaymentModeDialog;
import com.quickzetuser.ui.main.dialog.promocode.PromocodeDialog;
import com.quickzetuser.ui.main.dialog.reminingPayment.RemainingPaymentDialog;
import com.quickzetuser.ui.main.dialog.walletrecharge.WalletRechargeDialog;
import com.quickzetuser.ui.main.sidemenu.payment.wallet.WalletFragment;
import com.quickzetuser.ui.utilities.MapHandler;

import java.util.List;


/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class FareEstimateFragment extends AppBaseFragment implements DistanceMatrixHandler.DistanceMatrixListener {


    ConfirmBookingDialog confirmationDialog;
    private ImageView iv_current_loc;
    private TextView tv_available_time;
    private TextView tv_fare_estimate;
    private LinearLayout ll_payment;
    private ImageView iv_payment_icon;
    private TextView tv_payment_mode;
    private TextView tv_ride_now;
    private String PAYMENT_MODE = PAYMENT_METHOD;

    private RelativeLayout rl_book_schedule;
    private TextView tv_schedule_time;
    private ImageView iv_schedule_close;
    private LinearLayout ll_promocode;
    private ImageView iv_cancel_promocode;
    private TextView tv_promocode;

    private int openFrom = 0;

    public MapHandler getMapHandler() {
        MapHandler mapHandler = null;
        try {
            mapHandler = getMainActivity().getMapHandler();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mapHandler;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_fare_estimate;
    }

    public void setOpenFrom(int openFrom) {
        this.openFrom = openFrom;
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        iv_current_loc = getView().findViewById(R.id.iv_current_loc);
        tv_available_time = getView().findViewById(R.id.tv_available_time);
        tv_fare_estimate = getView().findViewById(R.id.tv_fare_estimate);
        ll_payment = getView().findViewById(R.id.ll_payment);
        iv_payment_icon = getView().findViewById(R.id.iv_payment_icon);
        tv_payment_mode = getView().findViewById(R.id.tv_payment_mode);
        tv_ride_now = getView().findViewById(R.id.tv_ride_now);

        rl_book_schedule = getView().findViewById(R.id.rl_book_schedule);
        tv_schedule_time = getView().findViewById(R.id.tv_schedule_time);
        iv_schedule_close = getView().findViewById(R.id.iv_schedule_close);

        ll_promocode = getView().findViewById(R.id.ll_promocode);
        iv_cancel_promocode = getView().findViewById(R.id.iv_cancel_promocode);
        tv_promocode = getView().findViewById(R.id.tv_promocode);


        iv_current_loc.setOnClickListener(this);
        tv_ride_now.setOnClickListener(this);
        ll_payment.setOnClickListener(this);

        iv_schedule_close.setOnClickListener(this);
        rl_book_schedule.setOnClickListener(this);
        ll_promocode.setOnClickListener(this);
        iv_cancel_promocode.setOnClickListener(this);

        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (openFrom == 2) {
                    getMapHandler().changePickUpAddress(getMapHandler().pickUpLocationModel);
                }
                setFareData();

            }
        });
    }

    private void updateDriverAvailableTime() {
        if (getMapHandler().cabTypeFragment != null &&
                getMapHandler().fareEstimateModel != null) {
            VehicleTypeModel vehicleTypeModel = getMapHandler().cabTypeFragment.
                    getVehicleTypeData(getMapHandler().fareEstimateModel.getVehicle());
            if (vehicleTypeModel != null && vehicleTypeModel.isValidString(
                    vehicleTypeModel.getAvailableDriverTime())) {
                tv_available_time.setText(vehicleTypeModel.getAvailableDriverTime());
            } else {
                tv_available_time.setText("N/A");
            }
        }
    }

    public String getAvailableTime() {
        return tv_available_time.getText().toString();
    }

    private void setFareData() {
        try {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iv_current_loc.getLayoutParams();
            getMainActivity().getMapHandler().setMapPadding(0,
                    getMainActivity().getMapHandler().getTopMapHeight(), 0,
                    (getView().getHeight() - layoutParams.height - layoutParams.bottomMargin));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (getMapHandler().fareEstimateModel != null) {
            updateScheduledUi();
            updateDriverAvailableTime();
            updatePromocodeUi();
            if (getMapHandler().fareEstimateModel.hasDropAddress()) {
                FareModel fare = getMapHandler().fareEstimateModel.getFare();
                if (fare != null)
                    tv_fare_estimate.setText(currency_symbol + fare.getPayble_fare());
            } else {
                tv_fare_estimate.setText("N/A");
            }
        } else {
            tv_available_time.setText("N/A");
            tv_fare_estimate.setText("N/A");
            rl_book_schedule.setVisibility(View.GONE);
        }
    }

    private void updatePromocodeUi() {
        BookCabModel bookCabModel = getMapHandler().fareEstimateModel;

        PromoCodeModel promocode = bookCabModel.getPromocode();
        if (!isValidObject(promocode)) {
            updateViewVisibility(iv_cancel_promocode, View.GONE);
            tv_promocode.setText("");
        } else {
            updateViewVisibility(iv_cancel_promocode, View.VISIBLE);
            tv_promocode.setText(promocode.getPromocode());
        }
    }

    private void updateScheduledUi() {
        BookCabModel bookCabModel = getMapHandler().fareEstimateModel;
        if (bookCabModel != null && bookCabModel.isScheduleBooking()) {
            tv_schedule_time.setText(bookCabModel.getFormattedCalendar(BaseModel.DATE_TIME_TWO,
                    (bookCabModel.getBooking_date())));
            updateViewVisibility(rl_book_schedule, View.VISIBLE);
        } else {
            updateViewVisibility(rl_book_schedule, View.GONE);
        }
    }

    public VehicleTypeModel getSelectedVehicleType() {
        if (getMapHandler().cabTypeFragment != null &&
                getMapHandler().fareEstimateModel != null) {
            VehicleTypeModel vehicleTypeModel = getMapHandler().cabTypeFragment.getSelectedVehicleType();
            //getVehicleTypeData(getMapHandler().fareEstimateModel.getVehicle());
            if (vehicleTypeModel != null){
                return vehicleTypeModel;
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ride_now:
            case R.id.rl_book_schedule:
                confirmationDialog();
                break;

            case R.id.iv_current_loc:
                try {
                    getMainActivity().zoomToCurrentLocation();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.ll_payment:
                addPaymentModeDialog();
                break;
            case R.id.ll_promocode:
                showPromocodeDialog();
                break;
            case R.id.iv_cancel_promocode:
                cancelPromocode();
                break;
        }
    }

    private void cancelPromocode() {
        tv_promocode.setText("");
        getFare(null);
    }

    private void showPromocodeDialog() {
        PromocodeDialog promocodeDialog = new PromocodeDialog();
        promocodeDialog.setPromocodeListener(new PromocodeDialog.PromocodeListener() {
            @Override
            public void onPromocodeEntered(PromocodeDialog promocodeDialog) {
                getFare(promocodeDialog);
            }
        });
        promocodeDialog.show(getFragmentManager(), promocodeDialog.getClass().getSimpleName());
    }

    private void confirmationDialog() {
        confirmationDialog = ConfirmBookingDialog.
                getNewInstance("");
        confirmationDialog.setBookCabModel(getMapHandler().fareEstimateModel);
        confirmationDialog.setConfirmationDialogListener(new ConfirmBookingDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm(ConfirmBookingDialog dialogFragment) {
                bookCab(dialogFragment);
            }

            @Override
            public String getAvailableTime() {
                return FareEstimateFragment.this.getAvailableTime();
            }

            @Override
            public PromoCodeModel getPromoCode() {
                if (getMapHandler().fareEstimateModel != null) {
                    return getMapHandler().fareEstimateModel.getPromocode();
                }
                return null;
            }

            @Override
            public VehicleTypeModel getVehicle() {
                if (getMapHandler().fareEstimateModel != null) {
                    return getMapHandler().fareEstimateModel.getVehicle();
                }
                return null;
            }

            @Override
            public RentalPackageModel getSelectedRentalPackage() {
                return null;
            }

            @Override
            public FareModel getFare() {
                if (getMapHandler().fareEstimateModel != null) {
                    return getMapHandler().fareEstimateModel.getFare();
                }
                return null;
            }
        });
        confirmationDialog.show(getActivity().getSupportFragmentManager(), confirmationDialog.getClass().getSimpleName());
    }

    private void addPaymentModeDialog() {
        int height = tv_ride_now.getHeight();
        PaymentModeDialog paymentModeDialog = PaymentModeDialog.getNewInstance("Payment Mode");
        paymentModeDialog.setBottomMargin(height);
        paymentModeDialog.setConfirmationDialogListener(new PaymentModeDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm(DialogFragment dialogFragment, String paymentMode) {
                PAYMENT_MODE = paymentMode;
                if (PAYMENT_MODE.equalsIgnoreCase("Cash")) {
                    iv_payment_icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_cash));
                } else {
                    iv_payment_icon.setImageDrawable(getResources().getDrawable(R.mipmap.wallet_icon));
                }
                tv_payment_mode.setText(PAYMENT_MODE);
                dialogFragment.dismiss();
            }
        });
        paymentModeDialog.show(getActivity().getSupportFragmentManager(), paymentModeDialog.getClass().getSimpleName());
    }

    private void bookCab(ConfirmBookingDialog dialogFragment) {
        BookCabModel bookCabModel = getMapHandler().fareEstimateModel;
        if (bookCabModel != null) {
            BookCabRequestModel model = null;
            try {
                model = new BookCabRequestModel();
                model.paymentmethod = PAYMENT_MODE.toUpperCase();
                model.booking_running_type = BOOKING_RUNNING_TYPE_INDIVIDUAL;
                model.mobile_code = getMainActivity().getUserModel().getMobile_code();
                model.phone = getMainActivity().getUserModel().getPhone();
                model.special_instruction = "";
                if (getMapHandler().pickUpLocationModel == null) return;
                LocationModelFull.LocationModel locationModel = getMapHandler().pickUpLocationModel;
                CityModel cityModel = getMyApplication().getCityHelper().
                        getCityModelFromLatLng(locationModel.getLatLng());
                if (isValidObject(cityModel)) {
                    model.utc_offset = cityModel.getUtc_offset();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            model.cab_type_id = bookCabModel.getVehicle().getId();

            model.taxi_id = bookCabModel.getTaxi_id();

            model.latitude_from = bookCabModel.getLatitude_from();
            model.longitude_from = bookCabModel.getLongitude_from();
            model.latitude_to = bookCabModel.getLatitude_to();
            model.longitude_to = bookCabModel.getLongitude_to();
            model.pickupaddress = bookCabModel.getPickup_address();
            model.dropaddress = bookCabModel.getDrop_address();
            model.booking_cancel_time_duration = bookCabModel.getBooking_cancel_time_duration();
            model.booking_driver_cancel_time_duration = bookCabModel.getBooking_driver_cancel_time_duration();
            model.booking_free_waiting_time_duration = bookCabModel.getBooking_free_waiting_time_duration();

            model.country = bookCabModel.getCountry();
            model.state = bookCabModel.getState();
            model.city = bookCabModel.getCity();
            model.promocode = bookCabModel.getPromocode();
            model.taxes = bookCabModel.getTotal_tax().getTaxes();
            model.fare = bookCabModel.getFare();
            model.toll_area_distance = bookCabModel.getToll_area_distance();
            if (bookCabModel.isScheduleBooking()) {
                model.sheduled_date = bookCabModel.getBooking_date();
            }

            displayProgressBar(false);
            getWebRequestHelper().bookCab(dialogFragment, model, this);
        }
    }

    private void getFare(PromocodeDialog promocodeDialog) {
        LocationModelFull.LocationModel pickUpLocationModel = getMapHandler().pickUpLocationModel;
        LocationModelFull.LocationModel dropLocationModel = getMapHandler().dropLocationModel;
        if (pickUpLocationModel == null) {
            showErrorMessage("Please set pickup address");
            return;
        }

        CityModel cityModel = null;
        try {
            cityModel = getMyApplication().getCityHelper().
                    getCityModelFromLatLng(pickUpLocationModel.getLatLng());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (!isValidObject(cityModel)) {
            showErrorMessage("Service is not available in your city");
            return;
        }
        CheckFareRequestModel requestModel = new CheckFareRequestModel();
        requestModel.latitude_from = pickUpLocationModel.getLatitude();
        requestModel.longitude_from = pickUpLocationModel.getLongitude();
        requestModel.pickupaddress = pickUpLocationModel.getFulladdress();
        requestModel.city_id = cityModel.getCity_id();
        if (dropLocationModel == null) {
            requestModel.dropaddress = "N/A";
            requestModel.latitude_to = pickUpLocationModel.getLatitude();
            requestModel.longitude_to = pickUpLocationModel.getLongitude();
        } else {
            requestModel.dropaddress = dropLocationModel.getFulladdress();
            requestModel.latitude_to = dropLocationModel.getLatitude();
            requestModel.longitude_to = dropLocationModel.getLongitude();
        }
        BookCabModel fareEstimateModel = getMapHandler().fareEstimateModel;
        if (fareEstimateModel == null) return;
        VehicleTypeModel vehicle = fareEstimateModel.getVehicle();
        if (vehicle == null) return;
        requestModel.cab_type_id = vehicle.getId();
        requestModel.utc_offset = cityModel.getUtc_offset();
        if (fareEstimateModel.isScheduleBooking()) {
            requestModel.schedule_time = fareEstimateModel.getBooking_date();
        }

        requestModel.promocode = tv_promocode.getText().toString().trim();
        if (promocodeDialog != null) {
            requestModel.promocode = promocodeDialog.getPromocode();
        }

        displayProgressBar(false);
        getWebRequestHelper().check_fare(requestModel, promocodeDialog, this);
    }


    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);


        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_CHECK_FARE:
                    handleCheckFareResponse(webRequest);
                    break;
                case ID_BOOK_CAB:
                    handleBookCabResponse(webRequest);
                    break;
            }
        } else {

            if (webRequest.getWebRequestId() == ID_CHECK_FARE) {
                BookCabResponseModel responseModel =
                        webRequest.getResponsePojo(BookCabResponseModel.class);
                if (responseModel != null && responseModel.isError() &&
                        responseModel.getCode() == 0) {
                    showRemainingPaymentDialog(responseModel);
                    return;
                }
            } else if (webRequest.getWebRequestId() == ID_BOOK_CAB) {
                BaseWebServiceModelResponse responsePojo = webRequest.getResponsePojo(BaseWebServiceModelResponse.class);
                if (responsePojo == null) return;
                int code = responsePojo.getCode();
                if (code == 45) {
                    addWalletRechargeDialog(responsePojo.getMessage());
                    return;
                }
            }
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                showErrorMessage(msg);
            }
        }

    }

    private void showRemainingPaymentDialog(BookCabResponseModel responseModel) {
        RemainingPaymentDialog runningBookingDialog = null;
        try {
            runningBookingDialog = new RemainingPaymentDialog(getMainActivity());
            runningBookingDialog.setBookCabModel(responseModel.getData());
            runningBookingDialog.show(getFragmentManager(), runningBookingDialog.getClass().getSimpleName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void handleCheckFareResponse(WebRequest webRequest) {
        PromocodeDialog promocodeDialog = webRequest.getExtraData(KEY_PROMOCODE_DIALOG);
        if (promocodeDialog != null) {
            promocodeDialog.dismiss();
        }
        BookCabResponseModel responseModel = webRequest.getResponsePojo(BookCabResponseModel.class);
        if (responseModel == null) return;

        BookCabModel data = responseModel.getData();
        if (data != null) {
            getMapHandler().fareEstimateModel = data;
            setFareData();
        }
    }


    private void handleBookCabResponse(WebRequest webRequest) {
        ConfirmBookingDialog confirmBookingDialog = webRequest.getExtraData(ConfirmBookingDialog.class.getSimpleName());
        BookCabResponseModel responseModel =
                webRequest.getResponsePojo(BookCabResponseModel.class);
        if (isValidObject(responseModel)) {
            BookCabModel bookCabModel = responseModel.getData();
            try {
                if (isValidObject(bookCabModel)) {
                    BookingTable.getInstance().addOrUpdateBooking(bookCabModel);
                    if (confirmBookingDialog != null) {
                        confirmBookingDialog.updateSuccessUI();
                    }
                    if (bookCabModel.isScheduleBooking()) {
                        if (bookCabModel.isValidString(bookCabModel.getSchedule_success_msg())) {
                            getMainActivity().showMessageDialog(getResources().getString(R.string.app_name),
                                    bookCabModel.getSchedule_success_msg());
                        }
                        getMainActivity().onBackPressed();
                    } else {
                        getMainActivity().getMapHandler().showConfirmBookingWaitingFragment(bookCabModel);
                    }
                }
            } catch (IllegalAccessException e) {
            }
        } else {
            String error = webRequest.getErrorMessageFromResponse();
            if (isValidString(error)) {
                showErrorMessage(error);
            }
        }
    }

    private void addWalletRechargeDialog(String msg) {
        WalletRechargeDialog rechargeDialog = WalletRechargeDialog.getNewInstance(msg);
        rechargeDialog.setConfirmationDialogListener(new WalletRechargeDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm(DialogFragment dialogFragment) {
                addMoneyFragment();
                if (confirmationDialog != null)
                    confirmationDialog.dismiss();
                dialogFragment.dismiss();
            }
        });
        rechargeDialog.show(getActivity().getSupportFragmentManager(), rechargeDialog.getClass().getSimpleName());
    }

    private void addMoneyFragment() {
        WalletFragment fragment = new WalletFragment();
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

    public void changePickUpAddress() {
        getMapHandler().startDriverUpdater(null);
        getFare(null);
    }

    public void changeDropAddress() {
        getFare(null);
    }

    @Override
    public void onPause() {
        super.onPause();
        getMapHandler().stopDriverUpdater();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMapHandler().startDriverUpdater(null);
    }

    @Override
    public void onDistanceMatrixRequestStart() {

    }

    @Override
    public void onDistanceMatrixRequestEnd(List<DistanceMatrixModel> rows) {
        if (isValidActivity() && isVisible()) {
            updateDriverAvailableTime();
            if (confirmationDialog != null && confirmationDialog.isVisible()) {
                confirmationDialog.onDistanceMatrixRequestEnd(rows);
            }
        }
    }


}
