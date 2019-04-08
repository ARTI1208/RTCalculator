package ru.art2000.calculator.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatDelegate;

import ru.art2000.calculator.R;

public class PrefsHelper {

    private static SharedPreferences sSharedPreferences;
    private static int sAppTheme;
    private static int sDefaultTab;
    private static int sUnitViewExtraButtonAction;
    private static boolean sIsUnitViewChanged = false;
    private static boolean sShouldSaveCurrencyConversion = false;
    private static String sUnitViewType;
    public static final int SWAP_DIMENSIONS = 0;
    public static final int SHOW_ALL_DIMENSIONS = 1;
    private static String sConversionCode;
    private static double sConversionValue;


    static void setUnitViewChanged(){
        sIsUnitViewChanged = true;
    }

    public static int getExtraButtonAction(){
        return sUnitViewExtraButtonAction;
    }

    public static void setExtraButtonAction(int action){
        sUnitViewExtraButtonAction = action;
        sSharedPreferences.edit().putInt("unit_view_extra_button_action", action).apply();
    }

    static void setShouldSaveCurrencyConversion(boolean value){
        sShouldSaveCurrencyConversion = value;
        sSharedPreferences.edit().putBoolean("save_currency_value", value).apply();
        if (!value)
            sSharedPreferences.edit()
                    .remove("last_conversion_code")
                    .remove("last_conversion_double")
                    .apply();
    }

    public static boolean isShouldSaveCurrencyConversion(){
        return sShouldSaveCurrencyConversion;
    }

    public static void putConversionValues(String from, double value){
        sSharedPreferences.edit().putString("last_conversion_code", from).apply();
        putConversionDouble(value);
    }

    private static void putConversionDouble(double value){
        putDouble("last_conversion_double", value);
    }

    private static void putDouble(final String key, final double value) {
        sSharedPreferences.edit().putLong(key, Double.doubleToRawLongBits(value)).apply();
    }

    private static double getDouble(final String key, final double defaultValue) {
        return Double.longBitsToDouble(sSharedPreferences.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static String getConversionCode(){
        return sConversionCode;
    }

    public static double getConversionValue(){
        return sConversionValue;
    }

    public static boolean isUnitViewChanged(){
        if (sIsUnitViewChanged){
            sIsUnitViewChanged = false;
            return true;
        }
        return false;
    }

    public static void initialSetup(Context mContext){
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (sSharedPreferences.getBoolean("is_first_run", true))
            sSharedPreferences
                    .edit()
                    .putString("app_theme", "light")
                    .putString("unit_view", "simple")
                    .putBoolean("is_first_run", false)
                    .apply();
        switch (sSharedPreferences.getString("app_theme", "light")){
            default:
            case "light":
                sAppTheme = R.style.AppTheme;
                break;
            case "dark":
                sAppTheme = R.style.AppTheme_Dark;
                break;
            case "day_night":
                sAppTheme = R.style.AppTheme_DayNight;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                break;
        }
        sShouldSaveCurrencyConversion = sSharedPreferences.getBoolean("save_currency_value", false);
        sConversionCode = sSharedPreferences.getString("last_conversion_code", "USD");
        Log.d("coddde", sConversionCode);
        sConversionValue = getDouble("last_conversion_double", 1);
        switch (sSharedPreferences.getInt("unit_view_extra_button_action", SWAP_DIMENSIONS)){
            default:
            case SWAP_DIMENSIONS:
                sUnitViewExtraButtonAction = SWAP_DIMENSIONS;
                break;
            case SHOW_ALL_DIMENSIONS:
                sUnitViewExtraButtonAction = SHOW_ALL_DIMENSIONS;
                break;
        }
        switch (sSharedPreferences.getString("tab_default", "calc_tab")) {
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

    public static String getsUnitViewType() {
        return sUnitViewType;
    }

    static void setUnitViewType(String view){
        sUnitViewType = view;
        sSharedPreferences.edit().putString("unit_view", view).apply();
    }

    static void setDefaultTab(String tab){
        sSharedPreferences.edit().putString("tab_default", tab).apply();
    }

    static void setZeroDivResult(boolean b){
        sSharedPreferences.edit().putBoolean("zero_div", b).apply();
    }

    static void setAppTheme(String theme){
        sSharedPreferences.edit().putString("app_theme", theme).apply();
    }

    public static int getAppTheme(){
        return sAppTheme;
    }

    public static int getZeroDivResult(){
         if (sSharedPreferences.getBoolean("zero_div", true))
             return R.string.infinity;
         else
             return R.string.error;
    }

    public static int getDefaultNavItem(){
            return sDefaultTab;
    }

}
