package ru.art2000.calculator.unit.model

internal enum class UnitCategory {
    VELOCITY,
    DISTANCE,
    AREA,
    VOLUME,
    MASS,
    PRESSURE,
    TEMPERATURE;

    companion object {

        fun ofOrdinal(ordinal: Int) = entries[ordinal]
    }
}