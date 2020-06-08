package ru.art2000.calculator.calculator.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CalculatorHistoryDao {

    @Query("SELECT * FROM history")
    fun getAll(): LiveData<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: HistoryItem): Long

    @Query("DELETE FROM history WHERE id = :id")
    fun deleteById(id: Int): Int

    @Query("DELETE FROM history")
    fun clear(): Int
}