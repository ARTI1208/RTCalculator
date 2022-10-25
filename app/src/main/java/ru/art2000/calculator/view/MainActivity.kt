package ru.art2000.calculator.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import ru.art2000.calculator.R
import ru.art2000.calculator.databinding.ActivityMainBinding
import ru.art2000.calculator.view.calculator.CalculatorFragment
import ru.art2000.calculator.view.currency.CurrencyConverterFragment
import ru.art2000.calculator.view.settings.SettingsFragment
import ru.art2000.calculator.view.unit.UnitConverterFragment
import ru.art2000.extensions.activities.AutoThemeActivity
import ru.art2000.extensions.fragments.INavigationFragment
import ru.art2000.extensions.views.*
import ru.art2000.helpers.isLightTheme

@AndroidEntryPoint
class MainActivity : AutoThemeActivity() {
    private val binding by viewBinding<ActivityMainBinding>(CreateMethod.INFLATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.isDrawingUnderStatusBarAllowed = true
        }

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

        val replaceableFragments: Array<INavigationFragment> = arrayOf(
            CurrencyConverterFragment(), CalculatorFragment(),
            UnitConverterFragment(), SettingsFragment(),
        )

        navigation.setupWithViewPager2(
            this,
            binding.pager2,
            *replaceableFragments,
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            && (!isLightTheme() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
        ) {
            val back = binding.navigation.background
            if (back is MaterialShapeDrawable) {
                window.navigationBarColor = back.resolvedTintColor
            }
        }

        if (isLandscape) {
            binding.pager2.applyWindowBottomInsets()
        }
    }

    private companion object {

        const val ACTION_CALCULATOR = "ru.art2000.calculator.action.CALCULATOR"
        const val ACTION_CONVERTER = "ru.art2000.calculator.action.CONVERTER"
        const val ACTION_CURRENCIES = "ru.art2000.calculator.action.CURRENCIES"
        const val ACTION_SETTINGS = "ru.art2000.calculator.action.SETTINGS"

    }
}