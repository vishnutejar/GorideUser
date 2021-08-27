package com.quickzetuser.ui.utilities;

import android.location.Location;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.addressfetching.AddressFetchModel;
import com.addressfetching.AddressResultReceiver;
import com.addressfetching.LocationModelFull;
import com.base.BaseFragment;
import com.customviews.GoogleMapFragment;
import com.distancematrix.DistanceMatrixHandler;
import com.distancematrix.DistanceMatrixModel;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseLocationService;
import com.quickzetuser.appBase.AppBaseMapFragment;
import com.quickzetuser.database.tables.BookingTable;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.DriverModel;
import com.quickzetuser.model.RentalFareModel;
import com.quickzetuser.model.RentalPackageModel;
import com.quickzetuser.model.VehicleTypeModel;
import com.quickzetuser.model.web_response.BookingHistoryResponseModel;
import com.quickzetuser.rest.WebRequestConstants;
import com.quickzetuser.rest.WebRequestHelper;
import com.quickzetuser.ui.main.MainActivity;
import com.quickzetuser.ui.main.booking.cabType.CabTypeFragment;
import com.quickzetuser.ui.main.booking.confirmBooking.ConfirmBookingFragment;
import com.quickzetuser.ui.main.booking.confirmbookingwaiting.ConfirmBookingWaitingFragment;
import com.quickzetuser.ui.main.booking.fareEstimate.FareEstimateFragment;
import com.quickzetuser.ui.main.booking.fareEstimateOutstation.FareEstimateOutstationFragment;
import com.quickzetuser.ui.main.booking.fareEstimateSharing.FareEstimateSharingFragment;
import com.quickzetuser.ui.main.booking.fareEstimaterentel.FareEstimateRentalFragment;
import com.quickzetuser.ui.main.booking.rideDetail.RideDetailFragment;
import com.quickzetuser.ui.main.booking.selectaddress.SelectedAddressFragment;
import com.utilities.GoogleApiClientHelper;

import java.util.List;

/**
 * Created by Sunil kumar yadav on 15/3/18.
 */

public class MapHandler implements GoogleMapFragment.GoogleMapListener,
        MapDriversHelper.MapDriversHelperListener, WebServiceResponseListener, DistanceMatrixHandler.DistanceMatrixListener {

    public static final String KEY_FRAG_CAB_TYPE = "cab_type";
    public static final String KEY_FRAG_RIDE_NOW = "ride_now";
    // relative to booking process
    public LocationModelFull.LocationModel pickUpLocationModel;
    public LocationModelFull.LocationModel dropLocationModel;
    public BookCabModel fareEstimateModel;
    public MapDriversHelper mapDriversHelper;
    public CabTypeFragment cabTypeFragment = null;
    public List<Object[]> durationDistance;
    MainActivity _activity;
    FrameLayout bottom_container, top_container, main_container;
    RelativeLayout rl_map_group;
    FrameLayout frag_map;
    int enterfromtop;
    int exittotop;
    int enterfrombottom;
    int exittobottom;


    public MapHandler(MainActivity _activity) {
        this._activity = _activity;
        mapDriversHelper = MapDriversHelper.getNewInstances(_activity);
        mapDriversHelper.setMapDriversHelperListener(this);
        mapDriversHelper.setDistanceMatrixListener(this);
        enterfromtop = R.anim.enterfromtop;
        exittotop = R.anim.exittotop;
        enterfrombottom = R.anim.enterfrombottom;
        exittobottom = R.anim.exittobottom;
        setupMap();
    }

    public LocationModelFull.LocationModel getPickUpLocationModel() {
        return pickUpLocationModel;
    }

    public void startDriverUpdater(WebRequest webRequest) {
        mapDriversHelper.startDriverUpdater(webRequest);
    }

    public void stopDriverUpdater() {
        mapDriversHelper.stopDriverUpdater();
    }

    public void changeSelectedVehicleType(VehicleTypeModel vehicleTypeModel) {
        mapDriversHelper.setPreviousSelectedVechileType(vehicleTypeModel);
        Fragment topFragment = getTopFragment();
        if (topFragment != null && top_container.getVisibility() == View.VISIBLE) {
            SelectedAddressFragment addressFragment = (SelectedAddressFragment) topFragment;
            addressFragment.changeSelectedVehicleType(vehicleTypeModel);
            drawRouteBetweenPickupAndDrop();
        }

    }

    public VehicleTypeModel getPreviousSelectedVehicleType() {
        return mapDriversHelper.getPreviousSelectedVechileType();
    }

    public void changeSelectedRentalPackage(RentalPackageModel rentalPackageModel) {
        mapDriversHelper.setRentalPackageModel(rentalPackageModel);
    }

    public RentalPackageModel getPreviousRentalPackage() {
        return mapDriversHelper.getRentalPackageModel();
    }


    public void changeSelectedRentalFare(RentalFareModel rentalFareModel) {
        mapDriversHelper.setRentalFareModel(rentalFareModel);
    }

    private void setupMap() {
        rl_map_group = _activity.findViewById(R.id.rl_map_group);
        bottom_container = _activity.findViewById(R.id.bottom_container);
        frag_map = _activity.findViewById(R.id.frag_map);
        top_container = _activity.findViewById(R.id.top_container);
        main_container = _activity.findViewById(R.id.main_container);

        rl_map_group.setVisibility(View.VISIBLE);
        addMapFragment();
    }


    public void showLayout() {
        _activity.clearFragmentBackStack();
        rl_map_group.setVisibility(View.VISIBLE);
        Fragment fragment = getBottomFragment();
        if (fragment != null) {
            _activity.getToolBarHelper().onCreateViewFragment((BaseFragment) fragment);
        }
    }

    public void hideLayout() {
        rl_map_group.setVisibility(View.GONE);
        main_container.setVisibility(View.VISIBLE);
    }

    public boolean onBackPressed() {
        Fragment fragment = _activity.getSupportFragmentManager()
                .findFragmentById(R.id.bottom_container);
        if (fragment != null && fragment instanceof BaseFragment) {
            BaseFragment fragment1 = (BaseFragment) fragment;

            if (fragment1 instanceof CabTypeFragment) {
                return (fragment1).handleOnBackPress();
            } else if (fragment1 instanceof FareEstimateFragment) {
                removeBottonFragment();
                showCabTypeFragment(1, pickUpLocationModel);
                return true;
            } else if (fragment1 instanceof FareEstimateRentalFragment) {
                removeBottonFragment();
                showCabTypeFragment(1, pickUpLocationModel);
                return true;
            } else if (fragment1 instanceof FareEstimateOutstationFragment) {
                if (!fragment1.handleOnBackPress()) {
                    removeBottonFragment();
                    showCabTypeFragment(1, pickUpLocationModel);
                }
                return true;
            } else if (fragment1 instanceof FareEstimateSharingFragment) {
                if (!fragment1.handleOnBackPress()) {
                    removeBottonFragment();
                    showCabTypeFragment(1, pickUpLocationModel);
                }
                return true;
            } else if (fragment1 instanceof ConfirmBookingFragment) {
                removeBottonFragment();
                showCabTypeFragment(2, pickUpLocationModel);
                return true;
            }

            if (fragment1 instanceof ConfirmBookingWaitingFragment) {
                return (fragment1).handleOnBackPress();
            }
        }

        return false;
    }

    public void removeBottonFragment() {
        Fragment fragment = _activity.getFm().findFragmentById(R.id.bottom_container);
        if (fragment == null) return;
        try {
            FragmentTransaction ft = _activity.getNewFragmentTransaction();
            ft.remove(fragment);
            ft.commit();
        } catch (IllegalStateException e) {
        }
    }

    public Fragment getTopFragment() {
        Fragment fragment = _activity.getFm().findFragmentById(R.id.top_container);
        return fragment;
    }

    public Fragment getMainFragment() {
        Fragment fragment = _activity.getFm().findFragmentById(R.id.main_container);
        return fragment;
    }

    public AppBaseMapFragment getMapFragment() {
        return (AppBaseMapFragment) _activity.getFm().findFragmentById(frag_map.getId());
    }

    public Fragment getBottomFragment() {
        Fragment fragment = _activity.getFm().findFragmentById(R.id.bottom_container);
        return fragment;
    }

    public int getTopMapHeight() {
        int topFragmentHeight = 5;
        Fragment topFragment = getTopFragment();
        if (topFragment != null && top_container.getVisibility() == View.VISIBLE) {
            topFragmentHeight = topFragment.getView().getHeight();
        }
        return topFragmentHeight;
    }

    public Location getRealCurrentLocation() {
        return getMapFragment().getRealCurrentLocation();
    }

    public LatLng getRealCurrentLatLng() {
        return getMapFragment().getRealCurrentLatLng();
    }

    public void drawRoute(LatLng from, LatLng to, boolean needLoader) {
        getMapFragment().drawRoute(from, to, needLoader);

    }

    public Marker getDriverMarker(DriverModel driverModel) {
        return getMapFragment().getDriverMarker(driverModel);
    }

    public boolean isNeedRouteFatchAgain(LatLng point) {
        return getMapFragment().isNeedRouteFatchAgain(point);
    }

    public void addMarker(String key, LatLng latLng, BitmapDescriptor bitmapDescriptor, boolean isFlat, float[] anchor) {
        if (anchor == null || anchor.length != 2) {
            getMapFragment().addMarker(key, latLng, bitmapDescriptor, isFlat, new float[]{0.5f, 0.5f});
        } else {
            getMapFragment().addMarker(key, latLng, bitmapDescriptor, isFlat, anchor);
        }
    }

    public void removeMarker(String key) {
        getMapFragment().removeMarker(key);
    }


    public void updateMapWithBound(LatLngBounds latLngBounds, int boundPadding) {
        getMapFragment().updateMapWithBound(latLngBounds, boundPadding);
    }

    public void clearMap() {
        getMapFragment().clearMap();
    }

    public void clearRoute() {
        getMapFragment().clearRoute();
    }

    public void setMapPadding(int left, int top, int right, int bottom) {
        getMapFragment().setMapPadding(left, top, right, bottom);
        getMapFragment().updateMapPaddingChange();
    }


    public void addMapFragment() {
        AppBaseMapFragment mapFragment = new AppBaseMapFragment();
        mapFragment.setGoogleMapListener(this);

        _activity.clearFragmentBackStack(0);

        try {
            FragmentTransaction ft = _activity.getNewFragmentTransaction();
            ft.add(frag_map.getId(), mapFragment, mapFragment.getClass().getSimpleName());
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addMainFragment(Fragment fragment) {
//        _activity.clearFragmentBackStack(0);
        try {
            FragmentTransaction ft = _activity.getNewFragmentTransaction();
            ft.add(frag_map.getId(), fragment, fragment.getClass().getSimpleName());
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addTopFragment(Fragment fragment) {
        try {
            FragmentTransaction ft = _activity.getFm().beginTransaction();
            ft.setCustomAnimations(enterfromtop, exittotop, enterfromtop, exittotop);
            ft.replace(R.id.top_container, fragment, fragment.getClass().getSimpleName());
            ft.commit();
        } catch (IllegalStateException e) {

        }
    }

    public void addBottomFragment(Fragment f) {
        try {
            FragmentTransaction ft = _activity.getFm().beginTransaction();
            ft.setCustomAnimations(enterfrombottom, exittobottom, enterfrombottom, exittobottom);
            ft.replace(R.id.bottom_container, f, f.getClass().getSimpleName());
            ft.commit();
        } catch (IllegalStateException e) {

        }
    }

    public void removeFragment(Fragment f) {
        if (f != null) {
            try {
                FragmentTransaction ft = _activity.getFm().beginTransaction();
                ft.remove(f);
                ft.commit();
            } catch (IllegalStateException e) {

            }
        }
    }

    public void showSelectAddressFragment(LocationModelFull.LocationModel locationModel) {
        top_container.setVisibility(View.VISIBLE);
        SelectedAddressFragment addressChooserFrag = new SelectedAddressFragment();
        addressChooserFrag.setLocationModel(locationModel);
        addTopFragment(addressChooserFrag);
        _activity.getToolBarHelper().onCreateViewFragment(addressChooserFrag);
    }


    public void showCabTypeFragment(int openFrom, LocationModelFull.LocationModel locationModel) {
        top_container.setVisibility(View.VISIBLE);
        pickUpLocationModel = locationModel;
        bottom_container.setVisibility(View.VISIBLE);
        if (this.cabTypeFragment == null) {
            this.cabTypeFragment = new CabTypeFragment();
        }
        this.cabTypeFragment.setOpenFrom(openFrom);
        addBottomFragment(this.cabTypeFragment);
        _activity.getToolBarHelper().onCreateViewFragment(this.cabTypeFragment);


    }

    public void showFareEstimateFragment(int openFrom, BookCabModel data) {
        if (data == null) return;
        top_container.setVisibility(View.VISIBLE);
        fareEstimateModel = data;
        bottom_container.setVisibility(View.VISIBLE);
        FareEstimateFragment fragment = new FareEstimateFragment();
        fragment.setOpenFrom(openFrom);
        addBottomFragment(fragment);
        _activity.getToolBarHelper().onCreateViewFragment(fragment);
    }

    public void showFareEstimateRentalFragment(int openFrom, BookCabModel data) {
        if (data == null) return;
        top_container.setVisibility(View.VISIBLE);
        fareEstimateModel = data;
        bottom_container.setVisibility(View.VISIBLE);
        FareEstimateRentalFragment fragment = new FareEstimateRentalFragment();
        fragment.setOpenFrom(openFrom);
        addBottomFragment(fragment);
        _activity.getToolBarHelper().onCreateViewFragment(fragment);
    }

    public void showFareEstimateOutstationFragment(int openFrom, BookCabModel data) {
        if (data == null) return;
        top_container.setVisibility(View.VISIBLE);
        fareEstimateModel = data;
        bottom_container.setVisibility(View.VISIBLE);
        final FareEstimateOutstationFragment fragment = new FareEstimateOutstationFragment();
        fragment.setOpenFrom(openFrom);
        addBottomFragment(fragment);
        frag_map.postDelayed(new Runnable() {
            @Override
            public void run() {
                _activity.getToolBarHelper().onCreateViewFragment(fragment);
            }
        }, 100);

    }

    public void showFareEstimateSharingFragment(int openFrom, BookCabModel data) {
        if (data == null) return;
//        top_container.setVisibility(View.VISIBLE);
        fareEstimateModel = data;
        bottom_container.setVisibility(View.VISIBLE);
        final FareEstimateSharingFragment fragment = new FareEstimateSharingFragment();
        fragment.setOpenFrom(openFrom);
        addBottomFragment(fragment);
        frag_map.postDelayed(new Runnable() {
            @Override
            public void run() {
                _activity.getToolBarHelper().onCreateViewFragment(fragment);
            }
        }, 100);

    }

    public void showConfirmBookingWaitingFragment(BookCabModel data) {
        bottom_container.setVisibility(View.VISIBLE);
        ConfirmBookingWaitingFragment fragment = new ConfirmBookingWaitingFragment();
        fragment.setBookCabModel(data);
        addBottomFragment(fragment);
        top_container.setVisibility(View.GONE);
        _activity.getToolBarHelper().onCreateViewFragment(fragment);
    }

    public void showConfirmBookingFragment(BookCabModel bookCabModel) {
        ConfirmBookingFragment fragment = new ConfirmBookingFragment();
        fragment.setBookCabModel(bookCabModel);
        addBottomFragment(fragment);
        top_container.setVisibility(View.GONE);
        _activity.onCreateViewFragment(fragment);
    }

    public void showRideDetailFragment(BookCabModel bookCabModel, boolean clearAll) {
        RideDetailFragment fragment = new RideDetailFragment();
        fragment.setBookCabModel(bookCabModel);
        _activity.changeFragment(fragment, true, clearAll, 0, false);
    }


    public AppBaseLocationService getLocationService() {
        AppBaseMapFragment mapFragment = getMapFragment();
        if (mapFragment == null) return null;
        return mapFragment.getLocationService();
    }

    public GoogleApiClientHelper getGoogleApiClientHelper() {
        if (getLocationService() != null)
            return getLocationService().getGoogleApiClientHelper();
        return null;
    }

    private void drawRouteBetweenPickupAndDrop() {
        VehicleTypeModel previousSelectedVehicleType = getPreviousSelectedVehicleType();

        if (pickUpLocationModel != null) {
            addMarker("PICKUP", pickUpLocationModel.getLatLng(),
                    BitmapDescriptorFactory.fromResource(R.mipmap.pickup_marker), false, new float[]{0.5f, 1.0f});
        } else {
            removeMarker("PICKUP");
        }
        if (dropLocationModel != null) {
            if (previousSelectedVehicleType != null && previousSelectedVehicleType.isRentalVehicleType()) {
                removeMarker("DROP");
            } else {
                addMarker("DROP", dropLocationModel.getLatLng(),
                        BitmapDescriptorFactory.fromResource(R.mipmap.drop_marker), false, new float[]{0.5f, 1.0f});
            }

        } else {
            removeMarker("DROP");
        }
        approximateDataChange(null);
        if (pickUpLocationModel != null && dropLocationModel != null) {

            if (previousSelectedVehicleType != null && previousSelectedVehicleType.isRentalVehicleType()) {
                clearRoute();
                if (pickUpLocationModel != null) {
                    updateMapWithBound(pickUpLocationModel.getBoundArround(1),
                            _activity.getResources().getDimensionPixelSize(R.dimen.dp20));
                }
            } else {
                drawRoute(new LatLng(pickUpLocationModel.getLatitude(),
                                pickUpLocationModel.getLongitude()),
                        new LatLng(dropLocationModel.getLatitude(),
                                dropLocationModel.getLongitude()), false);



            }
        } else {
            clearRoute();
            if (pickUpLocationModel != null) {
                updateMapWithBound(pickUpLocationModel.getBoundArround(1),
                        _activity.getResources().getDimensionPixelSize(R.dimen.dp20));
            }
        }
    }


    public void changePickUpAddress(LocationModelFull.LocationModel locationModel) {
        pickUpLocationModel = locationModel;
        Fragment fragment = getBottomFragment();
        if (fragment instanceof CabTypeFragment) {
            ((CabTypeFragment) fragment).changePickUpAddress();
        } else if (fragment instanceof FareEstimateFragment) {
            ((FareEstimateFragment) fragment).changePickUpAddress();
        } else if (fragment instanceof FareEstimateRentalFragment) {
            ((FareEstimateRentalFragment) fragment).changePickUpAddress();
        } else if (fragment instanceof FareEstimateOutstationFragment) {
            ((FareEstimateOutstationFragment) fragment).changePickUpAddress();
        }
        drawRouteBetweenPickupAndDrop();
    }

    public void changeDropAddress(LocationModelFull.LocationModel locationModel) {
        dropLocationModel = locationModel;
        Fragment fragment = getBottomFragment();
        if (fragment instanceof CabTypeFragment) {
            ((CabTypeFragment) fragment).changeDropAddress();
        } else if (fragment instanceof FareEstimateFragment) {
            ((FareEstimateFragment) fragment).changeDropAddress();
        } else if (fragment instanceof FareEstimateRentalFragment) {
            ((FareEstimateRentalFragment) fragment).changeDropAddress();
        } else if (fragment instanceof FareEstimateOutstationFragment) {
            ((FareEstimateOutstationFragment) fragment).changeDropAddress();
        }
        drawRouteBetweenPickupAndDrop();

    }

    public void clearDropAddress() {
        dropLocationModel = null;
        Fragment fragment = getBottomFragment();
        if (fragment instanceof CabTypeFragment) {
            ((CabTypeFragment) fragment).changeDropAddress();
        } else if (fragment instanceof FareEstimateFragment) {
            ((FareEstimateFragment) fragment).changeDropAddress();
        } else if (fragment instanceof FareEstimateRentalFragment) {
            ((FareEstimateRentalFragment) fragment).changeDropAddress();
        } else if (fragment instanceof FareEstimateOutstationFragment) {
            ((FareEstimateOutstationFragment) fragment).changeDropAddress();
        }
        drawRouteBetweenPickupAndDrop();
    }

    @Override
    public void onLocationAvailable(Location location) {
        _activity.displayProgressBar(false, "Getting location detail");
        rl_map_group.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getGoogleApiClientHelper() != null) {
                    setupMapFirstTime();
                }
            }
        }, 300);

       // _activity.getNotificationData();
    }

    private void setupMapFirstTime() {

        getGoogleApiClientHelper().fetchCurrentAddress(new AddressResultReceiver(_activity, new Handler()) {
            @Override
            public void onAddressFetch(boolean success, AddressFetchModel addressFetchModel) {
                if (success) {
                    _activity.dismissProgressBar();
                    try {
                        if (addressFetchModel.getLocationModel() != null && addressFetchModel.getLocationModel().getLatLng() != null) {
                            double distance = getGoogleApiClientHelper().
                                    getDistanceFromMyLocation(addressFetchModel.getLocationModel().getLatLng());
                            if (distance < 30) {
                                showSelectAddressFragment(addressFetchModel.getLocationModel());
                                _activity.getNotificationData();
                            } else {
                                setupMapFirstTime();
                            }
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    setupMapFirstTime();
                }
            }
        });
    }

    @Override
    public void onLocationSelected(AddressFetchModel addressFetchModel) {
        _activity.printLog("update Address onLocationSelected");
    }

    @Override
    public void onCameraIdeal(Location location) {
        _activity.printLog("onCameraIdeal");
    }

    @Override
    public void onDriverUpdate(VehicleTypeModel vehicle_type, List<DriverModel> list) {
        if (vehicle_type == null && list == null) return;
        getMapFragment().onDriverUpdate(vehicle_type, list);

    }

    public void getCurrentBookingFromServer() {
        WebRequestHelper webRequestHelper = _activity.getWebRequestHelper();
        webRequestHelper.getCurrentBookings(this);
    }

    @Override
    public void onWebRequestCall(WebRequest webRequest) {
        _activity.onWebRequestCall(webRequest);
    }

    @Override
    public void onWebRequestPreResponse(WebRequest webRequest) {

    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        _activity.dismissProgressBar();
        if (_activity.checkUnAuthCode(webRequest)) return;


        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case WebRequestConstants.ID_CURRENT_BOOKING:
                    handleGetCurrentBookingResponse(webRequest);
                    break;
            }
        } else {
            if (webRequest.getWebRequestId() == WebRequestConstants.ID_CURRENT_BOOKING) {
                getCurrentBookingFromServer();
                return;
            }
            String msg = webRequest.getErrorMessageFromResponse();
            if (_activity.isValidString(msg)) {
                webRequest.showInvalidResponse(msg);
            }
        }
    }

    private void handleGetCurrentBookingResponse(WebRequest webRequest) {
        BookingHistoryResponseModel responseModel = webRequest.getResponsePojo(BookingHistoryResponseModel.class);
        if (responseModel != null) {
            List<BookCabModel> bookCabList = responseModel.getData();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(0);
            if (bookCabList != null) {
                if (bookCabList.size() > 0) {
                    for (BookCabModel bookCabModel : bookCabList) {

                        stringBuilder.append(",").append(bookCabModel.getBooking_id());

                        BookingTable.getInstance().addOrUpdateBooking(bookCabModel);
                    }
                }
            }
            String currentBookings = stringBuilder.toString().trim();
            if (responseModel.isValidString(currentBookings)) {
                BookingTable.getInstance().deleteUnUsedBookings(currentBookings);
            }
        }
        Fragment fragment = _activity.getLatestFragment();
        if (fragment != null && fragment instanceof RideDetailFragment) {
            return;
        }
        if (getBottomFragment() != null && getBottomFragment() instanceof ConfirmBookingFragment) {
            return;
        }
        _activity.updateHeadService(false);
    }

    @Override
    public void onDistanceMatrixRequestStart() {

    }

    @Override
    public void onDistanceMatrixRequestEnd(List<DistanceMatrixModel> rows) {
        if (getPreviousRentalPackage() == null) {
            if (cabTypeFragment != null) {
                cabTypeFragment.onDistanceMatrixRequestEnd(rows);
            }
        }
        Fragment fragment = getBottomFragment();
        if (fragment != null) {
            if (fragment instanceof FareEstimateFragment) {
                if (getPreviousRentalPackage() == null) {
                    ((FareEstimateFragment) fragment).onDistanceMatrixRequestEnd(rows);
                }
            } else if (fragment instanceof FareEstimateRentalFragment) {
                ((FareEstimateRentalFragment) fragment).onDistanceMatrixRequestEnd(rows);
            } else if (fragment instanceof FareEstimateOutstationFragment) {
                ((FareEstimateOutstationFragment) fragment).onDistanceMatrixRequestEnd(rows);
            }
        }
    }

    public void approximateDataChange(List<Object[]> data) {
        this.durationDistance = data;
        Fragment fragment = getBottomFragment();
        if (fragment instanceof CabTypeFragment) {
            ((CabTypeFragment) fragment).approximateDataChange();
        }
    }

}
