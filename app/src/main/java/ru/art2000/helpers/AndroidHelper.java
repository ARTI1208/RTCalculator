package ru.art2000.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.util.Locale;

public class AndroidHelper {

    private static double ratio(Context ctx) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return ((double) metrics.heightPixels / (double) metrics.widthPixels);
    }

    public static boolean isLongScreen(Context ctx) {
        return ratio(ctx) > 1.8;
    }

    public static int dip2px(Context ctx, float dip) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
    }

    public static int sp2px(Context ctx, float dip) {
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

    public static String getLocalizedString(Context context, Locale desiredLocale, @StringRes int resId) {
        return getLocalizedResources(context, desiredLocale).getString(resId);
    }

    @ColorInt
    public static int getColorAttribute(@NonNull Context context, @AttrRes int attribute) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attribute, typedValue, true);
        return ContextCompat.getColor(context, typedValue.resourceId);
    }

    public static Drawable getDrawableAttribute(@NonNull Context context, @AttrRes int attribute) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attribute, typedValue, true);
        return ContextCompat.getDrawable(context, typedValue.resourceId);
    }
}
