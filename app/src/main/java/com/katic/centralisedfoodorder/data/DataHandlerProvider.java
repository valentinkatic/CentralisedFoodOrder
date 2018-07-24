package com.katic.centralisedfoodorder.data;

public class DataHandlerProvider {

    public static DataHandler provide() {
        return AppDataHandler.getInstance();
    }

}
