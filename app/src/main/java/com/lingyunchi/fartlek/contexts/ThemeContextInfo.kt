package com.lingyunchi.fartlek.contexts

import androidx.compose.runtime.compositionLocalOf
import com.lingyunchi.fartlek.DarkTheme

data class ThemeContextInfo(val dark: DarkTheme, val function: (DarkTheme) -> Unit) {
    var darkTheme: DarkTheme = DarkTheme.System
    var setDarkTheme: (DarkTheme) -> Unit = {}
}

val ThemeContext = compositionLocalOf {
    ThemeContextInfo(DarkTheme.Dark) { error("No ThemeContextInfo") }
}