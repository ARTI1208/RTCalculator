@file:Suppress("unused")

package ru.art2000.calculator.main.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.art2000.calculator.common.view.AutoThemeData
import ru.art2000.calculator.main.CalculatorThemeData

@Module
@InstallIn(SingletonComponent::class)
abstract class GeneralModule {

    @Binds
    abstract fun bindThemeData(
        data: CalculatorThemeData
    ): AutoThemeData
}
