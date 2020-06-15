package ru.art2000.calculator.view_model.calculator

import android.content.Context
import androidx.room.Room
import ru.art2000.calculator.model.calculator.CalculatorHistoryDB

object CalculatorDependencies {

    @Volatile private var INSTANCE: CalculatorHistoryDB? = null

    @JvmStatic
    fun getHistoryDatabase(context: Context): CalculatorHistoryDB =
            INSTANCE
                    ?: synchronized(this) {
                INSTANCE
                        ?: buildDatabase(context).also { INSTANCE = it }
            }

    private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                    CalculatorHistoryDB::class.java, "CalculationHistory.db")
                    .fallbackToDestructiveMigration()
                    .build()


}