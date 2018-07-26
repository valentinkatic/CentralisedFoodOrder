package com.katic.centralisedfoodorder.data.models;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CartItem {

    @Expose
    @SerializedName("mTitle")
    private String mTitle;

    @Expose
    @SerializedName("mType")
    private String mType;

    @Expose
    @SerializedName("amount")
    private int mAmount;

    @Expose
    @SerializedName("price")
    private float mPrice;

    @Expose
    @SerializedName("size")
    private String mSize;

    public CartItem() {
    }

    @PropertyName("title")
    public String getTitle() {
        return mTitle;
    }

    @PropertyName("title")
    public void setTitle(String title) {
        this.mTitle = title;
    }

    @PropertyName("type")
    public String getType() {
        return mType;
    }

    @PropertyName("type")
    public void setType(String type) {
        this.mType = type;
    }

    @PropertyName("amount")
    public int getAmount() {
        return mAmount;
    }

    @PropertyName("amount")
    public void setAmount(int amount) {
        this.mAmount = amount;
    }

    @PropertyName("price")
    public float getPrice() {
        return mPrice;
    }

    @PropertyName("price")
    public void setPrice(float price) {
        this.mPrice = price;
    }

    @PropertyName("size")
    public String getSize() {
        return mSize;
    }

    @PropertyName("size")
    public void setSize(String size) {
        this.mSize = size;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "title='" + mTitle + '\'' +
                ", type='" + mType + '\'' +
                ", amount=" + mAmount +
                ", price=" + mPrice +
                ", size='" + mSize + '\'' +
                '}';
    }
}
