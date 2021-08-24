package com.rider.ui.main.booking.confirmBooking;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.addressfetching.LocationModelFull;
import com.customviews.DiamondDrawable;
import com.distancematrix.DistanceMatrixModel;
import com.fcm.NotificationModal;
import com.fcm.PushNotificationHelper;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceBaseResponseModel;
import com.pusher.PusherHandler;
import com.pusher.PusherListener;
import com.rider.R;
import com.rider.appBase.AppBaseFragment;
import com.rider.database.tables.BookingTable;
import com.rider.fcm.AppNotificationModel;
import com.rider.model.BookCabModel;
import com.rider.model.DriverModel;
import com.rider.model.FareModel;
import com.rider.model.TaxiModel;
import com.rider.model.VehicleTypeModel;
import com.rider.model.pusher.PusherEtaModel;
import com.rider.model.pusher.PusherLocationModel;
import com.rider.model.request_model.ChangeDropRequestModel;
import com.rider.model.request_model.SosRequestModel;
import com.rider.model.web_response.BookingDetailResponseModel;
import com.rider.rest.WebRequestHelper;
import com.rider.ui.main.booking.addresschooser.AddressChooserFragment;
import com.rider.ui.main.dialog.cancelBooking.CancelBookingDialog;
import com.rider.ui.main.dialog.confirmationDialog.ConfirmationDialog;
import com.rider.ui.utilities.BookingStatusHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.rider.appBase.AppBaseMapFragment.getPusherEtaDropEventName;
import static com.rider.appBase.AppBaseMapFragment.getPusherEtaPickupEventName;
import static com.rider.appBase.AppBaseMapFragment.getPusherLocationEventName;

/**
 * Created by Sunil kumar yadav on 14/3/18.
 */

public class ConfirmBookingFragment extends AppBaseFragment implements
        BookingStatusHelper.BookingStatusHelperListener,
        PushNotificationHelper.PushNotificationHelperListener, PusherListener {

    BookingStatusHelper bookingStatusHelper;
    PushNotificationHelper pushNotificationHelper;
    private BookCabModel bookCabModel;
    private CancelBookingDialog cancelBookingDialog;
    private ImageView iv_current_loc;
    private ImageView iv_vehicle_type;
    private ImageView iv_sos;
    private RelativeLayout rl_otp_lay;
    private TextView tv_otp;
    private TextView tv_vehicle_color_model;
    private TextView tv_vehicle_no;
    private ImageView iv_driver_image;
    private TextView tv_driver_name;
    private TextView tv_driver_Rating;
    private LinearLayout ll_call_driver;
    private LinearLayout ll_cancel_ride;
    private LinearLayout ll_change_drop;
    private LinearLayout ll_view;
    private TextView tv_fare_estimate;

    private ProgressBar pb_time;
    private TextView eta_time;
    private ProgressBar pb_distance;
    private TextView eta_distance;

    public BookCabModel getBookCabModel() {
        return bookCabModel;
    }

    public void setBookCabModel(BookCabModel bookCabModel) {
        this.bookCabModel = bookCabModel;
    }

    AddressChooserFragment.ChooseAddressListener chooseAddressListener = new AddressChooserFragment.ChooseAddressListener() {
        @Override
        public void OnChooseSuccess(TextView textView, LocationModelFull.LocationModel locationModel) {
            if (locationModel != null) {
                ChangeDropRequestModel requestModel = new ChangeDropRequestModel();
                requestModel.dropaddress = locationModel.getFulladdress();
                requestModel.latitude_to = locationModel.getLatitude();
                requestModel.longitude_to = locationModel.getLongitude();
                requestModel.booking_id = getBookCabModel().getBooking_id();
                displayProgressBar(false);
                getWebRequestHelper().changeBookingDropLocation(requestModel, ConfirmBookingFragment.this);
            }
        }
    };

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_confirm_booking;
    }

    public String getTitle() {
        if (getActivity() == null) return "";
        if (bookCabModel == null || bookCabModel.getStatus() < 4) {
            return getResources().getString(R.string.text_title_pickup_arriving);
        }
        return getResources().getString(R.string.text_title_on_trip);
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        try {
            if (getMyApplication() != null) {
                pushNotificationHelper = getMyApplication().getPushNotificationHelper();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        iv_current_loc = getView().findViewById(R.id.iv_current_loc);

        rl_otp_lay = getView().findViewById(R.id.rl_otp_lay);
        rl_otp_lay.setBackground(new DiamondDrawable(getActivity()));
        tv_otp = getView().findViewById(R.id.tv_otp);
        iv_vehicle_type = getView().findViewById(R.id.iv_vehicle_type);
        tv_vehicle_no = getView().findViewById(R.id.tv_vehicle_no);
        tv_vehicle_color_model = getView().findViewById(R.id.tv_vehicle_color_model);
        iv_driver_image = getView().findViewById(R.id.iv_driver_image);
        tv_driver_name = getView().findViewById(R.id.tv_driver_name);
        tv_driver_Rating = getView().findViewById(R.id.tv_driver_Rating);
        ll_call_driver = getView().findViewById(R.id.ll_call_driver);
        ll_cancel_ride = getView().findViewById(R.id.ll_cancel_ride);
        ll_change_drop = getView().findViewById(R.id.ll_change_drop);
        tv_fare_estimate = getView().findViewById(R.id.tv_fare_estimate);
        ll_view = getView().findViewById(R.id.ll_view);
        iv_sos = getView().findViewById(R.id.iv_sos);
        pb_time = getView().findViewById(R.id.pb_time);
        eta_time = getView().findViewById(R.id.eta_time);
        pb_distance = getView().findViewById(R.id.pb_distance);
        eta_distance = getView().findViewById(R.id.eta_distance);

        updateViewVisibility(ll_change_drop, View.GONE);
        updateViewVisibility(iv_sos, View.GONE);
        updateViewVisibility(tv_otp, View.GONE);

        setUpData(bookCabModel);
        iv_sos.setOnClickListener(this);
        iv_current_loc.setOnClickListener(this);
        ll_call_driver.setOnClickListener(this);
        ll_cancel_ride.setOnClickListener(this);
        ll_change_drop.setOnClickListener(this);

        setupLayoutObserver();
    }

    private void setUpData(BookCabModel bookCabModel) {
        if (bookCabModel == null) return;
        VehicleTypeModel vehicle = bookCabModel.getVehicle();
        if (vehicle != null) {
            Picasso.get().load(vehicle.getImage_selected()).into(iv_vehicle_type);
        }
        DriverModel driver = bookCabModel.getDriver();
        if (driver != null) {
            tv_driver_name.setText(driver.getFullName());
            tv_driver_Rating.setText(driver.getRating());
            Picasso.get().load(driver.getImage()).into(iv_driver_image);
        }
        TaxiModel taxi = bookCabModel.getTaxi();
        if (taxi != null) {
            tv_vehicle_color_model.setText(taxi.getTaxiColorWithModelName());
            try {
                String license_plate_no = taxi.getLicense_plate_no();
                tv_vehicle_no.setText(license_plate_no);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tv_vehicle_color_model.setText("");
            tv_vehicle_no.setText("");
        }

       // tv_fare_estimate.setText(currency_symbol + bookCabModel.getFare().getEstimated_fare());

        if (bookCabModel.hasDropAddress()) {
            FareModel fare = bookCabModel.getFare();
            if (fare != null)
                tv_fare_estimate.setText(currency_symbol + fare.getEstimated_fare());
        }
        else {
            tv_fare_estimate.setText("N/A");
        }

        if (bookCabModel.getOtp().length() == 4) {
            if (tv_otp.getVisibility() != View.VISIBLE)
                tv_otp.setVisibility(View.VISIBLE);
            tv_otp.setText("OTP" + "\n" + bookCabModel.getOtp());
        }

        handleBookingStatusUpdate(false);
        addMarkers(bookCabModel);
    }

    private void drawRoute(boolean needLoader) {
        if (!isValidObject(bookCabModel)) return;

        LatLng destination = null;
        if (bookCabModel.getStatus() == 1) {
            destination = bookCabModel.getPickupLatLng();
        } else if (bookCabModel.getStatus() == 4) {
            if (bookCabModel.hasDropAddress()) {
                destination = bookCabModel.getDropLatLng();
            }
        }

        try {
            LatLng origin = null;
            DriverModel driverModel = bookCabModel.getDriver();
            origin = driverModel.getPosition();
            Marker marker = getMainActivity().getMapHandler().getDriverMarker(driverModel);
            if (marker != null) {
                origin = marker.getPosition();
            }
            if (origin != null && destination != null) {
                getMainActivity().getMapHandler().drawRoute(
                        origin, destination, needLoader);
            } else {
                getMainActivity().getMapHandler().clearRoute();
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void addMarkers(BookCabModel bookCabModel) {
        try {
            getMainActivity().getMapHandler().clearMap();

            if (!isValidObject(bookCabModel)) return;
            if (bookCabModel.getPickupLatLng() != null) {
                getMainActivity().getMapHandler().addMarker("PICKUP", bookCabModel.getPickupLatLng(),
                        BitmapDescriptorFactory.fromResource(R.mipmap.pickup_marker), false, new float[]{0.5f, 1.0f});
            }

            if (!isValidObject(bookCabModel)) return;
            if (bookCabModel.hasDropAddress()) {
                if (bookCabModel.getDropLatLng() != null) {
                    getMainActivity().getMapHandler().addMarker("DROP", bookCabModel.getDropLatLng(),
                            BitmapDescriptorFactory.fromResource(R.mipmap.drop_marker), false, new float[]{0.5f, 1.0f});
                }
            }

            if (!isValidObject(bookCabModel)) return;
            DriverModel driverModel = bookCabModel.getDriver();
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.icon_driver_marker);
            getMainActivity().getMapHandler().getMapFragment().addDriverMarker(driverModel, bitmapDescriptor);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        drawRoute(true);
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
        try {
            getMainActivity().getToolBarHelper().onCreateViewFragment(this);
            getMainActivity().getMapHandler().setMapPadding(0,
                    getMainActivity().getMapHandler().getTopMapHeight(), 0,
                    ll_view.getHeight());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
            case R.id.ll_call_driver:
                addCallDialog();
                break;
            case R.id.ll_cancel_ride:
                addCancelBookingDialog();
                break;
            case R.id.ll_change_drop:
                addAddressChooserFragment(null);
                break;
            case R.id.iv_sos:
                sos();
                break;
        }
    }

    private void addAddressChooserFragment(TextView textView) {
        AddressChooserFragment fragment = new AddressChooserFragment();
        fragment.setTextView(textView);
        fragment.setChooseAddressListener(chooseAddressListener);
        int enterAnimation = R.anim.fadein;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.fadeout;
        try {
            getMainActivity().changeFragment(fragment, true, true, 0,
                    enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack,
                    false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void sos() {
        try {

            WebRequestHelper webRequestHelper = getWebRequestHelper();
            if (!isValidObject(webRequestHelper)) return;
            BookCabModel bookingModel = getBookCabModel();
            if (!isValidObject(bookingModel)) return;
            LatLng currentLatLng = getCurrentLatLng();
            if (!isValidObject(currentLatLng)) return;

            SosRequestModel requestModel = new SosRequestModel();
            requestModel.booking_id = bookingModel.getBooking_id();
            requestModel.lat = currentLatLng.latitude;
            requestModel.lng = currentLatLng.longitude;
            displayProgressBar(false);
            webRequestHelper.sos(requestModel, this);

        } catch (IllegalAccessException e) {
        }


    }

    private void addCallDialog() {
        final DriverModel driver = bookCabModel.getDriver();
        ConfirmationDialog confirmationDialog = ConfirmationDialog.getNewInstance(driver.getFullMobile(),
                "Cancel", "Call Now");
        confirmationDialog.setConfirmationDialogListener(new ConfirmationDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm(DialogFragment dialogFragment) {
                try {
                    getMainActivity().makeCall(driver.getMobileForCall());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                dialogFragment.dismiss();
            }
        });
        confirmationDialog.show(getActivity().getSupportFragmentManager(), confirmationDialog.getClass().getSimpleName());
    }

    private void addCancelBookingDialog() {
        cancelBookingDialog = CancelBookingDialog.getNewInstance("");
        cancelBookingDialog.setBookCabModel(bookCabModel);
        cancelBookingDialog.setConfirmationDialogListener(new CancelBookingDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm(DialogFragment dialogFragment, BookCabModel data) {
                bookCabModel = data;
                handleBookingStatusUpdate(true);
            }
        });
        cancelBookingDialog.show(getActivity().getSupportFragmentManager(), cancelBookingDialog.getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        PusherHandler.getInstance().addPusherListener(this);
        if (pushNotificationHelper != null) {
            pushNotificationHelper.addNotificationHelperListener(this);
        }

        try {
            bookingStatusHelper = BookingStatusHelper.getNewInstances(getMainActivity(), bookCabModel);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (bookingStatusHelper != null) {
            bookingStatusHelper.setBookingStatusHelperListener(this);
            bookingStatusHelper.startBookingUpdater();
        }
        try {
            getMainActivity().updateHeadService(true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        PusherHandler.getInstance().removePusherListener(this);
        if (pushNotificationHelper != null) {
            pushNotificationHelper.removeNotificationHelperListener(this);
        }
        if (bookingStatusHelper != null) {
            bookingStatusHelper.stopBookingUpdater(true);
        }
        try {
            getMainActivity().updateHeadService(false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBookingUpdate(BookCabModel bookCabModel) {
        if (bookCabModel != null && this.bookCabModel != null) {
            if (this.bookCabModel.getStatus() != bookCabModel.getStatus()) {
                this.bookCabModel = bookCabModel;
                handleBookingStatusUpdate(true);
            }else{
                if (bookCabModel.getFare().getEstimated_fare()!=this.bookCabModel.getFare().getEstimated_fare()){
                    this.bookCabModel = bookCabModel;
                    setUpData(this.bookCabModel);
                }
            }
        }
    }

    private void handleBookingStatusUpdate(boolean showMessage) {
        if (this.bookCabModel != null) {
            if (this.bookCabModel.getStatus() == -1) {
                revertToFareEstimateScreen();
            } else if (this.bookCabModel.getStatus() == 5) {
                revertToCabTypeScreen();
                addRideDetailFragment(this.bookCabModel);
            } else if (this.bookCabModel.getStatus() == 6) { // driver side cancel
                revertToFareEstimateScreen();
            } else if (this.bookCabModel.getStatus() == 7) {  // rider side cancel
                revertToFareEstimateScreen();
            } else if (this.bookCabModel.getStatus() == 3) { // start successfully
                BookingTable.getInstance().addOrUpdateBooking(bookCabModel);
                if (showMessage) {
                    try {
                        String msg = "Driver is arrived at pickup location.";
                        getMainActivity().showMessageDialog(getString(R.string.app_name),
                                msg);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else if (this.bookCabModel.getStatus() == 4) { // start successfully
                BookingTable.getInstance().addOrUpdateBooking(bookCabModel);
                if (!bookCabModel.hasDropAddress()) {
                    eta_time.setText("Time : N/A");
                    eta_distance.setText("Distance : N/A");
                    updateViewVisibility(pb_time, View.GONE);
                    updateViewVisibility(pb_distance, View.GONE);
                }
                updateViewVisibility(ll_cancel_ride, View.GONE);
                if (bookCabModel.isOutstationBooking() || bookCabModel.isRentalBooking()) {
                    updateViewVisibility(ll_change_drop, View.GONE);
                }else {
                    updateViewVisibility(ll_change_drop, View.VISIBLE);
                }
                updateViewVisibility(iv_sos, View.VISIBLE);
                if (cancelBookingDialog != null && cancelBookingDialog.isVisible()) {
                    cancelBookingDialog.dismiss();
                }
                if (showMessage) {
                    drawRoute(false);
                    try {
                        getMainActivity().getToolBarHelper().onCreateViewFragment(this);
                        String msg = "Trip started by Driver.";
                        getMainActivity().showMessageDialog(getString(R.string.app_name),
                                msg);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void revertToFareEstimateScreen() {
        BookingTable.getInstance().deleteBooking(bookCabModel.getBooking_id());
        if (cancelBookingDialog != null && cancelBookingDialog.isVisible()) {
            cancelBookingDialog.dismiss();
        }
        try {
            BookCabModel data = getMainActivity().getMapHandler().fareEstimateModel;
            if (data != null) {
                if (bookCabModel.getStatus() == -1 || bookCabModel.getStatus() == 7) {
                    String msg = "Sorry! All of our drivers are busy. Please try again.";
                    if (bookCabModel.getStatus() == 7) {
                        msg = "Your booking is cancelled by driver.";
                    }
                    getMainActivity().showMessageDialog(getString(R.string.app_name), msg);
                }
                getMainActivity().clearFragmentBackStack();
                if (data.isRentalBooking()) {
                    getMainActivity().getMapHandler().showFareEstimateRentalFragment(2, data);
                } else if (data.isSharingBooking()) {
                    getMainActivity().getMapHandler().showFareEstimateSharingFragment(2, data);
                } else {
                    getMainActivity().getMapHandler().showFareEstimateFragment(2, data);
                }
            } else {
                LocationModelFull.LocationModel pickUpLocationModel =
                        getMainActivity().getMapHandler().getPickUpLocationModel();
                if (pickUpLocationModel != null)
                    getMainActivity().getMapHandler().showCabTypeFragment(2, pickUpLocationModel);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void revertToCabTypeScreen() {
        try {
            LocationModelFull.LocationModel pickUpLocationModel = getMainActivity().getMapHandler().getPickUpLocationModel();
            getMainActivity().getMapHandler().showCabTypeFragment(2, pickUpLocationModel);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void addRideDetailFragment(BookCabModel bookCabModel) {
        BookingTable.getInstance().addOrUpdateBooking(bookCabModel);
        try {
            getMainActivity().getMapHandler().showRideDetailFragment(bookCabModel, true);
        } catch (IllegalAccessException e) {

        }
    }

    @Override
    public void onPushNotificationReceived(NotificationModal notificationModal) {
        AppNotificationModel appNotificationModel = (AppNotificationModel) notificationModal;
        if (appNotificationModel.getBookingId() == null ||
                (appNotificationModel.getBookingId() != this.bookCabModel.getBooking_id())) return;

        if (appNotificationModel.isDriverCompletedBookingNotification()) {
            if (appNotificationModel.getStatus() == null) return;
            if (this.bookCabModel.getStatus() != appNotificationModel.getStatus()) {
                if (bookingStatusHelper != null) {
                    bookingStatusHelper.startBookingUpdater();
                }
            }
        } else if (appNotificationModel.isDriverCancelAcceptBookingNotification()) {
            if (appNotificationModel.getStatus() == null) return;
            if (this.bookCabModel.getStatus() != appNotificationModel.getStatus()) {
                this.bookCabModel.setStatus(appNotificationModel.getStatus());
                handleBookingStatusUpdate(true);

            }
        } else if (appNotificationModel.isDriveStartBookingNotification()) {
            if (appNotificationModel.getStatus() == null) return;
            if (this.bookCabModel.getStatus() != appNotificationModel.getStatus()) {
                this.bookCabModel.setStatus(appNotificationModel.getStatus());
                handleBookingStatusUpdate(true);
            }
        } else if (appNotificationModel.isDriverArrivedNotification()) {
            if (appNotificationModel.getStatus() == null) return;
            if (this.bookCabModel.getStatus() != appNotificationModel.getStatus()) {
                this.bookCabModel.setStatus(appNotificationModel.getStatus());
                handleBookingStatusUpdate(true);
            }
        }
    }


    @Override
    public void channelConnected(String channel) {

    }

    @Override
    public void pusherError(String message, Exception e) {
        e.printStackTrace();

    }

    @Override
    public void messageReceived(String channelName, String eventName, Object data) {
        if (eventName != null && !eventName.trim().isEmpty()) {
            if (eventName.equals(getPusherLocationEventName())) {
                try {
                    final PusherLocationModel locationModel = new Gson().fromJson((String) data, PusherLocationModel.class);
                    if (locationModel == null) return;
                    if (!isValidObject(bookCabModel)) return;
                    DriverModel driverModel = bookCabModel.getDriver();
                    if (!isValidObject(driverModel) ||
                            driverModel.getDriver_id() != locationModel.user_id) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                boolean needRouteFatchAgain = getMainActivity().getMapHandler().
                                        isNeedRouteFatchAgain(new LatLng(locationModel.latitude, locationModel.longitude));
                                if (needRouteFatchAgain) {
                                    drawRoute(false);
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            } else if (eventName.equals(getPusherEtaPickupEventName())) {
                try {
                    final PusherEtaModel pusherEtaModel = new Gson().fromJson((String) data, PusherEtaModel.class);
                    if (pusherEtaModel == null) return;
                    if (!isValidObject(bookCabModel)) return;
                    DriverModel driverModel = bookCabModel.getDriver();
                    if (bookCabModel.getBooking_id() != pusherEtaModel.booking_id || !isValidObject(driverModel) ||
                            driverModel.getDriver_id() != pusherEtaModel.user_id) return;
                    if (bookCabModel.getStatus() >= 4) return;
                    DistanceMatrixModel distanceMatrixModel = pusherEtaModel.eta;
                    if (bookCabModel == null || distanceMatrixModel == null) return;
                    List<DistanceMatrixModel.DestinationModel> destinationModelList = distanceMatrixModel.getDestinationModelList();
                    if (isValidObject(destinationModelList) && destinationModelList.size() > 0) {
                        final DistanceMatrixModel.DestinationModel destinationModel = destinationModelList.get(0);
                        if (isValidActivity() && isVisible()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateViewVisibility(pb_distance, View.GONE);
                                    updateViewVisibility(pb_time, View.GONE);
                                    eta_distance.setText("Distance : " + destinationModel.getDistance_text());
                                    eta_time.setText("Time : " + destinationModel.getDuration_text());
                                }
                            });
                        }
                    }

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            } else if (eventName.equals(getPusherEtaDropEventName())) {
                try {
                    final PusherEtaModel pusherEtaModel = new Gson().fromJson((String) data, PusherEtaModel.class);
                    if (pusherEtaModel == null) return;
                    if (!isValidObject(bookCabModel)) return;
                    DriverModel driverModel = bookCabModel.getDriver();
                    if (bookCabModel.getBooking_id() != pusherEtaModel.booking_id || !isValidObject(driverModel) ||
                            driverModel.getDriver_id() != pusherEtaModel.user_id) return;
                    if (bookCabModel.getStatus() >= 5) return;
                    DistanceMatrixModel distanceMatrixModel = pusherEtaModel.eta;
                    if (bookCabModel == null || distanceMatrixModel == null) return;
                    List<DistanceMatrixModel.DestinationModel> destinationModelList = distanceMatrixModel.getDestinationModelList();
                    if (isValidObject(destinationModelList) && destinationModelList.size() > 0) {
                        final DistanceMatrixModel.DestinationModel destinationModel = destinationModelList.get(0);
                        if (isValidActivity() && isVisible()) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateViewVisibility(pb_distance, View.GONE);
                                    updateViewVisibility(pb_time, View.GONE);
                                    eta_distance.setText("Distance : " + destinationModel.getDistance_text());
                                    eta_time.setText("Time : " + destinationModel.getDuration_text());
                                }
                            });
                        }
                    }

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        super.onWebRequestResponse(webRequest);
        dismissProgressBar();
        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_SOS:
                    handleSosResponse(webRequest);
                    break;
                case ID_CHANGE_BOOKING_DROP_LOCATION:
                    handleChangeDropLocationResponse(webRequest);
                    break;
            }
        } else {
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                webRequest.showInvalidResponse(msg);
            }
        }
    }

    private void handleSosResponse(WebRequest webRequest) {
        if (isValidObject(webRequest)) {
            WebServiceBaseResponseModel baseResponsePojo = webRequest.getBaseResponsePojo();
            if (isValidObject(baseResponsePojo))
                showCustomToast(baseResponsePojo.getMessage());
        }
    }

    private void handleChangeDropLocationResponse(WebRequest webRequest) {
        BookingDetailResponseModel responseModel = webRequest.getResponsePojo(BookingDetailResponseModel.class);
        if (responseModel != null) {
            if (!responseModel.isError()) {
                List<BookCabModel> data = responseModel.getData();
                if (data != null && data.size() > 0) {
                    bookCabModel = data.get(0);
                    BookingTable.getInstance().addOrUpdateBooking(bookCabModel);
                    setUpData(bookCabModel);
                    drawRoute(false);
                }
            }
        }
    }
}
