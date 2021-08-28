package com.fcm;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Manish Kumar
 * @since 14/12/17
 */



public class  PushNotificationHelper {

    Handler handler = new Handler(Looper.getMainLooper());
    Application myApplication;
    List<PushNotificationHelperListener> notificationHelperListenerList = new ArrayList<>();

    public PushNotificationHelper (Application myApplication) {
        this.myApplication = myApplication;
    }

    public void onDestroy () {
        clearNotificationHelperListener();
    }

    public void printLog (String msg) {
        if (msg == null) return;
        if (Utils.isDebugBuild(myApplication))
            Log.e("PushNotificationHelper", msg);
    }

    public void addNotificationHelperListener (PushNotificationHelperListener notificationHelperListener) {
        if (!this.notificationHelperListenerList.contains(notificationHelperListener))
            this.notificationHelperListenerList.add(notificationHelperListener);
    }

    public void removeNotificationHelperListener (PushNotificationHelperListener notificationHelperListener) {
        if (this.notificationHelperListenerList.contains(notificationHelperListener))
            this.notificationHelperListenerList.remove(notificationHelperListener);
    }

    public void clearNotificationHelperListener () {
        this.notificationHelperListenerList.clear();
    }

    public synchronized void triggerNotificationListener (final NotificationModal notificationModal) {
        if (this.notificationHelperListenerList == null) return;
        final List<PushNotificationHelperListener> list = notificationHelperListenerList;
        handler.post(new Runnable() {
            @Override
            public void run () {
                for (PushNotificationHelperListener pushNotificationHelperListener : list) {
                    pushNotificationHelperListener.onPushNotificationReceived(notificationModal);
                }
            }
        });

    }

    public interface PushNotificationHelperListener {
        void onPushNotificationReceived (NotificationModal notificationModal);
    }
}
