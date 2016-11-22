package com.katic.centralisedfoodorder;

public class Restaurant {
    public String name;
    public String address;
    public int photoId;
    public boolean bookmarked;

    public Restaurant(String name, String address, int photoId) {
        this.name = name;
        this.address = address;
        this.photoId = photoId;
        this.bookmarked = false;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }
}


