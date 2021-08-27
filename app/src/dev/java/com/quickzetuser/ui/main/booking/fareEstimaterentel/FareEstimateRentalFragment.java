package com.quickzetuser.ui.main.booking.fareEstimaterentel;

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
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.database.tables.BookingTable;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.CityModel;
import com.quickzetuser.model.FareModel;
import com.quickzetuser.model.PromoCodeModel;
import com.quickzetuser.model.RentalFareModel;
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
import com.quickzetuser.ui.main.dialog.rentalcabtype.RentalCabTypeDialog;
import com.quickzetuser.ui.main.dialog.walletrecharge.WalletRechargeDialog;
import com.quickzetuser.ui.main.sidemenu.payment.wallet.WalletFragment;
import com.quickzetuser.ui.signup.dialog.DataDialog;
import com.quickzetuser.ui.utilities.MapHandler;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class FareEstimateRentalFragment extends AppBaseFragment implements DistanceMatrixHandler.DistanceMatrixListener {


    ConfirmBookingDialog confirmationDialog;
    RentalCabTypeDialog rentalCabTypeDialog;
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

    private LinearLayout ll_sel_package;
    private LinearLayout ll_sel_cab_type;
    private LinearLayout ll_view;
    private TextView tv_package;
    private TextView tv_cab_type;
    private int openFrom = 0;

    private BookCabModel promocodeBookCabModel;

    public MapHandler getMapHandler () {
        MapHandler mapHandler = null;
        try {
            mapHandler = getMainActivity().getMapHandler();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mapHandler;
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.fragment_fare_estimate_rental;
    }

    public void setOpenFrom (int openFrom) {
        this.openFrom = openFrom;
    }

    @Override
    public void initializeComponent () {
        super.initializeComponent();
        getMapHandler().changeSelectedRentalPackage(null);
        getMapHandler().changeSelectedRentalFare(null);
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

        ll_sel_package = getView().findViewById(R.id.ll_sel_package);
        ll_sel_cab_type = getView().findViewById(R.id.ll_sel_cab_type);
        tv_package = getView().findViewById(R.id.tv_package);
        tv_cab_type = getView().findViewById(R.id.tv_cab_type);
        ll_view = getView().findViewById(R.id.ll_view);


        updateViewVisibility(ll_view, View.GONE);

        iv_current_loc.setOnClickListener(this);
        tv_ride_now.setOnClickListener(this);
        ll_payment.setOnClickListener(this);

        iv_schedule_close.setOnClickListener(this);
        rl_book_schedule.setOnClickListener(this);
        iv_cancel_promocode.setOnClickListener(this);
        ll_sel_package.setOnClickListener(this);
        ll_sel_cab_type.setOnClickListener(this);

        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout () {
                getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (openFrom == 2) {
                    getMapHandler().changePickUpAddress(getMapHandler().pickUpLocationModel);
                }

                setFareData();
            }
        });
    }

    private void updateDriverAvailableTime () {
        RentalFareModel rentalFareModel = (RentalFareModel) tv_cab_type.getTag();
        if (rentalFareModel == null) {
            tv_available_time.setText("N/A");
            return;
        }
        RentalPackageModel rentalPackageModel = getMapHandler().getPreviousRentalPackage();
        if (rentalPackageModel != null) {
            if (rentalPackageModel.getFares() != null) {
                for (RentalFareModel rentalFareModel1 : rentalPackageModel.getFares()) {
                    if (rentalFareModel.getVehicle().getId() == rentalFareModel1.getVehicle().getId()) {
                        if (rentalFareModel1.getVehicle().isValidString(
                                rentalFareModel1.getVehicle().getAvailableDriverTime())) {
                            tv_available_time.setText(rentalFareModel1.getVehicle().getAvailableDriverTime());
                        } else {
                            tv_available_time.setText("N/A");
                        }
                        return;
                    }
                }
            }
        }

        tv_available_time.setText("N/A");
    }

    public String getAvailableTime () {
        return tv_available_time.getText().toString();
    }

    private void setFareData () {

        RentalPackageModel rentalPackageModel = (RentalPackageModel) tv_package.getTag();
        getMapHandler().changeSelectedRentalPackage(rentalPackageModel);

        RentalFareModel rentalFareModel = (RentalFareModel) tv_cab_type.getTag();
        getMapHandler().changeSelectedRentalFare(rentalFareModel);
        if (rentalPackageModel == null || rentalFareModel == null) {
            updateViewVisibility(ll_view, View.GONE);
            ll_view.postDelayed(new Runnable() {
                @Override
                public void run () {
                    try {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iv_current_loc.getLayoutParams();
                        getMainActivity().getMapHandler().setMapPadding(0,
                                getMainActivity().getMapHandler().getTopMapHeight(), 0,
                                (getView().getHeight() - layoutParams.height - layoutParams.bottomMargin));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }, 100);
            getMapHandler().startDriverUpdater(null);
            return;
        }

        updateViewVisibility(ll_view, View.VISIBLE);
        ll_view.postDelayed(new Runnable() {
            @Override
            public void run () {
                try {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iv_current_loc.getLayoutParams();
                    getMainActivity().getMapHandler().setMapPadding(0,
                            getMainActivity().getMapHandler().getTopMapHeight(), 0,
                            (getView().getHeight() - layoutParams.height - layoutParams.bottomMargin));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }, 100);
        getMapHandler().startDriverUpdater(null);

        updateScheduledUi();
        updateDriverAvailableTime();
        updatePromocodeUi();
        if (getPromocode() != null) {
            tv_fare_estimate.setText(currency_symbol + promocodeBookCabModel.getPackages()
                    .get(0).getFares().get(0).getPayble_fare());
        } else {
            tv_fare_estimate.setText(currency_symbol + rentalFareModel.getPayble_fare());
        }
    }

    private PromoCodeModel getPromocode () {
        RentalFareModel rentalFareModel = (RentalFareModel) tv_cab_type.getTag();
        if (rentalFareModel == null) {
            return null;
        }
        PromoCodeModel promocode = null;
        if (promocodeBookCabModel != null) {
            RentalPackageModel rentalPackageModelPromocode = promocodeBookCabModel.getPackages().get(0);
            RentalFareModel rentalFareModelPromocode = rentalPackageModelPromocode.getFares().get(0);

            RentalPackageModel rentalPackageModel = (RentalPackageModel) tv_package.getTag();
            if (rentalPackageModel != null &&
                    rentalPackageModel.getPackage_id() == rentalPackageModelPromocode.getPackage_id() &&
                    rentalFareModel.getVehicle().getId() == rentalFareModelPromocode.getVehicle().getId()) {
                promocode = rentalFareModelPromocode.getPromocode();
            }
        }
        return promocode;
    }


    private void updatePromocodeUi () {
        PromoCodeModel promocode = getPromocode();
        if (promocode == null) {
            updateViewVisibility(iv_cancel_promocode, View.GONE);
            tv_promocode.setText("");
            ll_promocode.setOnClickListener(this);

        } else {
            updateViewVisibility(iv_cancel_promocode, View.VISIBLE);
            tv_promocode.setText(promocode.getPromocode());
            ll_promocode.setOnClickListener(null);

        }
    }

    private void updateScheduledUi () {
        BookCabModel bookCabModel = getMapHandler().fareEstimateModel;
        if (bookCabModel != null && bookCabModel.isScheduleBooking()) {
            tv_schedule_time.setText(bookCabModel.getFormattedCalendar(BaseModel.DATE_TIME_TWO,
                    (bookCabModel.getBooking_date())));
            updateViewVisibility(rl_book_schedule, View.VISIBLE);
        } else {
            updateViewVisibility(rl_book_schedule, View.GONE);
        }
    }


    @Override
    public void onClick (View v) {
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
            case R.id.ll_sel_package:

                List<RentalPackageModel> packageList = getPackageList();
                if (packageList != null && packageList.size() > 0) {
                    showDataDialog("Select Package", tv_package, packageList);
                } else {
                    showErrorMessage("No package found.");
                }
                break;
            case R.id.ll_sel_cab_type:
                List<RentalFareModel> cabTypeList = getCabTypeList();
                if (cabTypeList != null && cabTypeList.size() > 0) {
                    showRentalCabTypeDialog("Select Cab Type", tv_cab_type, cabTypeList);
                }
                break;
        }
    }

    private List<RentalPackageModel> getPackageList () {

        BookCabModel fareEstimateModel = getMapHandler().fareEstimateModel;
        if (!isValidObject(fareEstimateModel)) return null;
        return fareEstimateModel.getPackages();
    }

    private List<RentalFareModel> getCabTypeList () {
        Object tag = tv_package.getTag();
        if (tag == null) {
            showErrorMessage("Please select package.");
            return null;
        }
        RentalPackageModel rentalPackageModel = (RentalPackageModel) tag;

        if (!isValidObject(rentalPackageModel.getFares()) ||
                rentalPackageModel.getFares().size() == 0) {
            showErrorMessage("No cab type found.");
            return null;
        }
        return rentalPackageModel.getFares();
    }


    private void cancelPromocode () {
        this.promocodeBookCabModel = null;
        setFareData();
    }

    private void showPromocodeDialog () {
        PromocodeDialog promocodeDialog = new PromocodeDialog();
        promocodeDialog.setPromocodeListener(new PromocodeDialog.PromocodeListener() {
            @Override
            public void onPromocodeEntered (PromocodeDialog promocodeDialog) {
                checkPackagePromocode(promocodeDialog);
            }
        });
        promocodeDialog.show(getFragmentManager(), promocodeDialog.getClass().getSimpleName());
    }

    private void confirmationDialog () {
        confirmationDialog = ConfirmBookingDialog.
                getNewInstance("");
        confirmationDialog.setBookCabModel(getMapHandler().fareEstimateModel);
        confirmationDialog.setConfirmationDialogListener(new ConfirmBookingDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm (ConfirmBookingDialog dialogFragment) {
                bookCab(dialogFragment);
            }

            @Override
            public String getAvailableTime () {
                return FareEstimateRentalFragment.this.getAvailableTime();
            }

            @Override
            public PromoCodeModel getPromoCode () {
                RentalFareModel rentalFareModel = (RentalFareModel) getFare();
                if (rentalFareModel != null) {
                    return rentalFareModel.getPromocode();
                }
                return null;
            }

            @Override
            public VehicleTypeModel getVehicle () {
                RentalFareModel rentalFareModel = (RentalFareModel) getFare();
                if (rentalFareModel != null) {
                    return rentalFareModel.getVehicle();
                }
                return null;
            }

            @Override
            public RentalPackageModel getSelectedRentalPackage () {
                return (RentalPackageModel) tv_package.getTag();
            }


            @Override
            public FareModel getFare () {
                if (FareEstimateRentalFragment.this.getPromocode() == null) {
                    return (RentalFareModel) tv_cab_type.getTag();
                } else {
                    return (RentalFareModel) FareEstimateRentalFragment.this.promocodeBookCabModel
                            .getPackages().get(0).getFares().get(0);
                }

            }
        });
        confirmationDialog.show(getChildFm(), confirmationDialog.getClass().getSimpleName());
    }

    private void addPaymentModeDialog () {
        int height = tv_ride_now.getHeight();
        PaymentModeDialog paymentModeDialog = PaymentModeDialog.getNewInstance("Payment Mode");
        paymentModeDialog.setBottomMargin(height);
        paymentModeDialog.setConfirmationDialogListener(new PaymentModeDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm (DialogFragment dialogFragment, String paymentMode) {
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
        paymentModeDialog.show(getChildFm(), paymentModeDialog.getClass().getSimpleName());
    }

    private void bookCab (ConfirmBookingDialog dialogFragment) {
        RentalPackageModel rentalPackageModel = (RentalPackageModel) tv_package.getTag();
        RentalFareModel rentalFareModel = (RentalFareModel) tv_cab_type.getTag();
        BookCabModel bookCabModel = getMapHandler().fareEstimateModel;
        if (bookCabModel != null && rentalPackageModel != null && rentalFareModel != null) {
            if (getPromocode() != null) {
                rentalFareModel = promocodeBookCabModel.getPackages().get(0).getFares().get(0);
            }

            BookCabRequestModel model = null;
            try {
                model = new BookCabRequestModel();
                model.paymentmethod = PAYMENT_MODE.toUpperCase();
                model.booking_running_type = BOOKING_RUNNING_TYPE_RENTAL;

                model.package_id = String.valueOf(rentalPackageModel.getPackage_id());
                model.package_name = rentalPackageModel.getPackage_name();
                model.max_duration = String.valueOf(rentalPackageModel.getMax_duration());
                model.max_distance = String.valueOf(rentalPackageModel.getMax_distance());

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


            model.cab_type_id = rentalFareModel.getVehicle().getId();

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

            model.promocode = rentalFareModel.getPromocode();
            model.taxes = rentalFareModel.getTotal_tax().getTaxes();
            model.fare = rentalFareModel;

            model.toll_area_distance = bookCabModel.getToll_area_distance();
            if (bookCabModel.isScheduleBooking()) {
                model.sheduled_date = bookCabModel.getBooking_date();
            }

            displayProgressBar(false);
            getWebRequestHelper().bookCab(dialogFragment, model, this);
        }
    }

    private void checkPackagePromocode (PromocodeDialog promocodeDialog) {
        LocationModelFull.LocationModel pickUpLocationModel = getMapHandler().pickUpLocationModel;
        if (pickUpLocationModel == null) {
            showErrorMessage("Please select pickup address");
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

        requestModel.dropaddress = "N/A";
        requestModel.latitude_to = pickUpLocationModel.getLatitude();
        requestModel.longitude_to = pickUpLocationModel.getLongitude();
        requestModel.utc_offset = cityModel.getUtc_offset();

        RentalPackageModel rentalPackageModel = (RentalPackageModel) tv_package.getTag();
        RentalFareModel rentalFareModel = (RentalFareModel) tv_cab_type.getTag();
        BookCabModel fareEstimateModel = getMapHandler().fareEstimateModel;
        if (fareEstimateModel == null || rentalPackageModel == null || rentalFareModel == null)
            return;

        requestModel.package_id = rentalPackageModel.getPackage_id();
        requestModel.cab_type_id = rentalFareModel.getVehicle().getId();

        if (fareEstimateModel.isScheduleBooking()) {
            requestModel.schedule_time = fareEstimateModel.getBooking_date();
        }

        if (promocodeDialog != null) {
            requestModel.promocode = promocodeDialog.getPromocode();
        }

        displayProgressBar(false);
        getWebRequestHelper().check_package_promocode(requestModel, promocodeDialog, this);


    }


    @Override
    public void onWebRequestResponse (WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);


        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_CHECK_PACKAGE_PROMOCODE:
                    handleCheckPackagePromocodeResponse(webRequest);
                    break;
                case ID_BOOK_CAB:
                    handleBookCabResponse(webRequest);
                    break;
            }
        } else {

            if (webRequest.getWebRequestId() == ID_CHECK_PACKAGE_PROMOCODE) {
                BookCabResponseModel responseModel =
                        webRequest.getResponsePojo(BookCabResponseModel.class);
                if (responseModel != null && responseModel.isError() &&
                        responseModel.getCode() == 0) {
                    showRemainingPaymentDialog(responseModel);
                    return;
                }
            } else if (webRequest.getWebRequestId() == ID_BOOK_CAB)
            {
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

    private void showRemainingPaymentDialog (BookCabResponseModel responseModel) {
        RemainingPaymentDialog runningBookingDialog = null;
        try {
            runningBookingDialog = new RemainingPaymentDialog(getMainActivity());
            runningBookingDialog.setBookCabModel(responseModel.getData());
            runningBookingDialog.show(getChildFm(), runningBookingDialog.getClass().getSimpleName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void handleCheckPackagePromocodeResponse (WebRequest webRequest)
    {
        PromocodeDialog promocodeDialog = webRequest.getExtraData(KEY_PROMOCODE_DIALOG);
        if (promocodeDialog != null) {
            promocodeDialog.dismiss();
        }

        BookCabResponseModel responseModel = webRequest.getResponsePojo(BookCabResponseModel.class);
        if (responseModel == null) return;

        BookCabModel data = responseModel.getData();
        if (data != null) {
            this.promocodeBookCabModel = data;
            setFareData();
        }
    }


    private void showDataDialog (String title, final TextView textView, final List list) {
        if (list.size() == 0) return;
        final DataDialog dialog = new DataDialog();
        dialog.setDataList(list);
        dialog.setTitle(title);
        dialog.setOnItemSelectedListeners(new DataDialog.OnItemSelectedListener() {
            @Override
            public void onItemSelectedListener (int position) {
                dialog.dismiss();
                textView.setText(list.get(position).toString());
                textView.setTag(list.get(position));
                handleChangePackage();
                setFareData();
            }
        });
        dialog.show(getChildFm(), DataDialog.class.getSimpleName());
    }

    private void showRentalCabTypeDialog (String title, final TextView textView, final List list) {
        if (list.size() == 0) return;
        rentalCabTypeDialog = new RentalCabTypeDialog();
        rentalCabTypeDialog.setDataList(list);
        rentalCabTypeDialog.setTitle(title);
        rentalCabTypeDialog.setOnItemSelectedListener(new RentalCabTypeDialog.OnItemSelectedListener() {
            @Override
            public void onItemSelectedListener (int position) {
                rentalCabTypeDialog.dismiss();
                textView.setText(list.get(position).toString());
                textView.setTag(list.get(position));
                setFareData();
            }
        });
        rentalCabTypeDialog.show(getChildFm(), RentalCabTypeDialog.class.getSimpleName());
    }

    private void handleChangePackage () {
        RentalPackageModel rentalPackageModel = (RentalPackageModel) tv_package.getTag();
        RentalFareModel rentalFareModel = (RentalFareModel) tv_cab_type.getTag();

        boolean isSelected = false;
        if (rentalFareModel != null) {
            VehicleTypeModel vehicle = rentalFareModel.getVehicle();
            List<RentalFareModel> fares = rentalPackageModel.getFares();
            for (RentalFareModel fareModel : fares) {
                if (vehicle.getId() == fareModel.getVehicle().getId()) {
                    tv_cab_type.setText(fareModel.toString());
                    tv_cab_type.setTag(fareModel);
                    isSelected = true;
                    break;
                }
            }
        }

        if (!isSelected) {
            tv_cab_type.setText(getString(R.string.select_cab_type));
            tv_cab_type.setTag(null);
        }
    }


    private void handleBookCabResponse (WebRequest webRequest) {
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

    private void addWalletRechargeDialog (String msg) {
        WalletRechargeDialog rechargeDialog = WalletRechargeDialog.getNewInstance(msg);
        rechargeDialog.setConfirmationDialogListener(new WalletRechargeDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm (DialogFragment dialogFragment) {
                addMoneyFragment();
                if (confirmationDialog != null)
                    confirmationDialog.dismiss();
                dialogFragment.dismiss();
            }
        });
        rechargeDialog.show(getChildFm(), rechargeDialog.getClass().getSimpleName());
    }

    private void addMoneyFragment () {
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

    public void changePickUpAddress () {
        getMapHandler().startDriverUpdater(null);
        //   getFare(null);
    }

    public void changeDropAddress () {
        //  getFare(null);
    }

    @Override
    public void onPause () {
        super.onPause();
        getMapHandler().stopDriverUpdater();
    }

    @Override
    public void onResume () {
        super.onResume();
        getMapHandler().startDriverUpdater(null);
    }

    @Override
    public void onDestroyView () {
        getMapHandler().changeSelectedRentalPackage(null);
        getMapHandler().changeSelectedRentalFare(null);
        super.onDestroyView();
    }

    @Override
    public void onDistanceMatrixRequestStart () {

    }

    @Override
    public void onDistanceMatrixRequestEnd (List<DistanceMatrixModel> rows) {
        if (isValidActivity() && isVisible()) {
            RentalPackageModel previosRentalPackage = getMapHandler().getPreviousRentalPackage();
            if (previosRentalPackage != null) {
                if (previosRentalPackage.getFares() != null && rows != null) {
                    for (int i = 0; i < rows.size(); i++) {
                        if (i < previosRentalPackage.getFares().size()) {
                            previosRentalPackage.getFares().get(i).getVehicle().setDistanceMatrixModel(rows.get(i));
                        }
                    }
                }
            }

            updateDriverAvailableTime();
            if (confirmationDialog != null && confirmationDialog.isVisible()) {
                confirmationDialog.onDistanceMatrixRequestEnd(rows);
            }
            if (rentalCabTypeDialog != null && rentalCabTypeDialog.isVisible()) {
                rentalCabTypeDialog.onDistanceMatrixRequestEnd(rows);
            }
        }
    }


}
