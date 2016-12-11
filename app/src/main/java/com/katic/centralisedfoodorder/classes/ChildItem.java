package com.katic.centralisedfoodorder.classes;

public class ChildItem {
    public String title;
    public String ingredients;
    public String price;
    public boolean clicked = false;
    public boolean addedToCart = false;

    public ChildItem() {
    }

    public ChildItem(String title, String ingredients) {
        this.title = title;
        this.ingredients = ingredients;
    }

    public ChildItem(CartItem cart){
        this.title = cart.title;
        this.ingredients = cart.ingredients;
        this.price = cart.price;
    }

}
