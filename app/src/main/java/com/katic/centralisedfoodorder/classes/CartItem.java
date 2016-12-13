package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;
import java.util.List;

public class CartItem {
    public String title;
    public String ingredients;
    public float price;
    public String type;
    public List<Pizza> pizza = new ArrayList<>();

    public CartItem() {
    }

    public CartItem(String title, String ingredients, float price, String type) {
        this.title = title;
        this.ingredients = ingredients;
        this.price = price;
        this.type = type;
        this.pizza = pizza;
    }

}
