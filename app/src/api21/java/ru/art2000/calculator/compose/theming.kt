package ru.art2000.calculator.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun AutoThemed(content: @Composable () -> Unit) {
    val colors = when (isSystemInDarkTheme()) {
        true -> darkColors()
        false -> lightColors()
    }
    MaterialTheme(colors, content = content)
}