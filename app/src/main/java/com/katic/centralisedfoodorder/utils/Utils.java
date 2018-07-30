package com.katic.centralisedfoodorder.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.ui.signin.SignInActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Utils {

    static String ISO_8601_PATTERN = "yyyy-MM-dd\'T\'HH:mm:ssZ";
    static String DISPLAY_PATTERN = "dd.MM.yyyy. HH:mm";

    public static String getISO8601Date(long timeInMilliseconds) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ssZ", Locale.getDefault());
            Date d = new Date(timeInMilliseconds);
            return df.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NA";
    }

    public static String getDisplayDate(String ISO_8601_DATE){
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_PATTERN, Locale.getDefault());
        try {
            Date date = sdf.parse(ISO_8601_DATE);
            SimpleDateFormat ddf = new SimpleDateFormat(DISPLAY_PATTERN, Locale.getDefault());
            return ddf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ISO_8601_DATE;
        }
    }

    public static void dialNumber(Context ctx, String phone) {
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:" + phone));
        ctx.startActivity(i);
    }

    public static void startMaps(Context ctx, String address) {
        Uri uri = Uri.parse("http://maps.google.com/maps?q=" + address);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ctx.startActivity(intent);
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, int count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(String.format(Locale.getDefault(), "%d", count));
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public static Drawable buildCounterDrawable(Context context, int count) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
            view.setBackgroundResource(R.drawable.empty_cart);
        } else {
            TextView textView = view.findViewById(R.id.count);
            textView.setText(String.format(Locale.getDefault(), "%d", count));
            view.setBackgroundResource(R.drawable.ic_full_cart);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static void signOut(Activity context){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
        context.finishAffinity();
    }

}
