package com.fcm;

import android.net.Uri;
import android.os.Bundle;

import androidx.collection.ArrayMap;

import com.google.firebase.messaging.RemoteMessage;
import com.medy.retrofitwrapper.BaseModel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Manish Kumar
 * @since 15/11/17
 */



public class  NotificationModal extends BaseModel implements Serializable {

    final String from;
    final String to;
    final long sentTime;
    final int ttl;
    final String messageId;
    final String messageType;
    final String collapseKey;
    final Map<String, String> data;
    final RemoteMessage.Notification notification;


    public NotificationModal (RemoteMessage remoteMessage) {
        from = remoteMessage.getFrom();
        to = remoteMessage.getTo();
        sentTime = remoteMessage.getSentTime();
        ttl = remoteMessage.getTtl();
        messageId = remoteMessage.getMessageId();
        messageType = remoteMessage.getMessageType();
        collapseKey = remoteMessage.getCollapseKey();
        data = remoteMessage.getData();
        notification = remoteMessage.getNotification();
    }

    public NotificationModal (Bundle bundle) {
        if (bundle != null) {
            from = getFrom(bundle);
            to = getTo(bundle);
            sentTime = getSentTime(bundle);
            ttl = getTtl(bundle);
            messageId = getMessageId(bundle);
            messageType = getMessageType(bundle);
            collapseKey = getCollapseKey(bundle);
            data = getData(bundle);
            notification = getNotification(bundle);
        } else {
            from = "";
            to = "";
            sentTime = 0;
            ttl = 0;
            messageId = "";
            messageType = "";
            collapseKey = "";
            data = new ArrayMap();
            notification = null;
        }
    }

    private RemoteMessage.Notification getNotification (Bundle bundle) {

        return null;
    }

    private Map<String, String> getData (Bundle bundle) {
        Map<String, String> arrayMap = new ArrayMap();

        Iterator var1 = bundle.keySet().iterator();
        while (var1.hasNext()) {
            String var2 = (String) var1.next();
            Object var3;
            if ((var3 = bundle.get(var2)) instanceof String) {
                String var4 = (String) var3;
                if (!var2.startsWith("google.") && !var2.startsWith("gcm.") && !var2.equals("from") && !var2.equals("message_type") && !var2.equals("collapse_key")) {
                    arrayMap.put(var2, var4);
                }
            }
        }
        return arrayMap;
    }

    private String getCollapseKey (Bundle bundle) {
        return bundle.getString("collapse_key");
    }

    private String getMessageType (Bundle bundle) {
        return bundle.getString("message_type");
    }

    private String getMessageId (Bundle bundle) {
        String var1;
        if ((var1 = bundle.getString("google.message_id")) == null) {
            var1 = bundle.getString("message_id");
        }

        return var1;
    }

    private int getTtl (Bundle bundle) {
        Object var1;
        if ((var1 = bundle.get("google.ttl")) instanceof Integer) {
            return ((Integer) var1).intValue();
        } else {
            if (var1 instanceof String) {
                try {
                    return Integer.parseInt((String) var1);
                } catch (NumberFormatException var3) {
                    String var2 = String.valueOf(var1);
                }
            }

            return 0;
        }
    }

    private long getSentTime (Bundle bundle) {
        Object var1;
        if ((var1 = bundle.get("google.sent_time")) instanceof Long) {
            return ((Long) var1).longValue();
        } else {
            if (var1 instanceof String) {
                try {
                    return Long.parseLong((String) var1);
                } catch (NumberFormatException var3) {
                    String var2 = String.valueOf(var1);
                }
            }

            return 0L;
        }
    }

    private String getTo (Bundle bundle) {
        return bundle.getString("google.to");
    }

    public String getFrom (Bundle bundle) {
        return bundle.getString("from");
    }


    @Override
    public String toString () {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-----------------Notification Received---------------------");
        if (isValidString(from)) {
            stringBuilder.append("\n").append("from : ").append(from);
        }
        if (isValidString(to)) {
            stringBuilder.append("\n").append("to : ").append(to);
        }
        stringBuilder.append("\n").append("sentTime : ").append(sentTime);
        stringBuilder.append("\n").append("ttl : ").append(ttl);
        if (isValidString(messageId)) {
            stringBuilder.append("\n").append("messageId : ").append(messageId);
        }
        if (isValidString(messageType)) {
            stringBuilder.append("\n").append("messageType : ").append(messageType);
        }
        if (isValidString(collapseKey)) {
            stringBuilder.append("\n").append("collapseKey : ").append(collapseKey);
        }
        if (data != null && data.size() > 0) {
            stringBuilder.append("\n").append("data : ").append(data);
        }
        if (notification != null) {
            stringBuilder.append("\n\n").append("notification : ");

            String title = notification.getTitle();
            if (isValidString(title)) {
                stringBuilder.append("\n").append("title : ").append(title);
            }
            String titleLocalizationKey = notification.getTitleLocalizationKey();
            if (isValidString(titleLocalizationKey)) {
                stringBuilder.append("\n").append("titleLocalizationKey : ").append(titleLocalizationKey);
            }
            String[] titleLocalizationArgs = notification.getTitleLocalizationArgs();
            if (titleLocalizationArgs != null) {
                stringBuilder.append("\n").append("titleLocalizationArgs : ").append(Arrays.toString(titleLocalizationArgs));
            }
            String body = notification.getBody();
            if (isValidString(body)) {
                stringBuilder.append("\n").append("body : ").append(body);
            }
            String bodyLocalizationKey = notification.getBodyLocalizationKey();
            if (isValidString(bodyLocalizationKey)) {
                stringBuilder.append("\n").append("bodyLocalizationKey : ").append(bodyLocalizationKey);
            }
            String[] bodyLocalizationArgs = notification.getBodyLocalizationArgs();
            if (bodyLocalizationArgs != null) {
                stringBuilder.append("\n").append("bodyLocalizationArgs : ").append(Arrays.toString(bodyLocalizationArgs));
            }
            String icon = notification.getIcon();
            if (isValidString(icon)) {
                stringBuilder.append("\n").append("icon : ").append(icon);
            }
            String sound = notification.getSound();
            if (isValidString(sound)) {
                stringBuilder.append("\n").append("sound : ").append(sound);
            }
            String tag = notification.getTag();
            if (isValidString(tag)) {
                stringBuilder.append("\n").append("tag : ").append(tag);
            }
            String color = notification.getColor();
            if (isValidString(color)) {
                stringBuilder.append("\n").append("color : ").append(color);
            }
            String clickAction = notification.getClickAction();
            if (isValidString(clickAction)) {
                stringBuilder.append("\n").append("clickAction : ").append(clickAction);
            }
            Uri link = notification.getLink();
            if (link != null) {
                stringBuilder.append("\n").append("link : ").append(link.toString());
            }
        }
        stringBuilder.append("\n").append("-----------------Notification End---------------------");
        return stringBuilder.toString();
    }

    public String  getDataFromKey (String key) {
        if (data == null) return "";
        return data.get(key);
    }


}
