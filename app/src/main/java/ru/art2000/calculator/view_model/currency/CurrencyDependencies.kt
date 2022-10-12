package ru.art2000.calculator.view_model.currency

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.art2000.calculator.model.currency.CurrencyRoomDB


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

    private val codeToIdentifier = hashMapOf<String, Int>()

    @SuppressLint("DiscouragedApi")
    @JvmStatic
    fun getNameIdentifierForCode(context: Context, code: String): Int {

        return codeToIdentifier[code] ?: context.resources.getIdentifier(
                "currency_$code",
                "string",
                context.packageName
        ).also { codeToIdentifier[code] = it }
    }
}