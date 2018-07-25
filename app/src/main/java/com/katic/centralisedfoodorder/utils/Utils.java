package com.katic.centralisedfoodorder.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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

    public static void dialNumber(Context ctx, String phone) {
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:" + phone));
        ctx.startActivity(i);
    }

}
