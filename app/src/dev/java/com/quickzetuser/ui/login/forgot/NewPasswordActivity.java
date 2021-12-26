package com.quickzetuser.ui.login.forgot;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.customviews.OtpView;
import com.google.gson.Gson;
import com.medy.retrofitwrapper.WebRequest;
import com.models.DeviceInfoModal;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseActivity;
import com.quickzetuser.model.request_model.ForgotPasswordRequestModel;
import com.quickzetuser.model.request_model.VerifyOtpRequestModel;
import com.quickzetuser.model.web_response.NewRiderResponseModel;
import com.quickzetuser.model.web_response.UserResponseModel;
import com.quickzetuser.ui.login.LoginActivity;
import com.utilities.Utils;



public class NewPasswordActivity extends AppBaseActivity implements View.OnClickListener {


    private OtpView otpView;
    private TextView otpTimer;
    private TextView tv_resendOtp;
    private ImageView iv_menu_left;
    private ImageView iv_back;
    private ImageView iv_bagghi_icon;
    private TextView tv_title;
    private EditText ed_new_password;
    private EditText ed_confirm_password;
    private TextView tv_submit;
    private ForgotPasswordRequestModel requestModel;

    int grayColor = 0xff000000;
    int textLinkColor = 0xff000000;

    private final long COUNTDOWN_TIME = 60000;


    @Override
    public int getLayoutResourceId () {
        return R.layout.activity_new_password;
    }

    @Override
    public void initializeComponent () {
        super.initializeComponent();
        try {
            TypedArray typedArray = obtainStyledAttributes(R.styleable.AppTheme);
            grayColor = typedArray.getColor(R.styleable.AppTheme_app_divider_color, grayColor);
            textLinkColor = typedArray.getColor(R.styleable.AppTheme_app_link_text_color, textLinkColor);
            typedArray.recycle();
        } catch (Resources.NotFoundException ignore) {

        }
        findViews();
        init();
    }

    private void findViews () {
        iv_menu_left = findViewById(R.id.iv_menu_left);
        iv_menu_left.setVisibility(View.GONE);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setVisibility(View.VISIBLE);
        iv_bagghi_icon = findViewById(R.id.iv_bagghi_icon);
        iv_bagghi_icon.setVisibility(View.GONE);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.new_password_title));

        otpView = findViewById(R.id.otpView);

        otpTimer = findViewById(R.id.otpTimer);
        tv_resendOtp = findViewById(R.id.tv_resendOtp);


        ed_new_password = findViewById(R.id.ed_new_password);
        ed_confirm_password = findViewById(R.id.ed_confirm_password);
        tv_submit = findViewById(R.id.tv_submit);
    }

    private void init () {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String forgotPassword = bundle.getString("forgotPassword");
            requestModel = new Gson().fromJson(forgotPassword, ForgotPasswordRequestModel.class);
        }

        tv_resendOtp.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        startCounter(COUNTDOWN_TIME);
    }


    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_resendOtp:
                onResendOtp();
                break;

            case R.id.tv_submit:
                onSubmit();
                break;
        }
    }

    private void onResendOtp () {
        if (requestModel == null) {
            return;
        }
        displayProgressBar(false);
        getWebRequestHelper().forgotPassword(requestModel, this);
    }

    private void onSubmit () {
        String otp = otpView.getOTP();
        if (!getValidate().validOtp(otp, 4)) {
            return;
        }
        if (!getValidate().validPasswordLength(ed_new_password)) {
            return;
        }
        if (!getValidate().validConfirmPassword(ed_new_password.getText().toString(),
                ed_confirm_password)) {
            return;
        }
        verifyOtp(otp);
    }

    private void verifyOtp (String otp) {
        if (requestModel == null) {
            return;
        }

        VerifyOtpRequestModel verifyOtp = new VerifyOtpRequestModel();
        String con_pass = ed_new_password.getText().toString().trim();

        verifyOtp.mobileno = requestModel.mobileno;
        verifyOtp.otp = otp;
        verifyOtp.type = TYPE_F;
        if (requestModel.mobile_code != null) {
            verifyOtp.mobile_code = requestModel.mobile_code;
        }
        verifyOtp.password = con_pass;

        displayProgressBar(false);
        getWebRequestHelper().verifyotp(verifyOtp, this);
    }


    @Override
    public void onBackPressed () {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void startCounter (final long millisecond) {
        long remainMin = (millisecond / (60 * 1000));
        long remainSec = (millisecond - (remainMin * 60 * 1000)) / 1000;
        otpTimer.setText(String.format("%02d:%02d", remainMin, remainSec));

        CountDownTimer downTimer = new CountDownTimer(millisecond, 1000) {
            @Override
            public void onTick (long millisUntilFinished) {
                tv_resendOtp.setClickable(false);
                long remainMin = (millisUntilFinished / (60 * 1000));
                long remainSec = (millisUntilFinished - (remainMin * 60 * 1000)) / 1000;
                otpTimer.setText(String.format("%02d:%02d", remainMin, remainSec));
                tv_resendOtp.setTextColor(grayColor);
            }

            @Override
            public void onFinish () {
                otpTimer.setText("00:00");
                tv_resendOtp.setClickable(true);
                if (isFinishing()) {
                    return;
                }
                tv_resendOtp.setTextColor(textLinkColor);
            }
        };
        downTimer.start();
    }

    @Override
    public void onWebRequestCall (WebRequest webRequest) {
        webRequest.addHeader(HEADER_KEY_LANG, LANG_1);
        DeviceInfoModal deviceInfoModal = new DeviceInfoModal(this);
        webRequest.addHeader(HEADER_KEY_DEVICE_TYPE, DEVICE_TYPE_ANDROID);
        webRequest.addHeader(HEADER_KEY_DEVICE_INFO, deviceInfoModal.toString());
        webRequest.addHeader(HEADER_KEY_APP_INFO, deviceInfoModal.getAppInfo());
        super.onWebRequestCall(webRequest);
    }

    @Override
    public void onWebRequestResponse (WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);

        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_FORGOT_PASSWORD:
                    handleForgotPasswordResponse(webRequest);
                    break;
                case ID_VERIFY_OTP:
                    handleVerifyOtpResponse(webRequest);
                    break;
            }
        } else {
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                webRequest.showInvalidResponse(msg);
            }
        }
    }

    private void handleForgotPasswordResponse (WebRequest webRequest) {
        startCounter(COUNTDOWN_TIME);
        NewRiderResponseModel newDriver = webRequest.getResponsePojo(NewRiderResponseModel.class);
        requestModel = webRequest.getExtraData(KEY_FORGOT_PASSWORD);
        Utils.showOtp(this, newDriver.getOtp());
    }

    private void handleVerifyOtpResponse (WebRequest webRequest) {
        UserResponseModel responsePojo = webRequest.getResponsePojo(UserResponseModel.class);
        showCustomToast(responsePojo.getMessage());
        sendActivityFinish(NewPasswordActivity.this, LoginActivity.class);
    }

    @Override
    public void otpMessageReceived (String messageText) {
        otpView.setOTP(messageText);
    }

    @Override
    protected void onStart () {
        super.onStart();
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(9999);
//        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
//        registerReceiver(getSmsReceiver(), filter);
    }

    @Override
    protected void onStop () {
        super.onStop();
//        unregisterReceiver(getSmsReceiver());
    }

}
