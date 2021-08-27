package com.quickzetuser.ui.main;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.appupdater.AppUpdateChecker;
import com.appupdater.AppUpdateModal;
import com.appupdater.DialogAppUpdater;
import com.base.BaseFragment;
import com.fcm.NotificationModal;
import com.fcm.NotificationPrefs;
import com.fcm.PushNotificationHelper;
import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceBaseResponseModel;
import com.models.DeviceInfoModal;
import com.permissions.PermissionHelperNew;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseActivity;
import com.quickzetuser.database.tables.BookingTable;
import com.quickzetuser.database.tables.FavoriteTable;
import com.quickzetuser.fcm.AppNotificationMessagingService;
import com.quickzetuser.fcm.AppNotificationModel;
import com.quickzetuser.fcm.AppNotificationType;
import com.quickzetuser.model.BookCabModel;
import com.quickzetuser.model.FavouriteModel;
import com.quickzetuser.model.UserModel;
import com.quickzetuser.model.request_model.UpdateNotificationTokenRequestModel;
import com.quickzetuser.model.web_response.FavouriteResponseModel;
import com.quickzetuser.model.web_response.UserResponseModel;
import com.quickzetuser.preferences.UserPrefs;
import com.quickzetuser.ui.MyApplication;
import com.quickzetuser.ui.login.LoginActivity;
import com.quickzetuser.ui.main.booking.confirmBooking.ConfirmBookingFragment;
import com.quickzetuser.ui.main.dialog.message.MessageDialog;
import com.quickzetuser.ui.main.dialog.runningBooking.RunningBookingDialog;
import com.quickzetuser.ui.main.helpers.SideMenuHelper;
import com.quickzetuser.ui.main.helpers.ToolBarHelper;
import com.quickzetuser.ui.utilities.BookingStatusHelper;
import com.quickzetuser.ui.utilities.MapHandler;
import com.quickzetuser.ui.utilities.RunningBookingHead;
import com.utilities.DeviceUtils;
import com.utilities.Utils;

import java.util.List;

import static com.quickzetuser.appBase.AppBaseMapFragment.REQUEST_CHECK_SETTINGS;

public class MainActivity extends AppBaseActivity
        implements UserPrefs.UserPrefsListener, RunningBookingHead.RunningBookingHeadListener,
        PushNotificationHelper.PushNotificationHelperListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public SideMenuHelper sideMenuHelper;
    public ToolBarHelper toolBarHelper;
    public MapHandler mapHandler;
    public RelativeLayout rl_main;
    public FrameLayout main_container;
    public View runningBookingView;
    PermissionHelperNew.OnSpecificPermissionGranted onSpecificPermissionGranted;
    Handler backStackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                BaseFragment baseFragment = getLatestFragment();
                toolBarHelper.onCreateViewFragment(baseFragment);
                if (baseFragment != null)
                    baseFragment.viewCreateFromBackStack();
            }
        }
    };
    private PushNotificationHelper pushNotificationHelper;
    private FrameLayout android_content;
    PermissionHelperNew.OnSpecificPermissionGranted locationPermissionListener = new PermissionHelperNew.OnSpecificPermissionGranted() {
        @Override
        public void onPermissionGranted(boolean isGranted, boolean withNeverAsk, String permission, int requestCode) {
            if (requestCode == PermissionHelperNew.LOCATION_PERMISSION_REQUEST_CODE) {
                if (isGranted) {
                    if (android_content != null) {
                        android_content.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setUpActivity();
                            }
                        }, 100);
                    }
                } else {
                    if (withNeverAsk) {
                        PermissionHelperNew.showNeverAskAlert(MainActivity.this, true, requestCode);
                    } else {
                        PermissionHelperNew.showSpecificDenyAlert(MainActivity.this, permission, requestCode, true);
                    }
                }
            }
        }
    };
    private boolean checkLocationPermission = true;

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_main;
    }


    public SideMenuHelper getSideMenuHelper() {
        return sideMenuHelper;
    }

    public ToolBarHelper getToolBarHelper() {
        return toolBarHelper;
    }

    public MapHandler getMapHandler() {
        return mapHandler;
    }

    public UserPrefs getUserPrefs() {
        return ((MyApplication) getApplication()).getUserPrefs();
    }

    public UserModel getUserModel() {
        return ((MyApplication) getApplication()).getUserModel();
    }

    @Override
    public int getFragmentContainerResourceId() {
        return R.id.main_container;
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        rl_main = findViewById(R.id.rl_main);
        main_container = findViewById(R.id.main_container);

        getFm().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                backStackHandler.removeMessages(1);
                backStackHandler.sendEmptyMessageDelayed(1, 100);
            }
        });

        android_content = findViewById(android.R.id.content);
        ((MyApplication) getApplication()).getCityHelper().getCitiesFromServer();
        if (initializeUserModel()) {
            setUpActivity();
        }
    }

    private void setUpActivity() {
        if (!PermissionHelperNew.hasLocationPermission(this)) {
            return;
        }
        pushNotificationHelper = ((MyApplication) getApplication()).getPushNotificationHelper();
        sideMenuHelper = new SideMenuHelper(this);
        toolBarHelper = new ToolBarHelper(this);
        mapHandler = new MapHandler(this);

        setupScreen();

        startService(new Intent(MainActivity.this, RunningBookingHead.class));
    }

    private boolean initializeUserModel() {
        if (getUserModel() == null) {
            goToLoginActivity();
            return false;
        }
        getUserPrefs().addListener(this);
        return true;

    }

    @Override
    public void changeFragment(Fragment f, boolean addBackStack, boolean clearAll, int pos, boolean isReplace) {
        super.changeFragment(f, addBackStack, clearAll, pos, isReplace);
        mapHandler.hideLayout();
    }

    @Override
    public void changeFragment(Fragment f, boolean addBackStack, boolean clearAll, int pos, int enterAnim, int exitAnim, int enterAnimBackStack, int exitAnimBackStack, boolean isReplace) {
        super.changeFragment(f, addBackStack, clearAll, pos, enterAnim, exitAnim, enterAnimBackStack, exitAnimBackStack, isReplace);
        mapHandler.hideLayout();
    }

    @Override
    public void onCreateViewFragment(BaseFragment baseFragment) {
        toolBarHelper.onCreateViewFragment(baseFragment);

    }

    @Override
    public void onDestroyViewFragment(BaseFragment baseFragment) {
        toolBarHelper.onDestroyViewFragment(baseFragment);
    }

    @Override
    public void onBackPressed() {
        if (sideMenuHelper.closeDrawer()) {
            return;
        }
        BaseFragment appBaseFragment = getLatestFragment();
        if (appBaseFragment != null) {
            super.onBackPressed();
            if (getLatestFragment() == null) {
                mapHandler.showLayout();
            }
        } else if (mapHandler.onBackPressed()) {
           // mapHandler.showLayout();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mapHandler.showLayout();
                }
            },200);
        } else {
            super.onBackPressed();
        }

    }

    private void goToLoginActivity() {
        sendActivityFinish(this, LoginActivity.class);
    }

    private void setupScreen() {
        mapHandler.showLayout();
        checkForAppUpdate();
        getRiderDetail();
    }

    public void getNotificationData() {
        if (getIntent().getExtras() != null) {
            AppNotificationModel appNotificationModel = new AppNotificationModel(getIntent().getExtras());
            if (isValidObject(appNotificationModel)) {

                if (appNotificationModel.isAdminAlert()) {
                    sideMenuHelper.ll_notification.performClick();
                    return;
                }

                onPushNotificationReceived(appNotificationModel);
            }

        }
    }

    private void checkForAppUpdate() {
        AppUpdateChecker appUpdateChecker = new AppUpdateChecker(this, new AppUpdateChecker.onAppUpdateAvailable() {
            @Override
            public void appUpdateAvailable(AppUpdateModal appUpdatemodal) {
                printLog(appUpdatemodal.toString());
                if (!isFinishing()) {
                    String msg = String.format(getString(R.string.text_app_update),
                            appUpdatemodal.getFormatedVersionName());
                    new DialogAppUpdater(MainActivity.this, msg).show();
                }
            }
        });
        appUpdateChecker.checkForUpdate(URL_PLAY_STORE_APP_VERSION);
    }

    private void getRiderDetail() {
        getWebRequestHelper().getRiderDetail(this);
    }


    @Override
    public void userLoggedIn(UserModel userModel) {

    }

    @Override
    public void loggedInUserUpdate(UserModel userModel) {
        getSideMenuHelper().initSetUserData();
    }

    @Override
    public void loggedInUserClear() {
    }

    public void updateRiderProfile(UserModel model) {
        if (getUserModel() != null) {
            getUserPrefs().updateLoggedInUser(model);
        }
    }

    public void clearLoggedInUser() {
        if (getUserPrefs() != null) {
            getUserPrefs().clearLoggedInUser();
        }
    }

    public void getFavouriteAddress() {
        getWebRequestHelper().getFavouriteAddress(this);
    }

    @Override
    public void onWebRequestCall(WebRequest webRequest) {
        UserModel userModel = getUserModel();
        webRequest.addHeader(HEADER_KEY_LANG, LANG_1);

        DeviceInfoModal deviceInfoModal = new DeviceInfoModal(this);
        webRequest.addHeader(HEADER_KEY_DEVICE_TYPE, DEVICE_TYPE_ANDROID);
        webRequest.addHeader(HEADER_KEY_DEVICE_INFO, deviceInfoModal.toString());
        webRequest.addHeader(HEADER_KEY_APP_INFO, deviceInfoModal.getAppInfo());

        webRequest.addHeader(HEADER_KEY_TOKEN, DeviceUtils.getUniqueDeviceId());
        webRequest.setBasicAuth(userModel.getMobile(), userModel.getPassword());
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        if (webRequest.getResponseCode() == 401 || webRequest.getResponseCode() == 412) {
            WebServiceBaseResponseModel modelResponse =
                    webRequest.getBaseResponsePojo();
            ((MyApplication) getApplication()).unAuthorizedResponse(modelResponse);
            return;
        } else {
            if (webRequest.isSuccess()) {
                switch (webRequest.getWebRequestId()) {
                    case ID_GET_FAVOURITE:
                        handleFavouriteResponse(webRequest);
                        break;
                    case ID_GET_RIDER_DETAIL:
                        handleGetRiderDetailResponse(webRequest);
                        break;
                }
            } else {
                if (webRequest.getWebRequestId() == ID_GET_RIDER_DETAIL) {
                    return;
                }
                String msg = webRequest.getErrorMessageFromResponse();
                if (isValidString(msg)) {
                    showErrorMessage(msg);
                }
            }
        }
    }

    private void handleFavouriteResponse(WebRequest webRequest) {
        FavouriteResponseModel responseModel =
                webRequest.getResponsePojo(FavouriteResponseModel.class);
        if (responseModel == null) return;
        List<FavouriteModel> data = responseModel.getData();
        for (FavouriteModel datum : data) {
            FavoriteTable.getInstance().addFavorite(datum);
        }

    }

    private void handleGetRiderDetailResponse(WebRequest webRequest) {
        UserResponseModel newDriver = webRequest.getResponsePojo(UserResponseModel.class);
        if (newDriver != null && newDriver.getData() != null) {
            updateRiderProfile(newDriver.getData());
            getSideMenuHelper().initSetUserData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions != null && permissions.length > 0) {
            checkLocationPermission = false;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelperNew.onSpecificRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication myApplication = (MyApplication) getApplication();
        myApplication.setRunningBookingHeadListener(this);
        if (getUserModel() != null) {
            getFavouriteAddress();
            updateHeadService(false);
            AppNotificationMessagingService.generateLatestToken();
            if (checkLocationPermission) {
                PermissionHelperNew.needLocationPermission(this, locationPermissionListener);
            } else {
                checkLocationPermission = true;
            }
            checkLocationPermissionGrantFromSettring();
            if (isValidObject(pushNotificationHelper)) {
                pushNotificationHelper.addNotificationHelperListener(this);
            }

            updateToken();
        }

        boolean termsConditions = MyApplication.getInstance().getAppPrefs().getTermsConditions();
        if (!termsConditions) {
            showTermConditionDialog(getResources().getString(R.string.term_condition), "");
        }
    }

    public void showTermConditionDialog(String title, String message) {
        //TermConditionDialog dialog = TermConditionDialog.getNewInstance(title, message);
        //dialog.show(getFm(), TermConditionDialog.class.getSimpleName());
    }

    private void updateToken() {
        UpdateNotificationTokenRequestModel requestModel = new UpdateNotificationTokenRequestModel();
        requestModel.device_id = DeviceUtils.getUniqueDeviceId();
        requestModel.device_token = NotificationPrefs.getInstance(this).getNotificationToken();
        getWebRequestHelper().updateToken(requestModel, this);
    }

    private void checkLocationPermissionGrantFromSettring() {
        if (PermissionHelperNew.requestingPermissionFromSetting) {
            PermissionHelperNew.requestingPermissionFromSetting = false;
            if (PermissionHelperNew.requestingPermissionFromSettingCode == PermissionHelperNew.LOCATION_PERMISSION_REQUEST_CODE) {
                if (PermissionHelperNew.hasLocationPermission(this)) {
                    locationPermissionListener.onPermissionGranted(true, false,
                            PermissionHelperNew.LOCATION_PERMISSION, PermissionHelperNew.LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        }
    }

    public boolean checkUnAuthCode(WebRequest webRequest) {
        if (webRequest.getResponseCode() == 401 || webRequest.getResponseCode() == 412) {
            WebServiceBaseResponseModel modelResponse =
                    webRequest.getBaseResponsePojo();
            ((MyApplication) getApplication()).unAuthorizedResponse(modelResponse);
            return true;
        }
        return false;
    }

    /**
     * calling intent with permission
     *
     * @param number
     */
    @SuppressLint("MissingPermission")
    public void makeDirectCall(String number) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + number));
            startActivity(callIntent);
        } catch (ActivityNotFoundException e) {
            showToast("No app found for call.");
        }
    }

    public void makeCall(final String number) {
        /*PermissionHelperNew.OnSpecificPermissionGranted onSpecificPermissionGranted = new PermissionHelperNew.OnSpecificPermissionGranted() {
            @Override
            public void onPermissionGranted(boolean isGranted, boolean withNeverAsk,
                                            String permission, int requestCode) {
                if (isGranted) {
                    makeDirectCall(number);
                } else {
                    if (withNeverAsk) {
                        PermissionHelperNew.showNeverAskAlert(MainActivity.this, false, PermissionHelperNew.CALL_PERMISSION_REQUEST_CODE);
                    } else {
                        PermissionHelperNew.showSpecificDenyAlert(MainActivity.this, permission, requestCode, false);
                    }
                }
            }
        };
        if (!needCallPermission(onSpecificPermissionGranted)) {
            makeDirectCall(number);
        }*/
        makeDirectCall(number);
    }

    public boolean needCallPermission(PermissionHelperNew.OnSpecificPermissionGranted onSpecificPermissionGranted) {
        this.onSpecificPermissionGranted = onSpecificPermissionGranted;
        return PermissionHelperNew.needSpecificPermissions(this,
                PermissionHelperNew.CALL_PERMISSION, PermissionHelperNew.CALL_PERMISSION_REQUEST_CODE);
    }

    public void zoomToCurrentLocation() {
        mapHandler.getMapFragment().goToRealCurrentLocation();
    }

    @Override
    public void RunningBookingStart(RunningBookingHead runningBookingHead, View view) {
        runningBookingHead.setRootViewGroup(rl_main);
        this.runningBookingView = view;
    }

    @Override
    public void RunningBookingClick() {
        RunningBookingDialog runningBookingDialog = new RunningBookingDialog(this);
        runningBookingDialog.show(getSupportFragmentManager(), runningBookingDialog.getClass().getSimpleName());

    }

    @Override
    protected void onPause() {
        if (RunningBookingHead.isRunning(this)) {
            stopService(new Intent(MainActivity.this, RunningBookingHead.class));
        }
        if (isValidObject(pushNotificationHelper)) {
            pushNotificationHelper.removeNotificationHelperListener(this);
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (isValidObject(pushNotificationHelper)) {
            pushNotificationHelper.removeNotificationHelperListener(this);
        }
        ((MyApplication) getApplication()).stopLocationService();
        if (RunningBookingHead.isRunning(this)) {
            stopService(new Intent(MainActivity.this, RunningBookingHead.class));
        }
        super.onDestroy();
    }

    public void updateHeadService(boolean needClose) {
        if (needClose) {
            if (RunningBookingHead.isRunning(this)) {
                stopService(new Intent(MainActivity.this, RunningBookingHead.class));
            }
            return;
        }
        if (!RunningBookingHead.isRunning(this)) {
            startService(new Intent(MainActivity.this, RunningBookingHead.class));
            return;
        }
        if (runningBookingView != null) {
            TextView tv_count = runningBookingView.findViewById(R.id.tv_count);
            int count = BookingTable.getInstance().getRunningBookingCount();
            if (count > 0) {
                tv_count.setText(String.valueOf(count));
            } else {
                if (RunningBookingHead.isRunning(this)) {
                    stopService(new Intent(MainActivity.this, RunningBookingHead.class));
                }
            }
        }
    }

    @Override
    public void onPushNotificationReceived(NotificationModal notificationModal) {
        AppNotificationModel appNotificationModel = (AppNotificationModel) notificationModal;
        if (isValidObject(appNotificationModel)) {
            if (appNotificationModel.isAdminAlert()) {
                String notType = AppNotificationType.TYPE_ADMIN_ALERT;
                showMessageDialogWithCancel(appNotificationModel.getTitle(), appNotificationModel.getMessage(), notType);
                return;
            }
            if (appNotificationModel.isBookingOtp()) {
                showMessageDialog(appNotificationModel.getTitle(), appNotificationModel.getMessage());
                return;
            }

            if (appNotificationModel.isBookingToll()) {
                showMessageDialog(appNotificationModel.getTitle(), appNotificationModel.getMessage());
                return;
            }

           /* if (RunningBookingHead.isRunning(this)) {
                if (appNotificationModel.isAutoCancelBookingNotification() ||
                        appNotificationModel.isDriversDeclineBookingNotification() ||
                        appNotificationModel.isDriverCancelAcceptBookingNotification()
                ||appNotificationModel.isDriverCompletedBookingNotification()
                ||appNotificationModel.isDriveStartBookingNotification()
                ||appNotificationModel.isDriverCompletedBookingNotification()
                ||appNotificationModel.isDriverArrivedNotification()
                ) {
                    BookingTable.getInstance().deleteBooking(appNotificationModel.getBookingId());
                    updateHeadService(false);*/


            BookCabModel bookCabModel = null;
            Fragment bottomFragment = getMapHandler().getBottomFragment();
            if(bottomFragment instanceof ConfirmBookingFragment){
                bookCabModel = ((ConfirmBookingFragment)bottomFragment).getBookCabModel();

                if (appNotificationModel.getBookingId() != null &&
                        (appNotificationModel.getBookingId() != bookCabModel.getBooking_id())){
                    bookCabModel = null;
                }

            }


            if (RunningBookingHead.isRunning(this) || bookCabModel ==null) {
                if (appNotificationModel.isAutoCancelBookingNotification() ||
                        appNotificationModel.isDriversDeclineBookingNotification() ||
                        appNotificationModel.isDriverCancelAcceptBookingNotification()
                        ||appNotificationModel.isDriverCompletedBookingNotification()
                        ||appNotificationModel.isDriveStartBookingNotification()
                        ||appNotificationModel.isDriverArrivedNotification()
                        ||appNotificationModel.isDriverCompletedBookingNotification()
                ) {
                    if (appNotificationModel.isAutoCancelBookingNotification() ||
                            appNotificationModel.isDriversDeclineBookingNotification() ||
                            appNotificationModel.isDriverCancelAcceptBookingNotification()
                    ) {
                        BookingTable.getInstance().deleteBooking(appNotificationModel.getBookingId());

                    }else {
                        getBookingDetailBackground(String.valueOf(appNotificationModel.getBookingId()));
                    }
                    if(!(bottomFragment instanceof ConfirmBookingFragment)) {
                        updateHeadService(false);
                    }

                    String msg = "Booking id : " + appNotificationModel.getBookingId() + "\n"
                            + "Sorry! All of our drivers are busy. Please try again.";
                    if (appNotificationModel.getStatus() == 7) {
                        msg = "Booking id : " + appNotificationModel.getBookingId() + "\n"
                                + "is cancelled by driver.";
                    }else if (appNotificationModel.getStatus() == 3) {
                        msg = "Booking id : " + appNotificationModel.getBookingId() + "\n"
                                +  "Driver is arrived at pickup location.";
                    }else if (appNotificationModel.getStatus() == 4) {
                        msg = "Booking id : " + appNotificationModel.getBookingId() + "\n"
                                +  "Enjoy! now you are on trip.";
                    }else if (appNotificationModel.getStatus() == 5) {
                        msg = "Booking id : " + appNotificationModel.getBookingId() + "\n"
                                + "Completed Successfully.";
                    }
                    showMessageDialog(getString(R.string.app_name),
                            msg);
                }
            }
        }
    }

    public void showMessageDialog(String title, String message) {
        MessageDialog messageDialog = MessageDialog.getNewInstance(title, message);
        messageDialog.show(getFm(), MessageDialog.class.getSimpleName());
    }

    public void showMessageDialogWithCancel(String title, final String message, final String not_type) {
        final MessageDialog messageDialog = new MessageDialog().getNewInstance(title, message, not_type);
        messageDialog.show(getFm(), MessageDialog.class.getSimpleName());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_CANCELED) {
                if (!Utils.isGpsProviderEnabled(this)) {
                    showCustomToast(getString(R.string.turn_on_gps_msg));
                    finish();
                }
            }
        }
    }


    private void getBookingDetailBackground(String booking_id){
        BookCabModel bookCabModel = BookingTable.getInstance().getRunningBookingFromId(Long.parseLong(booking_id));
        if (bookCabModel == null) return;
        try {
            final BookingStatusHelper bookingStatusHelper = BookingStatusHelper.getNewInstances(this, bookCabModel);
            if (bookingStatusHelper != null) {
                bookingStatusHelper.setBookingStatusHelperListener(new BookingStatusHelper.BookingStatusHelperListener() {
                    @Override
                    public void onBookingUpdate(BookCabModel bookCabModel) {

                        bookingStatusHelper.stopBookingUpdater(true);
                        if(bookCabModel!=null) {
                            BookingTable.getInstance().addOrUpdateBooking(bookCabModel);
                        }
                    }
                });
                bookingStatusHelper.startBookingUpdater();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
