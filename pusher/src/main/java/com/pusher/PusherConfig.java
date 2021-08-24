package com.pusher;

/**
 * Created by ubuntu on 24/1/17.
 */

public class PusherConfig {

    static PusherConfig instance;

    static {
        instance = new PusherConfig();
    }

    public static PusherConfig getInstance() {
        return instance;
    }

    public void setupPusher(String pusherKey, String pusherAuthUrl) {
        PusherConstant.PUSHER_KEY = pusherKey;
        PusherConstant.PUSHER_AUTH_URL = pusherAuthUrl;
    }
}
