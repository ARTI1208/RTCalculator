package ru.art2000.calculator.model.calculator.history

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CalculatorHistoryDao {

    @Query("SELECT * FROM history")
    fun getAll(): LiveData<List<HistoryDatabaseItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryDatabaseItem): Long

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("DELETE FROM history")
    suspend fun clear(): Int
}