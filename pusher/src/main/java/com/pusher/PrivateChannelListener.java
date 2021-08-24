package com.pusher;

import com.pusher.client.channel.PrivateChannelEventListener;

import java.util.ArrayList;

/**
 * Created by ubuntu on 10/8/16.
 */
public abstract class PrivateChannelListener implements PrivateChannelEventListener {


    public abstract ArrayList<PusherListener> getListeners ();

    @Override
    public void onAuthenticationFailure (String message, Exception e) {
        triggerError(message, e);
    }

    @Override
    public void onSubscriptionSucceeded (String channelName) {
        triggerChannelConnect(channelName);
    }

    @Override
    public void onEvent (String channelName, String eventName, String data) {
        triggerMessageReceived(channelName, eventName, data);
    }


    private void triggerChannelConnect (String channel) {
        PusherHandler.printLog("triggerChannelConnect " + channel);
        if (getListeners() == null) return;
        PusherHandler.printLog("triggerChannelConnect on size " + getListeners().size());
        for (PusherListener pusherListener : getListeners()) {
            if (pusherListener != null) {
                pusherListener.channelConnected(channel);
            }
        }
    }

    private void triggerError (String message, Exception e) {
        PusherHandler.printLog("triggerError " + message);
        if (getListeners() == null) return;
        for (PusherListener pusherListener : getListeners()) {
            if (pusherListener != null) {
                pusherListener.pusherError(message, e);
            }
        }
    }

    private void triggerMessageReceived (String channel, String event, Object message) {
        PusherHandler.printLog("triggerMessageReceived " + channel + "," + event + "," + message);
        if (getListeners() == null) return;
        for (PusherListener pusherListener : getListeners()) {
            if (pusherListener != null) {
                pusherListener.messageReceived(channel, event, message);
            }
        }
    }

}
