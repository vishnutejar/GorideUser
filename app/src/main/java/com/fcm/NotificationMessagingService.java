package com.fcm;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.quickzetuser.ui.MyApplication;
import com.utilities.Utils;

import java.util.List;


public class NotificationMessagingService extends FirebaseMessagingService {


    public void printLog (String msg) {
        if (Utils.isDebugBuild(this))
            Log.e(getClass().getSimpleName(), msg);
    }


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived (RemoteMessage remoteMessage) {
        NotificationModal notificationModal = getNotificationModal(remoteMessage);
        if (notificationModal == null) {
            notificationModal = new NotificationModal(remoteMessage);
        }
        printLog(notificationModal.toString());
        pushMessageReceived(notificationModal);

    }

    public NotificationModal getNotificationModal (RemoteMessage remoteMessage) {
        return null;
    }

    public void pushMessageReceived (NotificationModal notificationModal) {

    }

    public boolean isAppIsInBackground () {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(getPackageName())) {
                            isInBackground = false;

                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }


    public static void createNotificationChannel (Context context, String id, String name,
                                                  int importance, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNewToken (String s) {
        super.onNewToken(s);
        NotificationPrefs.getInstance(getApplicationContext()).saveNotificationToken(s);
        printLog("Notification onNewToken : " + s);
    }

    public static void generateLatestToken () {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess (InstanceIdResult instanceIdResult) {
                if (instanceIdResult != null) {
                    NotificationPrefs.getInstance(MyApplication.getInstance()).saveNotificationToken(instanceIdResult.getToken());
                    if (MyApplication.getInstance().isDebugBuild()) {
                        Log.e("Notification",
                                "generateLatestToken : " + instanceIdResult.getToken());
                    }
                }
            }
        });
    }


}
