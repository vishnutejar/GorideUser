package com.rider.fcm;

/**
 * @author Manish Kumar
 * @since 14/12/17
 */


public interface AppNotificationType {

    String TYPE_AUTO_CANCEL_BOOKING = "autocancel_booking";
    String TYPE_DRIVER_ACCEPT_BOOKING = "driver_accept";
    String TYPE_DRIVER_CANCEL_ACCEPT_BOOKING = "driver_cancel_accept";
    String TYPE_DRIVERS_DECLINE_BOOKING = "drivers_decline";
    String TYPE_DRIVER_COMPLETED_BOOKING = "driver_complete";
    String TYPE_DRIVER_START_BOOKING = "driver_start";
    String TYPE_DRIVER_ARRIVED = "driver_arrived";
    String TYPE_DRIVER_VERIFIED = "driver_verified";
    String TYPE_DRIVER_BOOK_CAB = "driver_book_cab";
    String TYPE_ADMIN_ALERT = "adminalert";
    String TYPE_BOOKING_OTP = "booking_otp";
    String TYPE_BOOKING_TOLL = "booking_toll_applied";


}
