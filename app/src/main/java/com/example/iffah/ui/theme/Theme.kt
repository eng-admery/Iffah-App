package com.example.iffah.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val IffahColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryGreen,
    background = LightGreenBg,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White
)

@Composable
fun IffahTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = IffahColorScheme,
        content = content
    )
}