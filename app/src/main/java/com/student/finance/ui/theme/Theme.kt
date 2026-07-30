package com.student.finance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    background = BackgroundLight,
    surface = SurfaceLight,
    error = ExpenseRed
)

private val DarkColors = darkColorScheme(
    primary = GreenSecondary,
    secondary = GreenPrimaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = ExpenseRed
)

@Composable
fun StudentFinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
