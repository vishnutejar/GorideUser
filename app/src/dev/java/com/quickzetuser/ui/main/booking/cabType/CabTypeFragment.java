package com.quickzetuser.ui.main.booking.cabType;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.addressfetching.LocationModelFull;
import com.distancematrix.DistanceMatrixHandler;
import com.distancematrix.DistanceMatrixModel;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.medy.retrofitwrapper.WebRequest;
import com.models.BaseModel;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.database.tables.BookingTable;
import com.quickzetuser.fcm.AppNotificationMessagingService;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.CityModel;
import com.quickzetuser.model.VehicleTypeModel;
import com.quickzetuser.model.request_model.CheckFareRequestModel;
import com.quickzetuser.model.request_model.DriverOnMapRequestModel;
import com.quickzetuser.model.web_response.BookCabResponseModel;
import com.quickzetuser.model.web_response.DriversOnMapResponseModel;
import com.quickzetuser.ui.MyApplication;
import com.quickzetuser.ui.main.booking.cabType.adapter.CabTypeAdapter;
import com.quickzetuser.ui.main.dialog.message.MessageDialog;
import com.quickzetuser.ui.main.dialog.reminingPayment.RemainingPaymentDialog;
import com.quickzetuser.ui.utilities.MapHandler;
import com.utilities.ItemClickSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by Sunil kumar yadav on 15/3/18.
 */

public class CabTypeFragment extends AppBaseFragment implements DistanceMatrixHandler.DistanceMatrixListener {

    private CabTypeAdapter adapter;
    private RecyclerView recycler_view;
    private RelativeLayout rl_cabtypes_lay;
    private LinearLayout ll_layout;
    private View bg_view_white;
    private LinearLayout ll_noService;
    private LinearLayout ll_book_a_ride;
    private ImageView iv_current_loc;
    private List<VehicleTypeModel> list = new ArrayList<>();
    private LinearLayout ll_approx_layout;
    private TextView tv_approx_travel_distance;
    private TextView tv_approx_travel_time;

    private ImageView iv_schedule_booking;
    private SwitchDateTimeDialogFragment scheduleDateTimePicker;

    private RelativeLayout rl_book_schedule;
    private TextView tv_schedule_time;
    private ImageView iv_schedule_close;

    private int openFrom = 0;
    TextView tv_normal, tv_rental, tv_outstaion;
    LinearLayout ll_select_types;
    ImageView iv_backword, iv_forward;

    public void setOpenFrom(int openFrom) {
        this.openFrom = openFrom;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_cab_type;
    }

    public MapHandler getMapHandler() {
        MapHandler mapHandler = null;
        try {
            mapHandler = getMainActivity().getMapHandler();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return mapHandler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        childFm = getChildFragmentManager();
        if (getView() == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            setupPageData();
        }
        return getView();
    }

    @Override
    public void initializeComponent() {
        recycler_view = getView().findViewById(R.id.recycler_view);
        ll_layout = getView().findViewById(R.id.ll_layout);
        rl_cabtypes_lay = getView().findViewById(R.id.rl_cabtypes_lay);
        bg_view_white = getView().findViewById(R.id.bg_view_white);
        ll_noService = getView().findViewById(R.id.ll_noService);
        ll_book_a_ride = getView().findViewById(R.id.ll_book_a_ride);
        iv_current_loc = getView().findViewById(R.id.iv_current_loc);
        iv_schedule_booking = getView().findViewById(R.id.iv_schedule_booking);
        rl_book_schedule = getView().findViewById(R.id.rl_book_schedule);
        tv_schedule_time = getView().findViewById(R.id.tv_schedule_time);
        iv_schedule_close = getView().findViewById(R.id.iv_schedule_close);
        tv_approx_travel_distance = getView().findViewById(R.id.tv_approx_travel_distance);
        tv_approx_travel_time = getView().findViewById(R.id.tv_approx_travel_time);
        ll_approx_layout = getView().findViewById(R.id.ll_approx_layout);
        tv_normal = getView().findViewById(R.id.tv_normal);
        tv_rental = getView().findViewById(R.id.tv_rental);
        tv_outstaion = getView().findViewById(R.id.tv_outstaion);
        ll_select_types = getView().findViewById(R.id.ll_select_types);
        iv_backword = getView().findViewById(R.id.iv_backword);
        iv_forward = getView().findViewById(R.id.iv_forward);

        updateViewVisibility(ll_approx_layout, View.GONE);

        iv_current_loc.setOnClickListener(this);
        iv_schedule_booking.setOnClickListener(this);
        iv_schedule_close.setOnClickListener(this);
        rl_book_schedule.setOnClickListener(this);
        ll_book_a_ride.setOnClickListener(this);
        tv_normal.setOnClickListener(this);
        tv_rental.setOnClickListener(this);
        tv_outstaion.setOnClickListener(this);
        iv_backword.setOnClickListener(this);
        iv_forward.setOnClickListener(this);

        initializeRecyclerView();
        setupPageData();
    }

    private void getBundleData() {
        try {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.getExtras() != null) {
                String booking_id = intent.getExtras().getString(AppNotificationMessagingService.KEY_BOOKING_ID);
                printLog("booking_id: " + booking_id);
                if (booking_id != null && !booking_id.equalsIgnoreCase("null")) {

                    BookCabModel bookCabModel = BookingTable.getInstance().getRunningBookingFromId(Long.parseLong(booking_id));
                    if (bookCabModel == null) return;
                    Bundle extras = intent.getExtras();
                    extras.putString(AppNotificationMessagingService.KEY_BOOKING_ID, "0");
                    intent.putExtras(extras);
                    printLog("booking_id: " + bookCabModel.getBookingId());
                    if (bookCabModel.getStatus() < 1) {
                        getMainActivity().getMapHandler().showConfirmBookingWaitingFragment(bookCabModel);
                    } else if (bookCabModel.getStatus() < 5) {
                        getMainActivity().getMapHandler().showConfirmBookingFragment(bookCabModel);
                    } else {
                        getMainActivity().clearFragmentBackStack();
                        getMainActivity().getMapHandler().showRideDetailFragment(bookCabModel, false);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setupPageData() {
        //initDateTimePicker();
        if (openFrom == 2) {
            getMapHandler().changePickUpAddress(getMapHandler().pickUpLocationModel);
        } else {
            changePickUpAddress();
        }

        approximateDataChange();
        setupLayoutObserver();
        getMapHandler().getCurrentBookingFromServer();

        getBundleData();
    }

    private void setupLayoutObserver() {
        getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setupMapPadding();
            }
        });
    }

    private void setupMapPadding() {
        int ll_layout_H = ll_layout.getHeight();
        int bg_view_white_H = bg_view_white.getHeight();
        int margin = ll_layout_H - bg_view_white_H;
        try {
            getMainActivity().getMapHandler().setMapPadding(0,
                    getMainActivity().getMapHandler().getTopMapHeight(), 0,
                    (rl_cabtypes_lay.getHeight() - margin));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void initializeRecyclerView() {
        adapter = new CabTypeAdapter(getActivity(), list);
        if (list.size() > 0) {
            VehicleTypeModel vehicleTypeModel = getMapHandler().getPreviousSelectedVehicleType();
            int index = 0;
            if (vehicleTypeModel != null) {
                index = list.indexOf(vehicleTypeModel);
            }
            adapter.setSelectedVehicleModel(index);
            VehicleTypeModel selectedVehicleModel = adapter.getSelectedVehicleModel();
            if (selectedVehicleModel != null) {
                getMapHandler().changeSelectedVehicleType(selectedVehicleModel);
                getMapHandler().startDriverUpdater(null);
            }
            updateViewVisibility(ll_select_types, View.VISIBLE);
            if (vehicleTypeModel.isOutstationVehicleType()) {
                tv_outstaion.setBackground(getResources().getDrawable(R.drawable.bg_color_primary));
                tv_rental.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                tv_normal.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                // tv_normal, tv_rental, tv_outstaion;

            } else if (vehicleTypeModel.isRentalVehicleType()) {
                tv_outstaion.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                tv_rental.setBackground(getResources().getDrawable(R.drawable.bg_color_primary));
                tv_normal.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
            } else {

                tv_outstaion.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                tv_rental.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                tv_normal.setBackground(getResources().getDrawable(R.drawable.bg_color_primary));

            }

        } else {
            updateViewVisibility(ll_select_types, View.INVISIBLE);
        }

        recycler_view.setLayoutManager(getHorizontalLayoutManager());
        recycler_view.setAdapter(adapter);

        ItemClickSupport.addTo(recycler_view).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                adapter.setSelectedVehicleModel(position);
                adapter.notifyDataSetChanged();
                VehicleTypeModel selectedVehicleModel = adapter.getSelectedVehicleModel();

                if (selectedVehicleModel == null) return;
                getMapHandler().changeSelectedVehicleType(selectedVehicleModel);
                getMapHandler().startDriverUpdater(null);
                if (selectedVehicleModel.getId() == RENTAL_CAB_TYPE_ID) {

                    tv_outstaion.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                    tv_rental.setBackground(getResources().getDrawable(R.drawable.bg_color_primary));
                    tv_normal.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));

                } else if (selectedVehicleModel.getId() == OUSTATION_CAB_TYPE_ID) {
                    tv_outstaion.setBackground(getResources().getDrawable(R.drawable.bg_color_primary));
                    tv_rental.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                    tv_normal.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                } else {
                    tv_outstaion.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                    tv_rental.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                    tv_normal.setBackground(getResources().getDrawable(R.drawable.bg_color_primary));

                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_current_loc:
                try {
                    getMainActivity().zoomToCurrentLocation();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.ll_book_a_ride:

                try {
                    VehicleTypeModel selectedVehicleModel = adapter.getSelectedVehicleModel();
                    if (selectedVehicleModel == null) return;
                    long scheculeTime = 0;
                    if (selectedVehicleModel.isOutstationVehicleType()) {
                        scheculeTime = MyApplication.getInstance().getOutstationBookingMinTime().getTimeInMillis() / 1000;
                    }
                    getFare(scheculeTime);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_book_schedule:
                try {
                    long scheduleTime = getScheduleTime();
                    if (scheduleTime == 0) {
                        showErrorMessage("Please set schedule time first");
                        return;
                    }
                    if (!checkValidScheduledTime(scheduleTime)) {
                        showErrorMessage("Schedule time expire please change.");
                        return;
                    }
                    getFare(scheduleTime / 1000);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.iv_schedule_booking:
                iv_schedule_booking.setOnClickListener(null);
                initDateTimePicker();
                //openScheduleDateTimePicker();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_schedule_booking.setOnClickListener(CabTypeFragment.this);
                    }
                }, 1000);
                break;
            case R.id.iv_schedule_close:
                iv_schedule_booking.setTag(null);
                updateScheduledUi();
                break;

            case R.id.tv_normal:
                if (list != null && list.size() > 0) {
                    VehicleTypeModel vehicleTypeModel = adapter.getSelectedVehicleModel();
                    int index = 0;
                    if (vehicleTypeModel.isOutstationVehicleType()) {
                        adapter.setSelectedVehicleModel(0);
                        adapter.notifyDataSetChanged();
                    } else if (vehicleTypeModel.isRentalVehicleType()) {
                        adapter.setSelectedVehicleModel(0);
                        adapter.notifyDataSetChanged();
                    }
                    vehicleTypeModel = adapter.getSelectedVehicleModel();
                    if (vehicleTypeModel != null) {
                        index = list.indexOf(vehicleTypeModel);
                    }
                    tv_outstaion.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                    tv_rental.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                    tv_normal.setBackground(getResources().getDrawable(R.drawable.bg_color_primary));
                    recycler_view.scrollToPosition(index);

                }

                break;
            case R.id.tv_rental:

                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {

                        if (list.get(i).isRentalVehicleType()) {
                            adapter.setSelectedVehicleModel(i);
                            adapter.notifyDataSetChanged();
                            VehicleTypeModel selectedVehicleModel = adapter.getSelectedVehicleModel();
                            if (selectedVehicleModel == null) return;
                            getMapHandler().changeSelectedVehicleType(selectedVehicleModel);
                            getMapHandler().startDriverUpdater(null);
                            tv_outstaion.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                            tv_rental.setBackground(getResources().getDrawable(R.drawable.bg_color_primary));
                            tv_normal.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                            recycler_view.scrollToPosition(i);
                            break;
                        }

                    }
                }

                break;

            case R.id.tv_outstaion:

                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isOutstationVehicleType()) {
                            adapter.setSelectedVehicleModel(i);
                            adapter.notifyDataSetChanged();
                            VehicleTypeModel selectedVehicleModel = adapter.getSelectedVehicleModel();
                            if (selectedVehicleModel == null) return;
                            getMapHandler().changeSelectedVehicleType(selectedVehicleModel);
                            getMapHandler().startDriverUpdater(null);
                            tv_outstaion.setBackground(getResources().getDrawable(R.drawable.bg_color_primary));
                            tv_rental.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                            tv_normal.setBackground(getResources().getDrawable(R.drawable.bg_color_gray));
                            recycler_view.scrollToPosition(i);
                            break;
                        }

                    }
                }


                break;

            case R.id.iv_forward:
                /*try {*/

                    LinearLayoutManager layoutManager = ((LinearLayoutManager) recycler_view.getLayoutManager());
                    int last_pos = layoutManager.findLastVisibleItemPosition();
//                int size=list.size();
                    if (last_pos + 3 < list.size()) {
                        recycler_view.scrollToPosition(last_pos + 3);
                    }


               /* }catch (Exception e){
                    e.printStackTrace();
                }*/

                break;

            case R.id.iv_backword:
//                try {
                    LinearLayoutManager layoutManager1 = ((LinearLayoutManager) recycler_view.getLayoutManager());
                    int last_pos2 = layoutManager1.findLastVisibleItemPosition();
                    if(last_pos2-3<=0){
                        return;
                    }else{
                        recycler_view.scrollToPosition(last_pos2-3);
                    }
              /*  }catch (Exception e){
                    e.printStackTrace();
                }

*/
                break;


        }
    }


    private void getFare(long scheduleTime) throws IllegalAccessException {
        LocationModelFull.LocationModel pickUpLocationModel = getMapHandler().pickUpLocationModel;
        LocationModelFull.LocationModel dropLocationModel = getMapHandler().dropLocationModel;
        if (pickUpLocationModel == null) {
            showErrorMessage("Please select pickup address");
            return;
        }

        CityModel pickupCityModel = getMyApplication().getCityHelper().
                getCityModelFromLatLng(pickUpLocationModel.getLatLng());
        if (!isValidObject(pickupCityModel)) {
            showErrorMessage("Service is not available in your city");
            return;
        }

        VehicleTypeModel selectedVehicleModel = adapter.getSelectedVehicleModel();
        if (selectedVehicleModel == null) return;
        if (selectedVehicleModel.isSharingVehicleType()) {
            if (dropLocationModel == null) {
                showErrorMessage("Please select drop address");
                return;
            }
        }
        CheckFareRequestModel requestModel = new CheckFareRequestModel();
        requestModel.latitude_from = pickUpLocationModel.getLatitude();
        requestModel.longitude_from = pickUpLocationModel.getLongitude();
        requestModel.pickupaddress = pickUpLocationModel.getFulladdress();
        requestModel.city_id = pickupCityModel.getCity_id();


        if (!selectedVehicleModel.isRentalVehicleType()) {

            CityModel dropCityModel = null;
            if (dropLocationModel != null) {
                dropCityModel = getMyApplication().getCityHelper().
                        getCityModelFromLatLng(dropLocationModel.getLatLng());
            }
            if (selectedVehicleModel.isOutstationVehicleType()) {
                if (dropLocationModel == null) {
                    getMainActivity().showMessageDialog(getResources().getString(R.string.app_name),
                            "Please set drop address.");
                    return;
                }

                if (dropCityModel != null && dropCityModel.getCity_id() == pickupCityModel.getCity_id()) {
                    getMainActivity().showMessageDialog(getString(R.string.app_name),
                            "Drop address must be in different city.");
                    return;
                }
                requestModel.trip_return_by_time = (scheduleTime + (6 * 60 * 60));
                requestModel.drop_country = dropLocationModel.getCountry();
                requestModel.drop_admin_level1 = dropLocationModel.getState();
                requestModel.drop_admin_level2 = dropLocationModel.getCity();
                requestModel.drop_locality = dropLocationModel.getTownship();
                requestModel.dropaddress = dropLocationModel.getFulladdress();
                requestModel.latitude_to = dropLocationModel.getLatitude();
                requestModel.longitude_to = dropLocationModel.getLongitude();

            } else {

                if (dropCityModel != null && (dropCityModel.getCity_id() != pickupCityModel.getCity_id())) {
                    getMainActivity().showMessageDialog(getString(R.string.app_name),
                            "Drop address must be in same city.");
                    return;
                } else if (dropCityModel == null && dropLocationModel != null) {
                    String dropCity = dropLocationModel.getCity();
                    String pickupCity = pickupCityModel.getCity_name();
                    if (!dropCity.equalsIgnoreCase(pickupCity)) {
                        getMainActivity().showMessageDialog(getString(R.string.app_name),
                                "Drop address must be in same city.");
                        return;
                    }
                }
                if (dropLocationModel == null) {
                    requestModel.dropaddress = "N/A";
                    requestModel.latitude_to = pickUpLocationModel.getLatitude();
                    requestModel.longitude_to = pickUpLocationModel.getLongitude();
                } else {

                    requestModel.dropaddress = dropLocationModel.getFulladdress();
                    requestModel.latitude_to = dropLocationModel.getLatitude();
                    requestModel.longitude_to = dropLocationModel.getLongitude();
                }
            }


        } else {
            requestModel.dropaddress = "N/A";
            requestModel.latitude_to = pickUpLocationModel.getLatitude();
            requestModel.longitude_to = pickUpLocationModel.getLongitude();
        }


        requestModel.cab_type_id = selectedVehicleModel.getId();
        requestModel.utc_offset = pickupCityModel.getUtc_offset();
        requestModel.schedule_time = scheduleTime;

        displayProgressBar(false);
        if (selectedVehicleModel.isRentalVehicleType()) {
            getWebRequestHelper().check_package(requestModel, null, this);
        } else if (selectedVehicleModel.isOutstationVehicleType()) {
            getWebRequestHelper().check_outstation_package(requestModel, null, this);
        } else if (selectedVehicleModel.isSharingVehicleType()) {
            getWebRequestHelper().check_sharing_package(requestModel, null, this);
        } else {
            getWebRequestHelper().check_fare(requestModel, null, this);
        }

    }

    public void changePickUpAddress() {
        getMapHandler().stopDriverUpdater();
        if (getMapHandler().pickUpLocationModel == null) return;
        LocationModelFull.LocationModel locationModel = getMapHandler().pickUpLocationModel;
        CityModel cityModel = null;
        try {
            cityModel = getMyApplication().getCityHelper().
                    getCityModelFromLatLng(locationModel.getLatLng());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (!isValidObject(cityModel)) {
            list.clear();
            adapter.setSelectedVehicleModel(-1);
            updateViewVisibility(ll_noService, View.VISIBLE);
            adapter.notifyDataSetChanged();
            setupLayoutObserver();
        } else {
            if (list.size() == 0)
                displayProgressBar(false);
            DriverOnMapRequestModel requestModel = new DriverOnMapRequestModel();
            requestModel.latitude = locationModel.getLatitude();
            requestModel.longitude = locationModel.getLongitude();
            requestModel.city_id = cityModel.getCity_id();
            getWebRequestHelper().viewDriversOnMap(requestModel, this);
        }
    }


    public void changeDropAddress() {

    }

    public void approximateDataChange() {
        if (getMapHandler().durationDistance == null || getMapHandler().durationDistance.size() == 0) {
            updateViewVisibility(ll_approx_layout, View.GONE);
            return;
        }
        Object[] data = getMapHandler().durationDistance.get(0);
        long distance = (long) data[0];
        long duration = (long) data[1];
        BaseModel baseModel = new BaseModel() {
        };
        tv_approx_travel_distance.setText("Travel distance : " + baseModel.getValidDistanceText(distance));
        tv_approx_travel_time.setText("Travel time : " + baseModel.getValidTimeText((long) Math.ceil((double) duration / 60)));
        updateViewVisibility(ll_approx_layout, View.VISIBLE);
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);

        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_VIEW_DRIVER_ON_MAP:
                    handleDriverOnMapResponse(webRequest);
                    break;
                case ID_CHECK_FARE:
                    handleCheckFareResponse(webRequest);
                    break;
                case ID_CHECK_PACKAGE:
                    handleRentalPackageResponse(webRequest);
                    break;
                case ID_CHECK_OUTSTATION_PACKAGE:
                    handleOutstationPackageResponse(webRequest);
                    break;
                case ID_CHECK_SHARING_PACKAGE:
                    handleSharingPackageResponse(webRequest);
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
            } else if (webRequest.getWebRequestId() == ID_CHECK_PACKAGE) {
                BookCabResponseModel responseModel =
                        webRequest.getResponsePojo(BookCabResponseModel.class);
                if (responseModel != null && responseModel.isError() &&
                        responseModel.getCode() == 0) {
                    showRemainingPaymentDialog(responseModel);
                    return;
                }
            } else if (webRequest.getWebRequestId() == ID_CHECK_SHARING_PACKAGE) {
                BookCabResponseModel responseModel =
                        webRequest.getResponsePojo(BookCabResponseModel.class);
                if (responseModel != null && responseModel.isError() &&
                        responseModel.getCode() == 0) {
                    showRemainingPaymentDialog(responseModel);
                    return;
                }
            }
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                showErrorMessage(msg);
            }
        }

    }

    private void handleRentalPackageResponse(WebRequest webRequest) {
        BookCabResponseModel responseModel =
                webRequest.getResponsePojo(BookCabResponseModel.class);
        if (responseModel == null) return;
        BookCabModel data = responseModel.getData();
        if (data != null) {
            try {
                getMainActivity().getMapHandler().showFareEstimateRentalFragment(0, data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleOutstationPackageResponse(WebRequest webRequest) {
        BookCabResponseModel responseModel =
                webRequest.getResponsePojo(BookCabResponseModel.class);
        if (responseModel == null) return;
        BookCabModel data = responseModel.getData();
        if (data != null) {
            try {
                getMainActivity().getMapHandler().showFareEstimateOutstationFragment(0, data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSharingPackageResponse(WebRequest webRequest) {
        BookCabResponseModel responseModel =
                webRequest.getResponsePojo(BookCabResponseModel.class);
        if (responseModel == null) return;
        BookCabModel data = responseModel.getData();
        if (data != null) {
            try {
                getMainActivity().getMapHandler().showFareEstimateSharingFragment(0, data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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

    private void handleDriverOnMapResponse(WebRequest webRequest) {

        DriversOnMapResponseModel responseModel = webRequest.getResponsePojo(DriversOnMapResponseModel.class);
        List<VehicleTypeModel> data = responseModel.getData();
        for (VehicleTypeModel datum : data) {
            if (list != null) {
                int index = list.indexOf(datum);
                if (index >= 0) {
                    datum.setDistanceMatrixModel(list.get(index).getDistanceMatrixModel());
                }
            }
        }

        list.clear();
        if (data == null || data.size() == 0) {
            adapter.setSelectedVehicleModel(-1);
            if (ll_noService.getVisibility() != View.VISIBLE)
                ll_noService.setVisibility(View.VISIBLE);
            updateViewVisibility(ll_select_types, View.INVISIBLE);
        } else {
            if (ll_noService.getVisibility() != View.GONE)
                ll_noService.setVisibility(View.GONE);
            updateViewVisibility(ll_select_types, View.VISIBLE);
            list.addAll(data);
            if (adapter.getSelectedVehicleModel() == null) {
                adapter.setSelectedVehicleModel(getDefaultSelectedType());
            }
        }
        adapter.notifyDataSetChanged();
        setupLayoutObserver();
        VehicleTypeModel selectedVehicleModel = adapter.getSelectedVehicleModel();
        if (selectedVehicleModel == null) return;
        MapHandler mapHandler = getMapHandler();
        if (mapHandler == null) return;
        mapHandler.changeSelectedVehicleType(selectedVehicleModel);
        mapHandler.startDriverUpdater(webRequest);
    }

    public VehicleTypeModel getSelectedVehicleType() {
        if (adapter == null) return null;
        return adapter.getSelectedVehicleModel();
    }

    private int getDefaultSelectedType() {
        if (list == null || list.size() == 0) return -1;
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            VehicleTypeModel vehicleTypeModel = list.get(i);
            if (vehicleTypeModel.getDriver() != null && vehicleTypeModel.getDriver().size() > 0) {
                index = i;
                break;
            }
        }
        return index;
    }


    private void handleCheckFareResponse(WebRequest webRequest) {
        BookCabResponseModel responseModel =
                webRequest.getResponsePojo(BookCabResponseModel.class);
        if (responseModel == null) return;
        BookCabModel data = responseModel.getData();
        if (data != null) {
            try {
                getMainActivity().getMapHandler().showFareEstimateFragment(0, data);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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
        if (list != null && rows != null) {
            for (int i = 0; i < rows.size(); i++) {
                if (i < list.size()) {
                    list.get(i).setDistanceMatrixModel(rows.get(i));
                }
            }
            if (getActivity() != null && isVisible()) {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public VehicleTypeModel getVehicleTypeData(VehicleTypeModel vehicleTypeModel) {
        if (list != null && vehicleTypeModel != null) {
            int index = list.indexOf(vehicleTypeModel);
            if (index >= 0) {
                return list.get(index);
            }
        }
        return null;
    }


    private void updateScheduledUi() {
        Calendar calendar = (Calendar) iv_schedule_booking.getTag();
        if (calendar == null) {
            rl_book_schedule.setVisibility(View.GONE);
        } else {
            BaseModel baseModel = new BaseModel() {
            };
            tv_schedule_time.setText(baseModel.getFormattedCalendar(BaseModel.DATE_TIME_TWO,
                    (calendar.getTimeInMillis() / 1000)));
            rl_book_schedule.setVisibility(View.VISIBLE);
        }
    }

    private long getScheduleTime() {
        Calendar calendar = (Calendar) iv_schedule_booking.getTag();
        if (calendar == null) {
            return 0;
        }
        return calendar.getTimeInMillis();
    }

    private void initDateTimePicker() {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.SECOND, 0);
        currentCalendar.set(Calendar.MILLISECOND, 0);
        long currentMillis = currentCalendar.getTimeInMillis();

        final Date minDate = new Date(currentMillis + SCHEDULE_BOOKING_MIN_TIME);
        final Date maxDate = new Date(currentMillis + SCHEDULE_BOOKING_MAX_TIME_30);

        scheduleDateTimePicker = SwitchDateTimeDialogFragment.newInstance(
                getString(R.string.select_date_and_time),
                getString(R.string.done),
                getString(R.string.cancel)
        );
        scheduleDateTimePicker.startAtCalendarView();
        scheduleDateTimePicker.set24HoursMode(false);
        scheduleDateTimePicker.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if (calendar.getTimeInMillis() >= minDate.getTime() && calendar.getTimeInMillis() <= maxDate.getTime()) {
                    iv_schedule_booking.setTag(calendar);
                    updateScheduledUi();
                } else {
                    iv_schedule_booking.setTag(null);
                    updateScheduledUi();
                    String msg = "Time should be between " + minDate.toString() + " and " + maxDate.toString();
                    showMessageDialog(getResources().getString(R.string.app_name), msg);
                }
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                updateScheduledUi();
            }
        });


        Date defaultTime = new Date(currentMillis + SCHEDULE_BOOKING_MIN_TIME);
        if (iv_schedule_booking.getTag() != null) {
            Calendar alreadySelectedCalendar = ((Calendar) iv_schedule_booking.getTag());
            alreadySelectedCalendar.set(Calendar.SECOND, 0);
            alreadySelectedCalendar.set(Calendar.MILLISECOND, 0);
            if (alreadySelectedCalendar.getTimeInMillis() < defaultTime.getTime()) {
                alreadySelectedCalendar.setTimeInMillis(defaultTime.getTime());
            }
            defaultTime = alreadySelectedCalendar.getTime();
        }

        scheduleDateTimePicker.setDefaultDateTime(defaultTime);
        scheduleDateTimePicker.setMinimumDateTime(minDate);
        scheduleDateTimePicker.setMaximumDateTime(maxDate);
        scheduleDateTimePicker.show(getChildFm(), scheduleDateTimePicker.getClass().getSimpleName());
    }

    public void showMessageDialog(String title, String message) {
        MessageDialog messageDialog = MessageDialog.getNewInstance(title, message);
        messageDialog.show(getChildFm(), MessageDialog.class.getSimpleName());
    }


    /* private void initDateTimePicker() {
         scheduleDateTimePicker = SwitchDateTimeDialogFragment.newInstance(
                 getString(R.string.select_date_and_time),
                 getString(R.string.done),
                 getString(R.string.cancel)
         );
         scheduleDateTimePicker.startAtCalendarView();
         scheduleDateTimePicker.set24HoursMode(false);
         scheduleDateTimePicker.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
             @Override
             public void onPositiveButtonClick(Date date) {
                 Calendar calendar = Calendar.getInstance();
                 calendar.setTime(date);
                 calendar.set(Calendar.SECOND, 0);
                 calendar.set(Calendar.MILLISECOND, 0);
                 iv_schedule_booking.setTag(calendar);
                 updateScheduledUi();
             }

             @Override
             public void onNegativeButtonClick(Date date) {
                 updateScheduledUi();
             }
         });
     }
 */
    private void openScheduleDateTimePicker() {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.SECOND, 0);
        currentCalendar.set(Calendar.MILLISECOND, 0);
        long currentMillis = currentCalendar.getTimeInMillis();
        Date defaultTime = new Date(currentMillis + SCHEDULE_BOOKING_MIN_TIME);
        if (iv_schedule_booking.getTag() != null) {
            Calendar alreadySelectedCalendar = ((Calendar) iv_schedule_booking.getTag());
            alreadySelectedCalendar.set(Calendar.SECOND, 0);
            alreadySelectedCalendar.set(Calendar.MILLISECOND, 0);
            if (alreadySelectedCalendar.getTimeInMillis() < defaultTime.getTime()) {
                alreadySelectedCalendar.setTimeInMillis(defaultTime.getTime());
            }
            defaultTime = alreadySelectedCalendar.getTime();
        }

        scheduleDateTimePicker.setDefaultDateTime(defaultTime);
        scheduleDateTimePicker.setMinimumDateTime(new Date(currentMillis + SCHEDULE_BOOKING_MIN_TIME));
        scheduleDateTimePicker.setMaximumDateTime(new Date(currentMillis + SCHEDULE_BOOKING_MAX_TIME));
        scheduleDateTimePicker.show(getChildFm(), scheduleDateTimePicker.getClass().getSimpleName());
    }

    private boolean checkValidScheduledTime(long time) {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(Calendar.SECOND, 0);
        currentCalendar.set(Calendar.MILLISECOND, 0);
        long currentMillis = currentCalendar.getTimeInMillis();
        Date minTime = new Date(currentMillis + SCHEDULE_BOOKING_MIN_TIME);
        Date maxTime = new Date(currentMillis + SCHEDULE_BOOKING_MAX_TIME_30);

        return time >= minTime.getTime() && time <= maxTime.getTime();
    }

    @Override
    public boolean handleOnBackPress() {
        if (rl_book_schedule.getVisibility() == View.VISIBLE) {
            iv_schedule_close.performClick();
            return true;
        }
        return false;
    }
}
