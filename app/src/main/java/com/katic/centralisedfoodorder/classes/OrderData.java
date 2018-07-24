package com.katic.centralisedfoodorder.classes;

import java.util.List;

public class OrderData{

    private String phoneToken;
    private List<ChildItem> orderItems;
    private DeliveryAddress deliveryAddress;
    private boolean isDelivery;
    private String lastNamePickup;
    private String comment;
    private String orderTime;

    public OrderData(String phoneToken, List<ChildItem> orderItems, DeliveryAddress deliveryAddress, boolean isDelivery, String comment) {
        this.phoneToken = phoneToken;
        this.orderItems = orderItems;
        this.deliveryAddress = deliveryAddress;
        this.isDelivery = isDelivery;
        this.comment = comment;
    }

    public OrderData(String phoneToken, List<ChildItem> orderItems, boolean isDelivery, String lastNamePickup, String comment) {
        this.phoneToken = phoneToken;
        this.orderItems = orderItems;
        this.isDelivery = isDelivery;
        this.lastNamePickup = lastNamePickup;
        this.comment = comment;
    }

    public OrderData() {
    }

    public String getPhoneToken() {
        return phoneToken;
    }

    public void setPhoneToken(String phoneToken) {
        this.phoneToken = phoneToken;
    }

    public List<ChildItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<ChildItem> orderItems) {
        this.orderItems = orderItems;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public void setDelivery(boolean delivery) {
        isDelivery = delivery;
    }

    public String getLastNamePickup() {
        return lastNamePickup;
    }

    public void setLastNamePickup(String lastNamePickup) {
        this.lastNamePickup = lastNamePickup;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
