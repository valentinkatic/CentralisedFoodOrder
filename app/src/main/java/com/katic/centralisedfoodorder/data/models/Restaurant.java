package com.katic.centralisedfoodorder.data.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Restaurant {

    @Expose
    @SerializedName("name")
    private String mName;

    @Expose
    @SerializedName("address")
    private String mAddress;

    @Expose
    @SerializedName("photo_url")
    private String mPhotoURL;

    @Exclude
    private boolean mBookmarked;

    @Expose
    @SerializedName("phone")
    private String mPhone;

    @Expose
    @SerializedName("city")
    private String mCity;

    @Expose
    @SerializedName("food_types")
    private List<String> mFoodTypes = new ArrayList<>();

    @Expose
    @SerializedName("food_list")
    private Map<String, Map<String, Food>> mFoodList;

    @Exclude
    private List<String> mFoodTypeList;

    @Exclude
    private String mKey;

    public Restaurant(){

    }

    @PropertyName("name")
    public String getName() {
        return mName;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.mName = name;
    }

    @PropertyName("address")
    public String getAddress() {
        return mAddress;
    }

    @PropertyName("address")
    public void setAddress(String address) {
        this.mAddress = address;
    }

    @PropertyName("photo_url")
    public String getPhotoURL() {
        return mPhotoURL;
    }

    @PropertyName("photo_url")
    public void setPhotoURL(String photoID) {
        this.mPhotoURL = photoID;
    }

    @Exclude
    public boolean isBookmarked() {
        return mBookmarked;
    }

    @Exclude
    public void setBookmarked(boolean bookmarked) {
        this.mBookmarked = bookmarked;
    }

    @PropertyName("phone")
    public String getPhone() {
        return mPhone;
    }

    @PropertyName("phone")
    public void setPhone(String phone) {
        this.mPhone = phone;
    }

    @PropertyName("city")
    public String getCity() {
        return mCity;
    }

    @PropertyName("city")
    public void setCity(String city) {
        this.mCity = city;
    }

    @PropertyName("food_types")
    public List<String> getFoodTypes() {
        return mFoodTypes;
    }

    @PropertyName("food_types")
    public void setFoodTypes(List<String> foodTypes) {
        this.mFoodTypes = foodTypes;
    }

    @PropertyName("food_list")
    public Map<String, Map<String, Food>> getFoodList() {
        return mFoodList;
    }

    @PropertyName("food_list")
    public void setFoodList(Map<String, Map<String, Food>> foodList) {
        this.mFoodList = foodList;
    }

    @Exclude
    public List<String> getFoodTypeList() {
        return mFoodTypeList;
    }

    @Exclude
    public void setFoodTypeList(List<String> foodTypeList) {
        this.mFoodTypeList = foodTypeList;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        this.mKey = key;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "name='" + mName + '\'' +
                ", address='" + mAddress + '\'' +
                ", photoURL=" + mPhotoURL +
                ", bookmarked=" + mBookmarked +
                ", phone='" + mPhone + '\'' +
                ", city='" + mCity + '\'' +
                ", foodTypes=" + mFoodTypes +
                ", foodList=" + mFoodList +
                ", foodTypeList=" + mFoodTypeList +
                ", key='" + mKey + '\'' +
                '}';
    }
}


