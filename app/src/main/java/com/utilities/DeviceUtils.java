package com.utilities;

import android.provider.Settings;

import com.rider.ui.MyApplication;

/**
 * Created by bitu on 31/8/17.
 */

public class DeviceUtils {

    public static String getUniqueDeviceId(){


        String androidId = Settings.Secure.getString(MyApplication.getInstance()
                        .getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return androidId;


    }

}
