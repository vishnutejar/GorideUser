package com.quickzetuser.ui.main.booking.addresschooser;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.addressfetching.LocationModelFull;
import com.google.gson.Gson;
import com.permissions.PermissionHelperNew;
import com.quickzetuser.R;
import com.quickzetuser.appBase.AppBaseFragment;
import com.quickzetuser.database.tables.FavoriteTable;
import com.quickzetuser.database.tables.RecentTable;
import com.quickzetuser.model.FavouriteModel;
import com.quickzetuser.ui.main.booking.addresschooser.adapter.GooglePlacesAutocompleteAdapter;
import com.quickzetuser.ui.main.booking.addresschooser.adapter.RecyclerViewFavoriteAdapter;
import com.quickzetuser.ui.main.booking.addresschooser.adapter.RecyclerViewRecentAdapter;
import com.quickzetuser.ui.main.dialog.favoriteconfirm.FavoriteConfirmDialog;
import com.quickzetuser.ui.main.dialog.favoriteconfirm.FavoriteDeleteDialog;
import com.quickzetuser.ui.main.map.SelectAddressActivity;
import com.utilities.GoogleApiClientHelper;
import com.utilities.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Sunil kumar yadav on 12/4/18.
 */

public class AddressChooserFragment extends AppBaseFragment {

    private static final String TAG = AddressChooserFragment.class.getSimpleName();
    ChooseAddressListener chooseAddressListener;
    GoogleApiClientHelper googleApiClientHelper = null;
   // AutoCompleteAdapter autoCompleteAdapter;
    GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;
    private List<FavouriteModel> mFavoriteList = new ArrayList<>();
    private List<LocationModelFull.LocationModel> mRecentList = new ArrayList<>();
    private EditText et_address;
    private RelativeLayout ll_current_location;
    private RecyclerView recycler_view_favorite;
    private RecyclerViewFavoriteAdapter favoriteAdapter;
    private RecyclerView recycler_view_recent;
    private RecyclerViewRecentAdapter recentAdapter;
    private RecyclerView recycler_view;
    private ProgressBar pb_auto_search;
    private ImageView iv_clear_search;
    private TextView textView;
    private boolean isBound;
    private double pickup_latitude;
    private double pickup_longitude;


    public void setChooseAddressListener(ChooseAddressListener chooseAddressListener) {
        this.chooseAddressListener = chooseAddressListener;
    }

    public void setIsBound(boolean isBound){
        this.isBound = isBound;
    }

    public void setPickupLatitude(double pickup_latitude){
        this.pickup_latitude = pickup_latitude;
    }

    public void setPickupLongitude(double pickup_longitude){
        this.pickup_longitude = pickup_longitude;
    }

    private int getOpenWithRequestCode() {
        if (getArguments() != null) {
            return getArguments().getInt(OPEN_WITH_RQUEST_CODE, -1);
        }
        return -1;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.fragment_address_chooser;
    }

    @Override
    public void initializeComponent() {
        et_address = getView().findViewById(R.id.et_address);
        ll_current_location = getView().findViewById(R.id.ll_current_location);
        pb_auto_search = getView().findViewById(R.id.pb_auto_search);
        iv_clear_search = getView().findViewById(R.id.iv_clear_search);
        recycler_view_favorite = getView().findViewById(R.id.recycler_view_favorite);
        recycler_view_recent = getView().findViewById(R.id.recycler_view_recent);
        recycler_view = getView().findViewById(R.id.recycler_view);

        iv_clear_search.setOnClickListener(this);
        ll_current_location.setOnClickListener(this);


        try {
            googleApiClientHelper = getMainActivity().getMapHandler().getGoogleApiClientHelper();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        initRecyclerView();
        initRecyclerViewFavorite();
        initRecyclerViewRecent();
    }

    private void initRecyclerViewFavorite() {
        mFavoriteList.clear();
        mFavoriteList = FavoriteTable.getInstance().getAllFavorite();
        favoriteAdapter = new RecyclerViewFavoriteAdapter(mFavoriteList, et_address);
        recycler_view_favorite.setLayoutManager(getFullHeightLinearLayoutManager());
        recycler_view_favorite.setAdapter(favoriteAdapter);

        ItemClickSupport.addTo(recycler_view_favorite).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                FavouriteModel detail = favoriteAdapter.getItem(position);
                LocationModelFull.LocationModel locationModel = detail.getDetail();
                if (locationModel == null) return;
                if (v.getId() == R.id.iv_delete) {
                    addFavoriteDeleteDialog(detail);
                } else {
                    et_address.setTag(locationModel);
                    chooseAddressListener.OnChooseSuccess(textView, locationModel);
//                        Fragment mainFragment = getMainActivity().getMapHandler().getMainFragment();
//                        getMainActivity().getMapHandler().removeFragment(mainFragment);
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private void initRecyclerViewRecent() {
        mRecentList.clear();
        mRecentList = RecentTable.getInstance().getAllRecent();
        recentAdapter = new RecyclerViewRecentAdapter(mRecentList, et_address);
        recycler_view_recent.setLayoutManager(getFullHeightLinearLayoutManager());
        recycler_view_recent.setAdapter(recentAdapter);

        ItemClickSupport.addTo(recycler_view_recent).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                LocationModelFull.LocationModel locationModel = recentAdapter.getItem(position);
                if (locationModel == null) return;
                if (v.getId() == R.id.iv_favorite) {
                    addFavoriteConfirmDialog(locationModel);
                } else {
                    et_address.setTag(locationModel);
                    chooseAddressListener.OnChooseSuccess(textView, locationModel);
//                        Fragment mainFragment = getMainActivity().getMapHandler().getMainFragment();
//                        getMainActivity().getMapHandler().removeFragment(mainFragment);
                    getActivity().onBackPressed();
                }

            }
        });
    }

    private void addFavoriteConfirmDialog(final LocationModelFull.LocationModel locationModel) {
        FavoriteConfirmDialog addAddressDialog = FavoriteConfirmDialog.getNewInstance();
        addAddressDialog.setLocationModel(locationModel);
        addAddressDialog.setConfirmationDialogListener(new FavoriteConfirmDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm(List<FavouriteModel> modelList, DialogFragment dialogFragment) {
                /**
                 * add favorite address
                 */
                FavouriteModel favouriteModel = modelList.get(0);
                FavoriteTable.getInstance().addFavorite(favouriteModel);
                favoriteAdapter.addFavourite(favouriteModel);
                dialogFragment.dismiss();
            }
        });
        addAddressDialog.show(getActivity().getSupportFragmentManager(), addAddressDialog.getClass().getSimpleName());
    }

    private void addFavoriteDeleteDialog(final FavouriteModel favouriteModel) {
        FavoriteDeleteDialog deleteAddressDialog = FavoriteDeleteDialog.getNewInstance();
        deleteAddressDialog.setLocationModel(favouriteModel);
        deleteAddressDialog.setConfirmationDialogListener(new FavoriteDeleteDialog.ConfirmationDialogListener() {
            @Override
            public void onClickConfirm(DialogFragment dialogFragment) {
                /**
                 * delete favourite address
                 */
                FavoriteTable.getInstance().deleteFavorite(favouriteModel.getType());
                favoriteAdapter.deleteFavourite(favouriteModel);
                dialogFragment.dismiss();
            }
        });
        deleteAddressDialog.show(getActivity().getSupportFragmentManager(), deleteAddressDialog.getClass().getSimpleName());
    }

    private void initRecyclerView() {
        googlePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(getActivity(),mRecentList, et_address, recycler_view, googleApiClientHelper,
                isBound, pickup_latitude, pickup_longitude);
        googlePlacesAutocompleteAdapter.setAutoCompleteListener(new GooglePlacesAutocompleteAdapter.AutoCompleteListener() {
            @Override
            public void getPrediction(String text) {
                updateViewVisibility(pb_auto_search, View.VISIBLE);
                updateViewVisibility(iv_clear_search, View.GONE);

            }

            @Override
            public void onPredictionResult(List<LocationModelFull.LocationModel> list) {
                updateViewVisibility(pb_auto_search, View.GONE);
                updateViewVisibility(iv_clear_search, View.GONE);
                if (et_address.getText().toString().trim().length() > 0)
                    updateViewVisibility(iv_clear_search, View.VISIBLE);
            }

            @Override
            public void getPredicationDetail(LocationModelFull.LocationModel locationModel) {
                updateViewVisibility(pb_auto_search, View.VISIBLE);
                updateViewVisibility(iv_clear_search, View.GONE);
                displayProgressBar(false);
            }

            @Override
            public void onPredicationDetailResult(LocationModelFull.LocationModel locationModel) {
                dismissProgressBar();
                updateViewVisibility(pb_auto_search, View.GONE);
                if (locationModel != null) {
                    RecentTable.getInstance().addRecent(locationModel);
                    et_address.setTag(locationModel);
                    chooseAddressListener.OnChooseSuccess(textView, locationModel);
//                        Fragment mainFragment = getMainActivity().getMapHandler().getMainFragment();
//                        getMainActivity().getMapHandler().removeFragment(mainFragment);
                    getActivity().onBackPressed();
                }
            }
        });

    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear_search:
                if (et_address.getText().toString().trim().length() > 0) {
                    et_address.setText("");
                    updateViewVisibility(iv_clear_search, View.GONE);
                  //  hideKeyboard(v);
                }
                break;

            case R.id.ll_current_location:
                if (!PermissionHelperNew.needLocationPermission(getActivity(), new PermissionHelperNew.OnSpecificPermissionGranted() {
                    @Override
                    public void onPermissionGranted(boolean isGranted, boolean withNeverAsk, String permission, int requestCode) {
                        if (isGranted) {
                            openMapWithCurrentLocation();
                        } else {
                            showErrorMessage("Need location permission.");
                        }
                    }
                })) {
                    openMapWithCurrentLocation();
                }

                break;
        }
    }

    private void openMapWithCurrentLocation() {
        Bundle bundle = new Bundle();
        bundle.putInt(OPEN_WITH_RQUEST_CODE, REQUEST_CODE_SELECT_ADDRESS);
        goToSelectAddressActivity(bundle, REQUEST_CODE_SELECT_ADDRESS);
    }


    @Override
    public void onResume() {
        try {
            getMainActivity().getFavouriteAddress();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    public interface ChooseAddressListener {
        void OnChooseSuccess(TextView textView, LocationModelFull.LocationModel locationModel);
    }


    public void goToSelectAddressActivity(Bundle bundle, int requestCode) {
        Intent intent = new Intent(getActivity(), SelectAddressActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (requestCode > 0) {
            startActivityForResult(intent, requestCode);
        } else {
            startActivity(intent);
        }
//        getActivity().overridePendingTransition(R.anim.enter_alpha, R.anim.exit_alpha);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_ADDRESS) {
            if (resultCode == RESULT_OK) {
                String extra = data.getStringExtra(DATA);
                if (isValidString(extra)) {
                    LocationModelFull.LocationModel locationModel = new Gson().fromJson(extra, LocationModelFull.LocationModel.class);
                    RecentTable.getInstance().addRecent(locationModel);
                    et_address.setTag(locationModel);
                    chooseAddressListener.OnChooseSuccess(textView, locationModel);
                    getActivity().onBackPressed();
                }
            }
        }
    }

}
