package ru.art2000.calculator.shared.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import ru.art2000.calculator.common.preferences.GeneralPreferenceHelper
import ru.art2000.calculator.settings.preferences.PreferenceHelper
import ru.art2000.extensions.preferences.standardAppPreferences

val generalModule = module {

    single<GeneralPreferenceHelper> {
        PreferenceHelper(NSUserDefaults.standardAppPreferences)
    }
}

class GeneralHelper : KoinComponent {

    val generalPreferenceHelper: GeneralPreferenceHelper by inject()

}