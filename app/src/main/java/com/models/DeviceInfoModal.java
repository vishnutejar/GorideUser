package com.models;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author Manish Kumar
 * @since 28/5/18
 */


public class  DeviceInfoModal {
    String PhoneModel = android.os.Build.MODEL;
    String AndroidVersion = android.os.Build.VERSION.RELEASE;
    String Board = android.os.Build.BOARD;
    String Brand = android.os.Build.BRAND;
    String Model = android.os.Build.MODEL;
    String Product = android.os.Build.PRODUCT;
    String PackageName;
    String buildNumber;
    String appVersionName;
    int appVersionCode;

    public DeviceInfoModal(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);

            buildNumber = pi.versionName + (pi.versionCode > 0 ? " (" + pi.versionCode + ")" : "");
            PackageName = pi.packageName;
            appVersionName = pi.versionName;
            appVersionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException ignore) {
        }
    }


    public int getAppVersionCode() {
        return appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public String getAppInfo() {
        if (appVersionName == null) {
            appVersionName = "not found";
        }
        return appVersionName + " | " + String.valueOf(appVersionCode);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (Board != null) {
            stringBuilder.append("Board=").append(Board);
        }
        if (Brand != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" | ");
            }
            stringBuilder.append("Brand=").append(Brand);
        }
        if (Model != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" | ");
            }
            stringBuilder.append("Model=").append(Model);
        }
        if (Product != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" | ");
            }
            stringBuilder.append("Product=").append(Product);
        }
        if (PhoneModel != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" | ");
            }
            stringBuilder.append("PhoneModel=").append(PhoneModel);
        }
        if (AndroidVersion != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" | ");
            }
            stringBuilder.append("AndroidVersion=").append(AndroidVersion);
        }
        if (PackageName != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" | ");
            }
            stringBuilder.append("PackageName=").append(PackageName);
        }
        if (buildNumber != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" | ");
            }
            stringBuilder.append("buildNumber=").append(buildNumber);
        }
        return stringBuilder.toString();
    }
}
