package com.katic.centralisedfoodorder.classes;

public class Cart {

    public String ID;
    public String markedFoodGroup;
    public String markedFoodChild;

    public Cart(String ID, String markedFoodGroup, String markedFoodChild) {
        this.ID = ID;
        this.markedFoodGroup = markedFoodGroup;
        this.markedFoodChild = markedFoodChild;
    }

    public Cart() {
    }

}
