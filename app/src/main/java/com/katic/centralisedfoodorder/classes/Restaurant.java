package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;

public class Restaurant {
    public long restaurantID;
    public String name;
    public String address;
    public long photoID;
    public boolean bookmarked;
    public String phone;
    public String city;
    public ArrayList<String> food_type = new ArrayList<>();

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

}


