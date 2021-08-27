package com.quickzetuser.ui.main.map;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.addressfetching.AddressFetchModel;
import com.addressfetching.LocationModelFull;
import com.google.gson.Gson;
import com.permissions.PermissionHelperNew;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseActivity;
import com.quickzetuser.model.AddressModel;
import com.utilities.Utils;


public class SelectAddressActivity extends AppBaseActivity implements AddressMapFragment.AddressMapFragmentListener, View.OnClickListener {

    AddressMapFragment addressMapFragment;
    private ImageView iv_back;
    private TextView tv_submit;
    private TextView et_address;
    private AddressModel previousAddressModel;


    @Override
    public void onLocationServiceConnected() {
        super.onLocationServiceConnected();
        addMap();
        if (!Utils.isGpsProviderEnabled(SelectAddressActivity.this)) {
            checkLocationSetting();
        }
    }

    private int getOpenWithRequestCode() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            return extras.getInt(OPEN_WITH_RQUEST_CODE, -1);
        }
        return -1;
    }

    private AddressModel getAddressModel() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && isValidString((extras.getString(DATA, "")))) {
            try {
                return new Gson().fromJson(extras.getString(DATA), AddressModel.class);
            } catch (Exception ignore) {

            }
        }
        return null;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_select_address;
    }


    @Override
    public void initializeComponent() {
        super.initializeComponent();
        this.previousAddressModel = getAddressModel();
        if (!PermissionHelperNew.needLocationPermission(this, new PermissionHelperNew.OnSpecificPermissionGranted() {
            @Override
            public void onPermissionGranted(boolean isGranted, boolean withNeverAsk, String permission, int requestCode) {
                if (isGranted) {
                    bindLocationService();
                } else {
                    showCustomToast("Need location permission.");
                    supportFinishAfterTransition();
                }
            }
        })) {
            bindLocationService();
        }

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        et_address = findViewById(R.id.et_address);

        tv_submit = findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(this);
    }


    private void addMap() {
        addressMapFragment = new AddressMapFragment();
        if (previousAddressModel != null) {
            addressMapFragment.setMapLoadedLocation(previousAddressModel.getLocationModel());
        } else {
            addressMapFragment.setMapLoadedLocation(null);
        }

        addressMapFragment.setAddressMapFragmentListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_fragment_container, addressMapFragment, "fragmentTag")
                .disallowAddToBackStack()
                .commit();
    }


    @Override
    public void onClick(View v) {
        ObjectAnimator animation;
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
                onSubmit();
                break;
        }
    }

    private void onSubmit() {
        LocationModelFull.LocationModel locationModel = (LocationModelFull.LocationModel) et_address.getTag();
        if (locationModel != null) {
            if (getOpenWithRequestCode() == -1) {
                showErrorMessage("Programming error.");
                return;
            }
            else {
                Bundle bundle = new Bundle();
                bundle.putString(DATA, new Gson().toJson(locationModel));
                Intent intent = new Intent();
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                supportFinishAfterTransition();
            }
        }
    }

    @Override
    public void onLocationChange(AddressFetchModel addressFetchModel) {
        try {
            if (addressFetchModel == null || addressFetchModel.getLocationModel() == null) {
                et_address.setText("");
                et_address.setTag(null);
            } else {
                LocationModelFull.LocationModel locationModel = addressFetchModel.getLocationModel();

                AddressModel addressModel = new AddressModel(locationModel);
                et_address.setTag(locationModel);
                et_address.setText(addressModel.getAddress());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.enter_alpha, R.anim.exit_alpha);
    }

    @Override
    protected void onStop() {
        unBindLocationService();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unBindLocationService();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelperNew.onSpecificRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
