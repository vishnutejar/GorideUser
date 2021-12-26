package com.quickzetuser.ui.splash;

import android.content.Intent;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.permissions.PermissionHelperNew;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseActivity;
import com.quickzetuser.fcm.AppNotificationMessagingService;
import com.quickzetuser.preferences.UserPrefs;
import com.quickzetuser.ui.MyApplication;
import com.quickzetuser.ui.login.LoginActivity;
import com.quickzetuser.ui.main.MainActivity;
import com.quickzetuser.ui.main.dialog.outstationtimepicker.OutstationTimeDialog;
import com.utilities.Utils;

import static com.utilities.Utils.PLAY_SERVICES_RESOLUTION_REQUEST;



public class SplashActivity extends AppBaseActivity
        implements PermissionHelperNew.OnSpecificPermissionGranted {

    private static final long SPLASH_TIME_MS = 3000;
    private boolean callOnResume = true;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (userPrefs.getLoggedInUser() == null) {
                sendActivityFinish(SplashActivity.this, LoginActivity.class);
                return;
            }
            sendActivityFinish(SplashActivity.this, MainActivity.class);

        }
    };
    private Handler handler = new Handler();
    UserPrefs userPrefs = null;

    @Override
    public int getLayoutResourceId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_splash;
    }

    @Override
    public void initializeComponent() {
        userPrefs = new UserPrefs(SplashActivity.this);
    }


    private void goToForward() {
        if (PermissionHelperNew.needLocationPermission(this, this)) {
            return;
        }
        if (!Utils.isValidPlayServices(this)) return;
        ((MyApplication) getApplication()).startLocationService(null);
        handler.postDelayed(runnable, SPLASH_TIME_MS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (callOnResume) {
            goToForward();
        } else {
            callOnResume = true;
        }
        AppNotificationMessagingService.generateLatestToken();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onStop() {
        if (userPrefs.getLoggedInUser() == null) {
            ((MyApplication) getApplication()).stopLocationService();
        }
        super.onStop();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions != null && permissions.length > 0) {
            callOnResume = false;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelperNew.onSpecificRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(boolean isGranted, boolean withNeverAsk, String permission, int requestCode) {
        if (requestCode == PermissionHelperNew.LOCATION_PERMISSION_REQUEST_CODE) {
            if (isGranted) {
                goToForward();
            } else {
                if (withNeverAsk) {
                    PermissionHelperNew.showNeverAskAlert(this, true, requestCode);
                } else {
                    PermissionHelperNew.showSpecificDenyAlert(this, permission, requestCode, true);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_SERVICES_RESOLUTION_REQUEST) {
            if (resultCode == RESULT_OK) {
                goToForward();
            } else {
                showCustomToast("Please update Google play services.");
                finish();
            }
        }
    }

    public void test() {
        OutstationTimeDialog outstationTimeDialog = new OutstationTimeDialog();
        outstationTimeDialog.show(getFm(), outstationTimeDialog.getClass().getSimpleName());
    }
}

