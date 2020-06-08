package ru.art2000.calculator.calculator.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
class HistoryItem(val expression: String, val result: String) {

    @PrimaryKey(autoGenerate = true)
    public var id : Int = 0

}