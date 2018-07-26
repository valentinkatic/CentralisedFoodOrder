package com.katic.centralisedfoodorder.data.models;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryAddress {

    @Expose
    @SerializedName("last_name")
    private String mLastName;

    @Expose
    @SerializedName("street")
    private String mStreet;

    @Expose
    @SerializedName("street_number")
    private String mStreetNumber;

    @Expose
    @SerializedName("city")
    private String mCity;

    @Expose
    @SerializedName("floor")
    private String mFloor;

    @Expose
    @SerializedName("phone_number")
    private String mPhoneNumber;

    @Expose
    @SerializedName("default")
    private boolean mDefault;

    public DeliveryAddress() {
    }

    @PropertyName("last_name")
    public String getLastName() {
        return mLastName;
    }

    @PropertyName("last_name")
    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    @PropertyName("street")
    public String getStreet() {
        return mStreet;
    }

    @PropertyName("street")
    public void setStreet(String street) {
        this.mStreet = street;
    }

    @PropertyName("street_number")
    public String getStreetNumber() {
        return mStreetNumber;
    }

    @PropertyName("street_number")
    public void setStreetNumber(String streetNumber) {
        this.mStreetNumber = streetNumber;
    }

    @PropertyName("city")
    public String getCity() {
        return mCity;
    }

    @PropertyName("city")
    public void setCity(String city) {
        this.mCity = city;
    }

    @PropertyName("floor")
    public String getFloor() {
        return mFloor;
    }

    @PropertyName("floor")
    public void setFloor(String floor) {
        this.mFloor = floor;
    }

    @PropertyName("phone_number")
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    @PropertyName("phone_number")
    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    @PropertyName("default")
    public boolean isDefault() {
        return mDefault;
    }

    @PropertyName("default")
    public void setDefault(boolean value) {
        this.mDefault = value;
    }

    @Override
    public String toString() {
        return "DeliveryAddress{" +
                "lastName='" + mLastName + '\'' +
                ", street='" + mStreet + '\'' +
                ", streetNumber='" + mStreetNumber + '\'' +
                ", city='" + mCity + '\'' +
                ", floor='" + mFloor + '\'' +
                ", phoneNumber='" + mPhoneNumber + '\'' +
                ", default=" + mDefault +
                '}';
    }
}
