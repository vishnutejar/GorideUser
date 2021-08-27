package com.quickzetuser.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.fcm.NotificationMessagingService;
import com.fcm.NotificationModal;
import com.google.firebase.messaging.RemoteMessage;
import com.quickzetuser.R;
import com.quickzetuser.ui.MyApplication;
import com.quickzetuser.ui.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;



/**
 * @author Manish Kumar
 * @since 14/12/17
 */


public class AppNotificationMessagingService extends NotificationMessagingService implements AppNotificationType {

    public static final String KEY_NOTIFICATION_TITLE = "title";
    public static final String KEY_NOTIFICATION_MESSAGE = "message";
    public static final String KEY_NOTIFICATION_TYPE = "noti_type";
    public static final String KEY_BOOKING_ID = "booking_id";
    public static final String KEY_STATUS = "status";
    public static final String KEY_IMAGE_LARGE = "noti_large";
    public static final String KEY_IMAGE_SMALL = "noti_thumb";


    @Override
    public void pushMessageReceived (NotificationModal notificationModal) {
        super.pushMessageReceived(notificationModal);
        AppNotificationModel appNotificationModel = (AppNotificationModel) notificationModal;
        if (appNotificationModel != null) {
            if (isAppIsInBackground()) {
                sendNotification(appNotificationModel);
            } else {
                MyApplication.getInstance().getPushNotificationHelper().
                        triggerNotificationListener(notificationModal);
            }
        }
    }


    @Override
    public NotificationModal getNotificationModal (RemoteMessage remoteMessage) {
        return new AppNotificationModel(remoteMessage);
    }

    public static void createNotificationChannel (Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (context == null) return;
            String appName = context.getResources().getString(R.string.app_name);
            createNotificationChannel(context, appName, appName, NotificationManager.IMPORTANCE_HIGH,
                    appName);
        }
    }

    private void sendNotification (AppNotificationModel notificationModal) {
        String appName = getResources().getString(R.string.app_name);

        if (notificationModal.isAdminAlert()) {
            String imgUrl = notificationModal.getImageLarge();
            if (imgUrl != null && imgUrl.trim().length() > 0) {
                new GeneratePictureNotification(notificationModal).execute();
                return;
            }
        }

        String title = notificationModal.getTitle();
        String message = notificationModal.getMessage();

        Bundle bundle = new Bundle();
        bundle.putString(AppNotificationMessagingService.KEY_BOOKING_ID, String.valueOf(notificationModal.getBookingId()));
        bundle.putString(AppNotificationMessagingService.KEY_NOTIFICATION_TYPE, notificationModal.getNotiType());
        bundle.putString(AppNotificationMessagingService.KEY_STATUS, String.valueOf(notificationModal.getStatus()));
        bundle.putString(AppNotificationMessagingService.KEY_IMAGE_LARGE, notificationModal.getImageLarge());
        bundle.putString(AppNotificationMessagingService.KEY_IMAGE_SMALL, notificationModal.getImageSmall());
        bundle.putString(AppNotificationMessagingService.KEY_NOTIFICATION_TITLE, notificationModal.getTitle());
        bundle.putString(AppNotificationMessagingService.KEY_NOTIFICATION_MESSAGE, notificationModal.getMessage());

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, appName)
                .setSmallIcon(R.mipmap.ic_launcher_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public void sendPictureNotification (AppNotificationModel notificationModal, Bitmap bitmap) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        long notificationId = 0;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.layout_image_notification_small);
        RemoteViews notificationLayoutBig = new RemoteViews(getPackageName(), R.layout.layout_image_notification_big);

        notificationLayout.setTextViewText(R.id.tv_title, notificationModal.getTitle());
        notificationLayoutBig.setTextViewText(R.id.tv_title, notificationModal.getTitle());

        notificationLayout.setTextViewText(R.id.tv_message, notificationModal.getMessage());
        notificationLayoutBig.setTextViewText(R.id.tv_message, notificationModal.getMessage());

        if (bitmap != null) {
            notificationLayout.setImageViewBitmap(R.id.iv_image, bitmap);
            notificationLayoutBig.setImageViewBitmap(R.id.iv_image, bitmap);
            notificationLayout.setViewVisibility(R.id.ll_image_layout, View.VISIBLE);
        } else {
            notificationLayout.setViewVisibility(R.id.ll_image_layout, View.GONE);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher_notification)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        notificationBuilder.setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayoutBig)
                .setShowWhen(false);

        notificationBuilder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = notificationBuilder.build();
        notificationManager.notify((int) notificationId, notification);
    }

    public class GeneratePictureNotification extends AsyncTask<Void, Void, Bitmap> {
        AppNotificationModel appNotificationModel;

        public GeneratePictureNotification (AppNotificationModel appNotificationModel) {
            this.appNotificationModel = appNotificationModel;
        }


        @Override
        protected Bitmap doInBackground (Void... voids) {
            String imageUrl = appNotificationModel.getImageLarge();
            if (imageUrl != null && imageUrl.trim().length() > 0) {
                try {
                    Bitmap bitmap = Picasso.get()
                            .load(imageUrl).resize(200, 200).get();
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute (Bitmap bitmap) {
            super.onPostExecute(bitmap);
            sendPictureNotification(appNotificationModel, bitmap);
        }
    }
}
