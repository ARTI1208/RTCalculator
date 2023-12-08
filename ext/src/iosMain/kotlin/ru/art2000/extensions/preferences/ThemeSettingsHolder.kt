package ru.art2000.extensions.preferences

actual enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK,
}

actual interface ThemeSettingsHolder {

    actual val appTheme: AppTheme

    actual val isBlackThemePreferred: Boolean

    actual val darkThemeActivationTime: Int

    actual val darkThemeDeactivationTime: Int

}