package ru.art2000.calculator.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.R
import ru.art2000.calculator.calculator.view.CalculatorFragment
import ru.art2000.calculator.currency.view.CurrencyConverterFragment
import ru.art2000.calculator.databinding.ActivityMainBinding
import ru.art2000.calculator.common.view.AppActivity
import ru.art2000.calculator.settings.preferences.PreferenceValues
import ru.art2000.calculator.settings.view.SettingsFragment
import ru.art2000.calculator.unit.view.UnitConverterFragment
import ru.art2000.extensions.fragments.NavigationFragmentCreator
import ru.art2000.extensions.views.isLandscape
import ru.art2000.extensions.views.setMyOnItemSelectedListener
import ru.art2000.extensions.views.setSelectedItemId
import ru.art2000.extensions.views.setupWithViewPager2
import ru.art2000.calculator.calculator.R as CalculatorR
import ru.art2000.calculator.currency.R as CurrencyR
import ru.art2000.calculator.settings.R as SettingsR
import ru.art2000.calculator.unit.R as UnitR

@AndroidEntryPoint
class MainActivity : AppActivity() {
    private val binding by viewBinding<ActivityMainBinding>(CreateMethod.INFLATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val navigation = binding.navigation as NavigationBarView

        navigation.setMyOnItemSelectedListener { item ->
            generalPrefsHelper.defaultTabToOpen = when (item.itemId) {
                R.id.navigation_unit -> PreferenceValues.VALUE_TAB_DEFAULT_UNIT
                R.id.navigation_currency -> PreferenceValues.VALUE_TAB_DEFAULT_CURRENCY
                R.id.navigation_settings -> PreferenceValues.VALUE_TAB_DEFAULT_SETTINGS
                else -> PreferenceValues.VALUE_TAB_DEFAULT_CALC
            }
            intent.action = when (item.itemId) {
                R.id.navigation_unit -> ACTION_CONVERTER
                R.id.navigation_currency -> ACTION_CURRENCIES
                R.id.navigation_settings -> ACTION_SETTINGS
                else -> ACTION_CALCULATOR
            }

            true
        }

        navigation.setupWithViewPager2(
            this,
            binding.pager2,
            NavigationFragmentCreator(
                R.drawable.ic_currency,
                R.id.navigation_currency,
                CurrencyR.string.title_currency,
                ::CurrencyConverterFragment,
            ),
            NavigationFragmentCreator(
                R.drawable.ic_calc,
                R.id.navigation_calc,
                CalculatorR.string.title_calc,
                ::CalculatorFragment,
            ),
            NavigationFragmentCreator(
                R.drawable.ic_unit,
                R.id.navigation_unit,
                UnitR.string.title_unit,
                ::UnitConverterFragment,
            ),
            NavigationFragmentCreator(
                R.drawable.ic_settings,
                R.id.navigation_settings,
                SettingsR.string.title_settings,
                ::SettingsFragment,
            ),
        )

        val tabId = when (intent.action) {
            Intent.ACTION_MAIN -> when (generalPrefsHelper.defaultTabToOpen) {
                PreferenceValues.VALUE_TAB_DEFAULT_UNIT -> R.id.navigation_unit
                PreferenceValues.VALUE_TAB_DEFAULT_CURRENCY -> R.id.navigation_currency
                PreferenceValues.VALUE_TAB_DEFAULT_SETTINGS -> R.id.navigation_settings
                else -> R.id.navigation_calc
            }
            ACTION_CALCULATOR -> R.id.navigation_calc
            ACTION_CONVERTER -> R.id.navigation_unit
            ACTION_CURRENCIES -> R.id.navigation_currency
            ACTION_SETTINGS -> R.id.navigation_settings
            else -> R.id.navigation_calc
        }
        navigation.setSelectedItemId(tabId, false)
    }

    override val bottomViews: List<View>
        get() = if (isLandscape) emptyList() else listOf(binding.navigation)

    override val leftViews: List<View>
        get() = listOf(binding.root)

    override val rightViews: List<View>
        get() = if (isLandscape) listOf(binding.navigation) else listOf(binding.root)

    private companion object {

        const val ACTION_CALCULATOR = "ru.art2000.calculator.action.CALCULATOR"
        const val ACTION_CONVERTER = "ru.art2000.calculator.action.CONVERTER"
        const val ACTION_CURRENCIES = "ru.art2000.calculator.action.CURRENCIES"
        const val ACTION_SETTINGS = "ru.art2000.calculator.action.SETTINGS"

    }
}