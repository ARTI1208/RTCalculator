package ru.art2000.extensions.preferences

actual enum class AppTheme {
    SYSTEM,
    BATTERY,
    DAY_NIGHT,
    LIGHT,
    DARK,
    BLACK,
}

actual interface ThemeSettingsHolder {

    val areDynamicColorsEnabled: Boolean

    actual val appTheme: AppTheme

    actual val isBlackThemePreferred: Boolean

    actual val darkThemeActivationTime: Int

    actual val darkThemeDeactivationTime: Int

}

