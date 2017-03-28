package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;

public class User {

    private String email;
    private ArrayList<DeliveryAddress> deliveryAddress = new ArrayList<>();
    private ArrayList<Long> bookmarks;

    public User(String email, String lastName, String street, String streetNumber, String city, String phoneNumber) {
        this.email = email;
        this.deliveryAddress.add(new DeliveryAddress(lastName, street, streetNumber, city, phoneNumber));
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<DeliveryAddress> getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(ArrayList<DeliveryAddress> deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public ArrayList<Long> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(ArrayList<Long> bookmarks) {
        this.bookmarks = bookmarks;
    }

}
