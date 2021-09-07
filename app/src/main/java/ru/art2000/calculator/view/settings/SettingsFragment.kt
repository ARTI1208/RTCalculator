package ru.art2000.calculator.view.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import ru.art2000.calculator.BuildConfig
import ru.art2000.calculator.R
import ru.art2000.calculator.view.MainActivity
import ru.art2000.extensions.activities.DayNightActivity
import ru.art2000.extensions.fragments.PreferenceNavigationFragment
import ru.art2000.extensions.preferences.TimePickerPreference
import ru.art2000.helpers.PrefsHelper

class SettingsFragment : PreferenceNavigationFragment() {

    private var dev = 0

    companion object {
        private val CLICKS_TO_OPEN_HIDDEN_INFO = if (BuildConfig.DEBUG) 1 else 7

        private const val KEY_TAB_DEFAULT = "tab_default"
        private const val KEY_APP_THEME = "app_theme"
        private const val KEY_AUTO_DARK_THEME = "app_auto_dark_theme"
        private const val KEY_DARK_THEME_ACTIVATION = "app_auto_dark_theme_time_start"
        private const val KEY_DARK_THEME_DEACTIVATION = "app_auto_dark_theme_time_end"
        private const val KEY_SAVE_CURRENCY = "save_currency_value"
        private const val KEY_ZERO_DIVISION = "zero_div"
        private const val KEY_UNIT_VIEW = "unit_view"
        private const val KEY_APP_VERSION = "app_version"

        private const val THEME_SYSTEM = "system"
        private const val THEME_BATTERY = "battery"
        private const val THEME_DAY_NIGHT = "day_night"

        private val AUTO_THEMES_ANDROIDX = listOf(THEME_SYSTEM, THEME_BATTERY)
        private val AUTO_THEMES = AUTO_THEMES_ANDROIDX + THEME_DAY_NIGHT
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
            appTheme.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue: Any ->
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
        val appDarkThemeActivationTime = findPreference<TimePickerPreference>(KEY_DARK_THEME_ACTIVATION)

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
        val appDarkThemeDeactivationTime = findPreference<TimePickerPreference>(KEY_DARK_THEME_DEACTIVATION)

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
        saveCurrencyConversion?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            PrefsHelper.setShouldSaveCurrencyConversion(newValue as Boolean)
            true
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
            unitView.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                if (unitView.value == newValue) {
                    return@OnPreferenceChangeListener false
                }
                unitView.value = newValue.toString()
                PrefsHelper.setUnitViewType(newValue.toString())

                Handler(Looper.getMainLooper()).postDelayed({
                    (activity as MainActivity).updateUnitView()
                }, 400)

                true
            }
        }

        /* appVersion preference */
        val appVersion = findPreference<Preference>(KEY_APP_VERSION)
        appVersion?.summary = BuildConfig.VERSION_NAME + " (" + BuildConfig.BUILD_DATE + ")"
        appVersion?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            dev++
            if (dev == CLICKS_TO_OPEN_HIDDEN_INFO) {
                dev = 0
                startActivity(Intent(requireContext(), InfoActivity::class.java))
            }
            false
        }
    }

    override fun getOrder(): Int {
        return 3
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