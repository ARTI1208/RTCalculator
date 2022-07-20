package ru.art2000.calculator.model.common

import androidx.room.TypeConverter
import java.util.*

class RoomConverters {

    @TypeConverter
    fun calendarFromTimestamp(value: Long?) = value?.let { dateValue ->
        Calendar.getInstance().apply { time = Date(dateValue) }
    }

    @TypeConverter
    fun calendarToTimestamp(date: Calendar?) = date?.time?.time

}