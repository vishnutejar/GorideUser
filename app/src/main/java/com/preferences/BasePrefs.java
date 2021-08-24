package com.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Manish Kumar
 * @since 31/8/17
 */


public abstract class BasePrefs {


    public abstract String getPrefsName();

    public abstract Context getContext();

    private boolean isValidTransaction() {
        return getContext() != null && getPrefsName() != null;
    }

    public boolean isValidString(String data) {
        return data != null && !data.trim().isEmpty();
    }

    public void clearPrefsData() {
        if (!isValidTransaction()) return;
        SharedPreferences prefs = getContext().getSharedPreferences(getPrefsName(),
                Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }


    public boolean setStringKeyValuePrefs(String key,
                                          String value) {
        if (!isValidTransaction()) return false;
        SharedPreferences prefs = getContext().getSharedPreferences(getPrefsName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
        return true;

    }

    public String getStringKeyValuePrefs(String key) {
        if (!isValidTransaction()) return "";
        SharedPreferences prefs = getContext().getSharedPreferences(getPrefsName(),
                Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public String getStringKeyValuePrefs(String key, String defaultValue) {
        if (!isValidTransaction()) return defaultValue;
        SharedPreferences prefs = getContext().getSharedPreferences(getPrefsName(),
                Context.MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }

    public void setintKeyValuePrefs(String key, int value) {
        if (!isValidTransaction()) return;
        SharedPreferences prefs = getContext().getSharedPreferences(getPrefsName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();

    }

    public int getintKeyValuePrefs(String key) {
        if (!isValidTransaction()) return 0;
        SharedPreferences prefs = getContext().getSharedPreferences(getPrefsName(),
                Context.MODE_PRIVATE);
        return prefs.getInt(key, 0);
    }

    public void setfloatKeyValuePrefs(String key, float value) {
        if (!isValidTransaction()) return;
        SharedPreferences prefs = getContext().getSharedPreferences(getPrefsName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.commit();

    }

    public float getfloatKeyValuePrefs(String key) {
        if (!isValidTransaction()) return 0;
        SharedPreferences prefs = getContext().getSharedPreferences(getPrefsName(),
                Context.MODE_PRIVATE);
        return prefs.getFloat(key, 0);
    }


    public void setBooleanKeyValuePrefs(String key,
                                        boolean value) {
        if (!isValidTransaction()) return;
        SharedPreferences prefs = getContext().getSharedPreferences(getPrefsName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBooleanKeyValuePrefs(String key) {
        if (!isValidTransaction()) return false;
        SharedPreferences prefs = getContext().getSharedPreferences(getPrefsName(),
                Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

}
