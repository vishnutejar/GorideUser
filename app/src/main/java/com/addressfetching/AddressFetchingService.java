package com.addressfetching;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.rider.R;
import com.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Manish Kumar
 * @since 6/9/17
 */


public class AddressFetchingService extends IntentService {

    public static final String ACTION_FETCH_ADDRESS = ".fetch_address";
    public static final String ACTION_PLACE_DETAIL = ".place_detail";

    public static final String GEOCODING_URL = "https://maps.googleapis.com/maps/" +
            "api/geocode/json?latlng=%s,%s&key=%s";

    public static final String PLACEDETAIL_URL = "https://maps.googleapis.com/maps/" +
            "api/place/details/json?placeid=%s&key=%s";

    public static String APIKEY = "";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AddressFetchingService () {
        super("AddressFetchingService");
    }

    public static Intent createIntent (Context context,
                                       AddressResultReceiver addressResultReceiver,
                                       AddressFetchModel addressFetchModel) {
        APIKEY = context.getResources().getString(R.string.android_key);
        Intent intent = new Intent(context, AddressFetchingService.class);
        intent.putExtra(Constants.RESULT_RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, addressFetchModel);

        return intent;

    }

    @Override
    public void onCreate () {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent (@Nullable Intent intent) {
        if (intent.getAction() == null) return;
        if (intent.getAction().equals(getPackageName() + ACTION_FETCH_ADDRESS)) {
            fetchAddress(intent);
        } else if (intent.getAction().equals(getPackageName() + ACTION_PLACE_DETAIL)) {
            fetchPlaceDetail(intent);
        }
    }

    private synchronized void fetchAddress (Intent intent) {
        AddressFetchModel addressFetchModel = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        if (addressFetchModel == null || addressFetchModel.getLocation() == null) {
            throw new IllegalArgumentException("LOCATION_DATA_EXTRA cant be null");
        }
        int resultCode = 200;
        String errorMessage = "";
        String locationAddress = "";

        if (locationAddress == null || locationAddress.trim().isEmpty()) {
            printLog("errorMessage=" + errorMessage + ", resultCode=" + resultCode);
            getAddressFromLatLng(addressFetchModel, intent);
        } else {
            addressFetchModel.setSuccess(true);
            addressFetchModel.setAddress(locationAddress);
            ResultReceiver resultReceiver = intent.getParcelableExtra(
                    Constants.RESULT_RECEIVER);
            deliverResultToReceiver(resultCode, resultReceiver, addressFetchModel);
        }
    }

    private synchronized void fetchPlaceDetail (Intent intent) {
        AddressFetchModel addressFetchModel = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);
        if (addressFetchModel == null || addressFetchModel.getLocationModel() == null) {
            throw new IllegalArgumentException("LOCATION_DATA_EXTRA cant be null");
        }
        getPlaceDetailFromPlaceId(addressFetchModel, intent);
    }

    private void deliverResultToReceiver (int resultCode,
                                          ResultReceiver resultReceiver,
                                          AddressFetchModel addressFetchModel) {
        if (resultReceiver == null) return;
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.LOCATION_DATA_EXTRA, addressFetchModel);
        resultReceiver.send(resultCode, bundle);
    }

    private void printLog (String msg) {
        if (Utils.isDebugBuild(getApplicationContext()) && msg != null) {
            Log.e(getClass().getSimpleName(), msg);
        }
    }

    public void getAddressFromLatLng (final AddressFetchModel addressFetchModel,
                                      final Intent intent) {
        final Location location = addressFetchModel.getLocation();
        String url = String.format(GEOCODING_URL, String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()), APIKEY);
        printLog("getAddressFromLatLng=" + url);

        WebRequest webRequest = new WebRequest(1, url, WebRequest.GET_REQ);
        webRequest.send(getApplicationContext(), new WebServiceResponseListener() {
            @Override
            public void onWebRequestCall (WebRequest webRequest) {

            }

            @Override
            public void onWebRequestResponse (WebRequest webRequest) {
                int resultCode = 200;
                String errorMessage = "No address found please change your location";
                if (webRequest.getWebRequestException() != null) {
                    resultCode = 201;
                    addressFetchModel.setLocationModel(null);
                    addressFetchModel.setSuccess(false);
                    printLog("getAddressFromLatLng exception=" + webRequest.getWebRequestException().getMessage());
                    addressFetchModel.setErrorMessage(errorMessage);
                } else {
                    if (webRequest.isValidString(webRequest.getResponseString())) {
                        LocationModelFull locationModelFull = parseResponse(true,
                                webRequest.getResponseString());
                        if (locationModelFull.isError()) {
                            resultCode = 301;
                            addressFetchModel.setLocationModel(null);
                            addressFetchModel.setSuccess(false);
                            addressFetchModel.setErrorMessage(errorMessage);
                        } else {
                            locationModelFull.getData().setLatitude(location.getLatitude());
                            locationModelFull.getData().setLongitude(location.getLongitude());
                            addressFetchModel.setSuccess(true);
                            addressFetchModel.setLocationModel(locationModelFull.getData());
                            addressFetchModel.setAddress(locationModelFull.getData().getFulladdress());
                        }
                    } else {
                        resultCode = 201;
                        addressFetchModel.setLocationModel(null);
                        addressFetchModel.setSuccess(false);
                        addressFetchModel.setErrorMessage("Blank response");
                    }
                }
                ResultReceiver resultReceiver = intent.getParcelableExtra(
                        Constants.RESULT_RECEIVER);

                deliverResultToReceiver(resultCode, resultReceiver, addressFetchModel);
            }

            @Override
            public void onWebRequestPreResponse(WebRequest webRequest) {
            }
        });

    }

    public void getPlaceDetailFromPlaceId (final AddressFetchModel addressFetchModel,
                                           final Intent intent) {
        final LocationModelFull.LocationModel locationModel = addressFetchModel.getLocationModel();
        String url = String.format(PLACEDETAIL_URL, String.valueOf(locationModel.getPlaceId()),
                APIKEY);
        printLog("getPlaceDetailFromPlaceId=" + url);

        WebRequest webRequest = new WebRequest(2, url, WebRequest.GET_REQ);
        webRequest.send(getApplicationContext(), new WebServiceResponseListener() {
            @Override
            public void onWebRequestCall (WebRequest webRequest) {

            }

            @Override
            public void onWebRequestResponse (WebRequest webRequest) {
                int resultCode = 200;
                String errorMessage = "No address found please change your location";
                if (webRequest.getWebRequestException() != null) {
                    resultCode = 201;
                    printLog("getAddressFromLatLng exception=" + webRequest.getWebRequestException().getMessage());
                    addressFetchModel.setSuccess(false);
                    addressFetchModel.setErrorMessage(errorMessage);
                } else {
                    String result = webRequest.getResponseString();
                    if (webRequest.isValidString(result)) {
                        LocationModelFull locationModelFull = parseResponse(false, result);
                        if (locationModelFull.isError()) {
                            resultCode = 301;
                            printLog("getAddressFromLatLng error=" + locationModelFull.getMessage());
                            addressFetchModel.setSuccess(false);
                            addressFetchModel.setErrorMessage(errorMessage);
                        } else {
                            LocationModelFull.LocationModel locationModel1 = locationModelFull.getData();
                            locationModel1.setFulladdress(locationModel.getDescription());
                            locationModel1.setPrimaryText(locationModel.getPrimaryText());
                            locationModel1.setSecondaryText(locationModel.getSecondaryText());

                            addressFetchModel.setSuccess(true);
                            addressFetchModel.setAddress(locationModelFull.getData().getFulladdress());
                            addressFetchModel.setLocationModel(locationModelFull.getData());
                        }
                    } else {
                        resultCode = 201;
                        addressFetchModel.setSuccess(false);
                        addressFetchModel.setErrorMessage("blank response");
                    }

                }

                ResultReceiver resultReceiver = intent.getParcelableExtra(
                        Constants.RESULT_RECEIVER);

                deliverResultToReceiver(resultCode, resultReceiver, addressFetchModel);
            }

            @Override
            public void onWebRequestPreResponse(WebRequest webRequest) {

            }
        });

    }

    public LocationModelFull parseResponse (boolean isResultArray,
                                            String response) {
        LocationModelFull locationModelFull = new LocationModelFull();
        try {
            if (response != null && !response.isEmpty()) {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                locationModelFull.setMessage(status);
                if (status.equalsIgnoreCase("ok")) {
                    JSONArray results = null;
                    if (isResultArray) {
                        results = jsonObject.getJSONArray("results");
                    } else {
                        JSONObject resl = jsonObject.getJSONObject("result");
                        results = new JSONArray();
                        results.put(resl);
                    }


                    if (results != null && results.length() > 0) {

                        JSONObject jsonObject1 = results.getJSONObject(0);
                        if (jsonObject1 != null) {
                            LocationModelFull.LocationModel locationModel = new LocationModelFull.LocationModel();
                            JSONObject location = jsonObject1.getJSONObject("geometry")
                                    .getJSONObject("location");

                            locationModel.setLatitude(location.getDouble("lat"));
                            locationModel.setLongitude(location.getDouble("lng"));
                            locationModel.setFulladdress(jsonObject1.getString("formatted_address"));


                            JSONArray address_components = jsonObject1.getJSONArray("address_components");
                            if (address_components != null && address_components.length() > 0) {
                                for (int i = 0; i < address_components.length(); i++) {
                                    JSONObject comp = address_components.getJSONObject(i);
                                    JSONArray types = comp.getJSONArray("types");
                                    if (types.toString().matches(".*\\broute\\b.*")) {
                                        locationModel.setStreet(comp.getString("long_name"));
                                    } else if (types.toString().matches(".*\\blocality\\b.*")) {
                                        locationModel.setTownship(comp.getString("long_name"));
                                    } else if (types.toString().matches(".*\\badministrative_area_level_2\\b.*")) {
                                        locationModel.setCity(comp.getString("long_name"));
                                    } else if (types.toString().matches(".*\\badministrative_area_level_1\\b.*")) {
                                        locationModel.setStateFull(comp.getString("long_name"));
                                        locationModel.setState(comp.getString("short_name"));
                                    } else if (types.toString().matches(".*\\bpremise\\b.*")) {
                                        locationModel.setBuilding(comp.getString("long_name"));
                                    } else if (types.toString().matches(".*\\bpostal_code\\b.*")) {
                                        locationModel.setPostalCode(comp.getString("long_name"));
                                    } else if (types.toString().matches(".*\\bcountry\\b.*")) {
                                        locationModel.setCountryFull(comp.getString("long_name"));
                                        locationModel.setCountry(comp.getString("short_name"));
                                    }
                                }
                            }

                            String placeId = jsonObject1.optString("place_id");
                            locationModel.setPlaceId(placeId);
                            long utc_offset = jsonObject1.optLong("utc_offset", -1);
                            locationModel.setUtc_offset(utc_offset);
                            locationModel.setDescription(locationModel.getFulladdress());
                            locationModel.setPrimaryText("");
                            locationModel.setSecondaryText("");
                            int index = locationModel.getFulladdress().indexOf(",");
                            if (index != -1) {
                                locationModel.setPrimaryText(locationModel.getFulladdress().substring(0, index).trim());
                                locationModel.setSecondaryText(locationModel.getFulladdress().substring(index + 1).trim());
                            }
                            locationModelFull.setData(locationModel);
                        } else {
                            locationModelFull.setCode(2);
                            locationModelFull.setError(true);
                            locationModelFull.setMessage("result is not zero but json object is null");
                        }

                    } else {
                        locationModelFull.setCode(1);
                        locationModelFull.setError(true);
                        locationModelFull.setMessage("no result found");
                    }


                } else {
                    locationModelFull.setCode(3);
                    locationModelFull.setError(true);
                }


            } else {
                locationModelFull.setCode(4);
                locationModelFull.setError(true);
                locationModelFull.setMessage("Blank Response");
            }
        } catch (JSONException e) {
            locationModelFull.setCode(5);
            locationModelFull.setError(true);
            locationModelFull.setMessage("Excption in parsing Routes");
        }
        return locationModelFull;

    }


    public static final class Constants {
        public static final String LOCATION_DATA_EXTRA = "LOCATION_DATA_EXTRA";
        public static final String RESULT_RECEIVER = "RESULT_RECEIVER";
    }

}
