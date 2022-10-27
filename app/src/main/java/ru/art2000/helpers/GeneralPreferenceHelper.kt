package ru.art2000.helpers

import ru.art2000.extensions.preferences.ReadOnlyPreferenceDelegate

interface GeneralPreferenceHelper {

    var defaultNavItemId: Int

    val areDynamicColorsEnabled: Boolean

    val appThemeProperty: ReadOnlyPreferenceDelegate<Int>

    //    @StyleRes
    val appTheme: Int

    val isAppAutoDarkThemeBlack: Boolean

    val darkThemeActivationTime: Int

    val darkThemeDeactivationTime: Int

}