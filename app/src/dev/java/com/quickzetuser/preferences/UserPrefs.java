package com.quickzetuser.preferences;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.preferences.BasePrefs;
import com.quickzetuser.model.UserModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sunil kumar yadav on 13/3/18.
 */


public class UserPrefs extends BasePrefs {

    static final String Prefsname = "prefs_user";

    static String KEY_LOGGEDIN_USER = "logged_in_user";

    Context context;

    List<UserPrefsListener> userPrefsListenerList = new ArrayList<>();

    public UserPrefs (Context context) {
        this.context = context;
    }


    public void addListener (UserPrefsListener userPrefsListener) {
        userPrefsListenerList.add(userPrefsListener);
    }

    public void removeListener (UserPrefsListener userPrefsListener) {
        userPrefsListenerList.remove(userPrefsListener);
    }

    public void clearListeners () {
        userPrefsListenerList.clear();
    }

    @Override
    public String getPrefsName () {
        return Prefsname;
    }

    @Override
    public Context getContext () {
        return context;
    }

    public UserModel getLoggedInUser () {
        UserModel userModel = null;
        try {
            String userDetail = getStringKeyValuePrefs(KEY_LOGGEDIN_USER);
            if (isValidString(userDetail)) {
                userModel = new Gson().fromJson(userDetail, UserModel.class);
            }
        } catch (JsonSyntaxException e) {

        }
        return userModel;
    }

    public void saveLoggedInUser (UserModel userModel) {
        if (userModel == null) return;
        String userDetail = new Gson().toJson(userModel);
        if (setStringKeyValuePrefs(KEY_LOGGEDIN_USER, userDetail)) {
            triggerUserLoggedIn(userModel);
        }
    }


    private void triggerUserLoggedIn (UserModel userModel) {
        for (UserPrefsListener userPrefsListener : userPrefsListenerList) {
            userPrefsListener.userLoggedIn(userModel);
        }
    }

    public void updateLoggedInUser (UserModel userModel) {
        if (userModel == null) return;
        String userdetail = new Gson().toJson(userModel);
        if (setStringKeyValuePrefs(KEY_LOGGEDIN_USER, userdetail)) {
            triggerLoggedInUserUpdate(userModel);
        }
    }

    private void triggerLoggedInUserUpdate (UserModel userModel) {
        for (UserPrefsListener userPrefsListener : userPrefsListenerList) {
            userPrefsListener.loggedInUserUpdate(userModel);
        }
    }


    public void clearLoggedInUser () {
        if (setStringKeyValuePrefs(KEY_LOGGEDIN_USER, "")) {
            triggerLoggedInUserClear();
        }
    }

    private void triggerLoggedInUserClear () {
        for (UserPrefsListener userPrefsListener : userPrefsListenerList) {
            userPrefsListener.loggedInUserClear();
        }
    }


    public interface UserPrefsListener {
        void userLoggedIn(UserModel userModel);

        void loggedInUserUpdate(UserModel userModel);

        void loggedInUserClear();
    }
}
