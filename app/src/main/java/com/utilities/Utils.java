package com.utilities;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.base.BaseActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.quickzetuser.BuildConfig;

import java.lang.reflect.Field;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * Created by Sunil kumar yadav on 21/4/18.
 */

public class Utils {

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean isDebugBuild (Context context) {
        if (context == null) return false;
        try {
            String packageName = context.getPackageName();

            Bundle bundle = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA).metaData;
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
                // Proguard obfuscated build. Most likely a production build.
                return false;
            } else {
                return BuildConfig.DEBUG;
            }
        }
    }

    public static LatLng computeOffset (LatLng from, double distance, double heading) {
        distance /= 6371009;
        heading = toRadians(heading);
        // http://williams.best.vwh.net/avform.htm#LL
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double cosDistance = cos(distance);
        double sinDistance = sin(distance);
        double sinFromLat = sin(fromLat);
        double cosFromLat = cos(fromLat);
        double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * cos(heading);
        double dLng = atan2(
                sinDistance * cosFromLat * sin(heading),
                cosDistance - sinFromLat * sinLat);
        return new LatLng(toDegrees(asin(sinLat)), toDegrees(fromLng + dLng));
    }

    public static int getGooglePlayServicesAvailableStatus (Context context) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        return api.isGooglePlayServicesAvailable(context);
    }

    public static boolean isValidPlayServices (Context context) {
        int resultCode = getGooglePlayServicesAvailableStatus(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability instance = GoogleApiAvailability.getInstance();
            BaseActivity context1 = ((BaseActivity) context);
            if (instance.isUserResolvableError(resultCode))
                instance.getErrorDialog(context1,
                        resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else {
                context1.showCustomToast("This device is not supported.");
                (context1).finish();
            }
            return false;
        }
        return true;
    }

    public static boolean isGpsProviderEnabled (Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void printLog (Context context, String TAG, String msg) {
        if (context != null && isDebugBuild(context) && TAG != null && msg != null) {
            Log.e(Utils.class.getSimpleName() + ": ", TAG + ": " + msg);
        }
    }

    public static void showOtp (Context context, String msg) {
        if (msg == null) return;
        if (isDebugBuild(context) && msg != null) {
            Toast.makeText(context, "OTP is: " + msg, Toast.LENGTH_LONG).show();
        }
    }

    public static LatLngBounds getLatLngBounds (LatLng centerLatLng, double boundDistanceInKM) {
        return new LatLngBounds.Builder().
                include(Utils.computeOffset(centerLatLng, boundDistanceInKM * 1000, 0)).
                include(Utils.computeOffset(centerLatLng, boundDistanceInKM * 1000, 90)).
                include(Utils.computeOffset(centerLatLng, boundDistanceInKM * 1000, 180)).
                include(Utils.computeOffset(centerLatLng, boundDistanceInKM * 1000, 270)).build();
    }

}
