package com.rider.ui.utilities;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.addressfetching.LocationModelFull;
import com.distancematrix.DistanceMatrixHandler;
import com.google.android.gms.maps.model.LatLng;
import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.models.DeviceInfoModal;
import com.rider.model.CityModel;
import com.rider.model.DriverModel;
import com.rider.model.RentalFareModel;
import com.rider.model.RentalPackageModel;
import com.rider.model.VehicleTypeModel;
import com.rider.model.request_model.DriverOnMapRequestModel;
import com.rider.model.web_response.DriversOnMapResponseModel;
import com.rider.rest.WebRequestConstants;
import com.rider.rest.WebRequestHelper;
import com.rider.ui.MyApplication;
import com.rider.ui.main.MainActivity;
import com.utilities.DeviceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapDriversHelper implements WebRequestConstants {

    public static final int DRIVER_UPDATE_INTERVAL = 10000;
    MapDriversHelperListener mapDriversHelperListener;
    DriverAvailabilityHandler driverAvailabilityHandler;
    private boolean isUpdaterStart = false;
    private Context context;
    private Map<Long, List<DriverModel>> driversmap = new HashMap<>();
    private VehicleTypeModel previousSelectedVechileType;
    private RentalPackageModel rentalPackageModel;
    private RentalFareModel rentalFareModel;
    private WebRequestHelper webRequestHelper;
    private WebRequest previousRequest;
    private WebServiceResponseListener webServiceResponseListener = new WebServiceResponseListener() {
        @Override
        public void onWebRequestCall(WebRequest webRequest) {
            webRequest.addHeader(HEADER_KEY_LANG, LANG_1);
            DeviceInfoModal deviceInfoModal = new DeviceInfoModal(context);
            webRequest.addHeader(HEADER_KEY_DEVICE_TYPE, DEVICE_TYPE_ANDROID);
            webRequest.addHeader(HEADER_KEY_DEVICE_INFO, deviceInfoModal.toString());
            webRequest.addHeader(HEADER_KEY_APP_INFO, deviceInfoModal.getAppInfo());
            webRequest.addHeader(HEADER_KEY_TOKEN, DeviceUtils.getUniqueDeviceId());
        }

        @Override
        public void onWebRequestResponse(WebRequest webRequest) {
            if (webRequest.getResponseCode() == 401 || webRequest.getResponseCode() == 412) {
                driversmap.clear();
                triggerListener(previousSelectedVechileType, null);
                return;
            }
            if (isUpdaterStart)
                handler.sendEmptyMessageDelayed(1, DRIVER_UPDATE_INTERVAL);
            if (webRequest.checkSuccess()) {
                handleDriverOnMapResponse(webRequest);
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
            getLatestDrivers();
        }
    };

    private MapDriversHelper(Context context) {
        this.context = context;
        webRequestHelper = new WebRequestHelper(context);
        driverAvailabilityHandler = DriverAvailabilityHandler.getNewInstances(context);
    }

    public static MapDriversHelper getNewInstances(Context context) {
        return new MapDriversHelper(context);
    }

    public void setMapDriversHelperListener(MapDriversHelperListener mapDriversHelperListener) {
        this.mapDriversHelperListener = mapDriversHelperListener;
    }

    public void setDistanceMatrixListener(DistanceMatrixHandler.DistanceMatrixListener distanceMatrixListener) {
        driverAvailabilityHandler.setDistanceMatrixListener(distanceMatrixListener);
    }


    public VehicleTypeModel getPreviousSelectedVechileType() {
        return previousSelectedVechileType;
    }

    public void setPreviousSelectedVechileType(VehicleTypeModel previousSelectedVechileType) {
        if (previousSelectedVechileType == null) return;
        this.previousSelectedVechileType = previousSelectedVechileType;
        List<DriverModel> driverModelList = null;
        if (driversmap.containsKey(previousSelectedVechileType.getId())) {
            driverModelList = driversmap.get(previousSelectedVechileType.getId());
        }
        triggerListener(previousSelectedVechileType, driverModelList);
    }

    public void setRentalPackageModel(RentalPackageModel rentalPackageModel) {
        this.rentalPackageModel = rentalPackageModel;
    }

    public RentalPackageModel getRentalPackageModel() {
        return rentalPackageModel;
    }

    public void setRentalFareModel(RentalFareModel rentalFareModel) {
        this.rentalFareModel = rentalFareModel;
    }

    public RentalFareModel getRentalFareModel() {
        return rentalFareModel;
    }

    public void startDriverUpdater(WebRequest webRequest) {
        stopDriverUpdater();
        if (previousSelectedVechileType != null) {
            if (webRequest != null) {
                webServiceResponseListener.onWebRequestResponse(webRequest);
            }
            isUpdaterStart = true;
            getLatestDrivers();
        }

    }

    public void stopDriverUpdater() {
        handler.removeMessages(1);
        isUpdaterStart = false;
        if (previousRequest != null) {
            previousRequest.cancel();
        }
    }

    private void getLatestDrivers() {
        MapHandler mapHandler = ((MainActivity) context).getMapHandler();
        if (mapHandler == null) return;

        if (mapHandler.pickUpLocationModel == null) {
            driversmap.clear();
            triggerListener(previousSelectedVechileType, null);
            return;
        }
        LocationModelFull.LocationModel locationModel = mapHandler.pickUpLocationModel;
        CityModel cityModel = ((MyApplication) ((MainActivity) context).getApplication())
                .getCityHelper().
                        getCityModelFromLatLng(locationModel.getLatLng());
        if (cityModel != null) {
            DriverOnMapRequestModel requestModel = new DriverOnMapRequestModel();
            requestModel.latitude = locationModel.getLatitude();
            requestModel.longitude = locationModel.getLongitude();
            requestModel.city_id = cityModel.getCity_id();
            if (previousSelectedVechileType.isRentalVehicleType()) {
                if (rentalPackageModel != null) {
                    requestModel.cab_types = rentalPackageModel.getCabTypes();
                    previousRequest = webRequestHelper.viewDriversOnMapRental(requestModel, webServiceResponseListener);
                } else {
                    previousRequest = webRequestHelper.viewDriversOnMap(requestModel, webServiceResponseListener);
                }
            } else {
                previousRequest = webRequestHelper.viewDriversOnMap(requestModel, webServiceResponseListener);
            }
        } else {
            driversmap.clear();
            triggerListener(previousSelectedVechileType, null);
        }
    }


    private void handleDriverOnMapResponse(WebRequest webRequest) {
        MapHandler mapHandler = ((MainActivity) context).getMapHandler();
        DriversOnMapResponseModel responseModel = (DriversOnMapResponseModel)
                webRequest.getResponsePojo(DriversOnMapResponseModel.class);
        if (responseModel == null) {
            driversmap.clear();
            triggerListener(previousSelectedVechileType, null);
            return;
        }
        List<VehicleTypeModel> data = responseModel.getData();
        if (data == null || data.size() == 0) {
            driversmap.clear();
            triggerListener(previousSelectedVechileType, null);
            return;
        }
        LatLng[] availableDrivers = new LatLng[data.size()];
        List<DriverModel> rentalDrivers = null;
        for (int i = 0; i < data.size(); i++) {
            VehicleTypeModel driversOnMapModel = data.get(i);
            if (driversOnMapModel.getDriver() == null || driversOnMapModel.getDriver().size() == 0) {
                availableDrivers[i] = null;
            } else {
                DriverModel driverModel = driversOnMapModel.getDriver().get(0);
                if (mapHandler != null && mapHandler.getMapFragment() != null) {
                    LatLng latLng = mapHandler.getMapFragment().getDriverLiveLocation(driverModel.getDriver_id());
                    if (latLng != null) {
                        driverModel.setLatitude(latLng.latitude);
                        driverModel.setLongitude(latLng.longitude);
                    }
                }
                availableDrivers[i] = driverModel.getPosition();
            }
            if (webRequest.getWebRequestId() == WebRequestConstants.ID_VIEW_DRIVER_ON_MAP_RENTAL) {
                if (rentalDrivers == null) {
                    rentalDrivers = new ArrayList<>();
                }
                if (rentalFareModel != null) {
                    if (rentalFareModel.getVehicle().getId() == driversOnMapModel.getId()) {
                        rentalDrivers.addAll(driversOnMapModel.getDriver());
                    }
                } else {
                    rentalDrivers.addAll(driversOnMapModel.getDriver());
                }

            } else {
                List<DriverModel> driverModelList;
                if (driversmap.containsKey(driversOnMapModel.getId())) {
                    driverModelList = driversmap.get(driversOnMapModel.getId());
                    updateDrivers(driverModelList, driversOnMapModel.getDriver());
                } else {
                    driverModelList = new ArrayList<>(driversOnMapModel.getDriver());
                    driversmap.put(driversOnMapModel.getId(), driverModelList);
                }
            }

        }
        if (webRequest.getWebRequestId() == WebRequestConstants.ID_VIEW_DRIVER_ON_MAP_RENTAL) {
            List<DriverModel> driverModelList;
            if (driversmap.containsKey(WebRequestConstants.RENTAL_CAB_TYPE_ID)) {
                driverModelList = driversmap.get(WebRequestConstants.RENTAL_CAB_TYPE_ID);
                updateDrivers(driverModelList, rentalDrivers);
            } else {
                driverModelList = new ArrayList<>(rentalDrivers);
                driversmap.put(WebRequestConstants.RENTAL_CAB_TYPE_ID, driverModelList);
            }
        }
        callDriverAvailability(availableDrivers);
        if (previousSelectedVechileType != null &&
                driversmap.containsKey(previousSelectedVechileType.getId())) {
            triggerListener(previousSelectedVechileType, driversmap.get(previousSelectedVechileType.getId()));
        } else {
            triggerListener(previousSelectedVechileType, null);
        }

    }

    private void triggerListener(VehicleTypeModel vehicleTypeModel, List<DriverModel> list) {
        if (mapDriversHelperListener != null && vehicleTypeModel != null) {
            mapDriversHelperListener.onDriverUpdate(vehicleTypeModel, list);
        }
    }

    private void updateDrivers(List<DriverModel> savedDrivers, List<DriverModel> latestDrivers) {
        List<DriverModel> operationList = new ArrayList<>(latestDrivers);
        operationList.removeAll(savedDrivers);

        List<DriverModel> operationList1 = new ArrayList<>(savedDrivers);
        operationList1.removeAll(latestDrivers);

        if (operationList1.size() > 0) {
            savedDrivers.removeAll(operationList1);
        }
        if (operationList.size() > 0) {
            savedDrivers.addAll(operationList);
        }
    }

    private void callDriverAvailability(LatLng[] availableDrivers) {
        MapHandler mapHandler = ((MainActivity) context).getMapHandler();
        if (mapHandler == null) return;
        if (mapHandler.pickUpLocationModel == null) {
            return;
        }
        LocationModelFull.LocationModel locationModel = mapHandler.pickUpLocationModel;
        driverAvailabilityHandler.getDriverAvailability(availableDrivers, locationModel.getLatLng());
    }


    public interface MapDriversHelperListener {
        void onDriverUpdate(VehicleTypeModel vehicle_type, List<DriverModel> list);
    }
}
