package ru.art2000.calculator.view.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import androidx.recyclerview.widget.RecyclerView
import ru.art2000.calculator.BuildConfig
import ru.art2000.calculator.CalculatorApplication
import ru.art2000.calculator.R
import ru.art2000.calculator.view.MainScreenPreferenceFragment
import ru.art2000.extensions.views.isLandscape
import ru.art2000.helpers.PreferenceKeys.KEY_APP_THEME
import ru.art2000.helpers.PreferenceKeys.KEY_AUTO_DARK_THEME
import ru.art2000.helpers.PreferenceKeys.KEY_CURRENCIES_BACKGROUND
import ru.art2000.helpers.PreferenceKeys.KEY_CURRENCIES_INTERVAL
import ru.art2000.helpers.PreferenceKeys.KEY_DARK_THEME_ACTIVATION
import ru.art2000.helpers.PreferenceKeys.KEY_DARK_THEME_DEACTIVATION
import ru.art2000.helpers.PreferenceKeys.KEY_DYNAMIC_COLORS
import ru.art2000.helpers.PreferenceKeys.KEY_UNIT_VIEW
import ru.art2000.helpers.PreferenceValues
import ru.art2000.helpers.getColorAttribute
import java.text.DateFormat
import java.util.*

internal class SettingsFragment : MainScreenPreferenceFragment() {

    private var dev = 0

    companion object {
        private val CLICKS_TO_OPEN_HIDDEN_INFO = if (BuildConfig.DEBUG) 1 else 7

        private const val KEY_APP_VERSION = "app_version"

        private val AUTO_THEMES_ANDROIDX = listOf(
            PreferenceValues.VALUE_THEME_SYSTEM, PreferenceValues.VALUE_THEME_BATTERY,
        )
        private val AUTO_THEMES = AUTO_THEMES_ANDROIDX + PreferenceValues.VALUE_THEME_DAY_NIGHT

        private const val NO_UPDATE = PreferenceValues.VALUE_CURRENCY_BACKGROUND_NO_UPDATE
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

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
            if (appTheme?.value != PreferenceValues.VALUE_THEME_DAY_NIGHT) {
                it.parent?.removePreference(it)
                return@also
            }
        }

        appDarkThemeDeactivationTime?.also {
            if (appTheme?.value != PreferenceValues.VALUE_THEME_DAY_NIGHT) {
                it.parent?.removePreference(it)
                return@also
            }
        }

        val updateCurrenciesInBackground = findPreference<ListPreference>(KEY_CURRENCIES_BACKGROUND)
        val updateCurrenciesInterval = findPreference<ListPreference>(KEY_CURRENCIES_INTERVAL)

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

        /* unitView preference */
        val unitView = findPreference<ListPreference>(KEY_UNIT_VIEW)

        unitView?.also {
            unitView.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    unitView.value != newValue
                }
        }

        /* appVersion preference */
        val appVersion = findPreference<Preference>(KEY_APP_VERSION)
        val buildDate = DateFormat
            .getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)
            .format(Date(BuildConfig.BUILD_TIME))
        val suffix = if (BuildConfig.DEBUG) "-debug" else ""
        appVersion?.summary =
            "${BuildConfig.VERSION_NAME}-${BuildConfig.FLAVOR}$suffix ($buildDate)"
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

    override val topViews: List<View>
        get() = listOf(findRecyclerView(view as ViewGroup)!!)

    override val bottomViews: List<View>
        get() = if (requireContext().isLandscape) topViews else emptyList()

    private fun findRecyclerView(group: ViewGroup): RecyclerView? {
        for (v in group.children) {
            if (v is RecyclerView) return v
            if (v is ViewGroup) {
                val res = findRecyclerView(v)
                if (res != null) return res
            }
        }

        return null
    }
}