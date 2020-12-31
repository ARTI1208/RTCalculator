package ru.art2000.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import ru.art2000.calculator.R;

public class PrefsHelper {

    private static SharedPreferences sSharedPreferences;
    private static int sAppTheme;
    private static int sDefaultTab;
    private static boolean sShouldSaveCurrencyConversion = false;
    private static String sUnitViewType;
    private static String sConversionCode;
    private static double sConversionValue;

    public static boolean isShouldSaveCurrencyConversion() {
        return sShouldSaveCurrencyConversion;
    }

    public static void setShouldSaveCurrencyConversion(boolean value) {
        sShouldSaveCurrencyConversion = value;
        sSharedPreferences.edit().putBoolean("save_currency_value", value).apply();
        if (!value)
            sSharedPreferences.edit()
                    .remove("last_conversion_code")
                    .remove("last_conversion_double")
                    .apply();
    }

    public static void putConversionValues(String from, double value) {
        sSharedPreferences.edit().putString("last_conversion_code", from).apply();
        putConversionDouble(value);
    }

    private static void putConversionDouble(double value) {
        putDouble("last_conversion_double", value);
    }

    @SuppressWarnings("SameParameterValue")
    private static void putDouble(final String key, final double value) {
        sSharedPreferences.edit().putLong(key, Double.doubleToRawLongBits(value)).apply();
    }

    @SuppressWarnings("SameParameterValue")
    private static double getDouble(final String key, final double defaultValue) {
        return Double.longBitsToDouble(sSharedPreferences.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static String getConversionCode() {
        return sConversionCode;
    }

    public static double getConversionValue() {
        return sConversionValue;
    }

    public static void initialSetup(Context mContext) {
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (sSharedPreferences.getBoolean("is_first_run", true))
            sSharedPreferences
                    .edit()
                    .putString("app_theme", "light")
                    .putString("unit_view", "simple")
                    .putBoolean("is_first_run", false)
                    .apply();
        switch (sSharedPreferences.getString("app_theme", "light")) {
            default:
            case "light":
                sAppTheme = R.style.RT_AppTheme_Light;
                break;
            case "dark":
                sAppTheme = R.style.RT_AppTheme_Dark;
                break;
            case "day_night":
                sAppTheme = R.style.RT_AppTheme_DayNight;
                break;
            case "system":
                sAppTheme = R.style.RT_AppTheme_System;
                break;
            case "battery":
                sAppTheme = R.style.RT_AppTheme_Battery;
                break;
        }
        sShouldSaveCurrencyConversion = sSharedPreferences.getBoolean("save_currency_value", false);
        sConversionCode = sSharedPreferences.getString("last_conversion_code", "USD");
        sConversionValue = getDouble("last_conversion_double", 1);
        switch (sSharedPreferences.getString("default_opened_tab", "calc_tab")) {
            default:
            case "calc_tab":
                sDefaultTab = R.id.navigation_calc;
                break;
            case "currency_tab":
                sDefaultTab = R.id.navigation_currency;
                break;
            case "unit_tab":
                sDefaultTab = R.id.navigation_unit;
                break;
            case "settings_tab":
                sDefaultTab = R.id.navigation_settings;
                break;
        }
        sUnitViewType = sSharedPreferences.getString("unit_view", "powerful");
    }

    public static String getUnitViewType() {
        return sUnitViewType;
    }

    public static void setUnitViewType(String view) {
        sUnitViewType = view;
        sSharedPreferences.edit().putString("unit_view", view).apply();
    }

    public static void setDefaultTab(Context context, int pos) {
        if (PrefsHelper.isSaveLastEnabled() && (pos != 3 || context.getResources().getBoolean(R.bool.show_settings_as_default_tab))) {
            String tab;
            switch (pos) {
                case 0:
                    tab = "currency_tab";
                    break;
                case 1:
                    tab = "calc_tab";
                    break;
                case 2:
                    tab = "unit_tab";
                    break;
                case 3:
                    tab = "settings_tab";
                    break;
                default:
                    throw new IndexOutOfBoundsException("Tab index must be between 0 and 3, inclusive");
            }
            sSharedPreferences.edit().putString("default_opened_tab", tab).apply();
        }
    }

    public static void setDefaultTab(String tab) {
        sSharedPreferences.edit().putString("default_opened_tab", tab).apply();
    }

    public static int getAppTheme() {
        return sAppTheme;
    }

    public static void setAppTheme(String theme) {
        sSharedPreferences.edit().putString("app_theme", theme).apply();
    }

    public static int getZeroDivResult() {
        if (sSharedPreferences.getBoolean("zero_div", true))
            return R.string.infinity;
        else
            return R.string.error;
    }

    public static void setZeroDivResult(boolean b) {
        sSharedPreferences.edit().putBoolean("zero_div", b).apply();
    }

    public static int getDefaultNavItem() {
        return sDefaultTab;
    }

    private static boolean isSaveLastEnabled() {
        return sSharedPreferences.getString("tab_default", "").equals("last_tab");
    }

    public static void setDeleteTooltipShown() {
        sSharedPreferences.edit().putBoolean("delete_tooltip_shown", true).apply();
    }

    public static boolean isDeleteTooltipShown() {
        return sSharedPreferences.getBoolean("delete_tooltip_shown", false);
    }

}
