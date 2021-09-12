package ru.art2000.calculator.view

import ru.art2000.helpers.getColorAttribute
import ru.art2000.extensions.activities.AutoThemeActivity
import ru.art2000.calculator.view.currency.CurrencyConverterFragment
import ru.art2000.calculator.view.calculator.CalculatorFragment
import ru.art2000.calculator.view.unit.UnitConverterFragment
import ru.art2000.calculator.view.settings.SettingsFragment
import androidx.annotation.ColorInt
import android.os.Bundle
import ru.art2000.helpers.PrefsHelper
import ru.art2000.calculator.R
import android.widget.Toast
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.view.MenuItem
import ru.art2000.calculator.databinding.ActivityMainBinding
import ru.art2000.extensions.fragments.INavigationFragment

class MainActivity : AutoThemeActivity() {
    private var viewBinding: ActivityMainBinding? = null
    private var doubleBackToExitPressedOnce = false

    private val currentFragment: INavigationFragment
        get() {
            val binding = requireNotNull(viewBinding)
            val replaceable = requireNotNull(
                binding.navigation.getReplaceable(binding.pager2.currentItem)
            )
            return replaceable as? INavigationFragment
                ?: throw IllegalStateException("Current fragment ($replaceable) is not an instance of INavigationFragment")
        }

    @delegate:TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @get:ColorInt
    private val normalStatusBarColor by lazy { getColorAttribute(android.R.attr.statusBarColor) }

    @get:ColorInt
    private val calculatorStatusBarColor by lazy { getColorAttribute(R.attr.colorSurfaceVariant) }

    override fun onCreate(savedInstanceState: Bundle?) {
        PrefsHelper.initialSetup(this)
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater).also {
            viewBinding = it
        }

        setContentView(binding.root)

        binding.navigation.setOnItemSelectedListener { item: MenuItem ->
            PrefsHelper.setDefaultTab(item.order)
            when (item.itemId) {
                R.id.navigation_unit -> {
                    changeStatusBarColor(false)
                    intent.action = ACTION_CONVERTER
                }
                R.id.navigation_currency -> {
                    changeStatusBarColor(false)
                    intent.action = ACTION_CURRENCIES
                }
                R.id.navigation_settings -> {
                    changeStatusBarColor(false)
                    intent.action = ACTION_SETTINGS
                }
                else -> {
                    changeStatusBarColor(true)
                    intent.action = ACTION_CALCULATOR
                }
            }
            true
        }

        var currencyConverterFragment: CurrencyConverterFragment? = null
        var calculatorFragment: CalculatorFragment? = null
        var unitConverterFragment: UnitConverterFragment? = null
        var settingsFragment: SettingsFragment? = null

        for (fragment in supportFragmentManager.fragments) {
            when (fragment) {
                is CurrencyConverterFragment -> currencyConverterFragment = fragment
                is CalculatorFragment -> calculatorFragment = fragment
                is UnitConverterFragment -> unitConverterFragment = fragment
                is SettingsFragment -> settingsFragment = fragment
            }
        }

        currencyConverterFragment ?: run { currencyConverterFragment = CurrencyConverterFragment() }
        calculatorFragment        ?: run { calculatorFragment = CalculatorFragment() }
        unitConverterFragment     ?: run { unitConverterFragment = UnitConverterFragment() }
        settingsFragment          ?: run { settingsFragment = SettingsFragment() }

        binding.navigation.setupWithViewPager2(
            this,
            binding.pager2,
            currencyConverterFragment, calculatorFragment, unitConverterFragment, settingsFragment,
        )

        val tabId = when (intent.action) {
            Intent.ACTION_MAIN -> PrefsHelper.getDefaultNavItem()
            ACTION_CALCULATOR -> R.id.navigation_calc
            ACTION_CONVERTER -> R.id.navigation_unit
            ACTION_CURRENCIES -> R.id.navigation_currency
            ACTION_SETTINGS -> R.id.navigation_settings
            else -> R.id.navigation_calc
        }
        binding.navigation.selectedItemId = tabId
    }

    override fun onBackPressed() {
        if (currentFragment.onBackPressed()) {
            if (doubleBackToExitPressedOnce) {
                finish()
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, R.string.twice_tap_exit, Toast.LENGTH_SHORT).show()
            Handler(mainLooper).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    @SuppressLint("NewApi")
    private fun changeStatusBarColor(isCalculatorPage: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ||
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isDarkThemeApplied
        ) {
            if (isCalculatorPage) {
                window.statusBarColor = calculatorStatusBarColor
            } else {
                window.statusBarColor = normalStatusBarColor
            }
        }
    }

    private companion object {

        const val ACTION_CALCULATOR = "ru.art2000.calculator.action.CALCULATOR"
        const val ACTION_CONVERTER  = "ru.art2000.calculator.action.CONVERTER"
        const val ACTION_CURRENCIES = "ru.art2000.calculator.action.CURRENCIES"
        const val ACTION_SETTINGS   = "ru.art2000.calculator.action.SETTINGS"

    }
}