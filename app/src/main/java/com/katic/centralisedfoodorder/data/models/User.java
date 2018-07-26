package com.katic.centralisedfoodorder.data.models;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {

    @Expose
    @SerializedName("email")
    private String mEmail;

    @Expose
    @SerializedName("name")
    private String mName;

    @Expose
    @SerializedName("phone_token")
    private String mPhoneToken;

    @Expose
    @SerializedName("delivery_addresses")
    private List<DeliveryAddress> mDeliveryAddresses = new ArrayList<>();

    @Expose
    @SerializedName("bookmarks")
    private Map<String, Boolean> mBookmarks;

    @Expose
    @SerializedName("cart")
    private Cart mCart;

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

    @PropertyName("phone_token")
    public String getPhoneToken() {
        return mPhoneToken;
    }

    @PropertyName("phone_token")
    public void setPhoneToken(String phoneToken) {
        this.mPhoneToken = phoneToken;
    }

    @PropertyName("delivery_addresses")
    public List<DeliveryAddress> getDeliveryAddresses() {
        return mDeliveryAddresses;
    }

    @PropertyName("delivery_addresses")
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

    @PropertyName("cart")
    public Cart getCart() {
        return mCart;
    }

    @PropertyName("cart")
    public void setCart(Cart cart) {
        this.mCart = cart;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + mEmail + '\'' +
                ", name='" + mName + '\'' +
                ", phoneToken='" + mPhoneToken + '\'' +
                ", deliveryAddresses=" + mDeliveryAddresses +
                ", bookmarks=" + mBookmarks +
                ", cart=" + mCart +
                '}';
    }
}
