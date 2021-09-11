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
import android.os.Build
import android.os.Handler
import android.view.MenuItem
import ru.art2000.calculator.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AutoThemeActivity() {
    private var viewBinding: ActivityMainBinding? = null
    private var doubleBackToExitPressedOnce = false
    private var currencyConverterFragment: CurrencyConverterFragment? = null
    private var calculatorFragment: CalculatorFragment? = null
    private var unitConverterFragment: UnitConverterFragment? = null
    private var settingsFragment: SettingsFragment? = null

    @delegate:TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @get:ColorInt
    private val normalStatusBarColor by lazy { getColorAttribute(android.R.attr.statusBarColor) }

    @get:ColorInt
    private val calculatorStatusBarColor by lazy { getColorAttribute(R.attr.colorSurfaceVariant) }

    override fun onCreate(savedInstanceState: Bundle?) {
        PrefsHelper.initialSetup(this)
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding!!.root)
        val list = supportFragmentManager.fragments
        for (fragment in list) {
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

        viewBinding!!.navigation.setOnItemReselectedListener { item: MenuItem ->
            if (item.itemId == R.id.navigation_calc) {
                calculatorFragment!!.ensureHistoryPanelClosed()
            }
        }
        viewBinding!!.navigation.setOnItemSelectedListener { item: MenuItem ->
            PrefsHelper.setDefaultTab(item.order)
            when (item.itemId) {
                R.id.navigation_unit -> {
                    changeStatusBarColor(false)
                    intent.action = "ru.art2000.calculator.action.CONVERTER"
                }
                R.id.navigation_currency -> {
                    changeStatusBarColor(false)
                    intent.action = "ru.art2000.calculator.action.CURRENCIES"
                }
                R.id.navigation_settings -> {
                    changeStatusBarColor(false)
                    intent.action = "ru.art2000.calculator.action.SETTINGS"
                }
                else -> {
                    changeStatusBarColor(true)
                    intent.action = "ru.art2000.calculator.action.CALCULATOR"
                }
            }
            true
        }
        viewBinding!!.navigation.setupWithViewPager2(
            this,
            viewBinding!!.pager2,
            unitConverterFragment, currencyConverterFragment, calculatorFragment, settingsFragment
        )
        val tabId = if (Objects.requireNonNull(intent.action) == "android.intent.action.MAIN") {
            PrefsHelper.getDefaultNavItem()
        } else {
            when (intent.action) {
                "ru.art2000.calculator.action.CALCULATOR" -> R.id.navigation_calc
                "ru.art2000.calculator.action.CONVERTER" -> R.id.navigation_unit
                "ru.art2000.calculator.action.CURRENCIES" -> R.id.navigation_currency
                "ru.art2000.calculator.action.SETTINGS" -> R.id.navigation_settings
                else -> R.id.navigation_calc
            }
        }
        viewBinding!!.navigation.selectedItemId = tabId
    }

    override fun onBackPressed() {
        if (viewBinding!!.navigation.selectedItemId != R.id.navigation_calc
            || calculatorFragment!!.ensureHistoryPanelClosed()
        ) {
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
}