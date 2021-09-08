package ru.art2000.calculator.compose

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

data class CalculatorColors(
    val fallback: Colors,
    val calculatorInputBackground: Color,
    val buttonTextColor: Color,
    val colorAccentTransparent: Color,
    val floatingViewBackground: Color,
    val strokeColor: Color,
)
