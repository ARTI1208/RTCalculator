package ru.art2000.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import kotlin.Pair;
import ru.art2000.calculator.R;
import ru.art2000.extensions.preferences.TimePickerPreference;

public class PrefsHelper {

    private final static String DEFAULT_THEME = "system";
    private final static String DEFAULT_TAB = "calc_tab";
    private final static String DEFAULT_UNIT_VIEW = "powerful";
    private final static String DEFAULT_CONVERSION_CODE = "USD";
    private final static String DEFAULT_AUTO_DARK_ACTIVATION_TIME = "23:00";
    private final static String DEFAULT_AUTO_DARK_DEACTIVATION_TIME = "07:00";
    private final static boolean DEFAULT_DARK_THEME_IS_BLACK = true;
    private final static double DEFAULT_CONVERSION_VALUE = 1;
    private final static boolean DEFAULT_SHOULD_SAVE_CONVERSION_VALUE = false;
    private final static String DEFAULT_CURRENCY_BACKGROUND_UPDATE_TYPE = "no_update";
    private final static int DEFAULT_CURRENCY_BACKGROUND_UPDATE_INTERVAL = 8;

    private static SharedPreferences sSharedPreferences;
    private static int sAppTheme;
    private static boolean sAppAutoDarkThemeIsBlack;
    private static int sDefaultTab;
    private static boolean sShouldSaveCurrencyConversion = false;
    private static String sUnitViewType;
    private static String sConversionCode;
    private static double sConversionValue;
    private static int autoDarkThemeActivationTime;
    private static int autoDarkThemeDeactivationTime;

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
        return Double.longBitsToDouble(
                sSharedPreferences.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

    public static String getConversionCode() {
        return sConversionCode;
    }

    public static double getConversionValue() {
        return sConversionValue;
    }

    public static void initialSetup(Context mContext) {
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        switch (sSharedPreferences.getString("app_theme", DEFAULT_THEME)) {
            default:
            case "light":
                sAppTheme = R.style.RT_AppTheme_Light;
                break;
            case "dark":
                sAppTheme = R.style.RT_AppTheme_Dark;
                break;
            case "black":
                sAppTheme = R.style.RT_AppTheme_Black;
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
        sAppAutoDarkThemeIsBlack =
                sSharedPreferences.getBoolean("app_auto_dark_theme", DEFAULT_DARK_THEME_IS_BLACK);
        sShouldSaveCurrencyConversion = sSharedPreferences.getBoolean("save_currency_value",
                DEFAULT_SHOULD_SAVE_CONVERSION_VALUE);
        sConversionCode =
                sSharedPreferences.getString("last_conversion_code", DEFAULT_CONVERSION_CODE);
        sConversionValue = getDouble("last_conversion_double", DEFAULT_CONVERSION_VALUE);
        switch (sSharedPreferences.getString("default_opened_tab", DEFAULT_TAB)) {
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
        sUnitViewType = sSharedPreferences.getString("unit_view", DEFAULT_UNIT_VIEW);

        String activationTimeString = sSharedPreferences.getString(
                "app_auto_dark_theme_time_start", DEFAULT_AUTO_DARK_ACTIVATION_TIME);
        String deactivationTimeString = sSharedPreferences.getString(
                "app_auto_dark_theme_time_end", DEFAULT_AUTO_DARK_DEACTIVATION_TIME);

        autoDarkThemeActivationTime = timeStringToSeconds(activationTimeString);
        autoDarkThemeDeactivationTime = timeStringToSeconds(deactivationTimeString);
    }

    private static int timeStringToSeconds(String time) {
        Pair<Integer, Integer> timePair = TimePickerPreference.parseStringTime(time);
        return (timePair.getFirst() * 60 + timePair.getSecond()) * 60;
    }

    public static String getUnitViewType() {
        return sUnitViewType;
    }

    public static void setUnitViewType(String view) {
        sUnitViewType = view;
        sSharedPreferences.edit().putString("unit_view", view).apply();
    }

    public static void setDefaultTab(int pos) {
        if (PrefsHelper.isSaveLastEnabled() && pos != 3) {
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

    public static boolean isAppAutoDarkThemeBlack() {
        return sAppAutoDarkThemeIsBlack;
    }

    public static void setAppAutoDarkThemeIsBlack(boolean isBlack) {
        sSharedPreferences.edit().putBoolean("app_auto_dark_theme", isBlack).apply();
    }

    public static int getDarkThemeActivationTime() {
        return autoDarkThemeActivationTime;
    }

    public static int getDarkThemeDeactivationTime() {
        return autoDarkThemeDeactivationTime;
    }

    public static void setDarkThemeActivationTime(String time) {
        autoDarkThemeActivationTime = timeStringToSeconds(time);
    }

    public static void setDarkThemeDeactivationTime(String time) {
        autoDarkThemeDeactivationTime = timeStringToSeconds(time);
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

    public static String getCurrencyBackgroundUpdateType() {
        return sSharedPreferences.getString(
                "update_currencies_in_background",
                DEFAULT_CURRENCY_BACKGROUND_UPDATE_TYPE
        );
    }

    public static void setCurrencyBackgroundUpdateType(String type) {
        sSharedPreferences.edit().putString(
                "update_currencies_in_background",
                type
        ).apply();
    }

    private static int stringIntPref(String key, int defaultValue) {
        String stringValue = sSharedPreferences.getString(
                key,
                null
        );

        if (stringValue == null) return defaultValue;

        return Integer.parseInt(stringValue);
    }

    public static int getCurrencyBackgroundUpdateInterval() {
        return stringIntPref(
                "currency_update_interval",
                DEFAULT_CURRENCY_BACKGROUND_UPDATE_INTERVAL
        );
    }

    public static void setCurrencyBackgroundUpdateInterval(int interval) {
        sSharedPreferences.edit().putString(
                "currency_update_interval",
                String.valueOf(interval)
        ).apply();
    }
}
