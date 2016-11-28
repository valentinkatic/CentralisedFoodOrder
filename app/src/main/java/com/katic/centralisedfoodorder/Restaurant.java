package com.katic.centralisedfoodorder;

public class Restaurant {
    public String restaurantID;
    public String name;
    public String address;
    public String photoID;
    public String bookmarked;

    public Restaurant(){}

    public Restaurant(String restaurantID, String name, String address, String photoID, String bookmarked) {
        this.restaurantID = restaurantID;
        this.name = name;
        this.address = address;
        this.photoID = photoID;
        this.bookmarked = bookmarked;
    }

    public Restaurant(Restaurant rest) {
        this.restaurantID = rest.getRestaurantID();
        this.name = rest.getName();
        this.address = rest.getAddress();
        this.photoID = rest.getPhotoID();
        this.bookmarked = rest.isBookmarked();
    }

    public void setBookmarked(String bookmarked) {
        this.bookmarked = "true";
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhotoID() {
        return photoID;
    }

    public String isBookmarked() {
        return bookmarked;
    }
}


