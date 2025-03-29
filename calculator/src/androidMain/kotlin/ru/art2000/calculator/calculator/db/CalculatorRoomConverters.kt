package ru.art2000.calculator.calculator.db

import androidx.room.TypeConverter
import ru.art2000.calculator.calculator.model.AngleType

@Suppress("unused")
class CalculatorRoomConverters {

    @TypeConverter
    fun toAngleType(value: String) = enumValueOf<AngleType>(value)

    @TypeConverter
    fun fromAngleType(value: AngleType) = value.name

}