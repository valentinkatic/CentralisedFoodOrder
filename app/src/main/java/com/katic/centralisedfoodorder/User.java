package com.katic.centralisedfoodorder;

import java.util.ArrayList;

public class User {

    public String email;
    public String address;
    public String phoneNumber;
    public ArrayList<Long> bookmarks;

    public User(String email, String address, String phoneNumber) {
        this.email = email;
        this.address = address;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }


}
