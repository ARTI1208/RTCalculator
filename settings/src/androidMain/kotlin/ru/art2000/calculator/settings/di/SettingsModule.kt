package ru.art2000.calculator.settings.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.art2000.calculator.common.preferences.GeneralPreferenceHelper
import ru.art2000.calculator.settings.preferences.PreferenceHelper
import ru.art2000.extensions.preferences.getDefaultAppPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    companion object {

        @Singleton
        @Provides
        fun providePreferenceHelper(
            @ApplicationContext context: Context
        ): GeneralPreferenceHelper = PreferenceHelper(
            context.getDefaultAppPreferences()
        )
    }

}