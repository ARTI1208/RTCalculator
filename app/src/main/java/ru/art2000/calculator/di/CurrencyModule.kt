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
import ru.art2000.calculator.model.currency.*
import ru.art2000.helpers.CurrencyPreferenceHelper
import ru.art2000.helpers.PreferenceHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CurrencyModule {

    @Binds
    abstract fun bindPreferenceHelper(
        prefsHelper: PreferenceHelper
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
            Room.databaseBuilder(context.applicationContext,
                CurrencyRoomDB::class.java, "currency.db")
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