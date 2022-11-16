package ru.art2000.calculator.common.preferences

import ru.art2000.extensions.preferences.AppTheme
import ru.art2000.extensions.preferences.PreferenceDelegate
import ru.art2000.extensions.preferences.ThemeSettingsHolder

actual interface GeneralPreferenceHelper : ThemeSettingsHolder {

    actual var defaultTabToOpen: String

    actual val appThemeProperty: PreferenceDelegate<AppTheme>

    actual val autoDarkThemeProperty: PreferenceDelegate<Boolean>

    actual val darkThemeActivationProperty: PreferenceDelegate<Int>

    actual val darkThemeDeactivationProperty: PreferenceDelegate<Int>

}