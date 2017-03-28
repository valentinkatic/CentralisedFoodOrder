package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;
import java.util.List;

public class CartItem {
    private String title;
    private String ingredients;
    private float price;
    private String type;
    private List<Pizza> pizza = new ArrayList<>();
    private int quantity = 0;

    public CartItem() {
    }

    public CartItem(String title, String ingredients, float price, String type, int quantity) {
        this.title = title;
        this.ingredients = ingredients;
        this.price = price;
        this.type = type;
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Pizza> getPizza() {
        return pizza;
    }

    public void setPizza(List<Pizza> pizza) {
        this.pizza = pizza;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
