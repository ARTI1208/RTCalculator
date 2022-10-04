package ru.art2000.calculator.view.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import androidx.work.*
import ru.art2000.calculator.BuildConfig
import ru.art2000.calculator.R
import ru.art2000.calculator.background.currency.CurrencyFunctions
import ru.art2000.calculator.view.MainScreenPreferenceFragment
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_APP_THEME
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_AUTO_DARK_THEME
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_CURRENCIES_BACKGROUND
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_CURRENCIES_INTERVAL
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_DARK_THEME_ACTIVATION
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_DARK_THEME_DEACTIVATION
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_SAVE_CURRENCY
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_TAB_DEFAULT
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_UNIT_VIEW
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_ZERO_DIVISION
import ru.art2000.extensions.activities.DayNightActivity
import ru.art2000.extensions.preferences.TimePickerPreference
import ru.art2000.helpers.PrefsHelper

internal class SettingsFragment : MainScreenPreferenceFragment() {

    private var dev = 0

    companion object {
        private val CLICKS_TO_OPEN_HIDDEN_INFO = if (BuildConfig.DEBUG) 1 else 7

        private const val KEY_APP_VERSION = "app_version"

        private const val THEME_SYSTEM = "system"
        private const val THEME_BATTERY = "battery"
        private const val THEME_DAY_NIGHT = "day_night"

        private val AUTO_THEMES_ANDROIDX = listOf(THEME_SYSTEM, THEME_BATTERY)
        private val AUTO_THEMES = AUTO_THEMES_ANDROIDX + THEME_DAY_NIGHT

        private const val UPDATE_NEVER = "no_update"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        /* tabList preference */
        val tabList = findPreference<ListPreference>(KEY_TAB_DEFAULT)

        tabList?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue: Any ->
                value = newValue.toString()
                PrefsHelper.setDefaultTab(value)
                true
            }
        }

        /* appTheme preference */
        val appTheme = findPreference<ListPreference>(KEY_APP_THEME)

        appTheme?.also {
            appTheme.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue: Any ->
                        if (appTheme.value == newValue) {
                            return@OnPreferenceChangeListener false
                        }
                        appTheme.value = newValue.toString()
                        if (newValue.toString() in AUTO_THEMES_ANDROIDX) {
                            Toast.makeText(
                                    requireContext(),
                                    R.string.daynight_support_message,
                                    Toast.LENGTH_LONG
                            ).show()
                        }
                        PrefsHelper.setAppTheme(newValue.toString())
                        requireActivity().apply {
                            recreate()
                        }
                        true
                    }
        }

        /* appAutoDarkTheme preference */
        val appAutoDarkTheme = findPreference<SwitchPreferenceCompat>(KEY_AUTO_DARK_THEME)

        appAutoDarkTheme?.also {
            if (appTheme?.value !in AUTO_THEMES) {
                it.parent?.removePreference(it)
                return@also
            }

            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
//                appAutoDarkTheme.isChecked = newValue as Boolean

                (requireActivity() as DayNightActivity).apply {
                    if (isDarkThemeApplied) {
                        recreate()
                    } else {
                        appAutoDarkTheme.isChecked = newValue as Boolean
                    }
                }
                true
            }
        }

        /* appAutoDarkTheme preference */
        val appDarkThemeActivationTime =
                findPreference<TimePickerPreference>(KEY_DARK_THEME_ACTIVATION)

        appDarkThemeActivationTime?.also {
            if (appTheme?.value != THEME_DAY_NIGHT) {
                it.parent?.removePreference(it)
                return@also
            }

            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                PrefsHelper.setDarkThemeActivationTime(newValue as String)
                (requireActivity() as DayNightActivity?)?.requestThemeCheck()
                true
            }
        }

        /* appAutoDarkTheme preference */
        val appDarkThemeDeactivationTime =
                findPreference<TimePickerPreference>(KEY_DARK_THEME_DEACTIVATION)

        appDarkThemeDeactivationTime?.also {
            if (appTheme?.value != THEME_DAY_NIGHT) {
                it.parent?.removePreference(it)
                return@also
            }

            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                PrefsHelper.setDarkThemeDeactivationTime(newValue as String)
                (requireActivity() as DayNightActivity?)?.requestThemeCheck()
                true
            }
        }

        /* saveCurrencyConversion preference */
        val saveCurrencyConversion = findPreference<SwitchPreferenceCompat>(KEY_SAVE_CURRENCY)
        saveCurrencyConversion?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    PrefsHelper.setShouldSaveCurrencyConversion(newValue as Boolean)
                    true
                }

        val updateCurrenciesInBackground = findPreference<ListPreference>(KEY_CURRENCIES_BACKGROUND)
        val updateCurrenciesInterval = findPreference<ListPreference>(KEY_CURRENCIES_INTERVAL)

        updateCurrenciesInBackground?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->

                    val type = newValue.toString()

                    when {
                        updateCurrenciesInterval == null -> {}
                        updateCurrenciesInBackground?.value == UPDATE_NEVER -> {
                            updateCurrenciesInBackground.parent?.addPreference(updateCurrenciesInterval)
                        }
                        type == UPDATE_NEVER -> {
                            updateCurrenciesInterval.parent?.removePreference(updateCurrenciesInterval)
                        }
                    }

                    PrefsHelper.setCurrencyBackgroundUpdateType(type)

                    CurrencyFunctions.setupCurrencyDownload(
                            requireContext(),
                            type,
                            PrefsHelper.getCurrencyBackgroundUpdateInterval(),
                            ExistingPeriodicWorkPolicy.REPLACE,
                    )
                    true
                }


        updateCurrenciesInterval?.apply {

            if (updateCurrenciesInBackground?.value == UPDATE_NEVER) {
                parent?.removePreference(this)
            }

            onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->

                    val interval = newValue.toString().toInt()
                    PrefsHelper.setCurrencyBackgroundUpdateInterval(interval)

                    CurrencyFunctions.setupCurrencyDownload(
                        requireContext(),
                        PrefsHelper.getCurrencyBackgroundUpdateType(),
                        interval,
                        ExistingPeriodicWorkPolicy.REPLACE,
                    )
                    true
                }
        }

        /* zeroDiv preference */
        val zeroDiv = findPreference<SwitchPreferenceCompat>(KEY_ZERO_DIVISION)
        zeroDiv?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            PrefsHelper.setZeroDivResult(newValue as Boolean)
            true
        }

        /* unitView preference */
        val unitView = findPreference<ListPreference>(KEY_UNIT_VIEW)

        unitView?.also {
            unitView.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        if (unitView.value == newValue) {
                            return@OnPreferenceChangeListener false
                        }
                        unitView.value = newValue.toString()
                        PrefsHelper.setUnitViewType(newValue.toString())

                        true
                    }
        }

        /* appVersion preference */
        val appVersion = findPreference<Preference>(KEY_APP_VERSION)
        appVersion?.summary = "${BuildConfig.VERSION_NAME}-${BuildConfig.FLAVOR}${if (BuildConfig.DEBUG) "-debug" else ""} (${BuildConfig.BUILD_DATE})"
        appVersion?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            dev++
            if (dev == CLICKS_TO_OPEN_HIDDEN_INFO) {
                dev = 0
                startActivity(Intent(requireContext(), InfoActivity::class.java))
            }
            false
        }
    }

    override fun getTitle(): Int {
        return R.string.title_settings
    }

    override fun getIcon(): Int {
        return R.drawable.ic_settings
    }

    override fun getReplaceableId(): Int {
        return R.id.navigation_settings
    }
}