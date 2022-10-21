package ru.art2000.calculator.di

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
import ru.art2000.calculator.model.calculator.history.CalculatorHistoryDB
import ru.art2000.calculator.model.calculator.history.CalculatorHistoryDao
import ru.art2000.calculator.model.calculator.history.HistoryRepository
import ru.art2000.calculator.model.calculator.history.RoomHistoryRepository
import ru.art2000.helpers.CalculatorPreferenceHelper
import ru.art2000.helpers.PreferenceHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CalculatorModule {

    @Binds
    abstract fun bindPreferenceHelper(
        prefsHelper: PreferenceHelper
    ): CalculatorPreferenceHelper

    @Binds
    abstract fun bindHistoryRepository(
        repository: RoomHistoryRepository
    ): HistoryRepository

    companion object {

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