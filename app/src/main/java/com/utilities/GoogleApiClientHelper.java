package com.utilities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.addressfetching.AddressFetchModel;
import com.addressfetching.AddressFetchingService;
import com.addressfetching.AddressResultReceiver;
import com.addressfetching.LocationModelFull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.locationservice.LocationService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manish Kumar
 * @since 6/9/17
 */



public class  GoogleApiClientHelper implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    public static final String TAG = "GoogleApiClientHelper";

    public static final long UPDATE_INTERVAL = 5 * 1000;
    public static final long FASTEST_UPDATE_INTERVAL = 2 * 1000;
    public static final float UPDATE_DISPLACEMENT = 0.0f;


    private GoogleApiClient mGoogleApiClient;
    Context context;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false;
    public List<LocationListener> locationListeners=new ArrayList<>();

    public GoogleApiClientHelper(Context context) {
        this.context = context;
        if (context instanceof LocationListener){
            this.locationListeners.add((LocationListener)context);
        }
        mRequestingLocationUpdates = false;
        createLocationRequest();
        buildGoogleApiClient(context);
    }

    public void addLocationListener(LocationListener locationListener){
        if (locationListener!=null){
            locationListeners.add(locationListener);
        }
    }

    public void removeLocationListener(LocationListener locationListener){
        if (locationListener!=null){
            locationListeners.remove(locationListener);
        }
    }

    public void clearLocationListeners(){
            locationListeners.clear();
    }

    public GoogleApiClient getGoogleApiClient () {
        return mGoogleApiClient;
    }

    protected synchronized void buildGoogleApiClient (Context context) {
        printLog("buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
//                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
    }

    protected void createLocationRequest () {
        printLog("createLocationRequest");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setSmallestDisplacement(UPDATE_DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected (@Nullable Bundle bundle) {
        printLog("onConnected");

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended (int i) {
        printLog("onConnectionSuspended i=" + i);
        if (mGoogleApiClient != null) {
            stopLocationUpdates();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed (@NonNull ConnectionResult connectionResult) {
        if (context instanceof LocationService){
            ((LocationService)context).stopService();
        }
        printLog("onConnectionFailed");
    }

    @Override
    public void onLocationChanged (Location location) {
        if (location!=null)
            triggerLocationListeners(location);

    }

    private void triggerLocationListeners(Location location){
        for (LocationListener listener:locationListeners){
            if (listener!=null){
                listener.onLocationChanged(location);
            }
        }
    }

    public void startLocationUpdates () {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()
                && !mRequestingLocationUpdates) {

            if (ActivityCompat.
                    checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            printLog("startLocationUpdates");
            mRequestingLocationUpdates = true;
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            } catch (IllegalStateException e) {
                mRequestingLocationUpdates = false;
            }
        }
    }

    public void stopLocationUpdates () {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()
                && mRequestingLocationUpdates) {
            printLog("stopLocationUpdates");
            mRequestingLocationUpdates = false;
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    public void destroy () {
        stopLocationUpdates();
    }

    private void printLog (String msg) {
        if (Utils.isDebugBuild(context) && msg != null) {
            Log.e(TAG, msg);
        }
    }

    public Location getLatestLocation () {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return null;
        }
        return LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
    }

    public double getDistanceFromMyLocation(LatLng latLng) throws IllegalAccessException {
        Location location = new Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        Location myLocation = getLatestLocation();
        return myLocation.distanceTo(location);
    }

    public boolean fetchCurrentAddress (AddressResultReceiver addressResultReceiver) {
        return fetchLocationAddress(getLatestLocation(), addressResultReceiver);

    }

    public boolean fetchLocationAddress (Location location,
                                         AddressResultReceiver addressResultReceiver) {
        if (location == null) return false;
        AddressFetchModel addressFetchModel = new AddressFetchModel();
        addressFetchModel.setLocation(location);
        printLog("fetchLocationAddress location=" + location.getLatitude() + ", " + location.getLongitude());
        Intent intent = AddressFetchingService.createIntent(context,
                addressResultReceiver, addressFetchModel);
        intent.setAction(context.getPackageName() + AddressFetchingService.ACTION_FETCH_ADDRESS);

        context.startService(intent);
        return true;
    }

    public boolean fetchPlaceDetail (LocationModelFull.LocationModel locationModel,
                                     AddressResultReceiver addressResultReceiver) {
        if (locationModel == null) return false;
        AddressFetchModel addressFetchModel = new AddressFetchModel();
        addressFetchModel.setLocationModel(locationModel);
        printLog("fetchPlaceDetail placeId=" + locationModel.getPlaceId());
        Intent intent = AddressFetchingService.createIntent(context,
                addressResultReceiver, addressFetchModel);
        intent.setAction(context.getPackageName() + AddressFetchingService.ACTION_PLACE_DETAIL);
        context.startService(intent);
        return true;
    }

    public boolean isConnected () {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    public LocationRequest getmLocationRequest() {
        return mLocationRequest;
    }
}
