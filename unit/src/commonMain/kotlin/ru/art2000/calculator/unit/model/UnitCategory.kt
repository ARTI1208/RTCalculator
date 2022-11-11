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

        private val cached = values()

        val count = cached.size

        fun ofOrdinal(ordinal: Int) = cached[ordinal]
    }
}