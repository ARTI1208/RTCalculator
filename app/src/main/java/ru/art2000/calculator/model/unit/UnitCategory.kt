package ru.art2000.calculator.model.unit

enum class UnitCategory {
    VELOCITY,
    DISTANCE,
    AREA,
    VOLUME,
    MASS,
    PRESSURE,
    TEMPERATURE;

    companion object {

        private val cached = values()

        fun ofOrdinal(ordinal: Int) = cached[ordinal]
    }
}