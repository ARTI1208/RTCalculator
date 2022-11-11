package ru.art2000.calculator.calculator.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.art2000.calculator.calculator.db.model.HistoryDatabaseItem
import ru.art2000.calculator.calculator.model.HistoryValueItem

@Dao
internal interface CalculatorHistoryDao {

    @Query("SELECT * FROM history")
    fun getAll(): Flow<List<HistoryDatabaseItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryDatabaseItem): Long

    @Update(entity = HistoryDatabaseItem::class)
    suspend fun update(item: HistoryValueItem)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("DELETE FROM history")
    suspend fun clear(): Int
}