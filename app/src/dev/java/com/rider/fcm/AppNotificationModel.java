package com.rider.fcm;

import android.os.Bundle;

import com.fcm.NotificationModal;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Azher on 1/5/18.
 */

public class AppNotificationModel extends NotificationModal implements AppNotificationType {

    public AppNotificationModel (RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    public AppNotificationModel (Bundle bundle) {
        super(bundle);
    }

    public String getNotiType () {
        return getDataFromKey(AppNotificationMessagingService.KEY_NOTIFICATION_TYPE);
    }

    public String getTitle () {
        return getDataFromKey(AppNotificationMessagingService.KEY_NOTIFICATION_TITLE);
    }

    public String getMessage () {
        return getDataFromKey(AppNotificationMessagingService.KEY_NOTIFICATION_MESSAGE);
    }

    public Long getBookingId () {
        Long bookingId = null;
        try {
            bookingId = Long.parseLong(getDataFromKey(AppNotificationMessagingService.KEY_BOOKING_ID));
        } catch (NumberFormatException e) {

        }
        return bookingId;
    }

    public Integer getStatus () {
        Integer status = null;
        try {
            status = Integer.parseInt(getDataFromKey(AppNotificationMessagingService.KEY_STATUS));
        } catch (NumberFormatException e) {

        }
        return status;
    }

    public String getImageLarge () {
        return getDataFromKey(AppNotificationMessagingService.KEY_IMAGE_LARGE);
    }

    public String getImageSmall () {
        return getDataFromKey(AppNotificationMessagingService.KEY_IMAGE_SMALL);
    }

    public boolean isAutoCancelBookingNotification () {
        return getNotiType().equals(TYPE_AUTO_CANCEL_BOOKING);
    }

    public boolean isDriverAcceptBookingNotification () {
        return getNotiType().equals(TYPE_DRIVER_ACCEPT_BOOKING);
    }

    public boolean isDriversDeclineBookingNotification () {
        return getNotiType().equals(TYPE_DRIVERS_DECLINE_BOOKING);
    }

    public boolean isDriverCompletedBookingNotification () {
        return getNotiType().equals(TYPE_DRIVER_COMPLETED_BOOKING);
    }

    public boolean isDriverCancelAcceptBookingNotification () {
        return getNotiType().equals(TYPE_DRIVER_CANCEL_ACCEPT_BOOKING);
    }

    public boolean isDriveStartBookingNotification () {
        return getNotiType().equals(TYPE_DRIVER_START_BOOKING);
    }

    public boolean isDriverArrivedNotification () {
        return getNotiType().equals(TYPE_DRIVER_ARRIVED);
    }

    public boolean isDriveVerifiedNotification () {
        return getNotiType().equals(TYPE_DRIVER_VERIFIED);
    }

    public boolean isDriveBookCabNotification () {
        return getNotiType().equals(TYPE_DRIVER_BOOK_CAB);
    }

    public boolean isAdminAlert () {
        return getNotiType().equals(TYPE_ADMIN_ALERT);
    }

    public boolean isBookingOtp () {
        return getNotiType().equals(TYPE_BOOKING_OTP);
    }

    public boolean isBookingToll () {
        return getNotiType().equals(TYPE_BOOKING_TOLL);
    }

}
