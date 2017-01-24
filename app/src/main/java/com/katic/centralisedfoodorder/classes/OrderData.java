package com.katic.centralisedfoodorder.classes;

import java.util.List;

public class OrderData{

    public String phoneToken;
    public List<ChildItem> orderItems;
    public DeliveryAddress deliveryAddress;
    public boolean isDelivery;
    public String lastNamePickup;
    public String comment;

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

}
