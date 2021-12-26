package com.quickzetuser.ui.signup;

import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fcm.NotificationPrefs;
import com.medy.retrofitwrapper.WebRequest;
import com.models.DeviceInfoModal;
import com.permissions.PermissionHelperNew;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseActivity;
import com.quickzetuser.fcm.AppNotificationMessagingService;
import com.quickzetuser.model.CountryModel;
import com.quickzetuser.model.UserModel;
import com.quickzetuser.model.request_model.NewUserRequestModel;
import com.quickzetuser.model.web_response.CountriesResponseModel;
import com.quickzetuser.model.web_response.NewRiderResponseModel;
import com.quickzetuser.ui.MyApplication;
import com.quickzetuser.ui.main.MainActivity;
import com.quickzetuser.ui.signup.dialog.DataDialog;
import com.quickzetuser.ui.signup.dialog.OtpDialog;
import com.utilities.DeviceUtils;
import com.utilities.Utils;

import java.util.ArrayList;
import java.util.List;



public class SignUpActivity extends AppBaseActivity implements View.OnClickListener,
        PermissionHelperNew.OnSpecificPermissionGranted {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private TextView tv_sign_up;
    private TextView tv_login,txt_ride;
    private EditText et_email;
    private EditText et_firstname;
    private EditText et_lastname;
    private EditText et_password;
    private EditText et_confirm_password;
    private TextView tv_mobile_code;
    private EditText et_user_mobile;
    private EditText et_referrer_code;
    private List<CountryModel> countryList;
    LinearLayout ll_referal;
    TextView tv_have_referal_link;


    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_signup;
    }

    @Override
    public void initializeComponent() {
        super.initializeComponent();
        findViews();
        init();
        getCountries();
    }

    private void init() {
        countryList = new ArrayList<>();
        tv_mobile_code.setOnClickListener(this);
        tv_sign_up.setOnClickListener(this);
        tv_login.setOnClickListener(this);
    }

    private void findViews() {
        et_email = findViewById(R.id.et_email);
        txt_ride = findViewById(R.id.txt_ride);
        et_firstname = findViewById(R.id.et_firstname);
        et_lastname = findViewById(R.id.et_lastname);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        tv_mobile_code = findViewById(R.id.tv_mobile_code);
        et_user_mobile = findViewById(R.id.et_user_mobile);
        et_referrer_code = findViewById(R.id.et_referrer_code);
        tv_sign_up = findViewById(R.id.tv_sign_up);
        tv_login = findViewById(R.id.tv_login);
        ll_referal = findViewById(R.id.ll_referal);
        tv_have_referal_link = findViewById(R.id.tv_have_referal_link);
        tv_have_referal_link.setOnClickListener(this);
        txt_ride.setText(Html.fromHtml("<font color=#D7251A>R</font><font color=#414141>ider</font>"));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_mobile_code:
                showCountryDialog("Select Country", tv_mobile_code, countryList);
                break;

            case R.id.tv_sign_up:
                if (isValidate()) {
                    goToForward();
                }
                break;

            case R.id.tv_login:
                onBackPressed();
                break;

            case R.id.tv_have_referal_link:
                updateViewVisibility(ll_referal, View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private boolean isValidate() {
        /*if (et_email.getText().toString().length() > 0) {
            if (!getValidate().validEmailPattern(et_email)) {
                return false;
            }
        }*/

        if (!getValidate().validEmailAddress(et_email)) {
            return false;
        }
        if (!getValidate().validFirstName(et_firstname)) {
            return false;
        }
        if (!getValidate().validLastName(et_lastname)) {
            return false;
        }
        if (!getValidate().validPasswordLength(et_password)) {
            return false;
        }
        if (!getValidate().validConfirmPassword(et_password.getText().toString(),
                et_confirm_password)) {
            return false;
        }
        if (!getValidate().validMobileNumber(et_user_mobile)) {
            return false;
        }

        return true;
    }


    private void goToForward() {
        if (PermissionHelperNew.needSMSPermission(this, this)) {
            return;
        }
        onSubmit();
    }

    private void onSubmit() {
        String email = et_email.getText().toString().trim();
        String fname = et_firstname.getText().toString().trim();
        String lname = et_lastname.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String con_pass = et_confirm_password.getText().toString().trim();
        String mobileno = et_user_mobile.getText().toString().trim();
        String mobile_code = tv_mobile_code.getText().toString().trim();
        String referrer_code = et_referrer_code.getText().toString().trim();

        NewUserRequestModel requestModel = new NewUserRequestModel();
        requestModel.email = email;
        requestModel.firstname = fname;
        requestModel.lastname = lname;
        requestModel.password = password;
        requestModel.confirm_password = con_pass;
        requestModel.mobileno = mobileno;
        requestModel.mobile_code = mobile_code;
        requestModel.referral_code = referrer_code;
        requestModel.device_id = DeviceUtils.getUniqueDeviceId();
        requestModel.device_type = DEVICE_TYPE_ANDROID;
        requestModel.device_token = NotificationPrefs.getInstance(this).getNotificationToken();

        displayProgressBar(false);
        getWebRequestHelper().signUp(requestModel, this);
    }

    @Override
    public void onWebRequestCall(WebRequest webRequest) {
        webRequest.addHeader(HEADER_KEY_LANG, LANG_1);
        DeviceInfoModal deviceInfoModal = new DeviceInfoModal(this);
        webRequest.addHeader(HEADER_KEY_DEVICE_TYPE, DEVICE_TYPE_ANDROID);
        webRequest.addHeader(HEADER_KEY_DEVICE_INFO, deviceInfoModal.toString());
        webRequest.addHeader(HEADER_KEY_APP_INFO, deviceInfoModal.getAppInfo());
        super.onWebRequestCall(webRequest);

    }

    private void getCountries() {
        displayProgressBar(false);
        getWebRequestHelper().countries(this);
    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);

        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_COUNTRIES:
                    handleCountryResponse(webRequest);
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

    private void handleCountryResponse(WebRequest webRequest) {
        CountriesResponseModel countries = webRequest.getResponsePojo(CountriesResponseModel.class);
        countryList.addAll(countries.getData());
    }

    private void handleNewUserResponse(WebRequest webRequest) {
        NewRiderResponseModel newDriver = webRequest.getResponsePojo(NewRiderResponseModel.class);
        NewUserRequestModel requestModel = webRequest.getExtraData(KEY_NEW_USER);
        Utils.showOtp(this, newDriver.getOtp());
        showOtpDialog(requestModel, newDriver.getOtp());
    }

    private void showOtpDialog(NewUserRequestModel requestModel, String otp) {
        final OtpDialog otpDialog = new OtpDialog();
        otpDialog.setRequestModel(requestModel);
        otpDialog.setType(TYPE_V);
        otpDialog.setListeners(new OtpDialog.OnOtpConfirmListener() {
            @Override
            public void onOtpConfirmListener(UserModel userModel) {
                otpDialog.dismiss();
                ((MyApplication) getApplication()).getUserPrefs().saveLoggedInUser(userModel);
                sendActivityFinish(SignUpActivity.this, MainActivity.class);
            }
        });
        otpDialog.show(getFm(), otpDialog.getClass().getSimpleName());
    }

    private void showCountryDialog(String title, final TextView textView, final List list) {
        if (list.size() == 0) return;
        final DataDialog dialog = new DataDialog();
        dialog.setDataList(list);
        dialog.setTitle(title);
        dialog.setOnItemSelectedListeners(new DataDialog.OnItemSelectedListener() {
            @Override
            public void onItemSelectedListener(int position) {
                dialog.dismiss();
                textView.setText(list.get(position).toString());
                textView.setTag(list.get(position));

                switch (textView.getId()) {
                    case R.id.tv_mobile_code:
                        String mobile_code = ((CountryModel) list.get(position)).getCountry_mobile_code();
                        tv_mobile_code.setText(mobile_code);
                        break;
                }
            }
        });
        dialog.show(getFm(), DataDialog.class.getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppNotificationMessagingService.generateLatestToken();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelperNew.onSpecificRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(boolean isGranted, boolean withNeverAsk, String permission, int requestCode) {
        if (requestCode == PermissionHelperNew.SMS_PERMISSION_REQUEST_CODE) {
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
}
