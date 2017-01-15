package com.katic.centralisedfoodorder.classes;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Restaurant {
    public long restaurantID;
    public String name;
    public String address;
    public long photoID;
    public boolean bookmarked;
    public ArrayList<String> food_type = new ArrayList<>();

    public Restaurant(){}

    public Restaurant(long restaurantID, String name, String address, long photoID, boolean bookmarked, ArrayList<String> food_type) {
        this.restaurantID = restaurantID;
        this.name = name;
        this.address = address;
        this.photoID = photoID;
        this.bookmarked = bookmarked;
        this.food_type = food_type;
    }

    public Restaurant(Restaurant rest) {
        this.restaurantID = rest.getRestaurantID();
        this.name = rest.getName();
        this.address = rest.getAddress();
        this.photoID = rest.getPhotoID();
        this.bookmarked = rest.isBookmarked();
        this.food_type = rest.food_type;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public long getRestaurantID() {
        return restaurantID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public long getPhotoID() {
        return photoID;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

}


