package com.katic.centralisedfoodorder.data.models;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.katic.centralisedfoodorder.classes.DeliveryAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class User {

    @Expose
    @SerializedName("email")
    private String mEmail;

    @Expose
    @SerializedName("name")
    private String mName;

    @Expose
    @SerializedName("phoneToken")
    private String mPhoneToken;

    @Expose
    @SerializedName("deliveryAddresses")
    private List<DeliveryAddress> mDeliveryAddresses = new ArrayList<>();

    @Expose
    @SerializedName("bookmarks")
    private Map<String, Boolean> mBookmarks;

    public User() {
    }

    @PropertyName("email")
    public String getEmail() {
        return mEmail;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.mEmail = email;
    }

    @PropertyName("name")
    public String getName() {
        return mName;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.mName = name;
    }

    @PropertyName("phoneToken")
    public String getPhoneToken() {
        return mPhoneToken;
    }

    @PropertyName("phoneToken")
    public void setPhoneToken(String phoneToken) {
        this.mPhoneToken = phoneToken;
    }

    @PropertyName("deliveryAddresses")
    public List<DeliveryAddress> getDeliveryAddresses() {
        return mDeliveryAddresses;
    }

    @PropertyName("deliveryAddresses")
    public void setDeliveryAddresses(List<DeliveryAddress> deliveryAddresses) {
        this.mDeliveryAddresses = deliveryAddresses;
    }

    @PropertyName("bookmarks")
    public Map<String, Boolean> getBookmarks() {
        return mBookmarks;
    }

    @PropertyName("bookmarks")
    public void setBookmarks(Map<String, Boolean> mBookmarks) {
        this.mBookmarks = mBookmarks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mEmail, mPhoneToken);
    }
}
