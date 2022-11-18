@file:Suppress("unused")

package ru.art2000.calculator.calculator.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import ru.art2000.calculator.calculator.R
import ru.art2000.calculator.calculator.db.CalculatorHistoryDB
import ru.art2000.calculator.calculator.db.CalculatorHistoryDao
import ru.art2000.calculator.calculator.preferences.CalculatorPreferenceHelper
import ru.art2000.calculator.calculator.preferences.CalculatorSettingsSetup
import ru.art2000.calculator.calculator.preferences.CommonCalculatorPreferenceHelper
import ru.art2000.calculator.calculator.repo.HistoryRepository
import ru.art2000.calculator.calculator.repo.RoomHistoryRepository
import ru.art2000.calculator.calculator.view.CalculatorFragment
import ru.art2000.calculator.common.preferences.SettingsSetup
import ru.art2000.calculator.common.di.PageKey
import ru.art2000.calculator.common.model.MainPage
import ru.art2000.calculator.common.preferences.MainTabData
import ru.art2000.calculator.common.preferences.MainTabDataImpl
import ru.art2000.extensions.preferences.getDefaultAppPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CalculatorModule {

    @Binds
    abstract fun bindHistoryRepository(
        repository: RoomHistoryRepository
    ): HistoryRepository

    companion object {

        @Singleton
        @Provides
        fun providePreferenceHelper(
            @ApplicationContext context: Context
        ): CalculatorPreferenceHelper = CommonCalculatorPreferenceHelper(
            context.getDefaultAppPreferences()
        )

        @Provides
        @IntoMap
        @PageKey(MainPage.CALCULATOR)
        fun provideCalculatorSettingsSetup(): SettingsSetup = CalculatorSettingsSetup()

        @Provides
        @IntoMap
        @PageKey(MainPage.CALCULATOR)
        fun provideTabData(): MainTabData<*> = MainTabDataImpl(
            R.string.title_calc,
            "calc_tab",
            R.id.calc_tab,
            R.drawable.ic_calc,
            ::CalculatorFragment,
        )

        @Singleton
        @Provides
        fun provideHistoryDb(
            @ApplicationContext context: Context
        ): CalculatorHistoryDB = buildHistoryDatabase(context)

        @Provides
        fun provideHistoryDao(
            database: CalculatorHistoryDB
        ): CalculatorHistoryDao = database.historyDao()

        private fun buildHistoryDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CalculatorHistoryDB::class.java, "CalculationHistory.db"
            )
                .addMigrations(
                    CalculationHistoryMigration1to2(),
                    CalculationHistoryMigration2to3(),
                ).fallbackToDestructiveMigrationOnDowngrade()
                .build()

        private class CalculationHistoryMigration1to2 : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE history ADD COLUMN date INTEGER NOT NULL DEFAULT 0")
            }
        }

        private class CalculationHistoryMigration2to3 : Migration(2, 3) {

            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE history ADD COLUMN comment TEXT")
            }
        }
    }
}