package com.quickzetuser.rest;

/**
 * Created by ubuntu on 27/3/18.
 */

public interface WebRequestConstants extends ServerConstants {

    //need to change according to project detail
    String SMS_SENDER = "TAXIII";

    //need to change according to project detail
    String PUSHER_KEY = "d47196f21749e78078a6";

    long RENTAL_CAB_TYPE_ID = 10002;
    long OUSTATION_CAB_TYPE_ID = 10003;
    long SHARING_CAB_TYPE_ID = 10004;


    String DEVICE_TYPE_ANDROID = "A";
    String TYPE_V = "V";
    String TYPE_F = "F";
    String LANG_1 = "1";
    String HEADER_KEY_TOKEN = "token";
    String HEADER_KEY_LANG = "lang";
    String HEADER_KEY_DEVICE_TYPE = "devicetype";
    String HEADER_KEY_DEVICE_INFO = "deviceinfo";
    String HEADER_KEY_APP_INFO = "appinfo";

    String PAYMENT_METHOD = "CASH";
    String BOOKING_RUNNING_TYPE_INDIVIDUAL = "I";
    String BOOKING_RUNNING_TYPE_RENTAL = "R";
    String BOOKING_RUNNING_TYPE_OUTSTATION = "O";
    String BOOKING_RUNNING_TYPE_SHARING = "S";

    String OUTSTAION_TRIP_ONEWAY = "O";
    String OUTSTAION_TRIP_ROUND = "R";

    long SCHEDULE_BOOKING_MIN_TIME = 20 * 60 * 1000; //20 min millisecond
    long SCHEDULE_BOOKING_MAX_TIME = 24 * 60 * 60 * 1000;//24 hr millisecond
    long SCHEDULE_BOOKING_MAX_TIME_30 = 24 * 60 * 60 * 1000*15;//24 hr millisecond


    String BUCKET_PATH_PROFILE_IMAGE = "riders";
    String FILE_UPLOAD_METHOD_S3 = "s3";
    String FILE_UPLOAD_METHOD_LOCAL = "local";
    String APP_FILE_UPLOAD_METHOD = FILE_UPLOAD_METHOD_S3;

    String OPEN_WITH_RQUEST_CODE = "OPEN_WITH_REQUEST_CODE";
    String DATA = "data";
    int REQUEST_CODE_CHECK_SETTINGS = 1000;
    int REQUEST_CODE_SELECT_ADDRESS = 105;

    String CLIENT_LOCATION_EVENT = "client-driver_location";
    String PUSHER_AUTH_URL = BASE + "pusher_auth_private";


    String URL_COUNTRIES = BASE + "countries";
    int ID_COUNTRIES = 1;

    String URL_NEW_USER = BASE + "newuser";
    int ID_NEW_USER = 2;
    String KEY_NEW_USER = "newuser";

    String URL_VERIFY_OTP = BASE + "verifyotp";
    int ID_VERIFY_OTP = 3;

    String URL_LOGIN = BASE + "login";
    int ID_LOGIN = 4;

    String URL_FORGOT_PASSWORD = BASE + "forgotpassword";
    int ID_FORGOT_PASSWORD = 5;
    String KEY_FORGOT_PASSWORD = "forgotpassword";

    String URL_LOGOUT = BASE + "logout";
    int ID_LOGOUT = 6;

    String URL_UPDATE_PROFILE = BASE + "updateprofile";
    int ID_UPDATE_PROFILE = 7;

    String URL_CITIES = BASE + "cities/%s";
    int ID_CITIES = 8;

    String URL_VEHICLE_TYPE = BASE + "get_vehicle_types/%s";
    int ID_VEHICLE_TYPE = 9;

    String URL_CHECK_FARE = BASE + "check_fare";
    int ID_CHECK_FARE = 10;
    String KEY_PROMOCODE_DIALOG = "PromocodeDialog";

    int ID_GET_ROUTE = 11;

    String URL_CHECK_UPDATE = BASE + "get_playstore_app_version";
    int ID_CHECK_UPDATE = 12;

    String URL_VIEW_DRIVER_ON_MAP = BASE + "viewdriversonmap";
    int ID_VIEW_DRIVER_ON_MAP = 13;

    String URL_BOOK_CAB = BASE + "book_cab";
    int ID_BOOK_CAB = 14;

    String URL_BOOKING_DETAIL = BASE + "get_bookings_detail/%s";
    int ID_BOOKING_DETAIL = 15;

    String URL_CURRENT_BOOKING = BASE + "get_current_booking";
    int ID_CURRENT_BOOKING = 16;

    String URL_CANCEL_TRIP = BASE + "cancel_trip";
    int ID_CANCEL_TRIP = 17;

    String URL_BOOKING_HISTORY = BASE + "get_bookings_history/%s/%s";
    int ID_BOOKING_HISTORY = 18;

    String URL_PAYTM_WALLET_CALLBACK = BASE + "wallet_callback";
    String URL_WALLET_RECHARGE_PAYTM = BASE + "wallet_recharge?amount=%s&userid=%s";
    int ID_WALLET_RECHARGE_PAYTM = 19;


    String URL_GET_WALLET_DETAIL = BASE + "get_rider_wallet_detail";
    int ID_GET_WALLET_DETAIL = 20;

    String URL_GET_NOTIFICATION = BASE + "get_notification/%s";
    int ID_GET_NOTIFICATION = 21;

    String URL_GIVE_RATING = BASE + "give_rating";
    int ID_GIVE_RATING = 22;

    String URL_PAY_REMAINING_PAYMENT = BASE + "pay_remaining_payment";
    int ID_PAY_REMAINING_PAYMENT = 23;
    String KEY_PAY_REMAINING_PAYMENT = "pay_remaining_payment";

    String URL_GET_REMAINING_PAYMENT = BASE + "get_remaining_payment_bookings";
    int ID_GET_REMAINING_PAYMENT = 24;

    String URL_UPDATE_TOKEN = BASE + "update_token";
    int ID_UPDATE_TOKEN = 25;

    String URL_ADD_FAVOURITE = BASE + "add_rider_favourite_address";
    int ID_ADD_FAVOURITE = 26;

    String URL_GET_FAVOURITE = BASE + "get_rider_favourite_address";
    int ID_GET_FAVOURITE = 27;

    String URL_DELETE_FAVOURITE = BASE + "delete_rider_favourite_address/%s";
    int ID_DELETE_FAVOURITE = 28;

    String URL_BOOKING_ROUTE_IMAGE_SAVE = BASE + "booking_route_image_save/%s";
    int ID_BOOKING_ROUTE_IMAGE_SAVE = 29;


    String URL_SOS = BASE + "sos";
    int ID_SOS = 30;

    String URL_SEND_BILL = BASE + "send_bill";
    int ID_SEND_BILL = 31;


    String URL_GET_RIDER_DETAIL = BASE + "get_rider_detail";
    int ID_GET_RIDER_DETAIL = 32;

    String URL_PLAY_STORE_APP_VERSION = BASE + "get_playstore_app_version/%s";

    String URL_CHECK_PACKAGE = BASE + "package_list";
    int ID_CHECK_PACKAGE = 33;

    String URL_VIEW_DRIVER_ON_MAP_RENTAL = BASE + "viewdriversonmap_rental";
    int ID_VIEW_DRIVER_ON_MAP_RENTAL = 34;

    String URL_CHECK_PACKAGE_PROMOCODE = BASE + "package_list_promocode";
    int ID_CHECK_PACKAGE_PROMOCODE = 35;

    String URL_CHECK_OUTSTATION_PACKAGE = BASE + "outstation_fares";
    int ID_CHECK_OUTSTATION_PACKAGE = 36;

    String URL_CHECK_SHARING_PACKAGE = BASE + "pool_package_list";
    int ID_CHECK_SHARING_PACKAGE = 37;

    String URL_TERM_AND_CONDITION = DOMAIN + "p/sites/brtaxipool/welcome/tearms_and_condition";
    int ID_TERM_AND_CONDITION = 38;

    String URL_CHECK_BOOKING_DROP_LOCATION = BASE + "change_booking_drop_location";
    int ID_CHANGE_BOOKING_DROP_LOCATION = 39;
}
