package com.quickzetuser.appBase;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;
import com.locationservice.LocationService;
import com.utilities.GoogleApiClientHelper;


/**
 * @author Sunil kumar Yadav
 * @Since 23/5/18
 */
public class  AppBaseLocationService extends LocationService {


    private IBinder mBinder = new LocationBinder();

    public static boolean isRunning (Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if ((context.getPackageName().equals(service.service.getPackageName())) &&
                    (AppBaseLocationService.class.getName()).equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IBinder onBind (Intent intent) {
        printLog("onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind (Intent intent) {
        printLog("onUnbind");
        return super.onUnbind(intent);
    }

    public class LocationBinder extends Binder {
        public AppBaseLocationService getLocationService () {
            return AppBaseLocationService.this;
        }
    }

    public Task<LocationSettingsResponse> checkLocationSetting () {
        GoogleApiClientHelper googleApiClientHelper = getGoogleApiClientHelper();
        if (googleApiClientHelper != null && googleApiClientHelper.isConnected()) {
            LocationRequest locationRequest = googleApiClientHelper.getmLocationRequest();
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);
            Task<LocationSettingsResponse> locationSettingsResponseTask =
                    LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
            return locationSettingsResponseTask;
        }
        return null;
    }
}
