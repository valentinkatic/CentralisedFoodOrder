package com.katic.centralisedfoodorder.data.models;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Float.compare(cartItem.mPrice, mPrice) == 0 &&
                Objects.equals(mTitle, cartItem.mTitle) &&
                Objects.equals(mType, cartItem.mType) &&
                Objects.equals(mSize, cartItem.mSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTitle, mType, mPrice, mSize);
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
