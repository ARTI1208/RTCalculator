package ru.art2000.calculator.settings.view

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
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.BuildConfig
import ru.art2000.calculator.R
import ru.art2000.calculator.common.preferences.SettingsSetup
import ru.art2000.calculator.common.model.MainPage
import ru.art2000.calculator.common.view.MainScreenPreferenceFragment
import ru.art2000.calculator.main.CalculatorApplication
import ru.art2000.calculator.settings.preferences.DYNAMIC_COLORS_AVAILABLE
import ru.art2000.calculator.settings.preferences.PreferenceKeys.KEY_APP_THEME
import ru.art2000.calculator.settings.preferences.PreferenceKeys.KEY_AUTO_DARK_THEME
import ru.art2000.calculator.settings.preferences.PreferenceKeys.KEY_DARK_THEME_ACTIVATION
import ru.art2000.calculator.settings.preferences.PreferenceKeys.KEY_DARK_THEME_DEACTIVATION
import ru.art2000.calculator.settings.preferences.PreferenceKeys.KEY_DYNAMIC_COLORS
import ru.art2000.calculator.settings.preferences.PreferenceValues
import ru.art2000.extensions.getColorAttribute
import ru.art2000.extensions.views.isLandscape
import java.text.DateFormat
import java.util.*
import javax.inject.Inject
import com.google.android.material.R as MaterialR

@AndroidEntryPoint
internal class SettingsFragment : MainScreenPreferenceFragment() {

    private var dev = 0

    companion object {
        private val CLICKS_TO_OPEN_HIDDEN_INFO = if (BuildConfig.DEBUG) 1 else 7

        private const val KEY_APP_VERSION = "app_version"

        private val AUTO_THEMES_ANDROIDX = listOf(
            PreferenceValues.VALUE_THEME_SYSTEM, PreferenceValues.VALUE_THEME_BATTERY,
        )
        private val AUTO_THEMES = AUTO_THEMES_ANDROIDX + PreferenceValues.VALUE_THEME_DAY_NIGHT
    }

    @Inject
    lateinit var settingsSetups: Map<MainPage, @JvmSuppressWildcards SettingsSetup>

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.general_settings)

        settingsSetups
            .toSortedMap(Comparator.comparingInt { it.ordinal })
            .values
            .forEach {
                with (it) {
                    populateSettings()
                }
            }

        addPreferencesFromResource(R.xml.info)

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

        fun areDynamicColorsEnabled() = DYNAMIC_COLORS_AVAILABLE
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
            if (!DYNAMIC_COLORS_AVAILABLE) {
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
        view.setBackgroundColor(requireContext().getColorAttribute(MaterialR.attr.colorSurface))
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