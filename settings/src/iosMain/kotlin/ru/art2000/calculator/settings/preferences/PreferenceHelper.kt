package ru.art2000.calculator.settings.preferences

import ru.art2000.calculator.common.preferences.GeneralPreferenceHelper
import ru.art2000.extensions.preferences.AppPreferences

class PreferenceHelper(preferences: AppPreferences): GeneralPreferenceHelper by object : CommonPreferenceHelper(preferences) {

}