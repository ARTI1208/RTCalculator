package ru.art2000.calculator.model.currency

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "info")
class InfoItem(
        public var date: String? = "",
        public var size: Int? = 0
) {
        @PrimaryKey(autoGenerate = true)
        public var key: Int? = 0
}