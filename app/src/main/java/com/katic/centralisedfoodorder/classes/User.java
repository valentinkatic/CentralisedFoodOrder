package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;

public class User {

    public String email;
    public ArrayList<DeliveryAddress> deliveryAddress = new ArrayList<>();
    public ArrayList<Long> bookmarks;

    public User(String email, String lastName, String street, String streetNumber, String city, String phoneNumber) {
        this.email = email;
        this.deliveryAddress.add(new DeliveryAddress(lastName, street, streetNumber, city, phoneNumber));
    }

    public User() {
    }

}
