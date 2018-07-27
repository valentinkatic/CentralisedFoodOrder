package com.katic.centralisedfoodorder.data.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cart {

    @Expose
    @SerializedName("restaurant_key")
    private String mRestaurantKey;

    @Expose
    @SerializedName("cart_items")
    private List<CartItem> mCartItems;

    @Expose
    @SerializedName("delivery_address")
    private DeliveryAddress mDeliveryAddress;

    @Expose
    @SerializedName("delivery")
    private boolean mDelivery;

    @Expose
    @SerializedName("last_name_pickup")
    private String mLastNamePickup;

    @Expose
    @SerializedName("phone_token")
    private String mPhoneToken;

    @Expose
    @SerializedName("restaurant_name")
    private String mRestaurantName;

    public Cart() {
    }

    @PropertyName("restaurant_key")
    public String getRestaurantKey() {
        return mRestaurantKey;
    }

    @PropertyName("restaurant_key")
    public void setRestaurantKey(String restaurantKey) {
        this.mRestaurantKey = restaurantKey;
    }

    @PropertyName("cart_items")
    public List<CartItem> getCartItems() {
        return mCartItems;
    }

    @PropertyName("cart_items")
    public void setCartItems(List<CartItem> cartItems) {
        this.mCartItems = cartItems;
    }

    @PropertyName("delivery_address")
    public DeliveryAddress getDeliveryAddress() {
        return mDeliveryAddress;
    }

    @PropertyName("delivery_address")
    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.mDeliveryAddress = deliveryAddress;
    }

    @PropertyName("delivery")
    public boolean isDelivery() {
        return mDelivery;
    }

    @PropertyName("delivery")
    public void setDelivery(boolean delivery) {
        this.mDelivery = delivery;
    }

    @PropertyName("last_name_pickup")
    public String getLastNamePickup() {
        return mLastNamePickup;
    }

    @PropertyName("last_name_pickup")
    public void setLastNamePickup(String lastNamePickup) {
        this.mLastNamePickup = lastNamePickup;
    }

    @PropertyName("phone_token")
    public String getPhoneToken() {
        return mPhoneToken;
    }

    @PropertyName("phone_token")
    public void setPhoneToken(String phoneToken) {
        this.mPhoneToken = phoneToken;
    }

    @PropertyName("restaurant_name")
    public String getRestaurantName() {
        return mRestaurantName;
    }

    @PropertyName("restaurant_name")
    public void setRestaurantName(String restaurantName) {
        this.mRestaurantName = restaurantName;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "restaurantKey='" + mRestaurantKey + '\'' +
                ", cartItems=" + mCartItems +
                ", deliveryAddress=" + mDeliveryAddress +
                ", delivery=" + mDelivery +
                ", lastNamePickup='" + mLastNamePickup + '\'' +
                ", phoneToken='" + mPhoneToken + '\'' +
                ", restaurantName='" + mRestaurantName + '\'' +
                '}';
    }
}
