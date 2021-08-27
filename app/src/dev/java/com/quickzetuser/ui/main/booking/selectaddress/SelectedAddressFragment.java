package com.quickzetuser.ui.main.booking.selectaddress;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.addressfetching.LocationModelFull;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.model.CityModel;
import com.quickzetuser.model.VehicleTypeModel;
import com.quickzetuser.ui.main.booking.addresschooser.AddressChooserFragment;
import com.quickzetuser.ui.main.booking.cabType.CabTypeFragment;
import com.quickzetuser.ui.main.booking.fareEstimate.FareEstimateFragment;
import com.quickzetuser.ui.main.booking.fareEstimaterentel.FareEstimateRentalFragment;

/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class SelectedAddressFragment extends AppBaseFragment {

    private LocationModelFull.LocationModel locationModel;
    private TextView tv_pickup_address;
    private View v_line;
    private ImageView iv_red_dot;
    private LinearLayout ll_drop_view;
    VehicleTypeModel selectedVehicleType;
    LocationModelFull.LocationModel pickuplocationModel;


    AddressChooserFragment.ChooseAddressListener chooseAddressListener = new AddressChooserFragment.ChooseAddressListener() {
        @Override
        public void OnChooseSuccess(TextView textView, LocationModelFull.LocationModel locationModel) {

            try {
                VehicleTypeModel selectedVehicleType = null;
                Fragment bottomFragment = getMainActivity().getMapHandler().getBottomFragment();
                if (bottomFragment != null && bottomFragment instanceof CabTypeFragment) {
                    selectedVehicleType = ((CabTypeFragment) bottomFragment).getSelectedVehicleType();
                }else if(bottomFragment instanceof FareEstimateFragment){
                    selectedVehicleType = ((FareEstimateFragment) bottomFragment).getSelectedVehicleType();
                }


                if (textView.equals(tv_pickup_address)) {
                    pickuplocationModel = locationModel;
                    getMyApplication().setAutocompleteFilter(locationModel);
                    getMainActivity().getMapHandler().changePickUpAddress(locationModel);
                } else {
                    if (selectedVehicleType == null) {
                        return;
                    }
                    LocationModelFull.LocationModel pickUpLocationModel = getMainActivity().getMapHandler().pickUpLocationModel;
                    if (pickUpLocationModel == null) return;
                    CityModel pickupCityModel = getMyApplication().getCityHelper().
                            getCityModelFromLatLng(pickUpLocationModel.getLatLng());
                    CityModel dropCityModel = getMyApplication().getCityHelper().
                            getCityModelFromLatLng(locationModel.getLatLng());

                    if (selectedVehicleType.isOutstationVehicleType()) {
                        if (dropCityModel == null ||
                                (pickupCityModel.getCity_id() != dropCityModel.getCity_id())) {
                            getMainActivity().getMapHandler().changeDropAddress(locationModel);
                        } else {
                            getMainActivity().showMessageDialog(getString(R.string.app_name),
                                    "Drop address must be in different city.");
                            return;
                        }
                    } else {
                        if (isValidObject(dropCityModel) && pickupCityModel.getCity_id() == dropCityModel.getCity_id()) {
                            getMainActivity().getMapHandler().changeDropAddress(locationModel);
                        } else {
                            getMainActivity().showMessageDialog(getString(R.string.app_name),
                                    "Drop address must be in same city.");
                            return;
                        }
                    }


                }
                if (locationModel != null) {
                    textView.setText(locationModel.getFulladdress());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    };
    private TextView tv_drop_address;
    private ImageView iv_clear_drop;

    TextWatcher dropAddressWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged (CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged (CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged (Editable s) {
            if (s == null || s.toString().trim().isEmpty()) {
                iv_clear_drop.setVisibility(View.GONE);
            } else {
                iv_clear_drop.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public int getLayoutResourceId () {
        return R.layout.fragment_selected_address;
    }

    @Override
    public void initializeComponent () {
        tv_pickup_address = getView().findViewById(R.id.tv_pickup_address);
        tv_drop_address = getView().findViewById(R.id.tv_drop_address);
        iv_clear_drop = getView().findViewById(R.id.iv_clear_drop);

        v_line = getView().findViewById(R.id.v_line);
        iv_red_dot = getView().findViewById(R.id.iv_red_dot);
        ll_drop_view = getView().findViewById(R.id.ll_drop_view);

        tv_drop_address.addTextChangedListener(dropAddressWatcher);

        tv_pickup_address.setOnClickListener(this);
        tv_drop_address.setOnClickListener(this);
        iv_clear_drop.setOnClickListener(this);

        if (locationModel != null) {
            chooseAddressListener.OnChooseSuccess(tv_pickup_address, locationModel);
        }


    }

    @Override
    public void onAttach (Context context) {
        super.onAttach(context);
        try {
            getMainActivity().getMapHandler().showCabTypeFragment(0, locationModel);
        } catch (NullPointerException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setLocationModel (LocationModelFull.LocationModel locationModel) {
        this.locationModel = locationModel;
    }

    @Override
    public void onClick (View v) {
        super.onClick(v);
        try {
            Fragment bottomFragment = getMainActivity().getMapHandler().getBottomFragment();
            if (bottomFragment != null && bottomFragment instanceof CabTypeFragment) {
                selectedVehicleType = ((CabTypeFragment) bottomFragment).getSelectedVehicleType();
            }else if(bottomFragment instanceof FareEstimateFragment){
                selectedVehicleType = ((FareEstimateFragment) bottomFragment).getSelectedVehicleType();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        boolean is_bound = false;
        switch (v.getId()) {
            case R.id.tv_pickup_address:
                addAddressChooserFragment(tv_pickup_address, is_bound);
                break;

            case R.id.tv_drop_address:
                if(selectedVehicleType!=null){
                    if(selectedVehicleType.isOutstationVehicleType()){
                        is_bound = false;
                    }else {
                        is_bound = true;
                    }
                }
                addAddressChooserFragment(tv_drop_address, is_bound);
                break;
            case R.id.iv_clear_drop:
                clearDropAddress();
                break;
        }
    }

    private void clearDropAddress () {
        tv_drop_address.setTag(null);
        tv_drop_address.setText("");
        try {
            getMainActivity().getMapHandler().clearDropAddress();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void addAddressChooserFragment(TextView textView, boolean is_bound) {
        if (isRentalSelected()) return;
        AddressChooserFragment fragment = new AddressChooserFragment();
        fragment.setIsBound(is_bound);
        fragment.setTextView(textView);
        if(pickuplocationModel!=null) {
            fragment.setPickupLatitude(pickuplocationModel.getLatitude());
            fragment.setPickupLongitude(pickuplocationModel.getLongitude());
        }
        fragment.setChooseAddressListener(chooseAddressListener);
        int enterAnimation = R.anim.fadein;
        int exitAnimation = 0;
        int enterAnimationBackStack = 0;
        int exitAnimationBackStack = R.anim.fadeout;
        try {
            getMainActivity().changeFragment(fragment, true, true, 0,
                    enterAnimation, exitAnimation, enterAnimationBackStack, exitAnimationBackStack,
                    false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void changeSelectedVehicleType(VehicleTypeModel vehicleTypeModel) {
        if (vehicleTypeModel.isRentalVehicleType()) {
            updateViewVisibility(v_line, View.GONE);
            updateViewVisibility(iv_red_dot, View.GONE);
            updateViewVisibility(ll_drop_view, View.GONE);
        } else {
            updateViewVisibility(v_line, View.VISIBLE);
            updateViewVisibility(iv_red_dot, View.VISIBLE);
            updateViewVisibility(ll_drop_view, View.VISIBLE);
        }
    }

    public boolean isRentalSelected() {
        try {
            Fragment bottomFragment = getMainActivity().getMapHandler().getBottomFragment();
            if (bottomFragment != null && bottomFragment instanceof FareEstimateRentalFragment) {
                VehicleTypeModel previousSelectedVehicleType = getMainActivity().getMapHandler().getPreviousSelectedVehicleType();
                if (previousSelectedVehicleType != null && previousSelectedVehicleType.isRentalVehicleType())
                    return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
