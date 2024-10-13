package com.lingyunchi.fartlek.utils

import java.util.Locale

fun Long.secondToMinuteSecond(locale: Locale = Locale.getDefault()): String {
    // milliseconds to "m分s秒"
    return String.format(locale, "%d分%d秒", this / 60, this % 60)
}

fun Long.milliToMinuteSecond(locale: Locale = Locale.getDefault()): String {
    // milliseconds to "m分s秒"
    return String.format(locale, "%d分%d秒", this / 60000, this % 60000 / 1000)
}