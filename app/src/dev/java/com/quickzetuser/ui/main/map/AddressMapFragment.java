package com.quickzetuser.ui.main.map;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.addressfetching.AddressFetchModel;
import com.addressfetching.AddressResultReceiver;
import com.customviews.GoogleMapFragment;
import com.permissions.PermissionHelperNew;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;

/**
 *
 * @author Manish Kumar
 * @since 5/9/17
 */


public class AddressMapFragment extends AppBaseFragment implements GoogleMapFragment.GoogleMapListener {

    FrameLayout fl_map_container;
    ImageView iv_center_marker;
    ImageView iv_location;

    LinearLayout ll_loader_layout;
    TextView tv_loader_message;

    AddressMapFragmentListener addressMapFragmentListener;

    Location mapLoadedLocation;

    AddressMap addressMap;

    public void setMapLoadedLocation(Location mapLoadedLocation) {
        this.mapLoadedLocation = mapLoadedLocation;
    }

    public Location getMapLoadedLocation() {
        return mapLoadedLocation;
    }

    public AddressMapFragmentListener getAddressMapFragmentListener() {
        return addressMapFragmentListener;
    }

    public void setAddressMapFragmentListener(AddressMapFragmentListener addressMapFragmentListener) {
        this.addressMapFragmentListener = addressMapFragmentListener;
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_address_map;
    }

    @Override
    public void initializeComponent() {

        tv_loader_message = getView().findViewById(R.id.tv_loader_message);

        ll_loader_layout = getView().findViewById(R.id.ll_loader_layout);
        fl_map_container = getView().findViewById(R.id.fl_map_container);
        iv_center_marker = getView().findViewById(R.id.iv_center_marker);
        iv_location = getView().findViewById(R.id.iv_location);
        iv_location.setOnClickListener(this);
        if (getMapLoadedLocation() == null) {
            ll_loader_layout.setVisibility(View.VISIBLE);
        } else {
            ll_loader_layout.setVisibility(View.GONE);
        }
        addMap();
    }

    @Override
    public int getFragmentContainerResourceId(Fragment fragment) {
        return R.id.fl_map_container;
    }

//    @Override
//    public int getFragmentContainerResourceId(BaseFragment baseFragment) {
//        return R.id.fl_map_container;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_location:
                if (!PermissionHelperNew.needLocationPermission(getActivity(), new PermissionHelperNew.OnSpecificPermissionGranted() {
                    @Override
                    public void onPermissionGranted(boolean isGranted, boolean withNeverAsk, String permission, int requestCode) {
                        if (isGranted) {
                            addressMap.isMapLoadedWithAllPermission();
                            if (addressMap.goToRealCurrentLocation()) {
                                onLocationAvailable(null);
                            }
                        } else {
                            showErrorMessage("Need location permission.");
                        }
                    }
                })) {
                    if (addressMap.goToRealCurrentLocation()) {
                        onLocationAvailable(null);
                    }
                }
                break;
        }
    }

    private void addMap() {
        int topPadding = Math.round(getResources().
                getDimensionPixelSize(R.dimen.dp30));
        addressMap = new AddressMap();
        if (isValidActivity()) {
            addressMap.setGoogleApiClientHelper(((SelectAddressActivity) getContext()).getGoogleApiClientHelper());
        }
        addressMap.setGoogleMapListener(this);
        addressMap.setMapPadding(0, 0, 0, 0);
        addressMap.setMapLoadedLocation(getMapLoadedLocation());
        getChildFm()
                .beginTransaction()
                .add(R.id.fl_map_container, addressMap, "fragmentTag")
                .disallowAddToBackStack()
                .commit();
    }

    @Override
    public void onLocationAvailable(Location location) {
        ll_loader_layout.setVisibility(View.GONE);
        Log.e("12121212", "onLocationAvailable: ");
        fetchAddressFromLocation(location);
    }

    @Override
    public void onCameraIdeal(Location location) {
        Log.e("12121212", "onCameraIdeal: ");
        fetchAddressFromLocation(location);
    }

    public void goToSelectedLocation(AddressFetchModel addressFetchModel) {
        if (addressMap != null) {
            addressMap.goToSelectedLocation(addressFetchModel);
        }
    }

    @Override
    public void onLocationSelected(AddressFetchModel addressFetchModel) {
        if (getAddressMapFragmentListener() != null) {
            getAddressMapFragmentListener().onLocationChange(addressFetchModel);
        }
    }

    private void fetchAddressFromLocation(Location location) {
        if (location == null) return;
        if (isValidActivity()) {
            boolean fetch = ((SelectAddressActivity) getContext()).getGoogleApiClientHelper().fetchLocationAddress(location,
                    new AddressResultReceiver(getContext(), new Handler()) {
                        @Override
                        public void onAddressFetch(boolean success, AddressFetchModel addressFetchModel) {
                            if (isValidActivity()) {
                                if (success) {
                                    if (ll_loader_layout.getVisibility() != View.GONE) {
                                        ll_loader_layout.setVisibility(View.GONE);
                                    }
                                    if (getAddressMapFragmentListener() != null) {
                                        getAddressMapFragmentListener().onLocationChange(addressFetchModel);
                                    }
                                } else {
                                    tv_loader_message.setText(addressFetchModel.getErrorMessage());
                                    if (ll_loader_layout.getVisibility() != View.VISIBLE) {
                                        ll_loader_layout.setVisibility(View.VISIBLE);
                                    }
                                    if (getAddressMapFragmentListener() != null) {
                                        getAddressMapFragmentListener().onLocationChange(null);
                                    }
                                }
                            }
                        }
                    });
            if (fetch) {
                tv_loader_message.setText("Fetching address...");
                ll_loader_layout.setVisibility(View.VISIBLE);
            } else {
                ll_loader_layout.setVisibility(View.GONE);
                if (getAddressMapFragmentListener() != null) {
                    getAddressMapFragmentListener().onLocationChange(null);
                }
            }
        }
    }


    public interface AddressMapFragmentListener {
        void onLocationChange(AddressFetchModel addressFetchModel);
    }

    public static class AddressMap extends GoogleMapFragment {


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelperNew.onSpecificRequestPermissionsResult(getActivity(), requestCode, permissions, grantResults);
    }
}
