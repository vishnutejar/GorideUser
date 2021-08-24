package com.rider.ui.signup.dialog;

import android.app.Dialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.customviews.OtpView;
import com.medy.retrofitwrapper.WebRequest;
import com.models.DeviceInfoModal;
import com.rider.R;
import com.rider.appBase.AppBaseDialogFragment;
import com.rider.model.UserModel;
import com.rider.model.request_model.NewUserRequestModel;
import com.rider.model.request_model.VerifyOtpRequestModel;
import com.rider.model.web_response.NewRiderResponseModel;
import com.rider.model.web_response.UserResponseModel;
import com.utilities.Utils;

/**
 * Created by ubuntu on 22/3/18.
 */

public class OtpDialog extends AppBaseDialogFragment {

    private static final String TAG = OtpDialog.class.getSimpleName();
    private OtpView otpView;
    private TextView otpTimer;
    private TextView tv_resendOtp;
    private TextView tv_submit;
    private ImageView iv_close;

    private String type;

    private final long COUNTDOWN_TIME = 60000;
    private NewUserRequestModel requestModel;
    private OnOtpConfirmListener listener;
    private int grayColor = 0xff000000;
    private int textLinkColor = 0xff000000;


    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_verify_otp;
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        try {
            TypedArray typedArray = getContext().obtainStyledAttributes(R.styleable.AppTheme);
            grayColor = typedArray.getColor(R.styleable.AppTheme_app_divider_color, grayColor);
            textLinkColor = typedArray.getColor(R.styleable.AppTheme_app_link_text_color, textLinkColor);
            typedArray.recycle();
        } catch (Resources.NotFoundException ignore) {

        }

        otpView = getView().findViewById(R.id.otpView);
        otpTimer = getView().findViewById(R.id.otpTimer);
        tv_resendOtp = getView().findViewById(R.id.tv_resendOtp);
        tv_submit = getView().findViewById(R.id.tv_submit);
        iv_close = getView().findViewById(R.id.iv_close);

        tv_resendOtp.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        startCounter(COUNTDOWN_TIME);
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                onSubmit();
                break;
            case R.id.tv_resendOtp:
                onResendOtp();
                startCounter(COUNTDOWN_TIME);
                break;

            case R.id.iv_close:
                hideKeyboard();
                dismiss();
                break;
        }
    }

    private void onResendOtp () {
        if (requestModel == null) {
            return;
        }
        displayProgressBar(false);
        getWebRequestHelper().signUp(requestModel, this);
    }

    private void onSubmit () {
        String otp = otpView.getOTP();
        if (getValidate() == null || !getValidate().validOtp(otp, 4)) {
            return;
        }
        verifyOtp(otp);
    }

    @Override
    public void otpMessageReceived (String messageText) {
        if (otpView != null) {
            otpView.setOTP(messageText);
        }
    }

    @Override
    public void onStart () {
        super.onStart();
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(9999);
//        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
//        getContext().registerReceiver(getSmsReceiver(), filter);
    }

    @Override
    public void onStop () {
        super.onStop();
//        getContext().unregisterReceiver(getSmsReceiver());
    }

    private void verifyOtp (String otp) {
        if (requestModel == null) {
            return;
        }

        VerifyOtpRequestModel verifyOtp = new VerifyOtpRequestModel();

        verifyOtp.mobileno = requestModel.mobileno;
        verifyOtp.otp = otp;
        verifyOtp.type = type;
        if (requestModel.mobile_code != null) {
            verifyOtp.mobile_code = requestModel.mobile_code;
        }

        displayProgressBar(false);
        getWebRequestHelper().verifyotp(verifyOtp, this);
    }

    @Override
    public void onWebRequestCall(WebRequest webRequest) {
        webRequest.addHeader(HEADER_KEY_LANG, LANG_1);
        DeviceInfoModal deviceInfoModal = new DeviceInfoModal(getActivity());
        webRequest.addHeader(HEADER_KEY_DEVICE_TYPE, DEVICE_TYPE_ANDROID);
        webRequest.addHeader(HEADER_KEY_DEVICE_INFO, deviceInfoModal.toString());
        webRequest.addHeader(HEADER_KEY_APP_INFO, deviceInfoModal.getAppInfo());
        super.onWebRequestCall(webRequest);
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);
        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_VERIFY_OTP:
                    handleVerifyOtpResponse(webRequest);
                    break;
                case ID_NEW_USER:
                    handleNewUserResponse(webRequest);
                    break;
            }
        } else {
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                webRequest.showInvalidResponse(msg);
            }
        }
    }

    private void handleNewUserResponse(WebRequest webRequest) {
        NewRiderResponseModel newDriver = webRequest.getResponsePojo(NewRiderResponseModel.class);
        NewUserRequestModel requestModel = webRequest.getExtraData(KEY_NEW_USER);
        setRequestModel(requestModel);
        Utils.showOtp(getContext(), newDriver.getOtp());
    }

    private void handleVerifyOtpResponse(WebRequest webRequest) {
        UserResponseModel responsePojo = webRequest.getResponsePojo(UserResponseModel.class);
        listener.onOtpConfirmListener(responsePojo.getData());
    }

    @Override
    public int getFragmentContainerResourceId() {
        return 0;
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
                if (getContext() == null) {
                    return;
                }
                tv_resendOtp.setTextColor(grayColor);
            }

            @Override
            public void onFinish () {
                otpTimer.setText("00:00");
                tv_resendOtp.setClickable(true);
                if (getContext() == null) {
                    return;
                }
                tv_resendOtp.setTextColor(textLinkColor);
            }
        };
        downTimer.start();
    }

    @Override
    public void setupDialog (Dialog dialog, int style) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflate = LayoutInflater.from(getActivity());
        View layout = inflate.inflate(getLayoutResourceId(), null);

        //set dialog_country view
        dialog.setContentView(layout);
        //setup dialog_country window param
        WindowManager.LayoutParams wlmp = dialog.getWindow().getAttributes();
        // wlmp.gravity = Gravity.LEFT | Gravity.TOP;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlmp.gravity = Gravity.BOTTOM;

        dialog.setTitle(null);
        setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * NextTextWatcher
     * This Class is use to change focus of otp editText
     */


    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public OnOtpConfirmListener getListeners () {
        return listener;
    }

    public void setListeners (OnOtpConfirmListener listener) {
        this.listener = listener;
    }

    public interface OnOtpConfirmListener {
        void onOtpConfirmListener (UserModel userModel);
    }

    public NewUserRequestModel getRequestModel () {
        return requestModel;
    }

    public void setRequestModel (NewUserRequestModel requestModel) {
        this.requestModel = requestModel;
    }
}
