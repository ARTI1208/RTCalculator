package ru.art2000.helpers

interface GeneralPreferenceHelper {

    var defaultNavItemId: Int

    val areDynamicColorsEnabled: Boolean

    //    @StyleRes
    val appTheme: Int

    val isAppAutoDarkThemeBlack: Boolean

    val darkThemeActivationTime: Int

    val darkThemeDeactivationTime: Int

}