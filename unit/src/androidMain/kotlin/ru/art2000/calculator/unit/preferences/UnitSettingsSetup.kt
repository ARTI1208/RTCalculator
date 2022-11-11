package ru.art2000.calculator.unit.preferences

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.art2000.calculator.common.preferences.SettingsSetup
import ru.art2000.calculator.unit.R

internal class UnitSettingsSetup : SettingsSetup {

    override fun PreferenceFragmentCompat.populateSettings() {
        addPreferencesFromResource(R.xml.unit_settings)

        /* unitView preference */
        val unitView = findPreference<ListPreference>(UnitKeys.KEY_UNIT_VIEW)

        unitView?.also {
            unitView.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    unitView.value != newValue
                }
        }
    }
}