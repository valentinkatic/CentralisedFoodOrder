package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GroupItem {
    private String title;
    private List<ChildItem> items = new ArrayList<>();
    private boolean clickedGroup = false;
    private String orderTime;
    private String address;
    private String city;

    public GroupItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ChildItem> getItems() {
        return items;
    }

    public void setItems(List<ChildItem> items) {
        this.items = items;
    }

    public boolean isClickedGroup() {
        return clickedGroup;
    }

    public void setClickedGroup(boolean clickedGroup) {
        this.clickedGroup = clickedGroup;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
