package ru.art2000.calculator.calculator.preferences

import androidx.preference.PreferenceFragmentCompat
import ru.art2000.calculator.calculator.R
import ru.art2000.calculator.common.preferences.SettingsSetup

internal class CalculatorSettingsSetup : SettingsSetup {
    override fun PreferenceFragmentCompat.populateSettings() {
        addPreferencesFromResource(R.xml.calculator_settings)
    }
}