package ru.art2000.calculator.shared.di

import ru.art2000.calculator.calculator.di.calculatorModule

internal fun appModule() = listOf(generalModule, calculatorModule)