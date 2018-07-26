package com.katic.centralisedfoodorder.data.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pizza {

    @Expose
    @SerializedName("size")
    private String mSize;

    @Expose
    @SerializedName("price")
    private float mPrice;

    @Exclude
    private boolean mAddedToCart;

    @Exclude
    private boolean mChecked;

    @Exclude
    private int mAmount;

    public Pizza() {
    }

    @PropertyName("size")
    public String getSize() {
        return mSize;
    }

    @PropertyName("size")
    public void setSize(String size) {
        this.mSize = size;
    }

    @PropertyName("price")
    public float getPrice() {
        return mPrice;
    }

    @PropertyName("price")
    public void setPrice(float price) {
        this.mPrice = price;
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
    public boolean isChecked() {
        return mChecked;
    }

    @Exclude
    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    @Exclude
    public int getAmount() {
        return mAmount;
    }

    @Exclude
    public void setAmount(int amount) {
        this.mAmount = amount;
    }
}
