package com.quickzetuser.ui;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.addressfetching.LocationModelFull;
import com.crashlytics.android.Crashlytics;
import com.fcm.PushNotificationHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.gson.Gson;
import com.medy.retrofitwrapper.WebServiceBaseResponseModel;
import com.permissions.PermissionHelperNew;
import com.pusher.PusherConfig;
import com.goride.user.BuildConfig;
import com.quickzetuser.appBase.AppBaseLocationService;
import com.quickzetuser.database.DaoManager;
import com.quickzetuser.database.tables.BookingTable;
import com.quickzetuser.database.tables.FavoriteTable;
import com.quickzetuser.database.tables.RecentTable;
import com.quickzetuser.fcm.AppNotificationMessagingService;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.UserModel;
import com.quickzetuser.preferences.AppPrefs;
import com.quickzetuser.preferences.UserPrefs;
import com.quickzetuser.rest.WebRequestConstants;
import com.quickzetuser.ui.login.LoginActivity;
import com.quickzetuser.ui.utilities.CityHelper;
import com.quickzetuser.ui.utilities.RunningBookingHead;
import com.utilities.Utils;

import java.lang.reflect.Field;
import java.util.Calendar;

import io.fabric.sdk.android.Fabric;


/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class MyApplication extends MultiDexApplication implements UserPrefs.UserPrefsListener {

    private static final String TAG = "MyApplication";
    private static MyApplication instance;
    private RunningBookingHead.RunningBookingHeadListener runningBookingHeadListener;
    private CityHelper cityHelper;
    private PushNotificationHelper pushNotificationHelper;
    private BookCabModel runningBookingModel;

    //    private AutocompleteFilter autocompleteFilter;
//    private LatLngBounds autoCompleteBound;
    private String autoCompleteCountry;
    private RectangularBounds autoCompleteBound;

    private AppPrefs appPrefs;
    private UserPrefs userPrefs;
    private UserModel userModel;

    public static MyApplication getInstance() {
        return instance;
    }

    public UserPrefs getUserPrefs() {
        return userPrefs;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public AppPrefs getAppPrefs() {
        return appPrefs;
    }

    public BookCabModel getRunningBookingModel() {
        return runningBookingModel;
    }

    public void setRunningBookingModel(BookCabModel runningBookingModel) {
        this.runningBookingModel = runningBookingModel;
    }

    public RunningBookingHead.RunningBookingHeadListener getRunningBookingHeadListener() {
        return runningBookingHeadListener;
    }

    public void setRunningBookingHeadListener(RunningBookingHead.RunningBookingHeadListener runningBookingHeadListener) {
        this.runningBookingHeadListener = runningBookingHeadListener;
    }

    public PushNotificationHelper getPushNotificationHelper() {
        return pushNotificationHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppNotificationMessagingService.createNotificationChannel(this);
        pushNotificationHelper = new PushNotificationHelper(this);
        PusherConfig.getInstance().setupPusher(WebRequestConstants.PUSHER_KEY,
                WebRequestConstants.PUSHER_AUTH_URL);
        setUpDao();
        Fabric.with(this, new Crashlytics());
        cityHelper = new CityHelper(this);
        appPrefs = new AppPrefs(this);
        userPrefs = new UserPrefs(this);
        userPrefs.addListener(this);
        userModel = userPrefs.getLoggedInUser();

        startLocationService(null);

    }

    public CityHelper getCityHelper() {
        return cityHelper;
    }

    public void unAuthorizedResponse(WebServiceBaseResponseModel modelResponse) {
        UserPrefs userPrefs = new UserPrefs(getApplicationContext());
        userPrefs.clearPrefsData();
        moveToLoginActivity(modelResponse);
    }

    public void moveToLoginActivity(WebServiceBaseResponseModel modelResponse) {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        if (modelResponse != null) {
            Bundle bundle = new Bundle();
            bundle.putString("unAuth", new Gson().toJson(modelResponse));
            intent.putExtras(bundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setUpDao() {
        BookingTable.getInstance();
        FavoriteTable.getInstance();
        RecentTable.getInstance();
        DaoManager.getInstance().loadDao();
    }

    public void startLocationService(ServiceConnection serviceConnection) {
        if (Utils.getGooglePlayServicesAvailableStatus(this) != ConnectionResult.SUCCESS) return;
        if (PermissionHelperNew.hasLocationPermission(this)) {
            if (userPrefs.getLoggedInUser() == null) {
                stopLocationService();
                return;
            }
            Intent intent = new Intent(this, AppBaseLocationService.class);
            if (!AppBaseLocationService.isRunning(this)) {
                try {
                    if (serviceConnection == null) {
                        startService(intent);
                    } else {
                        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                    }

                } catch (IllegalStateException e) {

                }
            } else {
                if (serviceConnection != null) {
                    bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                }
            }

        }
    }

    public void stopLocationService() {
        try {
            Intent intent = new Intent(this, AppBaseLocationService.class);
            stopService(intent);
        } catch (IllegalStateException e) {

        }

    }

    @Override
    public void userLoggedIn(UserModel userModel) {
        this.userModel = userModel;

    }

    @Override
    public void loggedInUserUpdate(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    public void loggedInUserClear() {
        this.userModel = null;

    }

    public String getAutoCompleteCountry() {
        return autoCompleteCountry;
    }

    public RectangularBounds getAutoCompleteBound() {
        return autoCompleteBound;
    }

    private void setAutoCompleteBound(LocationModelFull.LocationModel locationModel) {
        if (locationModel == null || locationModel.getLatLng() == null) {
            this.autoCompleteBound = null;
            return;
        }
        LatLngBounds boundArround = Utils.getLatLngBounds(locationModel.getLatLng(), 50);
        this.autoCompleteBound = RectangularBounds.newInstance(
                boundArround.southwest,
                boundArround.northeast);

    }

    public void setAutocompleteFilter(LocationModelFull.LocationModel locationModel) {
        if (locationModel != null) {
            setAutoCompleteBound(locationModel);
            String countryCode = locationModel.getCountry();
            if (countryCode == null || countryCode.trim().isEmpty()) {
                this.autoCompleteCountry = null;
                return;
            }
            this.autoCompleteCountry = countryCode;
        } else {
            this.autoCompleteBound = null;
            this.autoCompleteCountry = null;
        }
    }


    public Calendar getOutstationBookingMinTime() {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        instance.add(Calendar.HOUR_OF_DAY, 1);

        int i = instance.get(Calendar.MINUTE);
        if (i < 15) {
            i = 0;
        } else if (i < 30) {
            i = 15;
        } else if (i < 45) {
            i = 30;
        } else if (i < 59) {
            i = 45;
        }
        instance.set(Calendar.MINUTE, i);
        return instance;
    }

    public boolean isDebugBuild () {

        try {
            String packageName = getPackageName();

            Bundle bundle = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA).metaData;
            String manifest_pkg = null;
            if (bundle != null) {
                manifest_pkg = bundle.getString("manifest_pkg", null);
            }
            if (manifest_pkg != null) {
                packageName = manifest_pkg;
            }
            final Class<?> buildConfig = Class.forName(packageName + ".BuildConfig");
            final Field DEBUG = buildConfig.getField("DEBUG");
            DEBUG.setAccessible(true);
            return DEBUG.getBoolean(null);
        } catch (final Throwable t) {
            final String message = t.getMessage();
            if (message != null && message.contains("BuildConfig")) {
                return false;
            } else {
                return BuildConfig.DEBUG;
            }
        }
    }

    public static void printLog (Context context, String msg) {
        if (msg == null) return;
        if (Utils.isDebugBuild(context)) {
            Log.e(TAG, msg);
        }
    }

}
