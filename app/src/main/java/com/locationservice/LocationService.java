package com.locationservice;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.utilities.GoogleApiClientHelper;
import com.utilities.Utils;


public class LocationService extends Service implements LocationListener {

    private static final String TAG = LocationService.class.getSimpleName();
    private GoogleApiClientHelper googleApiClientHelper;

    public LocationService () {

    }

    public GoogleApiClientHelper getGoogleApiClientHelper () {
        return googleApiClientHelper;
    }

    @Override
    public void onCreate () {
        super.onCreate();
        googleApiClientHelper = new GoogleApiClientHelper(this);
        printLog("onCreate");
    }

    @Override
    public IBinder onBind (Intent intent) {
        printLog("onBind");
        return null;
    }

    @Override
    public void onDestroy () {
        printLog("onDestroy");
        if (googleApiClientHelper != null) {
            googleApiClientHelper.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved (Intent rootIntent) {
        printLog("onTaskRemoved");
        if (googleApiClientHelper != null) {
            googleApiClientHelper.destroy();
        }
        super.onTaskRemoved(rootIntent);
    }

    public void stopService () {
        printLog("stopService");
        stopSelf();
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        printLog("onStartCommand: " + intent);
        return Service.START_STICKY;

    }

    @Override
    public void onLocationChanged (Location location) {
        printLog("onLocationChanged");

    }

    protected void printLog (String msg) {
        if (Utils.isDebugBuild(this)) {
            Log.e(TAG, msg);
        }
    }


}
