package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;

public class User {

    public String email;
    public ArrayList<DelieveryAddress> delieveryAddress = new ArrayList<>();
    public ArrayList<Long> bookmarks;

    public User(String email, String lastName, String street, String streetNumber, String city, String phoneNumber) {
        this.email = email;
        this.delieveryAddress.add(new DelieveryAddress(lastName, street, streetNumber, city, phoneNumber));
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

}
