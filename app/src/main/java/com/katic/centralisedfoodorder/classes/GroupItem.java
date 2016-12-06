package com.katic.centralisedfoodorder.classes;

import java.util.ArrayList;
import java.util.List;

public class GroupItem {
    public String title;
    public List<ChildItem> items = new ArrayList<>();
    public boolean clickedGroup = false;

    public GroupItem() {
    }

}
