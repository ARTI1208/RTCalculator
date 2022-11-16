package ru.art2000.extensions.preferences

expect enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK,
}

expect interface ThemeSettingsHolder {

    val appTheme: AppTheme

    val isBlackThemePreferred: Boolean

    val darkThemeActivationTime: Int

    val darkThemeDeactivationTime: Int

}