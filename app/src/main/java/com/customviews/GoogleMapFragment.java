package com.customviews;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.os.Handler;
import android.os.Message;

import androidx.core.app.ActivityCompat;

import com.addressfetching.AddressFetchModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.utilities.GoogleApiClientHelper;
import com.utilities.Utils;


/**
 * Created by bitu on 20/8/17.
 */

public class  GoogleMapFragment extends SupportMapFragment implements
        GoogleMap.OnMapLoadedCallback, OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    public static final int DEFAULT_MAP_ANIM_TIME = 300;
    public static final float DEFAULT_MAP_ZOOM = 17.0f;
    public static double DEFAULT_CAMERA_LATITUDE = -1000;
    public static double DEFAULT_CAMERA_LONGITUDE = -1000;
    Rect mapPaddingRect = new Rect();
    GoogleMap googleMap;
    boolean mapLoadedWithAllPermission = false;
    LatLngBounds latLngBounds;
    int boundPadding;
    GoogleMapListener googleMapListener;

    GoogleApiClientHelper googleApiClientHelper;
    Location mapLoadedLocation;
    Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (getActivity() != null && getGoogleMap() != null) {
                        Location location = getRealCurrentLocation();

                        if (location != null && Utils.isGpsProviderEnabled(getActivity())) {
                            onLocationAvailable(location);
                            goToRealCurrentLocation();
                            if (GoogleMapFragment.this.googleMapListener != null) {
                                GoogleMapFragment.this.googleMapListener.onLocationAvailable(location);
                            }
                            enableMapSettled();
                        } else {
                            handler.sendEmptyMessageDelayed(1, 200);
                        }
                    }

                    break;

            }
        }
    };
    boolean mapSattled = false;


    public void setGoogleApiClientHelper (GoogleApiClientHelper googleApiClientHelper) {
        this.googleApiClientHelper = googleApiClientHelper;
    }

    public void onLocationAvailable (Location location) {

    }

    public void setMapLoadedLocation (Location mapLoadedLocation) {
        this.mapLoadedLocation = mapLoadedLocation;
    }

    @Override
    public void onDetach () {
        super.onDetach();
        handler.removeMessages(1);
    }

    private void enableMapSettled () {
        if (getView() != null) {
            getView().postDelayed(new Runnable() {
                @Override
                public void run () {
                    mapSattled = true;

                }
            }, DEFAULT_MAP_ANIM_TIME + 500);
        }
    }

    public GoogleMap getGoogleMap () {
        return this.googleMap;
    }

    public void clearBound () {
        latLngBounds = null;
    }

    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        this.getMapAsync(this);
    }


    public void setGoogleMapListener (GoogleMapListener googleMapListener) {
        this.googleMapListener = googleMapListener;
    }

    public void setMapPadding (int left, int top, int right, int bottom) {
        this.mapPaddingRect.set(left, top, right, bottom);
        setupMapPadding();
    }

    private void setupMapPadding () {
        if (this.googleMap == null) return;
        this.googleMap.setPadding(mapPaddingRect.left,
                mapPaddingRect.top, mapPaddingRect.right, mapPaddingRect.bottom);
    }

    @Override
    public void onMapLoaded () {
        if (this.googleMap == null) return;


    }

    @Override
    public void onMapReady (GoogleMap googleMap) {
        mapSattled = false;
        if (this.googleMap == null)
            setDefaultProperty(googleMap);
        setMapCamera();

        if (this.mapLoadedLocation == null) {
            if (!isMapLoadedWithAllPermission()) return;
            handler.sendEmptyMessageDelayed(1, 100);
        } else {
            goToLocation(this.mapLoadedLocation);
            enableMapSettled();

        }
    }

    private void setMapCamera () {
        if (DEFAULT_CAMERA_LATITUDE == -1000 || DEFAULT_CAMERA_LONGITUDE == -1000)
            return;
        LatLng latLng = new LatLng(DEFAULT_CAMERA_LATITUDE, DEFAULT_CAMERA_LONGITUDE);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 3);
        this.googleMap.animateCamera(cameraUpdate, 50, null);
    }

    @Override
    public void onCameraIdle () {
        if (this.googleMap == null || !mapSattled) return;
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        if (cameraPosition == null) return;
        LatLng latLng = cameraPosition.target;
        if (latLng == null) return;
        Location location = new Location("CameraLocation");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        if (GoogleMapFragment.this.googleMapListener != null) {
            GoogleMapFragment.this.googleMapListener.onCameraIdeal(location);
        }
    }

    public void setDefaultProperty (GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.setOnMapLoadedCallback(this);
        googleMap.setOnCameraIdleListener(this);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setupMapPadding();
        setupMyLocationEnable();
    }


    private void setupMyLocationEnable () {
        if (getActivity() == null) return;
        if (ActivityCompat.
                checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            mapLoadedWithAllPermission = false;
            return;
        }
        mapLoadedWithAllPermission = true;
        googleMap.setMyLocationEnabled(false);

    }

    public void permissionGrantedDoWork () {
        if (getActivity() == null || this.googleMap == null) return;
        setupMyLocationEnable();
    }

    public boolean isMapLoadedWithAllPermission () {
        return this.getActivity() != null &&
                this.googleMap != null &&
                this.mapLoadedWithAllPermission;
    }

    public boolean goToRealCurrentLocation () {
        if (!isMapLoadedWithAllPermission()) return false;
        Location location = getRealCurrentLocation();
        if (location != null) {
            goToLocation(location);
            return true;
        }
        return false;
    }

    public void goToLocation (Location location) {
        if (location == null) return;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM);
        this.googleMap.animateCamera(cameraUpdate, DEFAULT_MAP_ANIM_TIME, null);
    }

    public void goToLatLng (LatLng latLng) {
        if (latLng == null) return;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM);
        this.googleMap.animateCamera(cameraUpdate, DEFAULT_MAP_ANIM_TIME, null);
    }

    public void goToSelectedLocation (AddressFetchModel addressFetchModel) {
        if (googleMap == null || addressFetchModel == null || addressFetchModel.getLocation() == null)
            return;
        googleMap.setOnCameraIdleListener(null);
        Location mapSelectedLocation = addressFetchModel.getLocation();
        LatLng latLng = new LatLng(mapSelectedLocation.getLatitude(), mapSelectedLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM);
        this.googleMap.animateCamera(cameraUpdate, DEFAULT_MAP_ANIM_TIME, null);
        if (googleMapListener != null) {
            googleMapListener.onLocationSelected(addressFetchModel);
        }
        if (getView() == null) return;
        getView().postDelayed(new Runnable() {
            @Override
            public void run () {
                if (googleMap != null) {
                    googleMap.setOnCameraIdleListener(GoogleMapFragment.this);
                }
            }
        }, DEFAULT_MAP_ANIM_TIME + DEFAULT_MAP_ANIM_TIME);

    }

    public void updateMapPaddingChange () {
        if (this.latLngBounds != null) {
            updateMapWithBound(this.latLngBounds, this.boundPadding);
        } else {
            goToRealCurrentLocation();
        }
    }

    public Location getRealCurrentLocation () {
        Location location = null;
        if (googleApiClientHelper != null && googleApiClientHelper.isConnected()) {
            location = googleApiClientHelper.getLatestLocation();
        }

        if (location == null && googleMap.isMyLocationEnabled()) {
            location = this.googleMap.getMyLocation();
        }
        return location;
    }

    public LatLng getRealCurrentLatLng () {
        LatLng latLng = null;
        Location location = getRealCurrentLocation();
        if (location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        return latLng;
    }

    public void updateMapWithBound (LatLngBounds latLngBounds, int boundPadding) {
        this.latLngBounds = latLngBounds;
        this.boundPadding = boundPadding;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, boundPadding);
        getGoogleMap().animateCamera(cameraUpdate, DEFAULT_MAP_ANIM_TIME, null);
    }


    public interface GoogleMapListener {
        void onLocationAvailable (Location location);

        void onLocationSelected (AddressFetchModel addressFetchModel);

        void onCameraIdeal (Location location);
    }


}
