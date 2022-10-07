package ru.art2000.calculator.compose

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.OverscrollConfiguration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import ru.art2000.calculator.R
import ru.art2000.helpers.PrefsHelper

@Composable
fun AutoThemed(content: @Composable () -> Unit) {

    val colors = when (PrefsHelper.areDynamicColorsEnabled()) {
        true -> when (isSystemInDarkTheme()) {
            true -> darkCalculatorColors(dynamicDarkColorScheme(LocalContext.current))
            false -> lightCalculatorColors(dynamicLightColorScheme(LocalContext.current))
        }
        false -> when (isSystemInDarkTheme()) {
            true -> if (isBlackTheme()) blackCalculatorColors() else darkCalculatorColors()
            false -> lightCalculatorColors()
        }
    }

    Themed(calculatorColors = colors, content = content)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Themed(
    calculatorColors: CalculatorColors,
    content: @Composable () -> Unit
) {

    val overScrollConfiguration = OverscrollConfiguration(
        glowColor = calculatorColors.fallback.background,
    )

    CompositionLocalProvider(
        LocalColors provides calculatorColors,
        LocalOverscrollConfiguration provides overScrollConfiguration,
        LocalRippleTheme provides MyRippleTheme,
    ) {
        MaterialTheme(
            colorScheme = calculatorColors.fallback,
        ) {
            CompositionLocalProvider(
                LocalRippleTheme provides MyRippleTheme,
                content = content
            )
        }
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

private object MyRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color = MaterialTheme.colorScheme.onSurface

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        Color.Black,
        lightTheme = !isSystemInDarkTheme()
    )
}

@Suppress("unused")
val MaterialTheme.calculatorColors: CalculatorColors
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current

@SuppressLint("CompositionLocalNaming")
private var backingLocalColors: ProvidableCompositionLocal<CalculatorColors>? = null
private val LocalColors: ProvidableCompositionLocal<CalculatorColors>
    @Composable
    @ReadOnlyComposable
    get() {
        if (backingLocalColors == null) {
            val colors = lightCalculatorColors()
            backingLocalColors = staticCompositionLocalOf { colors }
        }

        return backingLocalColors!!
    }

@Composable
@ReadOnlyComposable
fun lightCalculatorColors(fallback: ColorScheme = myLightColors()) = CalculatorColors(
    fallback,
    strokeColor = colorResource(R.color.LightTheme_strokeColor),
)

@Composable
@ReadOnlyComposable
private fun myLightColors() = lightColorScheme(
    surfaceTint = colorResource(R.color.LightTheme_surfaceTint),
    onErrorContainer = colorResource(R.color.LightTheme_onErrorContainer),
    onError = colorResource(R.color.LightTheme_onError),
    errorContainer = colorResource(R.color.LightTheme_errorContainer),
    onTertiaryContainer = colorResource(R.color.LightTheme_onTertiaryContainer),
    onTertiary = colorResource(R.color.LightTheme_onTertiary),
    tertiaryContainer = colorResource(R.color.LightTheme_tertiaryContainer),
    tertiary = colorResource(R.color.LightTheme_tertiary),
    error = colorResource(R.color.LightTheme_error),
    outline = colorResource(R.color.LightTheme_outline),
    onBackground = colorResource(R.color.LightTheme_onBackground),
    background = colorResource(R.color.LightTheme_background),
    inverseOnSurface = colorResource(R.color.LightTheme_inverseOnSurface),
    inverseSurface = colorResource(R.color.LightTheme_inverseSurface),
    onSurfaceVariant = colorResource(R.color.LightTheme_onSurfaceVariant),
    onSurface = colorResource(R.color.LightTheme_onSurface),
    surfaceVariant = colorResource(R.color.LightTheme_surfaceVariant),
    surface = colorResource(R.color.LightTheme_surface),
    onSecondaryContainer = colorResource(R.color.LightTheme_onSecondaryContainer),
    onSecondary = colorResource(R.color.LightTheme_onSecondary),
    secondaryContainer = colorResource(R.color.LightTheme_secondaryContainer),
    secondary = colorResource(R.color.LightTheme_secondary),
    inversePrimary = colorResource(R.color.LightTheme_inversePrimary),
    onPrimaryContainer = colorResource(R.color.LightTheme_onPrimaryContainer),
    onPrimary = colorResource(R.color.LightTheme_onPrimary),
    primaryContainer = colorResource(R.color.LightTheme_primaryContainer),
    primary = colorResource(R.color.LightTheme_primary),
)

@Composable
@ReadOnlyComposable
fun darkCalculatorColors(fallback: ColorScheme = myDarkColors()) = CalculatorColors(
    fallback,
    strokeColor = colorResource(R.color.DarkTheme_strokeColor),
)

@Composable
@ReadOnlyComposable
private fun myDarkColors(
    surfaceTint: Color = colorResource(R.color.DarkTheme_surfaceTint),
    onErrorContainer: Color = colorResource(R.color.DarkTheme_onErrorContainer),
    onError: Color = colorResource(R.color.DarkTheme_onError),
    errorContainer: Color = colorResource(R.color.DarkTheme_errorContainer),
    onTertiaryContainer: Color = colorResource(R.color.DarkTheme_onTertiaryContainer),
    onTertiary: Color = colorResource(R.color.DarkTheme_onTertiary),
    tertiaryContainer: Color = colorResource(R.color.DarkTheme_tertiaryContainer),
    tertiary: Color = colorResource(R.color.DarkTheme_tertiary),
    error: Color = colorResource(R.color.DarkTheme_error),
    outline: Color = colorResource(R.color.DarkTheme_outline),
    onBackground: Color = colorResource(R.color.DarkTheme_onBackground),
    background: Color = colorResource(R.color.DarkTheme_background),
    inverseOnSurface: Color = colorResource(R.color.DarkTheme_inverseOnSurface),
    inverseSurface: Color = colorResource(R.color.DarkTheme_inverseSurface),
    onSurfaceVariant: Color = colorResource(R.color.DarkTheme_onSurfaceVariant),
    onSurface: Color = colorResource(R.color.DarkTheme_onSurface),
    surfaceVariant: Color = colorResource(R.color.DarkTheme_surfaceVariant),
    surface: Color = colorResource(R.color.DarkTheme_surface),
    onSecondaryContainer: Color = colorResource(R.color.DarkTheme_onSecondaryContainer),
    onSecondary: Color = colorResource(R.color.DarkTheme_onSecondary),
    secondaryContainer: Color = colorResource(R.color.DarkTheme_secondaryContainer),
    secondary: Color = colorResource(R.color.DarkTheme_secondary),
    inversePrimary: Color = colorResource(R.color.DarkTheme_inversePrimary),
    onPrimaryContainer: Color = colorResource(R.color.DarkTheme_onPrimaryContainer),
    onPrimary: Color = colorResource(R.color.DarkTheme_onPrimary),
    primaryContainer: Color = colorResource(R.color.DarkTheme_primaryContainer),
    primary: Color = colorResource(R.color.DarkTheme_primary),
) = darkColorScheme(
    surfaceTint = surfaceTint,
    onErrorContainer = onErrorContainer,
    onError = onError,
    errorContainer = errorContainer,
    onTertiaryContainer = onTertiaryContainer,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    tertiary = tertiary,
    error = error,
    outline = outline,
    onBackground = onBackground,
    background = background,
    inverseOnSurface = inverseOnSurface,
    inverseSurface = inverseSurface,
    onSurfaceVariant = onSurfaceVariant,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    surface = surface,
    onSecondaryContainer = onSecondaryContainer,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    secondary = secondary,
    inversePrimary = inversePrimary,
    onPrimaryContainer = onPrimaryContainer,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    primary = primary,
)

@Composable
fun blackCalculatorColors(fallback: ColorScheme = myBlackColors()) = CalculatorColors(
    fallback,
    strokeColor = colorResource(R.color.BlackTheme_strokeColor),
)

@Composable
private fun myBlackColors() = myDarkColors(
    background = colorResource(R.color.BlackTheme_background),
    surface = colorResource(R.color.BlackTheme_surface),
    surfaceVariant = colorResource(R.color.BlackTheme_surfaceVariant),
)

private fun isBlackTheme() = PrefsHelper.getAppTheme() != R.style.RT_AppTheme_Dark &&
        PrefsHelper.isAppAutoDarkThemeBlack()