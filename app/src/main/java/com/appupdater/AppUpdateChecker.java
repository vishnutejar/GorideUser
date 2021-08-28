package com.appupdater;

import android.content.Context;

import com.medy.retrofitwrapper.WebRequest;
import com.medy.retrofitwrapper.WebServiceResponseListener;

import org.json.JSONObject;

/**
 * Created by ubuntu on 29/5/17.
 */


public class AppUpdateChecker implements WebServiceResponseListener {


    Context context;
    onAppUpdateAvailable onAppUpdateAvailable;

    public void setOnAppUpdateAvailable(onAppUpdateAvailable onAppUpdateAvailable) {
        this.onAppUpdateAvailable = onAppUpdateAvailable;
    }

    public AppUpdateChecker(Context context, onAppUpdateAvailable onAppUpdateAvailable) {
        this.context = context;
        this.onAppUpdateAvailable = onAppUpdateAvailable;

    }

    public void checkForUpdate(String url) {
        String package_name = AppUpdateUtils.getAppPackageName(context);

        getPlaystoreAppVersion(url, package_name, this);
    }

    private void getPlaystoreAppVersion(String url, String appId, WebServiceResponseListener webServiceResponseListener) {
        url = String.format(url, appId);
        WebRequest webRequest = new WebRequest(1, url, WebRequest.GET_REQ);
        webRequest.send(context, webServiceResponseListener);
    }

    @Override
    public void onWebRequestCall(WebRequest webRequest) {

    }

    @Override
    public void onWebRequestResponse(WebRequest webRequest) {
        String response = webRequest.getResponseString();
        AppUpdateModal latestAppUpdateModal = parsePlayStoreResponse(response);
        if (latestAppUpdateModal != null) {
            AppUpdateUtils.printLog(context, latestAppUpdateModal.toString());
            if (isUpdateAvailable(latestAppUpdateModal)) {
                if (onAppUpdateAvailable != null) {
                    onAppUpdateAvailable.appUpdateAvailable(latestAppUpdateModal);
                }
            }
        }
    }

    @Override
    public void onWebRequestPreResponse(WebRequest webRequest) {

    }


    private AppUpdateModal parsePlayStoreResponse(String response) {
        if (response != null && !response.trim().isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getBoolean("error")) {
                    return null;
                }
                String versionName = jsonObject.getString("data");
                return new AppUpdateModal(versionName);

            } catch (Exception e) {

            }
        }
        return null;
    }


    public interface onAppUpdateAvailable {
        void appUpdateAvailable(AppUpdateModal appUpdateModal);
    }

    Boolean isUpdateAvailable(AppUpdateModal latestAppUpdateModal) {
        if (latestAppUpdateModal == null) return false;
        AppUpdateModal installedAppUpdateModal = new AppUpdateModal();
        installedAppUpdateModal.setVersionCode(AppUpdateUtils.getAppInstalledVersionCode(context));
        installedAppUpdateModal.setVersionName(AppUpdateUtils.getAppInstalledVersionName(context));
        Boolean res = false;

        if (latestAppUpdateModal.getVersionCode() != null && latestAppUpdateModal.getVersionCode() > 0) {
            res = latestAppUpdateModal.getVersionCode() > installedAppUpdateModal.getVersionCode();
        } else {
            Integer latestVersionName = latestAppUpdateModal.getVersionNameInteger();
            Integer installedVersionName = installedAppUpdateModal.getVersionNameInteger();
            if (latestVersionName != null && installedVersionName != null) {
                res = latestVersionName > installedVersionName;
            }
        }
        return res;
    }
}
