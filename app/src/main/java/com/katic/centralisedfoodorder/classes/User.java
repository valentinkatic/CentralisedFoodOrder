package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;

public class User {

    public String email;
    public String address;
    public String streetNumber;
    public String phoneNumber;
    public ArrayList<Long> bookmarks;

    public User(String email, String address, String streetNumber, String phoneNumber) {
        this.email = email;
        this.address = address;
        this.streetNumber = streetNumber;
        this.phoneNumber = phoneNumber;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


}
