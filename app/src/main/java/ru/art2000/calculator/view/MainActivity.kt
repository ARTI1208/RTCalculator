package ru.art2000.calculator.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ActivityMainBinding
import ru.art2000.calculator.view.calculator.CalculatorFragment
import ru.art2000.calculator.view.currency.CurrencyConverterFragment
import ru.art2000.calculator.view.settings.SettingsFragment
import ru.art2000.calculator.view.unit.UnitConverterFragment
import ru.art2000.extensions.fragments.NavigationFragmentCreator
import ru.art2000.extensions.views.*

@AndroidEntryPoint
class MainActivity : AppActivity() {
    private val binding by viewBinding<ActivityMainBinding>(CreateMethod.INFLATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        val navigation = binding.navigation as NavigationBarView

        navigation.setMyOnItemSelectedListener { item ->
            generalPrefsHelper.defaultNavItemId = item.itemId
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
                R.string.title_currency,
                ::CurrencyConverterFragment,
            ),
            NavigationFragmentCreator(
                R.drawable.ic_calc,
                R.id.navigation_calc,
                R.string.title_calc,
                ::CalculatorFragment,
            ),
            NavigationFragmentCreator(
                R.drawable.ic_unit,
                R.id.navigation_unit,
                R.string.title_unit,
                ::UnitConverterFragment,
            ),
            NavigationFragmentCreator(
                R.drawable.ic_settings,
                R.id.navigation_settings,
                R.string.title_settings,
                ::SettingsFragment,
            ),
        )

        val tabId = when (intent.action) {
            Intent.ACTION_MAIN -> generalPrefsHelper.defaultNavItemId
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