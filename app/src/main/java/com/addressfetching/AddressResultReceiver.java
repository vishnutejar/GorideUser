package com.addressfetching;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.utilities.Utils;


/**
 * @author Manish Kumar
 * @since 6/9/17
 */


public abstract class AddressResultReceiver extends ResultReceiver {

    public static final String TAG = "AddressResultReceiver";

    private Context context;

    public AddressResultReceiver(Context context, Handler handler) {
        super(handler);
        this.context = context;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        AddressFetchModel addressFetchModel = resultData.getParcelable(AddressFetchingService.Constants.LOCATION_DATA_EXTRA);
        if (addressFetchModel == null) return;

        printLog(addressFetchModel.isSuccess() ? addressFetchModel.getAddress() :
                addressFetchModel.getErrorMessage());

        if (resultCode == 200) {
            onAddressFetch(true, addressFetchModel);
            return;
        }

        onAddressFetch(false, addressFetchModel);
    }

    public abstract void onAddressFetch(boolean success, AddressFetchModel addressFetchModel);


    private void printLog(String msg) {
        if (context != null && Utils.isDebugBuild(context) && msg != null) {
            Log.e(TAG, msg);
        }
    }
}
