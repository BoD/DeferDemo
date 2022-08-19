package com.example.deferdemo.ui.theme

import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*

private val DarkColorPalette = darkColors()

private val LightColorPalette = lightColors(background = Color(0xFFE0E0E0))

@Composable
fun DeferDemoTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}
