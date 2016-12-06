package com.katic.centralisedfoodorder.classes;

public class Cart {

    public long ID;
    public int markedFoodGroup;
    public int markedFoodChild;

    public Cart(long ID, int markedFoodGroup, int markedFoodChild) {
        this.ID = ID;
        this.markedFoodGroup = markedFoodGroup;
        this.markedFoodChild = markedFoodChild;
    }

    public Cart() {
    }

}
