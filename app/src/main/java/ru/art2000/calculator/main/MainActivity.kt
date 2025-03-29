package ru.art2000.calculator.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import dev.androidbroadcast.vbpd.viewBinding
import ru.art2000.calculator.R
import ru.art2000.calculator.common.model.MainPage
import ru.art2000.calculator.common.preferences.MainTabData
import ru.art2000.calculator.common.view.AppActivity
import ru.art2000.calculator.databinding.ActivityMainBinding
import ru.art2000.calculator.settings.preferences.PreferenceValues
import ru.art2000.extensions.views.isLandscape
import ru.art2000.extensions.views.setMyOnItemSelectedListener
import ru.art2000.extensions.views.setSelectedItemId
import ru.art2000.extensions.views.setupWithViewPager2
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppActivity(R.layout.activity_main) {
    private val binding by viewBinding(ActivityMainBinding::bind)

    @Inject
    lateinit var tabDatas: Map<MainPage, @JvmSuppressWildcards MainTabData<*>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navigation = binding.navigation as NavigationBarView

        val (pages, orderedData) = tabDatas
            .toSortedMap(Comparator.comparing { it.ordinal })
            .let { it.keys.toList() to it.values }

        navigation.setMyOnItemSelectedListener { item ->

            val page = pages[orderedData.indexOfFirst { it.idRes == item.itemId }]!!

            generalPrefsHelper.defaultTabToOpen = when (page) {
                MainPage.UNIT -> PreferenceValues.VALUE_TAB_DEFAULT_UNIT
                MainPage.CURRENCY -> PreferenceValues.VALUE_TAB_DEFAULT_CURRENCY
                MainPage.SETTINGS -> PreferenceValues.VALUE_TAB_DEFAULT_SETTINGS
                MainPage.CALCULATOR -> PreferenceValues.VALUE_TAB_DEFAULT_CALC
            }
            intent.action = when (page) {
                MainPage.UNIT -> ACTION_CONVERTER
                MainPage.CURRENCY -> ACTION_CURRENCIES
                MainPage.SETTINGS -> ACTION_SETTINGS
                MainPage.CALCULATOR -> ACTION_CALCULATOR
            }

            true
        }

        navigation.setupWithViewPager2(
            this,
            binding.pager2,
            orderedData.map { it.tabCreator },
        )

        val page = when (intent.action) {
            Intent.ACTION_MAIN -> when (generalPrefsHelper.defaultTabToOpen) {
                PreferenceValues.VALUE_TAB_DEFAULT_UNIT -> MainPage.UNIT
                PreferenceValues.VALUE_TAB_DEFAULT_CURRENCY -> MainPage.CURRENCY
                PreferenceValues.VALUE_TAB_DEFAULT_SETTINGS -> MainPage.SETTINGS
                else -> MainPage.CALCULATOR
            }
            ACTION_CALCULATOR -> MainPage.CALCULATOR
            ACTION_CONVERTER -> MainPage.UNIT
            ACTION_CURRENCIES -> MainPage.CURRENCY
            ACTION_SETTINGS -> MainPage.SETTINGS
            else -> MainPage.CALCULATOR
        }
        navigation.setSelectedItemId(tabDatas[page]!!.idRes, false)
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