package com.quickzetuser.ui.login;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcm.NotificationPrefs;
import com.medy.retrofitwrapper.WebRequest;
import com.models.DeviceInfoModal;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseActivity;
import com.quickzetuser.database.DaoManager;
import com.quickzetuser.fcm.AppNotificationMessagingService;
import com.quickzetuser.model.CountryModel;
import com.quickzetuser.model.request_model.LoginRequestModel;
import com.quickzetuser.model.web_response.CountriesResponseModel;
import com.quickzetuser.model.web_response.UserResponseModel;
import com.quickzetuser.ui.MyApplication;
import com.quickzetuser.ui.login.forgot.ForgotPasswordActivity;
import com.quickzetuser.ui.main.MainActivity;
import com.quickzetuser.ui.signup.SignUpActivity;
import com.quickzetuser.ui.signup.dialog.DataDialog;
import com.utilities.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class LoginActivity extends AppBaseActivity implements View.OnClickListener {

    public static final int EMAIL_MAX_LENGTH = 50;
    public static final int PHONE_MAX_LENGTH = 15;
    public static final InputFilter.LengthFilter EMAIL_FILTER_LENGTH = new InputFilter.LengthFilter(EMAIL_MAX_LENGTH);
    public static final InputFilter.LengthFilter PHONE_FILTER_LENGTH = new InputFilter.LengthFilter(PHONE_MAX_LENGTH);
    private ImageView iv_email_image;
    private TextView tv_mobile_code;
    private EditText et_email;
    private EditText et_password;
    private TextView tv_sign_in;
    private TextView tv_create_account;
    private TextView tv_forgot_password;
    private List<CountryModel> countryList;
    private int lastFilterLength = EMAIL_MAX_LENGTH;

    @Override
    protected void onResume () {
        super.onResume();
        AppNotificationMessagingService.generateLatestToken();
    }

    @Override
    public int getLayoutResourceId () {
        return R.layout.activity_login;
    }

    @Override
    public void initializeComponent () {
        super.initializeComponent();
        findViews();
        getCountries();
        DaoManager.getInstance().clearDao();
    }

    private void getCountries () {
        displayProgressBar(false);
        getWebRequestHelper().countries(this);
    }

    private void findViews () {
        iv_email_image = findViewById(R.id.iv_email_image);
        tv_mobile_code = findViewById(R.id.tv_mobile_code);
        et_email = findViewById(R.id.et_email);
        et_email.setFilters(new InputFilter[]{EMAIL_FILTER_LENGTH});

        et_password = findViewById(R.id.et_password);
        tv_sign_in = findViewById(R.id.tv_sign_in);
        tv_create_account = findViewById(R.id.tv_create_account);
        tv_forgot_password = findViewById(R.id.tv_forgot_password);

        countryList = new ArrayList<>();
        tv_mobile_code.setOnClickListener(this);
        tv_sign_in.setOnClickListener(this);
        tv_create_account.setOnClickListener(this);
        tv_forgot_password.setOnClickListener(this);

        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged (Editable s) {
                String data = s.toString();
                if (!data.isEmpty()) {
                    if (TextUtils.isDigitsOnly(data)) {
                        updateViewVisibility(iv_email_image, View.INVISIBLE);
                        updateViewVisibility(tv_mobile_code, View.VISIBLE);
                        if (lastFilterLength != PHONE_MAX_LENGTH) {
                            lastFilterLength = PHONE_MAX_LENGTH;
                            et_email.setFilters(new InputFilter[]{PHONE_FILTER_LENGTH});
                        }
                    } else {
                        updateViewVisibility(iv_email_image, View.VISIBLE);
                        updateViewVisibility(tv_mobile_code, View.INVISIBLE);
                        if (lastFilterLength != EMAIL_MAX_LENGTH) {
                            lastFilterLength = EMAIL_MAX_LENGTH;
                            et_email.setFilters(new InputFilter[]{EMAIL_FILTER_LENGTH});
                        }
                    }
                } else {
                    updateViewVisibility(iv_email_image, View.VISIBLE);
                    updateViewVisibility(tv_mobile_code, View.INVISIBLE);
                    if (lastFilterLength != EMAIL_MAX_LENGTH) {
                        lastFilterLength = EMAIL_MAX_LENGTH;
                        et_email.setFilters(new InputFilter[]{EMAIL_FILTER_LENGTH});
                    }
                }

            }
        });

    }


    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.tv_mobile_code:
                showDataDialog("Select Country", tv_mobile_code, countryList);
                break;
            case R.id.tv_sign_in:
                onSubmit();
                break;
            case R.id.tv_create_account:
                sendActivity(this, SignUpActivity.class);
                break;
            case R.id.tv_forgot_password:
                sendActivity(this, ForgotPasswordActivity.class);
                break;


        }
    }

    private void showDataDialog (String title, final TextView textView, final List list) {
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

    private void onSubmit () {

        if (getValidate().validEmailOrMobile(et_email)) {
            if (getValidate().validPassword(et_password)) {
                String mobile_code = tv_mobile_code.getText().toString().trim();
                String email = et_email.getText().toString().trim();

                LoginRequestModel requestModel = new LoginRequestModel();
                requestModel.email = TextUtils.isDigitsOnly(email) ? (mobile_code + email) : email;
                requestModel.password = et_password.getText().toString();
                requestModel.device_id = DeviceUtils.getUniqueDeviceId();
                requestModel.device_type = DEVICE_TYPE_ANDROID;
                requestModel.device_token = NotificationPrefs.getInstance(this).getNotificationToken();

                displayProgressBar(false);
                getWebRequestHelper().login(requestModel, this);

            }
        }
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
                case ID_LOGIN:
                    handleLoginResponse(webRequest);
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

    private void handleLoginResponse (WebRequest webRequest) {
        UserResponseModel responseModel = webRequest.getResponsePojo(UserResponseModel.class);
        ((MyApplication) getApplication()).getUserPrefs().saveLoggedInUser(responseModel.getData());
        String message = responseModel.getMessage();
        if (isValidString(message)) {
            showCustomToast(message);
        }
        sendActivityFinish(this, MainActivity.class);
    }

    private void handleCountryResponse (WebRequest webRequest) {
        CountriesResponseModel countries = webRequest.getResponsePojo(CountriesResponseModel.class);
        countryList.clear();
        if (countries == null) return;
        countryList.addAll(countries.getData());
    }
}
