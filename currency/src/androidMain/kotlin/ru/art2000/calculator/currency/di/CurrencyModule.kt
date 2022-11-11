package ru.art2000.calculator.currency.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.ExistingPeriodicWorkPolicy
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import ru.art2000.calculator.common.AppStartupListener
import ru.art2000.calculator.common.preferences.SettingsSetup
import ru.art2000.calculator.common.di.PageKey
import ru.art2000.calculator.common.model.MainPage
import ru.art2000.calculator.currency.background.CurrencyDownloadWorker
import ru.art2000.calculator.currency.db.CurrencyDao
import ru.art2000.calculator.currency.db.CurrencyRoomDB
import ru.art2000.calculator.currency.preferences.AndroidCurrencyPreferenceHelper
import ru.art2000.calculator.currency.preferences.AndroidCurrencyPreferenceHelperImpl
import ru.art2000.calculator.currency.preferences.CurrencyPreferenceHelper
import ru.art2000.calculator.currency.preferences.CurrencySettingsSetup
import ru.art2000.calculator.currency.remote.CurrencyRemoteBackend
import ru.art2000.calculator.currency.remote.cbr.CbrBackend
import ru.art2000.calculator.currency.repo.CurrencyRepository
import ru.art2000.calculator.currency.repo.DefaultCurrencyRepo
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CurrencyModule {

    @Binds
    abstract fun bindAndroidPreferenceHelper(
        prefsHelper: AndroidCurrencyPreferenceHelperImpl
    ): AndroidCurrencyPreferenceHelper

    @Binds
    abstract fun bindPreferenceHelper(
        prefsHelper: AndroidCurrencyPreferenceHelperImpl
    ): CurrencyPreferenceHelper

    @Binds
    abstract fun bindCurrencyRemoteBackend(
        backend: CbrBackend
    ): CurrencyRemoteBackend

    @Binds
    abstract fun bindCurrencyRepository(
        repository: DefaultCurrencyRepo
    ): CurrencyRepository

    companion object {

        @Provides
        @IntoMap
        @PageKey(MainPage.CURRENCY)
        fun provideCurrencySettingsSetup(): SettingsSetup = CurrencySettingsSetup()

        @Singleton
        @Provides
        @IntoSet
        fun provideAppStartupListener(prefsHelper: CurrencyPreferenceHelper) =
            AppStartupListener { context ->

                CurrencyDownloadWorker.setupCurrencyDownload(
                    context,
                    prefsHelper.currencyBackgroundUpdateType,
                    prefsHelper.currencyBackgroundUpdateInterval,
                    ExistingPeriodicWorkPolicy.KEEP,
                )
            }

        @Singleton
        @Provides
        fun provideCurrencyDb(
            @ApplicationContext context: Context
        ): CurrencyRoomDB = buildCurrencyDatabase(context)

        @Provides
        fun provideCurrencyDao(
            database: CurrencyRoomDB
        ): CurrencyDao = database.currencyDao()

        private fun buildCurrencyDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CurrencyRoomDB::class.java, "currency.db"
            )
                .createFromAsset("currency.db")
                .addMigrations(V2Migration(), V3Migration())
                .build()

        private class V2Migration : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("drop table info")
            }
        }

        private class V3Migration : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // language=sql
                listOf(
                    """
                    CREATE TABLE currency3(
                        codeLetter TEXT NOT NULL PRIMARY KEY, 
                        rate REAL NOT NULL, 
                        position INTEGER NOT NULL
                    );
                """.trimIndent(),
                    "INSERT INTO currency3 SELECT * FROM currency;",
                    "DROP TABLE currency;",
                    "ALTER TABLE currency3 RENAME TO currency;",
                ).forEach {
                    database.execSQL(it)
                }
            }
        }
    }
}