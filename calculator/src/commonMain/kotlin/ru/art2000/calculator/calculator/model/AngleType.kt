package ru.art2000.calculator.calculator.model

enum class AngleType {
    DEGREES {

        override val next get() = RADIANS

        override val display get() = "DEG"
    },
    RADIANS {

        override val next get() = DEGREES

        override val display get() = "RAD"
    };

    abstract val next: AngleType

    abstract val display: String

    override fun toString() = display
}