package ru.art2000.calculator.common.view

import android.os.Bundle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import ru.art2000.calculator.common.R
import ru.art2000.calculator.common.preferences.GeneralPreferenceHelper
import ru.art2000.extensions.activities.AutoThemeActivity
import ru.art2000.extensions.activities.IEdgeToEdgeActivity
import ru.art2000.extensions.preferences.AppTheme
import ru.art2000.extensions.preferences.ThemeSettingsHolder
import ru.art2000.extensions.views.isDarkThemeApplied
import ru.art2000.extensions.preferences.listen

abstract class AppActivity : AutoThemeActivity(), IEdgeToEdgeActivity {

    private val holder by lazy {
        EntryPointAccessors.fromApplication<PreferenceHelperHolder>(this)
    }

    protected val generalPrefsHelper: GeneralPreferenceHelper by lazy {
        holder.prefsHelper.apply {

            val recreation = { _: Any -> recreate() }
            val themeChecker = { _: Any -> requestThemeCheck() }

            appThemeProperty.listen(this@AppActivity, recreation)
            dynamicColorsProperty.listen(this@AppActivity, recreation)
            autoDarkThemeProperty.listen(this@AppActivity) {
                if (isDarkThemeApplied) recreate()
            }
            darkThemeActivationProperty.listen(this@AppActivity, themeChecker)
            darkThemeDeactivationProperty.listen(this@AppActivity, themeChecker)
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PreferenceHelperHolder {
        val prefsHelper: GeneralPreferenceHelper

        val autoThemeData: AutoThemeData
    }

    override val themeSettingsHolder: ThemeSettingsHolder
        get() = generalPrefsHelper

    override fun getSystemTheme(black: Boolean) = holder.autoThemeData.getSystemTheme(black)

    override fun getBatteryTheme(black: Boolean) = holder.autoThemeData.getBatteryTheme(black)

    override fun getDayNightTheme(black: Boolean) = holder.autoThemeData.getDayNightTheme(black)

    override fun getThemeResId(theme: AppTheme) = holder.autoThemeData.getThemeRes(theme)

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        applyEdgeToEdgeIfAvailable()
    }
}