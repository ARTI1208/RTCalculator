package ru.art2000.calculator.view_model.currency

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.model.currency.CurrencyRoomDB
import kotlin.system.measureTimeMillis


object CurrencyDependencies {

    @Volatile
    private var INSTANCE: CurrencyRoomDB? = null

    @JvmStatic
    fun getCurrencyDatabase(context: Context): CurrencyRoomDB {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }
    }

    private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                    CurrencyRoomDB::class.java, "currency.db")
                    .addMigrations(RoomMigration())
                    .build()

    private class RoomMigration : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Schema is not changed, but we show Room that we switched from android low-level
            // database APIs to Room
        }
    }

    private val codeToIdentifier = hashMapOf<String, Int>()

    @JvmStatic
    fun getNameIdentifierForCode(context: Context, code: String): Int {

        return codeToIdentifier[code] ?: context.resources.getIdentifier(
                "currency_$code",
                "string",
                context.packageName
        ).also { codeToIdentifier[code] = it }
    }
}