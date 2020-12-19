package ru.art2000.calculator.model.calculator

enum class AngleType {
    DEGREES {
        override fun toString(): String {
            return "Deg"
        }
    },
    RADIANS {
        override fun toString(): String {
            return "Rad"
        }
    }
}