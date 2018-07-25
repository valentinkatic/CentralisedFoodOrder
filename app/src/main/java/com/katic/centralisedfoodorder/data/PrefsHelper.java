package com.katic.centralisedfoodorder.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton class for dealing with Shared Preferences and local storage. This class is package
 * private so all communications of this class happens via {@link DataHandler}
 */
class PrefsHelper {

    private static final String PREFERENCES_NAME = "app_prefs";

    private SharedPreferences mPrefs;

    private static PrefsHelper sInstance = null;

    private PrefsHelper(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static PrefsHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PrefsHelper.class) {
                if (sInstance == null) {
                    sInstance = new PrefsHelper(context);
                }
            }
        }
        return sInstance;
    }

    private static final String KEY_USER_NAME = "key_user_name";
    private String mUserName;

    private static final String KEY_USER_EMAIL = "key_user_email";
    private String mUserEmail;

    public void setUserName(String userName) {
        this.mUserName = userName;
        mPrefs.edit().putString(KEY_USER_NAME, userName).apply();
    }

    public String getUserName() {
        if (mUserName == null) {
            mUserName = mPrefs.getString(KEY_USER_NAME, null);
        }
        return mUserName;
    }

    public void setUserEmail(String email) {
        this.mUserEmail = email;
        mPrefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public String getUserEmail() {
        if (mUserEmail == null) {
            mUserEmail = mPrefs.getString(KEY_USER_EMAIL, null);
        }
        return mUserEmail;
    }

    public void destroy() {
        mPrefs.edit().clear().apply();
        mUserEmail = null;
        mUserName = null;
    }

}
