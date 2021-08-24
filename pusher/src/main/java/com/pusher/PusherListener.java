package com.pusher;

/**
 * Created by ubuntu on 10/8/16.
 */
public interface PusherListener {

    void channelConnected (String channel);

    void pusherError (String message, Exception e);

    void messageReceived (String channelName, String eventName, Object data);
}
