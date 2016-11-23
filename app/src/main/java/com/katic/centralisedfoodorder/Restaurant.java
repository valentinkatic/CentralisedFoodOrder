package com.katic.centralisedfoodorder;

public class Restaurant {
    int restaurantID;
    public String name;
    public String address;
    public int photoId;
    public boolean bookmarked;

    public Restaurant(int restaurantID, String name, String address, int photoId) {
        this.restaurantID = restaurantID;
        this.name = name;
        this.address = address;
        this.photoId = photoId;
        this.bookmarked = false;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPhotoId() {
        return photoId;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }
}


