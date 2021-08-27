package com.quickzetuser.preferences;

import android.content.Context;

import com.preferences.BasePrefs;


/**
 * Created by Sunil kumar yadav on 13/3/18.
 */

public class AppPrefs extends BasePrefs {

    static final String Prefsname = "prefs_app";
    static String KEY_TERMS_CONDITION = "term_condition_visible";
    Context context;


    public AppPrefs (Context context) {
        this.context = context;
    }

    @Override
    public String getPrefsName () {
        return Prefsname;
    }

    @Override
    public Context getContext () {
        return context;
    }

    public void setTermsConditions (boolean data) {
        setBooleanKeyValuePrefs(KEY_TERMS_CONDITION, data);
    }

    public boolean getTermsConditions () {
        return getBooleanKeyValuePrefs(KEY_TERMS_CONDITION);
    }

   /* public void saveEventOptions (List<BookEventOptionModel> list) {
        if (list == null || list.size() == 0) {
            setStringKeyValuePrefs(KEY_BOOK_EVENT_OPTION, "[]");
        } else {
            setStringKeyValuePrefs(KEY_BOOK_EVENT_OPTION, new Gson().toJson(list));
        }

    }

    public List<BookEventOptionModel> getEventOptions () {
        String data = getStringKeyValuePrefs(KEY_BOOK_EVENT_OPTION, "[]");
        return new Gson().fromJson(data, new TypeToken<List<BookEventOptionModel>>() {
        }.getType());
    }

    public void saveTermsConditions (String data) {
        setStringKeyValuePrefs(KEY_TERMS_CONDITION, data);
    }

    public String getTermsConditions () {
        return getStringKeyValuePrefs(KEY_TERMS_CONDITION);
    }

    public void savePrivacyPolicy (String data) {
        setStringKeyValuePrefs(KEY_PRIVACY_POLICY, data);
    }

    public String getPrivacyPolicy () {
        return getStringKeyValuePrefs(KEY_PRIVACY_POLICY);
    }

    public void saveFaqData (List<FaqModel> list) {
        if (list == null || list.size() == 0) {
            setStringKeyValuePrefs(KEY_FAQ_DATA, "[]");
        } else {
            setStringKeyValuePrefs(KEY_FAQ_DATA, new Gson().toJson(list));
        }
    }

    public List<FaqModel> getFaqData () {
        String data = getStringKeyValuePrefs(KEY_FAQ_DATA, "[]");
        try {
            return new Gson().fromJson(data, new TypeToken<List<FaqModel>>() {
            }.getType());
        } catch (JsonSyntaxException e) {

        }
        return new ArrayList<>();
    }


    public void saveAboutUs (String data) {
        setStringKeyValuePrefs(KEY_ABOUT_US, data);
    }

    public String getAboutUs () {
        return getStringKeyValuePrefs(KEY_ABOUT_US);
    }*/
}
