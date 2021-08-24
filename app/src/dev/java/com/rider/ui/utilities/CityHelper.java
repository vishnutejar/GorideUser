package com.rider.ui.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceResponseListener;
import com.models.DeviceInfoModal;
import com.rider.model.CityModel;
import com.rider.model.web_response.CityResponseModel;
import com.rider.rest.WebRequestConstants;
import com.rider.rest.WebRequestHelper;
import com.utilities.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ubuntu on 12/4/18.
 */

public class CityHelper implements WebRequestConstants {

    private final String KEY_CITY_RESPONSE = "city_response";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private List<CityModel> cityModelList;
    private WebRequestHelper webRequestHelper;
    private WebServiceResponseListener webServiceResponseListener = new WebServiceResponseListener() {
        @Override
        public void onWebRequestCall(WebRequest webRequest) {
            webRequest.addHeader(HEADER_KEY_LANG, LANG_1);

            DeviceInfoModal deviceInfoModal = new DeviceInfoModal(context);
            webRequest.addHeader(HEADER_KEY_DEVICE_TYPE, DEVICE_TYPE_ANDROID);
            webRequest.addHeader(HEADER_KEY_DEVICE_INFO, deviceInfoModal.toString());
            webRequest.addHeader(HEADER_KEY_APP_INFO, deviceInfoModal.getAppInfo());

            webRequest.addHeader(HEADER_KEY_TOKEN, DeviceUtils.getUniqueDeviceId());
        }

        @Override
        public void onWebRequestResponse(WebRequest webRequest) {
            if (webRequest.getResponseCode() == 401 || webRequest.getResponseCode() == 412) {
                return;
            }

            if (webRequest.checkSuccess()) {
                CityResponseModel responseModel = (CityResponseModel)
                        webRequest.getResponsePojo(CityResponseModel.class);
                setCityModelList(responseModel.getData());
            }

        }

        @Override
        public void onWebRequestPreResponse(WebRequest webRequest) {

        }
    };

    public CityHelper(Context context) {
        this.context = context;
        final String PREF_NAME = "city_helper";
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        webRequestHelper = new WebRequestHelper(context);

        getCityModelList();
    }

    public void getCitiesFromServer() {
        webRequestHelper.getCities(0, webServiceResponseListener);
    }

    public void setCityModelList(List<CityModel> cityModelList) {
        if (cityModelList == null) cityModelList = new ArrayList<>();

        String cityResponse = new Gson().toJson(cityModelList);
        editor.putString(KEY_CITY_RESPONSE, cityResponse);
        editor.commit();
        this.cityModelList = cityModelList;
    }

    public List<CityModel> getCityModelList() {
        String cityResponse = preferences.getString(KEY_CITY_RESPONSE, "[]");
        cityModelList = new Gson().fromJson(cityResponse, new TypeToken<List<CityModel>>() {
        }.getType());
        return cityModelList;
    }

    public CityModel getCityModelFromLatLng(LatLng latLng) {
        if (cityModelList == null || cityModelList.size() == 0) return null;

        synchronized (cityModelList) {
            for (CityModel cityModel : cityModelList) {
                Object[] path_array = cityModel.getPathArray();
                if (path_array == null) continue;

                boolean isInside = isPointInPolygon(latLng.latitude, latLng.longitude,
                        (double[]) path_array[0], (double[]) path_array[1]);

                if (isInside) return cityModel;

            }
        }
        return null;
    }

    public CityModel getCityModelFromLatLng(double latitude, double longitude) {
        return getCityModelFromLatLng(new LatLng(latitude, longitude));
    }

    public boolean isPointInPolygon(double latitude, double longitude,
                                    double latitude_array[], double longitude_array[]) {
        int size = longitude_array.length;
        boolean flag1 = false;
        int k = size - 1;
        int j = 0;
        while (j < size) {
            boolean flag;
            boolean flag2;
            boolean flag3;
            if (latitude_array[j] > latitude) {
                flag2 = true;
            } else {
                flag2 = false;
            }
            if (latitude_array[k] > latitude) {
                flag3 = true;
            } else {
                flag3 = false;
            }
            flag = flag1;
            if (flag2 != flag3) {
                flag = flag1;
                if (longitude < ((longitude_array[k] - longitude_array[j]) * (latitude - latitude_array[j])) / (latitude_array[k] - latitude_array[j])
                        + longitude_array[j]) {
                    if (!flag1) {
                        flag = true;
                    } else {
                        flag = false;
                    }
                }
            }
            k = j;
            j++;
            flag1 = flag;
        }
        return flag1;
    }
}
