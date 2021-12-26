package com.quickzetuser.ui.login.forgot;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.medy.retrofitwrapper.WebRequest;
import com.models.DeviceInfoModal;
import com.permissions.PermissionHelperNew;
import com.goride.user.R;
import com.quickzetuser.appBase.AppBaseActivity;
import com.quickzetuser.model.CountryModel;
import com.quickzetuser.model.request_model.ForgotPasswordRequestModel;
import com.quickzetuser.model.web_response.CountriesResponseModel;
import com.quickzetuser.model.web_response.NewRiderResponseModel;
import com.quickzetuser.ui.signup.dialog.DataDialog;
import com.utilities.Utils;

import java.util.ArrayList;
import java.util.List;



public class ForgotPasswordActivity extends AppBaseActivity implements View.OnClickListener,
        PermissionHelperNew.OnSpecificPermissionGranted {

    private ImageView iv_menu_left;
    private ImageView iv_back;
    private ImageView iv_bagghi_icon;
    private TextView tv_title;
    private TextView tv_mobile_code;
    private EditText et_mobile;
    private TextView tv_submit;
    private List<CountryModel> countryList;

    @Override
    public int getLayoutResourceId () {
        return R.layout.activity_forgot_password;
    }

    @Override
    public void initializeComponent () {
        super.initializeComponent();
        findViews();
        init();
        getCountries();
    }

    private void findViews () {
        iv_back = findViewById(R.id.iv_back);

        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.forgot_password_title));

        tv_mobile_code = findViewById(R.id.tv_mobile_code);
        et_mobile = findViewById(R.id.et_mobile);
        tv_submit = findViewById(R.id.tv_submit);
    }

    private void init () {
        countryList = new ArrayList<>();
        tv_mobile_code.setOnClickListener(this);
        tv_submit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }


    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.tv_mobile_code:
                showCountryDialog("Select Country", tv_mobile_code, countryList);
                break;

            case R.id.tv_submit:
                if (getValidate().validMobileNumber(et_mobile)) {
                    goToForward();
                }
                break;
        }
    }

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void goToForward () {
        if (PermissionHelperNew.needSMSPermission(this, this)) {
            return;
        }
        onSubmit();
    }

    private void onSubmit () {

        String mobileno = et_mobile.getText().toString().trim();
        String mobile_code = tv_mobile_code.getText().toString().trim();

        ForgotPasswordRequestModel requestModel = new ForgotPasswordRequestModel();

        requestModel.mobileno = mobileno;
        requestModel.mobile_code = mobile_code;

        displayProgressBar(false);
        getWebRequestHelper().forgotPassword(requestModel, this);
    }

    private void getCountries () {
        displayProgressBar(false);
        getWebRequestHelper().countries(this);
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
                case ID_COUNTRIES:
                    handleCountryResponse(webRequest);
                    break;
            }
        } else {
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                showErrorMessage(msg);
            }
        }
    }

    private void handleForgotPasswordResponse (WebRequest webRequest) {
        NewRiderResponseModel newDriver = webRequest.getResponsePojo(NewRiderResponseModel.class);
        if (webRequest.getResponseCode() != 400) {
            Utils.showOtp(this, newDriver.getOtp());
            ForgotPasswordRequestModel requestModel = webRequest.getExtraData(KEY_FORGOT_PASSWORD);
            Bundle bundle = new Bundle();
            bundle.putString("forgotPassword", new Gson().toJson(requestModel));
            sendActivity(getApplicationContext(), NewPasswordActivity.class, bundle);
            String msg = newDriver.getMessage();
            if (isValidString(msg)) {
                showCustomToast(msg);
            }
        } else {
            String msg = newDriver.getMessage();
            if (isValidString(msg)) {
                showErrorMessage(msg);
            }
        }
    }

    private void handleCountryResponse (WebRequest webRequest) {
        CountriesResponseModel countries = webRequest.getResponsePojo(CountriesResponseModel.class);
        countryList.addAll(countries.getData());
    }

    private void showCountryDialog (String title, final TextView textView, final List list) {
        if (list.size() == 0) return;
        final DataDialog dialog = new DataDialog();
        dialog.setDataList(list);
        dialog.setTitle(title);
        dialog.setOnItemSelectedListeners(new DataDialog.OnItemSelectedListener() {
            @Override
            public void onItemSelectedListener (int position) {
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
    protected void onResume () {
        super.onResume();
    }


    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelperNew.onSpecificRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted (boolean isGranted, boolean withNeverAsk, String permission, int requestCode) {
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
