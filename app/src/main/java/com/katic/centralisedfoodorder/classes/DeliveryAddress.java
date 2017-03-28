package com.katic.centralisedfoodorder.classes;

public class DeliveryAddress {

    private String lastName;
    private String street;
    private String streetNumber;
    private String city;
    private String floor;
    private String apartmentNumber;
    private String phoneNumber;
    private boolean defaultAddress;

    public DeliveryAddress() {
    }

    public DeliveryAddress(String lastName, String street, String streetNumber, String city, String floor, String apartmentNumber, String phoneNumber) {
        this.lastName = lastName;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.floor = floor;
        this.apartmentNumber = apartmentNumber;
        this.phoneNumber = phoneNumber;
        this.defaultAddress = true;
    }

    public DeliveryAddress(DeliveryAddress da) {
        this.lastName = da.lastName;
        this.street = da.street;
        this.streetNumber = da.streetNumber;
        this.floor = da.floor;
        this.apartmentNumber = da.apartmentNumber;
        this.phoneNumber = da.phoneNumber;
    }

    public DeliveryAddress(String lastName, String street, String streetNumber, String city, String phoneNumber) {
        this.lastName = lastName;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.defaultAddress = true;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
