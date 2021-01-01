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
import ru.art2000.extensions.PreferenceNavigationFragment
import ru.art2000.helpers.PrefsHelper

class SettingsFragment : PreferenceNavigationFragment() {

    private var dev = 0

    companion object {
        private val CLICKS_TO_OPEN_HIDDEN_INFO = if (BuildConfig.DEBUG) 1 else 7
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        /* tabList preference */
        val tabList = findPreference<ListPreference>("tab_default")

        tabList?.apply {
            if (!resources.getBoolean(R.bool.show_settings_as_default_tab)) {

                val settingsTabIndex = entryValues.indexOf("settings_tab")
                if (settingsTabIndex < 0) return@apply

                entries = entries.filterIndexed { index, _ ->
                    index != settingsTabIndex
                }.toTypedArray()

                entryValues = entryValues.filterIndexed { index, _ ->
                    index != settingsTabIndex
                }.toTypedArray()
            }
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue: Any ->
                value = newValue.toString()
                PrefsHelper.setDefaultTab(value)
                true
            }
        }

        /* appTheme preference */
        val appTheme = findPreference<ListPreference>("app_theme")

        appTheme?.also {
            appTheme.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue: Any ->
                if (appTheme.value == newValue) {
                    return@OnPreferenceChangeListener false
                }
                appTheme.value = newValue.toString()
                when (newValue.toString()) {
                    "system", "battery" ->
                        Toast.makeText(
                                requireContext(),
                                R.string.daynight_support_message,
                                Toast.LENGTH_LONG
                        ).show()
                }
                PrefsHelper.setAppTheme(newValue.toString())
                requireActivity().apply {
                    finish()
                    startActivity(intent)
                }
                true
            }
        }

        /* saveCurrencyConversion preference */
        val saveCurrencyConversion = findPreference<SwitchPreferenceCompat>("save_currency_value")
        saveCurrencyConversion?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue: Any ->
            PrefsHelper.setShouldSaveCurrencyConversion(newValue as Boolean)
            true
        }

        /* zeroDiv preference */
        val zeroDiv = findPreference<SwitchPreferenceCompat>("zero_div")
        zeroDiv?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue: Any ->
            PrefsHelper.setZeroDivResult(newValue as Boolean)
            true
        }

        /* unitView preference */
        val unitView = findPreference<ListPreference>("unit_view")

        unitView?.also {
            unitView.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue: Any ->
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
        val appVersion = findPreference<Preference>("app_version")
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