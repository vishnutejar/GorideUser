package com.appupdater;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.utilities.Utils;


/**
 * Created by ubuntu on 29/5/17.
 */

public class AppUpdateUtils {


    public static final String TAG = "AppUpdateUtils";

    public static void printLog (Context context, String msg) {
        if (msg == null) return;
        if (Utils.isDebugBuild(context)) {
            Log.e(TAG, msg);
        }
    }


    public static String getAppPackageName (Context context) {
        return context.getPackageName();
    }

    public static String getAppInstalledVersionName (Context context) {
        String version = "0";

        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            if (version != null) {
                version = version.replaceAll("\\.", "");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    static Integer getAppInstalledVersionCode (Context context) {
        Integer versionCode = 0;

        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionCode;
    }
}
