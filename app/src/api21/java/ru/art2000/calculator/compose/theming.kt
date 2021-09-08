package ru.art2000.calculator.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.gestures.OverScrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import ru.art2000.calculator.R
import ru.art2000.helpers.PrefsHelper

@Composable
fun AutoThemed(content: @Composable () -> Unit) {
    val colors = when (isSystemInDarkTheme()) {
        true -> when (isBlackTheme()) {
            true -> blackCalculatorColors()
            false -> darkCalculatorColors()
        }
        false -> lightCalculatorColors()
    }

    Themed(calculatorColors = colors, content = content)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Themed(
    calculatorColors: CalculatorColors,
    content: @Composable () -> Unit
) {

    val overScrollConfiguration = OverScrollConfiguration(
        glowColor = calculatorColors.fallback.background,
    )

    CompositionLocalProvider(
        LocalColors provides calculatorColors,
        LocalOverScrollConfiguration provides overScrollConfiguration,
    ) {
        MaterialTheme(
            colors = calculatorColors.fallback,
            content = content,
        )
    }
}

@Composable
fun LightTheme(content: @Composable () -> Unit) = Themed(
    calculatorColors = lightCalculatorColors(),
    content = content,
)

@Composable
fun DarkTheme(content: @Composable () -> Unit) = Themed(
    calculatorColors = darkCalculatorColors(),
    content = content,
)

@Composable
fun BlackTheme(content: @Composable () -> Unit) = Themed(
    calculatorColors = blackCalculatorColors(),
    content = content,
)

@Suppress("unused")
val MaterialTheme.calculatorColors: CalculatorColors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current

private val LocalColors = staticCompositionLocalOf { lightCalculatorColors() }

fun lightCalculatorColors(fallback: Colors = myLightColors()) = CalculatorColors(
    fallback,
    calculatorInputBackground = Color(0xffdedede),
    buttonTextColor = fallback.primary, //accent
    colorAccentTransparent = Color(0x203F51B5),
    floatingViewBackground = Color(0xffdedede),
    strokeColor = Color(0xffa7a7a7),
)

private fun myLightColors() = lightColors(
    primary = Color(0XFF3F51B5),
    background = Color(0xfffafafa),
)

fun darkCalculatorColors(fallback: Colors = myDarkColors()) = CalculatorColors(
    fallback,
    calculatorInputBackground = Color(0xff000000),
    buttonTextColor = fallback.primary, //accent
    colorAccentTransparent = Color(0x208E8CD8),
    floatingViewBackground = Color(0xff1e1e1e),
    strokeColor = Color(0xff8a8a8a),
)

private fun myDarkColors() = darkColors(
    primary = Color(0XFF8E8CD8),
    background = Color(0xff404040),
)

fun blackCalculatorColors(fallback: Colors = myBlackColors()) = CalculatorColors(
    fallback,
    calculatorInputBackground = Color(0xff000000),
    buttonTextColor = fallback.primary, //accent
    colorAccentTransparent = Color(0x208E8CD8),
    floatingViewBackground = Color(0xff1e1e1e),
    strokeColor = Color(0xffa7a7a7),
)

private fun myBlackColors() = darkColors(
    primary = Color(0XFF8E8CD8),
    background = Color(0xff000000),
)

private fun isBlackTheme() = PrefsHelper.getAppTheme() != R.style.RT_AppTheme_Dark &&
        PrefsHelper.isAppAutoDarkThemeBlack()