package ru.art2000.calculator.common.preferences

import ru.art2000.extensions.preferences.AppTheme
import ru.art2000.extensions.preferences.PreferenceDelegate
import ru.art2000.extensions.preferences.ThemeSettingsHolder

expect interface GeneralPreferenceHelper : ThemeSettingsHolder {

    var defaultTabToOpen: String

    val appThemeProperty: PreferenceDelegate<AppTheme>

    val autoDarkThemeProperty: PreferenceDelegate<Boolean>

    val darkThemeActivationProperty: PreferenceDelegate<Int>

    val darkThemeDeactivationProperty: PreferenceDelegate<Int>

}