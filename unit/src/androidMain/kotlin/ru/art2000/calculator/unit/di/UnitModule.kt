@file:Suppress("unused")

package ru.art2000.calculator.unit.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.art2000.calculator.common.preferences.SettingsSetup
import ru.art2000.calculator.common.di.PageKey
import ru.art2000.calculator.common.model.MainPage
import ru.art2000.calculator.common.preferences.MainTabData
import ru.art2000.calculator.common.preferences.MainTabDataImpl
import ru.art2000.calculator.unit.R
import ru.art2000.calculator.unit.functions.ConverterFunctionsProvider
import ru.art2000.calculator.unit.functions.AndroidDoubleFunctionsProvider
import ru.art2000.calculator.unit.preferences.CommonUnitPreferenceHelper
import ru.art2000.calculator.unit.preferences.UnitPreferenceHelper
import ru.art2000.calculator.unit.preferences.UnitSettingsSetup
import ru.art2000.extensions.preferences.getDefaultAppPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UnitModule {

    @Binds
    abstract fun bindFunctionsProvider(
        provider: AndroidDoubleFunctionsProvider
    ): ConverterFunctionsProvider<Int>

    companion object {

        @Singleton
        @Provides
        fun providePreferenceHelper(
            @ApplicationContext context: Context
        ): UnitPreferenceHelper = CommonUnitPreferenceHelper(
            context.getDefaultAppPreferences()
        )

        @Provides
        @IntoMap
        @PageKey(MainPage.UNIT)
        fun provideUnitSettingsSetup(): SettingsSetup = UnitSettingsSetup()

        @Provides
        @IntoMap
        @PageKey(MainPage.UNIT)
        fun provideTabData(): MainTabData = MainTabDataImpl(R.string.title_unit, "unit_tab")

    }
}