package ru.art2000.calculator.common.preferences

import ru.art2000.extensions.preferences.ReadOnlyPreferenceDelegate
import ru.art2000.extensions.preferences.ThemeSettingsHolder

interface GeneralPreferenceHelper : ThemeSettingsHolder {

    var defaultNavItemId: Int

    val appThemeProperty: ReadOnlyPreferenceDelegate<Int>

    val dynamicColorsProperty: ReadOnlyPreferenceDelegate<Boolean>

    val autoDarkThemeProperty: ReadOnlyPreferenceDelegate<Boolean>

    val darkThemeActivationProperty: ReadOnlyPreferenceDelegate<Int>

    val darkThemeDeactivationProperty: ReadOnlyPreferenceDelegate<Int>

}