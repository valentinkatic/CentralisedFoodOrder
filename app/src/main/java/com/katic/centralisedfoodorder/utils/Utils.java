package com.katic.centralisedfoodorder.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String getDisplayDate(long timeInMilliseconds) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ssZ", Locale.getDefault());
            Date d = new Date(timeInMilliseconds);
            return df.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NA";
    }

}
