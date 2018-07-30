package com.katic.centralisedfoodorder.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.katic.centralisedfoodorder.data.models.DeliveryAddress;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private static final String KEY_USER_DELIVERY_ADDRESSES = "key_user_delivery_addresses";
    private List<DeliveryAddress> addresses;

    private static final String KEY_USER_PHONE_TOKEN = "key_user_phone_token";
    private String mUserPhoneToken;

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

    public void setUserAddresses(List<DeliveryAddress> addresses){
        if (addresses == null){
            addresses = new ArrayList<>();
        }
        this.addresses = addresses;
        Gson gson = new Gson();
        mPrefs.edit().putString(KEY_USER_DELIVERY_ADDRESSES, gson.toJson(addresses)).apply();
    }

    public List<DeliveryAddress> getUserAddresses(){
        if (addresses == null){
            Gson gson = new Gson();
            addresses = gson.fromJson(mPrefs.getString(KEY_USER_DELIVERY_ADDRESSES, "[]"), new TypeToken<ArrayList<DeliveryAddress>>(){}.getType());
        }
        return addresses;
    }

    public void setUserPhoneToken(String phoneToken) {
        this.mUserPhoneToken = phoneToken;
        mPrefs.edit().putString(KEY_USER_PHONE_TOKEN, phoneToken).apply();
    }

    public String getUserPhoneToken() {
        if (mUserPhoneToken == null) {
            mUserPhoneToken = mPrefs.getString(KEY_USER_PHONE_TOKEN, null);
        }
        return mUserPhoneToken;
    }

    public void destroy() {
        mPrefs.edit().clear().apply();
        mUserEmail = null;
        mUserName = null;
    }

}
