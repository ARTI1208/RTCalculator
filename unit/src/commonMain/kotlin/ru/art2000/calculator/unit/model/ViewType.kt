package ru.art2000.calculator.unit.model

internal enum class ViewType {
    SIMPLE,
    ERGONOMIC,
    POWERFUL;

    companion object {

        fun of(viewType: String) = when (viewType) {
            "simple" -> SIMPLE
            "powerful" -> POWERFUL
            else -> ERGONOMIC
        }
    }
}