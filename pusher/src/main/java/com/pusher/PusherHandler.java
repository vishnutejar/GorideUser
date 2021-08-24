package com.pusher;

import android.util.Log;

import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.impl.InternalChannel;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ubuntu on 10/8/16.
 */
public class PusherHandler {

    public static final String TAG = "PusherHandler";

    private static PusherHandler pusherHandler;
    private final Map<String, InternalChannel> channelNameToChannelMap = new ConcurrentHashMap();

    private Pusher mPusher;
    private PrivateChannelListener pusherPrivateChannelListener;
    private ArrayList<PusherListener> pusherListenerArrayList;

    private Gson gson;

    ConnectionEventListener connectionEventListener = new ConnectionEventListener() {
        @Override
        public void onConnectionStateChange(ConnectionStateChange change) {
            printLog(change.getCurrentState().toString());
            if (change.getCurrentState() == ConnectionState.CONNECTED) {
                printLog("Pusher connected");
            } else if (change.getCurrentState() == ConnectionState.DISCONNECTED) {
                printLog("Pusher disconnected");
            } else if (change.getCurrentState() == ConnectionState.CONNECTING) {
                printLog("Pusher connecting");
            } else if (change.getCurrentState() == ConnectionState.DISCONNECTING) {
                printLog("Pusher disconnecting");
            }
        }

        @Override
        public void onError(String message, String code, Exception e) {
            printLog("message:" + message + "code:" + code);

        }
    };


    private PusherHandler() {
        gson = new Gson();
        pusherListenerArrayList = new ArrayList<>();
        pusherPrivateChannelListener = new PrivateChannelListener() {
            @Override
            public ArrayList<PusherListener> getListeners() {
                return pusherListenerArrayList;
            }
        };

        HttpAuthorizer authorise = new HttpAuthorizer(
                PusherConstant.PUSHER_AUTH_URL);
        PusherOptions options = new PusherOptions().setEncrypted(true)
                .setAuthorizer(authorise);
        mPusher = new Pusher(PusherConstant.PUSHER_KEY, options);
        connectPusher();
    }

    public static void printLog(String msg) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, msg);

    }

    public static PusherHandler getInstance() {
        if (pusherHandler == null) {
            pusherHandler = new PusherHandler();
        }
        return pusherHandler;
    }

    public void connectPusher() {
        ConnectionState currentState = mPusher.getConnection().getState();
        if (currentState == ConnectionState.CONNECTED ||
                currentState == ConnectionState.CONNECTING) {
            return;
        }
        mPusher.connect(connectionEventListener, ConnectionState.ALL);

    }


    public void addPusherListener(PusherListener pusherListener) {
        pusherListenerArrayList.add(pusherListener);
    }

    public void removePusherListener(PusherListener pusherListener) {
        if (pusherListenerArrayList.size() > 0) {
            int index = pusherListenerArrayList.indexOf(pusherListener);
            if (index < 0) return;
            pusherListenerArrayList.remove(index);
        }
    }


    public void removeAllPusherListeners() {
        pusherListenerArrayList.clear();
    }


    public void subscribePrivateChannelWithEvent(String channelName, String... eventName) {
        if (mPusher != null) {
            PrivateChannel channel = mPusher.getPrivateChannel(channelName);
            if (channel == null) {
                try {
                    channel = mPusher.subscribePrivate(channelName, pusherPrivateChannelListener, eventName);

                } catch (IllegalArgumentException e) {
                    printLog(e.getMessage());
                } catch (IllegalStateException e) {
                    printLog(e.getMessage());
                }
            }
            if (channel != null)
                channelNameToChannelMap.put(channel.getName(), (InternalChannel) channel);
        }

    }


    public void unSubscribePrivateChannel(String channelName) {

        if (mPusher != null) {
            Channel channel = mPusher.getPrivateChannel(channelName);
            if (channel != null) {
                try {
                    mPusher.unsubscribe(channelName);
                    printLog("unSubscribePrivateChannel " + channelName);
                } catch (IllegalArgumentException e) {
                    printLog(e.getMessage());
                } catch (IllegalStateException e) {
                    printLog(e.getMessage());
                }
            }
            this.channelNameToChannelMap.remove(channelName);
        }
    }


    public void publishMessageOnPrivateChannel(String channelName, String eventName, JSONObject message) {
        publishMessageOnPrivateChannel(channelName, eventName, message.toString());

    }

    public void publishMessageOnPrivateChannel(String channelName, String eventName, Object message) {
        if (message == null) return;
        publishMessageOnPrivateChannel(channelName, eventName, gson.toJson(message));
    }


    public void publishMessageOnPrivateChannel(String channelName, String eventName, String message) {
        PrivateChannel channel = mPusher.getPrivateChannel(channelName);
        if (channel != null) {
            try {
                channel.trigger(eventName, message);
                printLog("Message Publish on " + "\n" + "ChannelName= " + channelName + "\n" +
                        "EventName= " + eventName + "\n" + "Message= " + message);
            } catch (IllegalStateException e) {
                printLog(e.getMessage());
            } catch (IllegalArgumentException e) {
                printLog(e.getMessage());
            }
        } else {
            subscribePrivateChannelWithEvent(channelName, eventName);
        }
    }

    public void destroyPusher() {
        if (mPusher != null) {
            for (InternalChannel channel : this.channelNameToChannelMap.values()) {
                unSubscribePrivateChannel(channel.getName());
            }
            mPusher.disconnect();
            printLog("destroyPusher");
        }
    }


}
