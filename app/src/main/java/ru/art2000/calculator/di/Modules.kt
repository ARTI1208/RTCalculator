@file:Suppress("unused")

package ru.art2000.calculator.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.art2000.helpers.*

@Module
@InstallIn(SingletonComponent::class)
abstract class GeneralModule {

    @Binds
    abstract fun bindPreferenceHelper(
        prefsHelper: PreferenceHelper
    ): GeneralPreferenceHelper
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UnitModule {

    @Binds
    abstract fun bindPreferenceHelper(
        prefsHelper: PreferenceHelper
    ): UnitPreferenceHelper
}
