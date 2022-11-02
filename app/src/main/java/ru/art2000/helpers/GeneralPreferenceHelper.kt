package ru.art2000.helpers

import ru.art2000.extensions.preferences.ReadOnlyPreferenceDelegate
import ru.art2000.extensions.preferences.ThemeSettingsHolder

interface GeneralPreferenceHelper : ThemeSettingsHolder {

    var defaultNavItemId: Int

    val appThemeProperty: ReadOnlyPreferenceDelegate<Int>

}