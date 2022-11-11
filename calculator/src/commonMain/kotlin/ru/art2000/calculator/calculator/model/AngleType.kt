package ru.art2000.calculator.calculator.model

enum class AngleType {
    DEGREES {
        override fun toString() = "DEG"
    },
    RADIANS {
        override fun toString() = "RAD"
    }
}