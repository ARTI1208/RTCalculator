package ru.art2000.calculator.currency.preferences

import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.art2000.calculator.common.preferences.SettingsSetup
import ru.art2000.calculator.currency.R

internal class CurrencySettingsSetup : SettingsSetup {
    override fun PreferenceFragmentCompat.populateSettings() {
        addPreferencesFromResource(R.xml.currency_settings)

        val updateCurrenciesInBackground = findPreference<ListPreference>(CurrencyKeys.KEY_CURRENCIES_BACKGROUND)
        val updateCurrenciesInterval = findPreference<ListPreference>(CurrencyKeys.KEY_CURRENCIES_INTERVAL)

        updateCurrenciesInBackground?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->

                val type = newValue.toString()

                when {
                    updateCurrenciesInterval == null -> {}
                    updateCurrenciesInBackground?.value == NO_UPDATE -> {
                        updateCurrenciesInBackground.parent?.addPreference(updateCurrenciesInterval)
                    }
                    type == NO_UPDATE -> {
                        updateCurrenciesInterval.parent?.removePreference(updateCurrenciesInterval)
                    }
                }

                true
            }


        updateCurrenciesInterval?.apply {

            if (updateCurrenciesInBackground?.value == NO_UPDATE) {
                parent?.removePreference(this)
            }
        }
    }

    companion object {
        private const val NO_UPDATE = CurrencyValues.VALUE_CURRENCY_BACKGROUND_NO_UPDATE
    }
}