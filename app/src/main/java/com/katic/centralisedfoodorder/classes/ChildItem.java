package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;
import java.util.List;

public class ChildItem {
    public String title;
    public String ingredients;
    public float price;
    public List<Pizza> pizza = new ArrayList<>();
    public String type;
    public boolean clicked = false;
    public boolean addedToCart = false;

    public ChildItem() {
    }

    public ChildItem(String title, String ingredients, float price, String type) {
        this.title = title;
        this.ingredients = ingredients;
        this.price = price;
        this.type = type;
    }

    public ChildItem(CartItem cart){
        this.title = cart.title;
        this.ingredients = cart.ingredients;
        this.price = cart.price;
        this.type = cart.type;
        this.pizza = cart.pizza;
    }

}
