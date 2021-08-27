package com.quickzetuser.ui.main.sidemenu.payment.wallet;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.medy.retrofitwrapper.WebRequest;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.model.UserModel;
import com.quickzetuser.model.WalletModel;
import com.quickzetuser.model.WalletRechargeModel;
import com.quickzetuser.model.web_response.WalletRechargeResponseModel;
import com.quickzetuser.model.web_response.WalletResponseModel;
import com.quickzetuser.preferences.UserPrefs;
import com.quickzetuser.ui.main.dialog.paytmpayment.PaytmPaymentDialog;

/**
 * Created by Sunil kumar yadav on 15/3/18.
 */

public class WalletFragment extends AppBaseFragment implements UserPrefs.UserPrefsListener {

    private static final String TAG = WalletFragment.class.getSimpleName();
    private TextView tv_wallet;
    private EditText et_wallet;
    public final int MAX_AMOUNT = 10000;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged (CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged (Editable s) {
            String data = s.toString().trim();
            if (isValidString(data)) {
                if (data.equals(currency_symbol.trim())) {
                    et_wallet.setText("");
                    et_wallet.setSelection(0);
                } else if (!data.startsWith(currency_symbol.trim())) {
                    data = currency_symbol + data;
                    et_wallet.setText(data);
                    et_wallet.setSelection(data.length());
                }
            }
        }
    };
    private TextView tv_100;
    private TextView tv_200;
    private TextView tv_500;
    private TextView tv_next;

    @Override
    public int getLayoutResourceId () {
        return R.layout.fragment_wallet;
    }

    @Override
    public void initializeComponent () {
        super.initializeComponent();
        tv_wallet = getView().findViewById(R.id.tv_wallet);
        et_wallet = getView().findViewById(R.id.et_wallet);
        tv_100 = getView().findViewById(R.id.tv_100);
        tv_200 = getView().findViewById(R.id.tv_200);
        tv_500 = getView().findViewById(R.id.tv_500);
        tv_next = getView().findViewById(R.id.tv_next);

        tv_100.setOnClickListener(this);
        tv_200.setOnClickListener(this);
        tv_500.setOnClickListener(this);
        tv_next.setOnClickListener(this);

        et_wallet.addTextChangedListener(textWatcher);

        getWalletDetail();
    }

    private void getWalletDetail () {
        try {
            getMainActivity().getWebRequestHelper().getWalletDetail(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick (View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_100:
                setRupeeText(100);
                break;

            case R.id.tv_200:
                setRupeeText(200);
                break;

            case R.id.tv_500:
                setRupeeText(500);
                break;

            case R.id.tv_next:
                onNext();
                break;
        }
    }

    private void setRupeeText (long rupee) {
        et_wallet.setText(String.valueOf(rupee));
    }

    private void onNext () {
        String amount = et_wallet.getText().toString().trim();
        if (amount.startsWith(currency_symbol) && amount.length() > 1) {
            amount = amount.substring(1).trim();
        }

        if (!isValidString(amount)) {
            showErrorMessage("Please enter amount");
            return;
        }

        if (!checkValidAmount(amount)) {
            showErrorMessage("Please enter valid amount");
            return;
        }
        long amt = Long.parseLong(amount);
        if (amt > MAX_AMOUNT) {
            showErrorMessage(getString(R.string.amount_validation_20));
            return;
        }
     //   showErrorMessage("Please contact to admin for wallet recharge.");

        PaytmPaymentDialog paytmPaymentDialog = new PaytmPaymentDialog();
        paytmPaymentDialog.setTargetFragment(this, 100);
        paytmPaymentDialog.setWalletRechargeAmount(amount);
        paytmPaymentDialog.show(getFragmentManager(), PaytmPaymentDialog.class.getSimpleName());
    }

    private boolean checkValidAmount (String amount) {
        if (isValidString(amount)) {
            try {
                double amt = Double.parseDouble(amount);
                return amt > 0;
            } catch (NumberFormatException e) {

            }
        }

        return false;
    }

    @Override
    public void onWebRequestCall (WebRequest webRequest) {
        super.onWebRequestCall(webRequest);
    }


    @Override
    public void onWebRequestResponse (WebRequest webRequest) {
        dismissProgressBar();
        super.onWebRequestResponse(webRequest);

        if (webRequest.getWebRequestId() == ID_WALLET_RECHARGE_PAYTM) {
            PaytmPaymentDialog paytmPaymentDialog = webRequest.getExtraData(PaytmPaymentDialog.class.getSimpleName());
            if (isValidObject(paytmPaymentDialog)) {
                paytmPaymentDialog.dismiss();
            }
        }
        if (webRequest.isSuccess()) {
            switch (webRequest.getWebRequestId()) {
                case ID_GET_WALLET_DETAIL:
                    handleGetWalletDetailsResponse(webRequest);
                    break;
                case ID_WALLET_RECHARGE_PAYTM:
                    handleWalletRechargeResponse(webRequest);
                    break;
            }
        } else {
            String msg = webRequest.getErrorMessageFromResponse();
            if (isValidString(msg)) {
                showErrorMessage(msg);
            }
        }
    }

    private void handleWalletRechargeResponse (WebRequest webRequest) {
        et_wallet.setText("");
        WalletRechargeResponseModel responseModel = webRequest.getResponsePojo(WalletRechargeResponseModel.class);
        if (!isValidObject(responseModel)) return;
        showCustomToast(responseModel.getMessage());

        try {
            UserModel userModel = getMainActivity().getUserModel();
            if (!isValidObject(userModel)) return;
            WalletRechargeModel rechargeModel = responseModel.getData();
            if (!isValidObject(rechargeModel)) return;

            userModel.setWallet(rechargeModel.getWallet());
            getMainActivity().updateRiderProfile(userModel);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private void handleGetWalletDetailsResponse (WebRequest webRequest) {
        WalletResponseModel responseModel = webRequest.getResponsePojo(WalletResponseModel.class);
        if (isValidObject(responseModel)) {
            try {
                WalletModel walletModel = responseModel.getData();
                if (!isValidObject(walletModel)) return;
                UserModel userModel = getMainActivity().getUserModel();
                if (!isValidObject(userModel)) return;
                userModel.setWallet(walletModel);
                getMainActivity().updateRiderProfile(userModel);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume () {
        super.onResume();
        UserModel userModel = null;
        try {
            getMainActivity().getUserPrefs().addListener(this);
            userModel = getMainActivity().getUserModel();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (!isValidObject(userModel)) return;
        loggedInUserUpdate(userModel);


    }

    @Override
    public void onPause () {
        super.onPause();
        try {
            getMainActivity().getUserPrefs().removeListener(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void userLoggedIn (UserModel userModel) {

    }

    @Override
    public void loggedInUserUpdate (UserModel userModel) {
        if (userModel.getWallet() != null) {
            tv_wallet.setText(String.valueOf(currency_symbol + userModel.getWallet().getWallet_amountText()));
        } else {
            tv_wallet.setText(String.valueOf(currency_symbol + "0.00"));
        }


    }

    @Override
    public void loggedInUserClear () {

    }

}
