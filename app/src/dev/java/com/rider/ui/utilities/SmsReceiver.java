package com.rider.ui.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.rider.rest.WebRequestConstants;

/**
 * @author Sunil kumar Yadav
 * @Since 5/6/18
 */
public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;

    public SmsReceiver (SmsListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive (Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    if (senderAddress != null &&
                            senderAddress.trim().toLowerCase().contains(WebRequestConstants.SMS_SENDER.toLowerCase())) {
                        String message = currentMessage.getDisplayMessageBody();
                        String verificationCode = getVerificationCode(message);
                        if (verificationCode != null) {
                            mListener.otpMessageReceived(verificationCode);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private String getVerificationCode (String message) {
        String code = null;
        if (message == null || message.trim().length() < 4) {
            return null;
        } else {
            code = message.substring((message.length() - 5), (message.length() - 1));
        }
        return code;
    }
}
