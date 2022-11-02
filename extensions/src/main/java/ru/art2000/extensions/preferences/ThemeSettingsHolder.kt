package ru.art2000.extensions.preferences

interface ThemeSettingsHolder {

    val areDynamicColorsEnabled: Boolean

    //    @StyleRes
    val appTheme: Int

    val isBlackThemePreferred: Boolean

    val darkThemeActivationTime: Int

    val darkThemeDeactivationTime: Int

}