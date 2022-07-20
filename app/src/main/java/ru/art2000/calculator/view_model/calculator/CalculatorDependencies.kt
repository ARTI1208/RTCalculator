package ru.art2000.calculator.view_model.calculator

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.art2000.calculator.model.calculator.history.CalculatorHistoryDB

object CalculatorDependencies {

    @Volatile
    private var INSTANCE: CalculatorHistoryDB? = null

    @JvmStatic
    fun getHistoryDatabase(context: Context): CalculatorHistoryDB =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

    private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                    CalculatorHistoryDB::class.java, "CalculationHistory.db")
                    .addMigrations(
                            CalculationHistoryMigration1to2(),
                    ).fallbackToDestructiveMigrationOnDowngrade()
                    .build()

    private class CalculationHistoryMigration1to2 : Migration(1, 2) {

        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE history ADD COLUMN date INTEGER NOT NULL DEFAULT 0")
        }
    }
}