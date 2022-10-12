package ru.art2000.calculator.view.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import androidx.work.*
import ru.art2000.calculator.BuildConfig
import ru.art2000.calculator.CalculatorApplication
import ru.art2000.calculator.R
import ru.art2000.calculator.background.currency.CurrencyFunctions
import ru.art2000.calculator.view.MainScreenPreferenceFragment
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_APP_THEME
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_AUTO_DARK_THEME
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_CURRENCIES_BACKGROUND
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_CURRENCIES_INTERVAL
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_DARK_THEME_ACTIVATION
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_DARK_THEME_DEACTIVATION
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_DYNAMIC_COLORS
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_SAVE_CURRENCY
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_TAB_DEFAULT
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_UNIT_VIEW
import ru.art2000.calculator.view.settings.PreferenceKeys.KEY_ZERO_DIVISION
import ru.art2000.helpers.PrefsHelper
import ru.art2000.helpers.getColorAttribute
import java.text.DateFormat
import java.util.*

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

        val dynamicColors = findPreference<SwitchPreferenceCompat>(KEY_DYNAMIC_COLORS)
        /* appTheme preference */
        val appTheme = findPreference<ListPreference>(KEY_APP_THEME)
        /* appAutoDarkTheme preference */
        val appAutoDarkTheme = findPreference<SwitchPreferenceCompat>(KEY_AUTO_DARK_THEME)
        /* appAutoDarkTheme preference */
        val appDarkThemeActivationTime =
            findPreference<Preference>(KEY_DARK_THEME_ACTIVATION)
        /* appAutoDarkTheme preference */
        val appDarkThemeDeactivationTime =
            findPreference<Preference>(KEY_DARK_THEME_DEACTIVATION)

        val dynamicColorsDisable = listOf(
            appTheme, appAutoDarkTheme,
            appDarkThemeActivationTime, appDarkThemeDeactivationTime,
        )

        val themingGroup = dynamicColors?.parent

        fun areDynamicColorsEnabled() = CalculatorApplication.DYNAMIC_COLORS_AVAILABLE
                && (dynamicColors?.isChecked ?: false)

        fun showThemingPreferences(dynamicColorsEnabled: Boolean) {
            if (dynamicColorsEnabled) {
                dynamicColorsDisable.forEach { preference ->
                    preference?.also { themingGroup?.removePreference(it) }
                }
            } else {
                dynamicColorsDisable.forEach { preference ->
                    preference?.also { themingGroup?.addPreference(it) }
                }
            }
        }

        showThemingPreferences(areDynamicColorsEnabled())

        dynamicColors?.also {
            if (!CalculatorApplication.DYNAMIC_COLORS_AVAILABLE) {
                it.parent?.removePreference(it)
            }

            it.setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                PrefsHelper.setDynamicColorsEnabled(enabled)
                true
            }

        }

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
                        true
                    }
        }

        appAutoDarkTheme?.also {
            if (appTheme?.value !in AUTO_THEMES) {
                it.parent?.removePreference(it)
                return@also
            }

            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                appAutoDarkTheme.isChecked = newValue as Boolean
                true
            }
        }

        appDarkThemeActivationTime?.also {
            if (appTheme?.value != THEME_DAY_NIGHT) {
                it.parent?.removePreference(it)
                return@also
            }

            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                PrefsHelper.setDarkThemeActivationTime(newValue as String)
                true
            }
        }

        appDarkThemeDeactivationTime?.also {
            if (appTheme?.value != THEME_DAY_NIGHT) {
                it.parent?.removePreference(it)
                return@also
            }

            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                PrefsHelper.setDarkThemeDeactivationTime(newValue as String)
                true
            }
        }

        /* saveCurrencyConversion preference */
        val saveCurrencyConversion = findPreference<SwitchPreferenceCompat>(KEY_SAVE_CURRENCY)
        saveCurrencyConversion?.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    PrefsHelper.isShouldSaveCurrencyConversion = newValue as Boolean
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

                    PrefsHelper.currencyBackgroundUpdateType = type

                    CurrencyFunctions.setupCurrencyDownload(
                            requireContext(),
                            type,
                            PrefsHelper.currencyBackgroundUpdateInterval,
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
                    PrefsHelper.currencyBackgroundUpdateInterval = interval

                    CurrencyFunctions.setupCurrencyDownload(
                        requireContext(),
                        PrefsHelper.currencyBackgroundUpdateType,
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
                        PrefsHelper.unitViewType = newValue.toString()

                        true
                    }
        }

        /* appVersion preference */
        val appVersion = findPreference<Preference>(KEY_APP_VERSION)
        val buildDate = DateFormat
            .getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)
            .format(Date(BuildConfig.BUILD_TIME))
        val suffix = if (BuildConfig.DEBUG) "-debug" else ""
        appVersion?.summary = "${BuildConfig.VERSION_NAME}-${BuildConfig.FLAVOR}$suffix ($buildDate)"
        appVersion?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            dev++
            if (dev == CLICKS_TO_OPEN_HIDDEN_INFO) {
                dev = 0
                startActivity(Intent(requireContext(), InfoActivity::class.java))
            }
            false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(requireContext().getColorAttribute(com.google.android.material.R.attr.colorSurface))
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