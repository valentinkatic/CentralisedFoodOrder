package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;
import java.util.List;

public class ChildItem {
    private String title;
    private String ingredients;
    private float price;
    private List<Pizza> pizza = new ArrayList<>();
    private String type;
    private boolean clicked = false;
    private boolean addedToCart = false;
    private int quantity = 0;

    public ChildItem() {
    }

    public ChildItem(String title, String ingredients, float price, String type, int quantity) {
        this.title = title;
        this.ingredients = ingredients;
        this.price = price;
        this.type = type;
        this.quantity = quantity;
    }

    public ChildItem(CartItem cart){
        this.title = cart.getTitle();
        this.ingredients = cart.getIngredients();
        this.price = cart.getPrice();
        this.type = cart.getType();
        this.pizza = cart.getPizza();
        this.quantity = cart.getQuantity();
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

    public List<Pizza> getPizza() {
        return pizza;
    }

    public void setPizza(List<Pizza> pizza) {
        this.pizza = pizza;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public boolean isAddedToCart() {
        return addedToCart;
    }

    public void setAddedToCart(boolean addedToCart) {
        this.addedToCart = addedToCart;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
