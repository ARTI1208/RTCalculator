package ru.art2000.calculator.calculator.di

import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import ru.art2000.calculator.calculator.preferences.CommonCalculatorPreferenceHelper
import ru.art2000.extensions.preferences.standardAppPreferences
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.art2000.calculator.calculator.preferences.CalculatorPreferenceHelper

val calculatorModule = module {

    single<CalculatorPreferenceHelper> {
        CommonCalculatorPreferenceHelper(NSUserDefaults.standardAppPreferences)
    }
}

class CalculatorHelper : KoinComponent {

    val calculatorPreferenceHelper: CalculatorPreferenceHelper by inject()

}