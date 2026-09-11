package com.cetin072.worknotebook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Navy = Color(0xFF111827)
private val Blue = Color(0xFF2563EB)

private val LightColors = lightColorScheme(
    primary = Navy,
    secondary = Blue,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE5E7EB),
    secondary = Color(0xFF93C5FD),
)

@Composable
fun WorkNotebookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
