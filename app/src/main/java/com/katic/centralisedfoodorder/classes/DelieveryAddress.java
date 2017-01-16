package com.katic.centralisedfoodorder.classes;

public class DelieveryAddress {

    public String lastName;
    public String street;
    public String streetNumber;
    public String city;
    public String floor;
    public String apartmentNumber;
    public String phoneNumber;
    public boolean defaultAddress;

    public DelieveryAddress() {
    }

    public DelieveryAddress(String lastName, String street, String streetNumber, String city, String floor, String apartmentNumber, String phoneNumber) {
        this.lastName = lastName;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.floor = floor;
        this.apartmentNumber = apartmentNumber;
        this.phoneNumber = phoneNumber;
        this.defaultAddress = true;
    }

    public DelieveryAddress(DelieveryAddress da) {
        this.lastName = da.lastName;
        this.street = da.street;
        this.streetNumber = da.streetNumber;
        this.floor = da.floor;
        this.apartmentNumber = da.apartmentNumber;
        this.phoneNumber = da.phoneNumber;
    }

    public DelieveryAddress(String lastName, String street, String streetNumber, String city, String phoneNumber) {
        this.lastName = lastName;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.defaultAddress = true;
    }
}
