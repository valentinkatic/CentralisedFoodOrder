package com.katic.centralisedfoodorder.classes;

public class Pizza {
    public String size;
    public float price;
    public boolean addedToCart = false;

    public Pizza() {
    }

    public Pizza(String size, float price) {
        this.size = size;
        this.price = price;
    }

}
