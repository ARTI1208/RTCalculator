package ru.art2000.calculator;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Locale;

import androidx.annotation.NonNull;

public class Helper {

    private static double ratio(Context ctx){
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return ((double) metrics.heightPixels / (double) metrics.widthPixels);
    }

    public static boolean isLongScreen(Context ctx){
        return ratio(ctx) > 1.8;
    }

    public static int dip2px(Context ctx, float dip){
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
    }

    public static int sp2px(Context ctx, float dip){
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dip, metrics);
    }

    @NonNull
    public static Resources getLocalizedResources(Context context, Locale desiredLocale) {
        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(desiredLocale);
        Context localizedContext = context.createConfigurationContext(conf);
        return localizedContext.getResources();
    }

}
