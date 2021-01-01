package ru.art2000.calculator.model.calculator

enum class AngleType {
    DEGREES {
        override fun toString() = "DEG"
    },
    RADIANS {
        override fun toString() = "RAD"
    }
}