package com.quickzetuser.appBase;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.customviews.GoogleMapFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.models.DeviceScreenModel;
import com.pusher.PusherHandler;
import com.pusher.PusherListener;
import com.quickzetuser.R;
import com.quickzetuser.model.DriverModel;
import com.quickzetuser.model.VehicleTypeModel;
import com.quickzetuser.model.pusher.PusherLocationModel;
import com.quickzetuser.ui.MyApplication;
import com.quickzetuser.ui.main.MainActivity;
import com.quickzetuser.ui.utilities.MapDriversHelper;
import com.quickzetuser.ui.utilities.MapHandler;
import com.quickzetuser.ui.utilities.MapRouteHandler;
import com.utilities.GoogleApiClientHelper;
import com.utilities.Utils;
import com.utilities.marker.MarkerAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by ubuntu on 10/4/18.
 */

public class  AppBaseMapFragment extends GoogleMapFragment
        implements LocationListener, MapRouteHandler.MapRouteListener,
        MapDriversHelper.MapDriversHelperListener, PusherListener {

    public static final int REQUEST_CHECK_SETTINGS = 1000;
    private static final String LOCATION_BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    OnCompleteListener<LocationSettingsResponse> onCompleteListener = new OnCompleteListener<LocationSettingsResponse>() {
        @Override
        public void onComplete (@NonNull Task<LocationSettingsResponse> task) {

            try {
                LocationSettingsResponse response =
                        task.getResult(ApiException.class);

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        ResolvableApiException resolvableApiException = (ResolvableApiException) exception;
                        try {
                            if (!Utils.isGpsProviderEnabled(getActivity())) {
                                if (getActivity() != null) {
                                    resolvableApiException.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                }
                            }
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;

                }
            }

        }
    };
    Handler gpsCheckHandler = new Handler();
    private boolean isServiceBound = false;
    private int circle_center_color;
    private int circle_edge_color;
    private Marker my_location_marker;
    private Map<String, Marker> markerMap = new HashMap<>();
    private Map<Long, Marker> driverMarkerMap = new HashMap<>();
    private AppBaseLocationService locationService;
    Runnable gpsRunnable = new Runnable() {
        @Override
        public void run () {
            if (locationService != null) {
                Task<LocationSettingsResponse> locationSettingsResponseTask = locationService.checkLocationSetting();
                locationSettingsResponseTask.addOnCompleteListener(onCompleteListener);
            }
        }
    };
    private GroundOverlay my_location_overlay;
    private MapRouteHandler mapRouteHandler;
    private Dialog alertDialogProgressBar;
    private BroadcastReceiver gpsStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive (Context context, Intent intent) {
            if (intent.getAction().matches(LOCATION_BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (!Utils.isGpsProviderEnabled(getActivity())) {
                        checkLocationSetting();
                    }
                }
            }
        }
    };
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected (ComponentName name) {
            isServiceBound = false;
        }

        @Override
        public void onServiceConnected (ComponentName name, IBinder service) {
            isServiceBound = true;
            AppBaseLocationService.LocationBinder locationBinder = (AppBaseLocationService.LocationBinder) service;
            locationService = locationBinder.getLocationService();
            setGoogleApiClientHelper(locationService.getGoogleApiClientHelper());
            if (!Utils.isGpsProviderEnabled(getActivity())) {
                checkLocationSetting();
            }
        }
    };

    public static String getPusherLocationEventName () {
        return "client-driver_location";
    }

    public static String getPusherEtaPickupEventName () {
        return "client-driver_etapickup";
    }

    public static String getPusherEtaDropEventName () {
        return "client-driver_etadrop";
    }

    private void registerGpsStateReceiver () {
        if (gpsStateReceiver != null)
            getActivity().registerReceiver(gpsStateReceiver, new IntentFilter(LOCATION_BROADCAST_ACTION));
    }

    private void unRegisterGpsStateReceiver () {
        if (gpsStateReceiver != null)
            getActivity().unregisterReceiver(gpsStateReceiver);
    }

    public MainActivity getMainActivity () {
        return ((MainActivity) getActivity());
    }

    public MapHandler getMapHandler () {
        return getMainActivity().getMapHandler();
    }

    @Override
    public View onCreateView (LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        circle_center_color = getResources().getColor(R.color.map_my_location_color);
        circle_edge_color = getResources().getColor(R.color.map_my_location_acc_color);
        bindLocationService();
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    public void unBindLocationService () {
        PusherHandler.getInstance().destroyPusher();
        if (isServiceBound) {
            getActivity().getApplication().unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    public void bindLocationService () {
        PusherHandler.getInstance().connectPusher();
        if (getActivity() == null) return;
        ((MyApplication) getActivity().getApplication()).startLocationService(serviceConnection);
    }

    @Override
    public void onLocationAvailable (Location location) {
        super.onLocationAvailable(location);
        dismissProgressBar();
        addOrUpdateCurrentLocationMarker();
        try {
            locationService.getGoogleApiClientHelper().addLocationListener(AppBaseMapFragment.this);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public Marker getMarker (String key) {
        if (getGoogleMap() != null) {
            if (markerMap.containsKey(key)) {
                return markerMap.get(key);
            }
        }
        return null;
    }

    public Marker addMarker (String key, LatLng latLng, BitmapDescriptor bitmapDescriptor,
                             boolean isFlat, float[] anchor) {
        if (getGoogleMap() != null) {
            removeMarker(key);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(bitmapDescriptor);
            markerOptions.anchor(anchor[ 0 ], anchor[ 1 ]);
            markerOptions.flat(isFlat);
            Marker marker = getGoogleMap().addMarker(markerOptions);
            markerMap.put(key, marker);
            return marker;
        }
        return null;
    }

    public void removeMarker (String key) {
        if (markerMap.containsKey(key)) {
            markerMap.get(key).remove();
            markerMap.remove(key);
        }
    }

    public void clearMarkers () {
        if (markerMap != null && markerMap.size() > 0) {
            for (Marker marker : markerMap.values()) {
                marker.remove();
            }
            markerMap.clear();
        }
    }

    public Marker getDriverMarker (DriverModel driverModel) {
        if (getGoogleMap() != null) {
            if (driverMarkerMap.containsKey(driverModel.getDriver_id())) {
                return driverMarkerMap.get(driverModel.getDriver_id());
            }
        }
        return null;
    }

    public Marker addDriverMarker (DriverModel driverModel, BitmapDescriptor bitmapDescriptor) {
        if (getGoogleMap() != null) {
            if (!driverMarkerMap.containsKey(driverModel.getDriver_id())) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(driverModel.getPosition());
                markerOptions.icon(bitmapDescriptor);
                markerOptions.anchor(0.5f, 0.5f);
                markerOptions.flat(false);
                Marker marker = getGoogleMap().addMarker(markerOptions);
                MarkerAnimation markerAnimation = new MarkerAnimation(marker);
                marker.setTag(markerAnimation);
                driverMarkerMap.put(driverModel.getDriver_id(), marker);
                subscribeDriverPusherChannel(driverModel.getDriver_id());
                return marker;
            }
        }
        return null;
    }

    public void removeDriverMarker (long key) {
        if (driverMarkerMap.containsKey(key)) {
            driverMarkerMap.get(key).remove();
            driverMarkerMap.remove(key);
        }
    }

    public void clearDriverMarkers () {
        if (driverMarkerMap != null && driverMarkerMap.size() > 0) {
            for (Map.Entry<Long, Marker> next : driverMarkerMap.entrySet()) {
                unSubscribeDriverPusherChannel(next.getKey());
                next.getValue().remove();
            }
            driverMarkerMap.clear();
        }
    }

    public void clearMap () {
        if (getGoogleMap() != null) {
            clearMarkers();
            clearDriverMarkers();
            if (mapRouteHandler != null) {
                mapRouteHandler.removePreviousPolyLine();
            }
        }
    }

    public void clearRoute () {
        if (getGoogleMap() != null) {
            if (mapRouteHandler != null) {
                mapRouteHandler.removePreviousPolyLine();
            }
        }
    }

    public void addOrUpdateCurrentLocationMarker () {
        if (getGoogleMap() != null) {
            Location location = getRealCurrentLocation();
            if (location == null) return;

            LatLng pickup = new LatLng(location.getLatitude(), location.getLongitude());

            if (my_location_marker != null) {
                my_location_marker.setPosition(pickup);
                my_location_overlay.setPosition(pickup);
                my_location_overlay.setDimensions(location.getAccuracy() * 2);
            } else {

                GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
                groundOverlayOptions.position(pickup, location.getAccuracy() * 2);
                groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(getAccuracyCircle()));
                groundOverlayOptions.anchor(0.5f, 0.5f);
                my_location_overlay = getGoogleMap().addGroundOverlay(groundOverlayOptions);
                my_location_overlay.setZIndex(9);

                Bitmap bitmap = getMyCustomMarker();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(pickup);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                markerOptions.anchor(0.5f, 0.5f);
                markerOptions.flat(true);
                my_location_marker = getGoogleMap().addMarker(markerOptions);
                my_location_marker.setZIndex(10);
            }

        }
    }

    public Bitmap getMyCustomMarker () {

        try {
            int dotSize = getActivity().getResources().getDimensionPixelSize(R.dimen.dp20);
            Drawable dotImg = getActivity().getResources().getDrawable(R.drawable.location_marker_dot);

            RectF rectF = new RectF(0, 0, dotSize + 1, dotSize + 1);
            double dotLeft = rectF.centerX() - (dotSize * 0.5);
            double dotTop = rectF.centerY() - (dotSize * 0.5);
            double dotRight = rectF.centerX() + (dotSize * 0.5);
            double dotBottom = rectF.centerY() + (dotSize * 0.5);
            dotImg.setBounds((int) dotLeft, (int) dotTop, (int) dotRight, (int) dotBottom);
            Bitmap bitmap = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                bitmap = Bitmap.createBitmap(DeviceScreenModel.getInstance().getDisplayMetrics()
                        , (int) rectF.width(), (int) rectF.height(), Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap((int) rectF.width(), (int) rectF.height(),
                        Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            dotImg.draw(canvas);
            return bitmap;
        } catch (Exception e) {

        }

        return null;
    }

    public Bitmap getAccuracyCircle () {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xffc83c6e);
        paint.setStyle(Paint.Style.FILL);

        int dotSize = getActivity().getResources().getDimensionPixelSize(R.dimen.dp150);
        RectF rectF = new RectF(0, 0, dotSize, dotSize);
        Bitmap bitmap = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bitmap = Bitmap.createBitmap(DeviceScreenModel.getInstance().getDisplayMetrics(),
                    (int) rectF.width(), (int) rectF.height(), Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap((int) rectF.width(), (int) rectF.height(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        paint.setShader(new RadialGradient(rectF.centerX(),
                rectF.centerY(), (rectF.width() * 0.5f),
                circle_center_color, circle_edge_color, Shader.TileMode.MIRROR));
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), (rectF.width() * 0.5f), paint);
        return bitmap;
    }

    @Override
    public void onLocationChanged (Location location) {
        addOrUpdateCurrentLocationMarker();
    }

    public AppBaseLocationService getLocationService () {
        return locationService;
    }

    public GoogleApiClientHelper getGoogleApiClientHelper () {
        if (getLocationService() != null)
            return getLocationService().getGoogleApiClientHelper();
        return null;
    }

    public boolean isNeedRouteFatchAgain (LatLng point) {
        if (mapRouteHandler != null) {
            return mapRouteHandler.isNeedRouteFatchAgain(point);
        }
        return false;
    }

    public void drawRoute (LatLng from, LatLng to, boolean needLoader) {
        if (mapRouteHandler != null) {
            mapRouteHandler.drawRoute(from, to, getResources().getColor(R.color.map_route_start_color)
                    , -1, this, needLoader);

        }
    }

    private void drawPolylines () {
        GoogleMap googleMap = getGoogleMap();
        if (getActivity() != null && googleMap != null) {

            LinkedHashMap<PolylineOptions, Polyline> polylineList = mapRouteHandler.getPolylineList();
            if (polylineList != null) {
                for (PolylineOptions polylineOptions :
                        polylineList.keySet()) {
                    if (polylineOptions == null) continue;
                    Polyline polyline = googleMap.addPolyline(polylineOptions);
                    polyline.setTag(polylineOptions.getPoints());
                    polylineList.put(polylineOptions, polyline);
                }
            }
            LatLngBounds bound = mapRouteHandler.getBound();
            LatLng latLng = getRealCurrentLatLng();
            if (bound != null) {
                /*if (latLng != null) {
                    bound = bound.including(latLng);
                }*/
                updateMapWithBound(bound, getResources().getDimensionPixelSize(R.dimen.dp20));
            } else {
                if (latLng != null) {
                    clearBound();
                    goToLatLng(latLng);
                }
            }
            mapRouteHandler.startAnim();
        }
    }

    public void displayProgressBar (boolean isCancellable) {
        displayProgressBar(isCancellable, "");
    }

    public void displayProgressBar (boolean isCancellable, String loaderMsg) {
        dismissProgressBar();
        if (getActivity() == null) return;
        alertDialogProgressBar = new Dialog(getActivity(),
                R.style.CustomAlertDialogStyle);
        alertDialogProgressBar.setCancelable(isCancellable);
        alertDialogProgressBar
                .requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialogProgressBar.setContentView(R.layout.progress_dialog);
        TextView tv_loader_msg = alertDialogProgressBar.findViewById(R.id.tv_loader_msg);
        if (loaderMsg != null && !loaderMsg.trim().isEmpty()) {
            tv_loader_msg.setText(loaderMsg);
        } else {
            tv_loader_msg.setVisibility(View.GONE);
        }

        alertDialogProgressBar.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        alertDialogProgressBar.show();
        Utils.printLog(getActivity(), getClass().getSimpleName(), " displayProgressBar");
    }

    public void dismissProgressBar () {
        if (alertDialogProgressBar != null && alertDialogProgressBar.isShowing()) {
            alertDialogProgressBar.dismiss();
            Utils.printLog(getActivity(), getClass().getSimpleName(), " dismissProgressBar");
        }
    }

    @Override
    public void onRouteFatchStart (boolean needLoader) {
        if (needLoader)
            displayProgressBar(false);
    }

    @Override
    public void onRouteFatchEnd () {
        dismissProgressBar();
        GoogleMap googleMap = getGoogleMap();
        if (getActivity() != null && googleMap != null) {
            getMapHandler().approximateDataChange(mapRouteHandler.getDurationDistance());
        }
        drawPolylines();
    }

    @Override
    public void onDriverUpdate (VehicleTypeModel vehicle_type, List<DriverModel> list) {
        GoogleMap googleMap = getGoogleMap();
        if (getActivity() != null && googleMap != null) {
            if (list == null || list.size() == 0) {
                clearDriverMarkers();
            } else {
                Set<Long> drivers = driverMarkerMap.keySet();
                updateDrivers(drivers, list);
            }
        }
    }

    private void updateDrivers (Set<Long> savedDrivers, List<DriverModel> latestDrivers) {
        List<DriverModel> operationList = new ArrayList<>(latestDrivers);
        for (Long driverId : savedDrivers) {
            DriverModel driverModel = new DriverModel();
            driverModel.setDriver_id(driverId);
            int index = operationList.indexOf(driverModel);
            if (index == -1) continue;
            operationList.remove(index);
        }

        Set<Long> operationList1 = new HashSet<>(savedDrivers);
        for (DriverModel latestDriver : latestDrivers) {
            if (operationList1.contains(latestDriver.getDriver_id())) {
                operationList1.remove(latestDriver.getDriver_id());
            }
        }

        if (operationList1.size() > 0) {
            for (long driverId : operationList1) {
                removeDriverMarker(driverId);
                unSubscribeDriverPusherChannel(driverId);
            }
        }
        if (operationList.size() > 0) {
            for (DriverModel driverModel : operationList) {
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.icon_driver_marker);
                addDriverMarker(driverModel, bitmapDescriptor);
            }
        }
    }

    private String getPusherChannelNameForDriver (long id) {

        return "private-driver_" + id;
    }

    public void subscribeDriverPusherChannel (long driverId) {
        PusherHandler.getInstance().subscribePrivateChannelWithEvent(
                getPusherChannelNameForDriver(driverId),
                getPusherLocationEventName(), getPusherEtaPickupEventName(), getPusherEtaDropEventName());
    }

    private void unSubscribeDriverPusherChannel (long driverId) {
        PusherHandler.getInstance().unSubscribePrivateChannel(getPusherChannelNameForDriver(driverId));
    }

    @Override
    public void channelConnected (String channel) {
    }

    @Override
    public void pusherError (String message, Exception e) {
    }

    @Override
    public void messageReceived (String channelName, String eventName, Object data) {
        if (eventName != null && !eventName.trim().isEmpty()) {
            if (eventName.equals(getPusherLocationEventName())) {
                try {
                    final PusherLocationModel locationModel = new Gson().fromJson((String) data, PusherLocationModel.class);
                    if (locationModel == null) return;

                   // animateDriverMarker(locationModel);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run () {
                            animateDriverMarker(locationModel);
                        }
                    });
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void animateDriverMarker (PusherLocationModel locationModel) {
        if (driverMarkerMap.containsKey(locationModel.user_id)) {
            Marker marker = driverMarkerMap.get(locationModel.user_id);
            if (marker == null) return;
            MarkerAnimation markerAnimation = (MarkerAnimation) marker.getTag();
            if (markerAnimation == null) {
                markerAnimation = new MarkerAnimation(marker);
                marker.setTag(markerAnimation);
            } else {
                markerAnimation.animateMarkerToGB(locationModel.getLatLng());
            }

        }
    }

    public LatLng getDriverLiveLocation (long driverId) {
        if (driverMarkerMap != null && driverMarkerMap.containsKey(driverId)) {
            Marker marker = driverMarkerMap.get(driverId);
            return marker.getPosition();
        }
        return null;
    }


    private void checkLocationSetting () {
        gpsCheckHandler.removeCallbacks(gpsRunnable);
        gpsCheckHandler.postDelayed(gpsRunnable, 500);
    }

    @Override
    public void onResume () {
        super.onResume();
        if (locationService != null && !Utils.isGpsProviderEnabled(getActivity())) {
            checkLocationSetting();
        }
        registerGpsStateReceiver();
        PusherHandler.getInstance().addPusherListener(this);
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        DEFAULT_CAMERA_LATITUDE = 28.704059;
        DEFAULT_CAMERA_LONGITUDE = 77.102490;
        mapRouteHandler = new MapRouteHandler(activity);
    }


    @Override
    public void onPause () {
        super.onPause();
        unRegisterGpsStateReceiver();
        PusherHandler.getInstance().removePusherListener(this);
    }

    @Override
    public void onDetach () {
        if (locationService != null)
            locationService.getGoogleApiClientHelper().removeLocationListener(this);
        unBindLocationService();
        super.onDetach();
    }

    @Override
    public void onDestroyView () {
        dismissProgressBar();
        super.onDestroyView();
    }

    @Override
    public void onMapReady (GoogleMap googleMap) {
        super.onMapReady(googleMap);
        displayProgressBar(false, "Please Wait..." + "\n" + "Getting GPS Location");

    }

}
