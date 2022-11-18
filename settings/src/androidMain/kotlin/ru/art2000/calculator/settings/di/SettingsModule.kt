package ru.art2000.calculator.settings.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.art2000.calculator.common.di.PageKey
import ru.art2000.calculator.common.model.MainPage
import ru.art2000.calculator.common.preferences.GeneralPreferenceHelper
import ru.art2000.calculator.common.preferences.MainTabData
import ru.art2000.calculator.common.preferences.MainTabDataImpl
import ru.art2000.calculator.settings.R
import ru.art2000.calculator.settings.preferences.PreferenceHelper
import ru.art2000.calculator.settings.view.SettingsFragment
import ru.art2000.extensions.preferences.getDefaultAppPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SettingsModule {

    companion object {

        @Singleton
        @Provides
        fun providePreferenceHelper(
            @ApplicationContext context: Context
        ): GeneralPreferenceHelper = PreferenceHelper(
            context.getDefaultAppPreferences()
        )

        @Provides
        @IntoMap
        @PageKey(MainPage.SETTINGS)
        fun provideTabData(): MainTabData<*> = MainTabDataImpl(
            R.string.title_settings,
            "settings_tab",
            R.id.settings_tab,
            R.drawable.ic_settings,
            ::SettingsFragment,
        )
    }

}