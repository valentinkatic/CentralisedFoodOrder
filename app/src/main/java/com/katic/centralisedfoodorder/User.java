package com.katic.centralisedfoodorder;

public class User {

    public String email;
    public String address;
    public String phoneNumber;
    public boolean anon;

    public User(String email, String address, String phoneNumber, boolean anon) {
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.anon = anon;
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

    public boolean isAnon() {
        return anon;
    }
}
