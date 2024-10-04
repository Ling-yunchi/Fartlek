package com.lingyunchi.fartlek

enum class DarkTheme(val value: Int) {
    Light(0),
    Dark(1),
    System(2);

    override fun toString(): String {
        return when (this) {
            Light -> "Light"
            Dark -> "Dark"
            System -> "System"
        }
    }
}

fun String.toDarkTheme(): DarkTheme {
    return when (this) {
        "Light" -> DarkTheme.Light
        "Dark" -> DarkTheme.Dark
        "System" -> DarkTheme.System
        else -> throw IllegalArgumentException("Unknown dark theme: $this")
    }
}