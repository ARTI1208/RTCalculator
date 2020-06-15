package ru.art2000.calculator.view_model.currency

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import androidx.room.Room
import ru.art2000.calculator.model.currency.CurrencyItem
import ru.art2000.calculator.model.currency.CurrencyRoomDB
import kotlin.system.measureTimeMillis


object CurrencyDependencies {

    @Volatile
    private var INSTANCE: CurrencyRoomDB? = null

    @JvmStatic
    fun getCurrencyDatabase(context: Context): CurrencyRoomDB {

        var res: CurrencyRoomDB? = null
        val loadTime = measureTimeMillis {
            res = INSTANCE
                    ?: synchronized(this) {
                INSTANCE
                        ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        Log.d("DBLOAD", loadTime.toString())

        return res!!
    }

    private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                    CurrencyRoomDB::class.java, "currency.db")
                    .fallbackToDestructiveMigration()
                    .build()


    private val codeToIdentifier = hashMapOf<String, Int>()

    @JvmStatic
    fun getNameIdentifierForCode(context: Context, code: String): Int {

        return codeToIdentifier[code] ?: context.resources.getIdentifier(
                "currency_$code",
                "string",
                context.packageName
        ).also { codeToIdentifier[code] = it }
    }


    @JvmStatic
    fun getDiffCallback(oldData: List<CurrencyItem>, newData: List<CurrencyItem>): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldData.size
            }

            override fun getNewListSize(): Int {
                return newData.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldData[oldItemPosition].code == newData[newItemPosition].code
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsTheSame(oldItemPosition, newItemPosition)
            }
        }
    }
}