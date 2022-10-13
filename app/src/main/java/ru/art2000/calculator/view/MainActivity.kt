package ru.art2000.calculator.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.shape.MaterialShapeDrawable
import ru.art2000.calculator.R
import ru.art2000.calculator.background.currency.CurrencyFunctions
import ru.art2000.calculator.databinding.ActivityMainBinding
import ru.art2000.calculator.view.calculator.CalculatorFragment
import ru.art2000.calculator.view.currency.CurrencyConverterFragment
import ru.art2000.calculator.view.settings.SettingsFragment
import ru.art2000.calculator.view.unit.UnitConverterFragment
import ru.art2000.extensions.activities.AutoThemeActivity
import ru.art2000.extensions.fragments.INavigationFragment
import ru.art2000.extensions.views.allowDrawingUnderStatusBar
import ru.art2000.helpers.PrefsHelper
import ru.art2000.helpers.isLightTheme

class MainActivity : AutoThemeActivity() {
    private val binding by viewBinding<ActivityMainBinding>(CreateMethod.INFLATE)
    private var doubleBackToExitPressedOnce = false

    private val currentFragment: INavigationFragment
        get() {
            val replaceable = requireNotNull(
                binding.navigation.getReplaceable(binding.pager2.currentItem)
            )
            return replaceable as? INavigationFragment
                ?: throw IllegalStateException("Current fragment ($replaceable) is not an instance of INavigationFragment")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        PrefsHelper.initialSetup(this)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        window.allowDrawingUnderStatusBar(true)

        ViewCompat.setOnApplyWindowInsetsListener(binding.pager2,
            OnApplyWindowInsetsListener { v, insets ->
                val newInsets = ViewCompat.onApplyWindowInsets(v, insets)
                if (newInsets.isConsumed) return@OnApplyWindowInsetsListener newInsets
                var consumed = false

                val recyclerView = binding.pager2[0] as RecyclerView

                repeat(recyclerView.childCount) { i ->
                    val child = recyclerView[i]
                    ViewCompat.dispatchApplyWindowInsets(child, newInsets)
                    if (newInsets.isConsumed) {
                        consumed = true
                    }
                }

                if (consumed) WindowInsetsCompat.CONSUMED else newInsets
            })

        binding.navigation.setOnItemSelectedListener { item: MenuItem ->
            PrefsHelper.setDefaultTab(item.order)
            intent.action = when (item.itemId) {
                R.id.navigation_unit -> ACTION_CONVERTER
                R.id.navigation_currency -> ACTION_CURRENCIES
                R.id.navigation_settings -> ACTION_SETTINGS
                else -> ACTION_CALCULATOR
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
        calculatorFragment ?: run { calculatorFragment = CalculatorFragment() }
        unitConverterFragment ?: run { unitConverterFragment = UnitConverterFragment() }
        settingsFragment ?: run { settingsFragment = SettingsFragment() }

        binding.navigation.setupWithViewPager2(
            this,
            binding.pager2,
            currencyConverterFragment!!, calculatorFragment!!, unitConverterFragment!!, settingsFragment!!,
        )

        val tabId = when (intent.action) {
            Intent.ACTION_MAIN -> PrefsHelper.defaultNavItem
            ACTION_CALCULATOR -> R.id.navigation_calc
            ACTION_CONVERTER -> R.id.navigation_unit
            ACTION_CURRENCIES -> R.id.navigation_currency
            ACTION_SETTINGS -> R.id.navigation_settings
            else -> R.id.navigation_calc
        }
        binding.navigation.selectedItemId = tabId

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            && (!isLightTheme() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
        ) {
            val back = binding.navigation.background
            if (back is MaterialShapeDrawable) {
                window.navigationBarColor = back.resolvedTintColor
            }
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        CurrencyFunctions.setupCurrencyDownload(
            this,
            PrefsHelper.currencyBackgroundUpdateType,
            PrefsHelper.currencyBackgroundUpdateInterval,
            ExistingPeriodicWorkPolicy.KEEP,
        )
    }

    private fun handleBackPress() {
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

    private companion object {

        const val ACTION_CALCULATOR = "ru.art2000.calculator.action.CALCULATOR"
        const val ACTION_CONVERTER = "ru.art2000.calculator.action.CONVERTER"
        const val ACTION_CURRENCIES = "ru.art2000.calculator.action.CURRENCIES"
        const val ACTION_SETTINGS = "ru.art2000.calculator.action.SETTINGS"

    }
}