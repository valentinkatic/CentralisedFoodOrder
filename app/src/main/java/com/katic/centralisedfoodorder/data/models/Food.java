package com.katic.centralisedfoodorder.data.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.katic.centralisedfoodorder.classes.CartItem;
import com.katic.centralisedfoodorder.classes.Pizza;

import java.util.ArrayList;
import java.util.List;

public class Food {

    @Exclude
    private String mTitle;

    @Expose
    @SerializedName("ingredients")
    private String mIngredients;

    @Expose
    @SerializedName("price")
    private float mPrice;

    @Expose
    @SerializedName("pizza")
    private List<Pizza> mPizza = new ArrayList<>();

    @Exclude
    private boolean mClicked = false;

    @Exclude
    private boolean mAddedToCart = false;

    @Exclude
    private int mQuantity = 0;

    public Food() {
    }

    @Exclude
    public String getTitle() {
        return mTitle;
    }

    @Exclude
    public void setTitle(String title) {
        this.mTitle = title;
    }

    @PropertyName("ingredients")
    public String getIngredients() {
        return mIngredients;
    }

    @PropertyName("ingredients")
    public void setIngredients(String ingredients) {
        this.mIngredients = ingredients;
    }

    @PropertyName("price")
    public float getPrice() {
        return mPrice;
    }

    @PropertyName("price")
    public void setPrice(float price) {
        this.mPrice = price;
    }

    @PropertyName("pizza")
    public List<Pizza> getPizza() {
        return mPizza;
    }

    @PropertyName("pizza")
    public void setPizza(List<Pizza> pizza) {
        this.mPizza = pizza;
    }

    @Exclude
    public boolean isClicked() {
        return mClicked;
    }

    @Exclude
    public void setClicked(boolean clicked) {
        this.mClicked = clicked;
    }

    @Exclude
    public boolean isAddedToCart() {
        return mAddedToCart;
    }

    @Exclude
    public void setAddedToCart(boolean addedToCart) {
        this.mAddedToCart = addedToCart;
    }

    @Exclude
    public int getQuantity() {
        return mQuantity;
    }

    @Exclude
    public void setQuantity(int quantity) {
        this.mQuantity = quantity;
    }
}
