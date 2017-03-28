package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;

public class Restaurant {
    private long restaurantID;
    private String name;
    private String address;
    private long photoID;
    private boolean bookmarked;
    private String phone;
    private String city;
    private ArrayList<String> food_type = new ArrayList<>();

    public Restaurant(){}

    public Restaurant(long restaurantID, String name, String address, String city, long photoID, boolean bookmarked, String phone, ArrayList<String> food_type) {
        this.restaurantID = restaurantID;
        this.name = name;
        this.address = address;
        this.city = city;
        this.photoID = photoID;
        this.bookmarked = bookmarked;
        this.phone = phone;
        this.food_type = food_type;
    }

    public Restaurant(Restaurant rest) {
        this.restaurantID = rest.restaurantID;
        this.name = rest.name;
        this.address = rest.address;
        this.city = rest.city;
        this.photoID = rest.photoID;
        this.bookmarked = rest.bookmarked;
        this.phone = rest.phone;
        this.food_type = rest.food_type;
    }

    public long getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(long restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getPhotoID() {
        return photoID;
    }

    public void setPhotoID(long photoID) {
        this.photoID = photoID;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ArrayList<String> getFood_type() {
        return food_type;
    }

    public void setFood_type(ArrayList<String> food_type) {
        this.food_type = food_type;
    }
}


