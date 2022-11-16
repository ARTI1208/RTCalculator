package ru.art2000.calculator.settings.preferences

import ru.art2000.extensions.preferences.AppPreferences
import ru.art2000.extensions.preferences.PreferenceDelegate
import ru.art2000.extensions.preferences.booleanPreference
import javax.inject.Singleton

@Singleton
internal class PreferenceHelper(preferences: AppPreferences) : CommonPreferenceHelper(preferences) {

    override val dynamicColorsProperty: PreferenceDelegate<Boolean> =
        preferences.booleanPreference(
            PreferenceKeys.KEY_DYNAMIC_COLORS, PreferenceDefaults.DEFAULT_DYNAMIC_COLORS
        ).mapGetter {
            DYNAMIC_COLORS_AVAILABLE && it
        }

    override val areDynamicColorsEnabled by dynamicColorsProperty

}