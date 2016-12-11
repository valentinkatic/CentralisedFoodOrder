package com.katic.centralisedfoodorder.classes;

public class CartItem {
    public String title;
    public String ingredients;
    public String price;

    public CartItem() {
    }

    public CartItem(String title, String ingredients, String price) {
        this.title = title;
        this.ingredients = ingredients;
        this.price = price;
    }

}
