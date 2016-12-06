package com.katic.centralisedfoodorder.classes;

public class Restaurant {
    public long restaurantID;
    public String name;
    public String address;
    public long photoID;
    public boolean bookmarked;

    public Restaurant(){}

    public Restaurant(long restaurantID, String name, String address, long photoID, boolean bookmarked) {
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

