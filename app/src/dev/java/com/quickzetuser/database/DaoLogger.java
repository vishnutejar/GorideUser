package com.quickzetuser.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.quickzetuser.BuildConfig;



/**
 * Created by bitu on 1/9/17.
 */

public class  DaoLogger {
    public static final String TAG = "DaoLogger";

    public static void printLog(@NonNull String tag, @NonNull String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void printLog(String msg) {
        printLog(TAG, msg);
    }
}
