package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GroupItem {
    public String title;
    public List<ChildItem> items = new ArrayList<>();
    public Boolean clickedGroup = false;
    public String orderTime;
    public String address;
    public String city;

    public GroupItem() {
    }

}
