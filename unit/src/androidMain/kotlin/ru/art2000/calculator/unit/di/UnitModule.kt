@file:Suppress("unused")

package ru.art2000.calculator.unit.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.art2000.calculator.common.preferences.SettingsSetup
import ru.art2000.calculator.common.di.PageKey
import ru.art2000.calculator.common.model.MainPage
import ru.art2000.calculator.unit.functions.ConverterFunctionsProvider
import ru.art2000.calculator.unit.functions.AndroidDoubleFunctionsProvider
import ru.art2000.calculator.unit.preferences.AndroidUnitPreferenceHelperImpl
import ru.art2000.calculator.unit.preferences.UnitPreferenceHelper
import ru.art2000.calculator.unit.preferences.UnitSettingsSetup

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UnitModule {

    @Binds
    abstract fun bindPreferenceHelper(
        prefsHelper: AndroidUnitPreferenceHelperImpl
    ): UnitPreferenceHelper

    @Binds
    abstract fun bindFunctionsProvider(
        provider: AndroidDoubleFunctionsProvider
    ): ConverterFunctionsProvider<Int>

    companion object {

        @Provides
        @IntoMap
        @PageKey(MainPage.UNIT)
        fun provideUnitSettingsSetup(): SettingsSetup = UnitSettingsSetup()

    }
}