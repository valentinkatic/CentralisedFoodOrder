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
    @SerializedName("restaurantID")
    private long mRestaurantID;

    @Exclude
    private String mName;

    @Expose
    @SerializedName("address")
    private String mAddress;

    @Expose
    @SerializedName("photoID")
    private long mPhotoID;

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

    public Restaurant(){

    }

    @PropertyName("restaurantID")
    public long getRestaurantID() {
        return mRestaurantID;
    }

    @PropertyName("restaurantID")
    public void setRestaurantID(long restaurantID) {
        this.mRestaurantID = restaurantID;
    }

    @Exclude
    public String getName() {
        return mName;
    }

    @Exclude
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

    @PropertyName("photoID")
    public long getPhotoID() {
        return mPhotoID;
    }

    @PropertyName("photoID")
    public void setPhotoID(long photoID) {
        this.mPhotoID = photoID;
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

    public Map<String, Map<String, Food>> getFoodList() {
        return mFoodList;
    }

    public void setFoodList(Map<String, Map<String, Food>> foodList) {
        this.mFoodList = foodList;
    }

}


