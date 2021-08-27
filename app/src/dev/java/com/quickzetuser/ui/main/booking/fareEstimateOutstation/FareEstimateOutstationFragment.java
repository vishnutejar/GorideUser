package com.quickzetuser.ui.main.booking.fareEstimateOutstation;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addressfetching.LocationModelFull;
import com.distancematrix.DistanceMatrixHandler;
import com.distancematrix.DistanceMatrixModel;
import com.medy.retrofitwrapper.WebRequest;
import com.models.BaseModel;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseDialogFragment;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.database.tables.BookingTable;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.CityModel;
import com.quickzetuser.model.OutstationFareModel;
import com.quickzetuser.model.OutstationPackageModel;
import com.quickzetuser.model.VehicleTypeModel;
import com.quickzetuser.model.request_model.BookCabRequestModel;
import com.quickzetuser.model.request_model.CheckFareRequestModel;
import com.quickzetuser.model.web_response.BaseWebServiceModelResponse;
import com.quickzetuser.model.web_response.BookCabResponseModel;
import com.quickzetuser.ui.main.booking.fareEstimateOutstation.adapter.OutstationCabTypeAdapter;
import com.quickzetuser.ui.main.dialog.confirmBooking.ConfirmBookingDialog;
import com.quickzetuser.ui.main.dialog.outstationtimepicker.OutstationTimeDialog;
import com.quickzetuser.ui.main.dialog.promocode.PromocodeDialog;
import com.quickzetuser.ui.main.dialog.reminingPayment.RemainingPaymentDialog;
import com.quickzetuser.ui.main.dialog.walletrecharge.WalletRechargeDialog;
import com.quickzetuser.ui.main.helpers.ToolBarHelper;
import com.quickzetuser.ui.main.sidemenu.payment.wallet.WalletFragment;
import com.quickzetuser.ui.utilities.MapHandler;
import com.utilities.ItemClickSupport;

import java.util.Calendar;
import java.util.List;


/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class FareEstimateOutstationFragment extends AppBaseFragment implements DistanceMatrixHandler.DistanceMatrixListener, RadioGroup.OnCheckedChangeListener {


    private TextView tv_pickup_address;
    private TextView tv_drop_address;

    RadioGroup rg_trip_type;
    RadioButton rb_oneway, rb_roundtrip;
    LinearLayout ll_leaveon;
    TextView tv_leaveon_date;

    LinearLayout ll_returnby;
    TextView tv_returnby_date;
    TextView tv_trip_detail;

    RecyclerView recycler_view;

    ConfirmBookingDialog confirmationDialog;
    OutstationCabTypeAdapter adapter;

    int userSelectedTripType = -1;

    private int openFrom = 0;
    private int selectedFarePosition = 0;

    boolean isBookingConfirmed = false;

    Handler backStackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    ToolBarHelper toolBarHelper = getMainActivity().getToolBarHelper();
                    toolBarHelper.onCreateViewFragment(FareEstimateOutstationFragment.this);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    };

    FragmentManager.OnBackStackChangedListener onBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
        @Override
        public void onBackStackChanged() {
            backStackHandler.removeMessages(1);
            backStackHandler.sendEmptyMessageDelayed(1, 100);
        }
    };


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
        return R.layout.fragment_fare_estimate_outstation;
    }

    @Override
    public int getFragmentContainerResourceId(Fragment fragment) {
        return R.id.child_container;
    }

    public void setOpenFrom(int openFrom) {
        this.openFrom = openFrom;
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        getChildFm().removeOnBackStackChangedListener(onBackStackChangedListener);
        getChildFm().addOnBackStackChangedListener(onBackStackChangedListener);

        tv_pickup_address = getView().findViewById(R.id.tv_pickup_address);
        tv_drop_address = getView().findViewById(R.id.tv_drop_address);
        rg_trip_type = getView().findViewById(R.id.rg_trip_type);
        rb_oneway = getView().findViewById(R.id.rb_oneway);
        rb_roundtrip = getView().findViewById(R.id.rb_roundtrip);
        ll_leaveon = getView().findViewById(R.id.ll_leaveon);
        tv_leaveon_date = getView().findViewById(R.id.tv_leaveon_date);
        tv_leaveon_date.setOnClickListener(this);


        ll_returnby = getView().findViewById(R.id.ll_returnby);
        tv_returnby_date = getView().findViewById(R.id.tv_returnby_date);
        tv_returnby_date.setOnClickListener(this);

        tv_trip_detail = getView().findViewById(R.id.tv_trip_detail);

        recycler_view = getView().findViewById(R.id.recycler_view);
        initializeRecyclerView();
        setFareData();
    }

    public boolean isOnewaySelected() {
        return rb_oneway.isChecked();
    }


    public int getSelectedFarePosition() {
        return selectedFarePosition;
    }

    private void initializeRecyclerView() {
        adapter = new OutstationCabTypeAdapter(null) {
            @Override
            public boolean isOnewaySelected() {
                return FareEstimateOutstationFragment.this.isOnewaySelected();
            }
        };
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view.setAdapter(adapter);
        ItemClickSupport.addTo(recycler_view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                selectedFarePosition = position;
                FareEstimateOutstationDetailFragment fragment = new FareEstimateOutstationDetailFragment();
                changeChildFragment(fragment, true, true, 0,
                        R.anim.enterfrombottom, R.anim.exittobottom, R.anim.enterfrombottom, R.anim.exittobottom, true);
            }
        });
    }


    private void setFareData() {
        BookCabModel fareEstimateModel = getMapHandler().fareEstimateModel;
        if (fareEstimateModel != null) {
            rg_trip_type.setOnCheckedChangeListener(null);
            tv_pickup_address.setText(fareEstimateModel.getPickup_address());
            tv_drop_address.setText(fareEstimateModel.getDrop_address());

            tv_leaveon_date.setText(fareEstimateModel.getFormattedBookingTime(BaseModel.TAG_TWO));
            tv_returnby_date.setText(fareEstimateModel.getFormattedRetrunByTime(BaseModel.TAG_TWO));

            rg_trip_type.clearCheck();
            rb_oneway.setChecked(false);
            rb_roundtrip.setChecked(false);
            rb_oneway.setEnabled(false);
            rb_roundtrip.setEnabled(false);
            OutstationPackageModel outstation = fareEstimateModel.getOutstation();
            if (outstation != null) {

                if (outstation.isOneway()) {
                    rb_oneway.setEnabled(true);
                    rb_oneway.setChecked(true);
                }if (outstation.isRoundtrip()) {
                    rb_roundtrip.setEnabled(true);
                    if (!rb_oneway.isChecked()) {
                        rb_roundtrip.setChecked(true);
                    }
                }

                if (userSelectedTripType != -1) {
                    if (!rb_oneway.isEnabled() && userSelectedTripType == rb_oneway.getId()) {
                        userSelectedTripType = -1;
                    }

                    if (!rb_roundtrip.isEnabled() && userSelectedTripType == rb_roundtrip.getId()) {
                        userSelectedTripType = -1;
                    }
                    if (userSelectedTripType == rb_oneway.getId() && !rb_oneway.isChecked()) {
                        rb_oneway.setChecked(true);
                    } else if (userSelectedTripType == rb_roundtrip.getId() && !rb_roundtrip.isChecked()) {
                        rb_roundtrip.setChecked(true);
                    }


                }
                rg_trip_type.setOnCheckedChangeListener(this);

            }

        }
        updateFareData();
    }


    private void updateFareData() {
        BookCabModel fareEstimateModel = getMapHandler().fareEstimateModel;
        if (fareEstimateModel != null) {
            if (rb_roundtrip.isChecked()) {
                updateViewVisibility(ll_returnby, View.VISIBLE);
                tv_trip_detail.setText(fareEstimateModel.getRoundtripMessageDetail());
            } else {
                updateViewVisibility(ll_returnby, View.GONE);
                tv_trip_detail.setText(fareEstimateModel.getOnewayMessageDetail());
            }
            OutstationPackageModel outstation = fareEstimateModel.getOutstation();
            if (outstation != null) {
                adapter.updateData(outstation.getFares());
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_leaveon_date:
                openOutStationTimePicker(tv_leaveon_date);
                break;
            case R.id.tv_returnby_date:
                openOutStationTimePicker(tv_returnby_date);
                break;

        }
    }

    private void openOutStationTimePicker(TextView textView) {
        BookCabModel fareEstimateModel = getMapHandler().fareEstimateModel;
        if (fareEstimateModel != null) {
            Calendar startCal = Calendar.getInstance();
            long booking_date = fareEstimateModel.getBooking_date();
            startCal.setTimeInMillis(booking_date * 1000);

            Calendar returnByCal = Calendar.getInstance();
            long return_by_time = fareEstimateModel.getReturn_by_time();
            returnByCal.setTimeInMillis(return_by_time * 1000);

            int openFor = OutstationTimeDialog.OPEN_FOR_ROUNDTRIP;
            if (isOnewaySelected()) {
                openFor = OutstationTimeDialog.OPEN_FOR_ONEWAY;
            }
            OutstationTimeDialog outstationTimeDialog = new OutstationTimeDialog();
            outstationTimeDialog.setOpenFor(openFor);
            if (textView.getId() == R.id.tv_returnby_date) {
                outstationTimeDialog.setDefaultPagePosition(1);
            }
            outstationTimeDialog.setStartTimeCal(startCal);
            outstationTimeDialog.setReturnByTimeCal(returnByCal);
            outstationTimeDialog.setOutstationTimeDialogListener(new OutstationTimeDialog.OutstationTimeDialogListener() {
                @Override
                public void onDateTimeSelected(AppBaseDialogFragment appBaseDialogFragment, Calendar startTimeCal, Calendar retrunByTimeCal) {
                    appBaseDialogFragment.dismiss();
                    getFare(startTimeCal, retrunByTimeCal);
                }
            });

            outstationTimeDialog.show(getChildFm(), outstationTimeDialog.getClass().getSimpleName());
        }
    }


    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);
        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_CHECK_OUTSTATION_PACKAGE:
                    handleOutstationPackageResponse(webRequest);
                    break;
                case ID_BOOK_CAB:
                    handleBookCabResponse(webRequest);
                    break;
            }
        } else {
            if (webRequest.getWebRequestId() == ID_CHECK_OUTSTATION_PACKAGE) {
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
            runningBookingDialog.show(getChildFm(), runningBookingDialog.getClass().getSimpleName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void handleOutstationPackageResponse(WebRequest webRequest) {
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
                    if (confirmBookingDialog != null && confirmBookingDialog.isVisible()) {
                        confirmBookingDialog.updateSuccessUI();
                    }
                    if (bookCabModel.isScheduleBooking()) {
                        if (bookCabModel.isValidString(bookCabModel.getSchedule_success_msg())) {
                            getMainActivity().showMessageDialog(getResources().getString(R.string.app_name),
                                    bookCabModel.getSchedule_success_msg());
                        }
                        isBookingConfirmed = true;
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
        rechargeDialog.show(getChildFm(), rechargeDialog.getClass().getSimpleName());
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

    public String getTitle() {
        String title = "Book your Outstation ride";
        if (getChildFm() != null && getChildFm().getBackStackEntryCount() > 0) {
            title = "Confirm your booking";
        }
        return title;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.rg_trip_type) {
            userSelectedTripType = checkedId;
            updateFareData();
        }
    }


    private void getFare(Calendar startTime, Calendar returnByTime) {
        LocationModelFull.LocationModel pickUpLocationModel = getMapHandler().pickUpLocationModel;
        LocationModelFull.LocationModel dropLocationModel = getMapHandler().dropLocationModel;
        if (pickUpLocationModel == null) {
            showErrorMessage("Please select pickup address");
            return;
        }

        CityModel pickupCityModel = null;
        try {
            pickupCityModel = getMyApplication().getCityHelper().
                    getCityModelFromLatLng(pickUpLocationModel.getLatLng());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (!isValidObject(pickupCityModel)) {
            showErrorMessage("Service is not available in your city");
            return;
        }

        VehicleTypeModel selectedVehicleModel = getMapHandler().getPreviousSelectedVehicleType();
        if (selectedVehicleModel == null) return;
        CheckFareRequestModel requestModel = new CheckFareRequestModel();
        requestModel.latitude_from = pickUpLocationModel.getLatitude();
        requestModel.longitude_from = pickUpLocationModel.getLongitude();
        requestModel.pickupaddress = pickUpLocationModel.getFulladdress();
        requestModel.city_id = pickupCityModel.getCity_id();
        if (dropLocationModel == null) {
            try {
                getMainActivity().showMessageDialog(getResources().getString(R.string.app_name),
                        "Please set drop address.");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return;

        } else {
            CityModel dropCityModel = null;
            try {
                dropCityModel = getMyApplication().getCityHelper().
                        getCityModelFromLatLng(dropLocationModel.getLatLng());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (dropCityModel != null && dropCityModel.getCity_id() == pickupCityModel.getCity_id()) {
                try {
                    getMainActivity().showMessageDialog(getString(R.string.app_name),
                            "Drop address must be in different city.");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return;
            } else {
                requestModel.trip_return_by_time = returnByTime.getTimeInMillis() / 1000;
                requestModel.drop_country = dropLocationModel.getCountry();
                requestModel.drop_admin_level1 = dropLocationModel.getState();
                requestModel.drop_admin_level2 = dropLocationModel.getCity();
                requestModel.drop_locality = dropLocationModel.getTownship();
            }

            requestModel.dropaddress = dropLocationModel.getFulladdress();
            requestModel.latitude_to = dropLocationModel.getLatitude();
            requestModel.longitude_to = dropLocationModel.getLongitude();
        }


        requestModel.cab_type_id = selectedVehicleModel.getId();
        requestModel.utc_offset = pickupCityModel.getUtc_offset();
        requestModel.schedule_time = startTime.getTimeInMillis() / 1000;

        displayProgressBar(false);
        getWebRequestHelper().check_outstation_package(requestModel, null, this);

    }


    public void bookCab() {

        FareEstimateOutstationDetailFragment fareEstimateOutstationDetailFragment =
                (FareEstimateOutstationDetailFragment) getChildFm().
                        findFragmentById(getFragmentContainerResourceId(new FareEstimateOutstationDetailFragment()));
        if (fareEstimateOutstationDetailFragment == null) return;

        BookCabModel bookCabModel = getMapHandler().fareEstimateModel;
        if (bookCabModel == null) return;
        OutstationPackageModel outstation = bookCabModel.getOutstation();
        if (outstation == null) return;
        OutstationFareModel outstationFareModel = outstation.getFares().get(getSelectedFarePosition());
        if (outstationFareModel == null) return;

        OutstationFareModel.FareData onewayfare = outstationFareModel.getOnewayfare();
        OutstationFareModel.FareData roundtripfare = outstationFareModel.getRoundtripfare();


        BookCabRequestModel model = null;
        try {
            model = new BookCabRequestModel();
            model.paymentmethod = fareEstimateOutstationDetailFragment.PAYMENT_MODE.toUpperCase();
            model.booking_running_type = BOOKING_RUNNING_TYPE_OUTSTATION;

            model.mobile_code = getMainActivity().getUserModel().getMobile_code();
            model.phone = getMainActivity().getUserModel().getPhone();
            model.special_instruction = "";
            model.utc_offset = bookCabModel.getUtc_offset();

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

            model.cab_type_id = outstationFareModel.getVehicle().getId();

            model.toll_area_distance = bookCabModel.getToll_area_distance();
            if (bookCabModel.isScheduleBooking()) {
                model.sheduled_date = bookCabModel.getBooking_date();
            }

            if (isOnewaySelected()) {
                model.outstation_trip_type = OUTSTAION_TRIP_ONEWAY;
                model.max_duration = String.valueOf(bookCabModel.getTotal_travel_time());
                model.max_distance = String.valueOf(onewayfare.getBase_km());
                model.taxes = onewayfare.getTotal_tax().getTaxes();
                model.fare = onewayfare;
                model.package_name = bookCabModel.getOnewayMessageDetail();

            } else {
                model.outstation_trip_type = OUTSTAION_TRIP_ROUND;
                model.max_duration = String.valueOf(bookCabModel.getTotal_travel_time_roundtrip());
                model.max_distance = String.valueOf(roundtripfare.getBase_km());
                model.taxes = roundtripfare.getTotal_tax().getTaxes();
                model.fare = roundtripfare;
                model.package_name = bookCabModel.getRoundtripMessageDetail();
            }

            displayProgressBar(false);
            getWebRequestHelper().bookCab(null, model, this);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDistanceMatrixRequestStart() {

    }

    @Override
    public void onDistanceMatrixRequestEnd(List<DistanceMatrixModel> rows) {

    }


    public void changePickUpAddress() {

    }

    public void changeDropAddress() {

    }

    @Override
    public boolean handleOnBackPress() {
        if (isBookingConfirmed) return false;
        return super.handleOnBackPress();
    }
}
